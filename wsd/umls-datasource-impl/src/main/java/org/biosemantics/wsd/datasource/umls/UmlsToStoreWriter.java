package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.LabelType;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.NotationSourceConstant;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class UmlsToStoreWriter {

	public void writeUmlsToStore() throws SQLException {
		writeSemanticTypePredicates();
		writeSemanticTypePredicateHierarchy();
		writeAndMapConceptPredicates();
		writeConcepts();
		writeConceptSchemes();
		writeRlspsBetweenConcepts();
		writeRlspsBetweenConceptsAndSchemes();
	}

	public void writeConcepts() throws SQLException {
		Connection distinctCuiConnection = dataSource.getConnection();
		Statement distinctCuiStatement = distinctCuiConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		distinctCuiStatement.setFetchSize(Integer.MIN_VALUE);
		ResultSet distinctCuiRs = distinctCuiStatement.executeQuery(GET_ALL_DISTINCT_CUIS);
		Connection cuiDetailsConnection = dataSource.getConnection();
		PreparedStatement cuiDetailsPstmt = cuiDetailsConnection.prepareStatement(GET_CUI_DETAILS);
		try {
			Transaction tx = template.getGraphDatabaseService().beginTx();
			int ctr = 0;
			while (distinctCuiRs.next()) {
				String cui = distinctCuiRs.getString("CUI");
				if (!ignoredCuiReader.isIgnored(cui)) {
					cuiDetailsPstmt.setString(1, cui);
					ResultSet cuiDetailsRs = cuiDetailsPstmt.executeQuery();
					Notation notation = notationRepository.save(new Notation(NotationSourceConstant.UMLS.toString(),
							cui));
					Concept concept = conceptRepository.save(new Concept(ConceptType.CONCEPT));
					concept.addNotationIfNoneExists(template, notation, MRCONSO);
					try {
						while (cuiDetailsRs.next()) {
							String text = cuiDetailsRs.getString("STR");
							String language = cuiDetailsRs.getString("LAT");
							String ts = cuiDetailsRs.getString("TS");
							String isPref = cuiDetailsRs.getString("ISPREF");
							String stt = cuiDetailsRs.getString("STT");
							LabelType labelType = getLabelType(ts, isPref, stt);
							Label label = labelRepository.save(new Label(text, language));
							concept.addLabelIfNoneExists(template, label, labelType, MRCONSO);
						}
						if (++ctr % txSize == 0) {
							tx.success();
							tx.finish();
							tx = template.getGraphDatabaseService().beginTx();
							System.out.println("millis:" + System.currentTimeMillis() + " ctr:" + ctr);
						}
						logger.info("cui {} inserted", cui);

					} catch (Exception e) {
						logger.error("some error when inserting cui: " + cui, e);
					} finally {
						cuiDetailsRs.close();
					}
				} else {
					logger.info("ignoring cui: {} ", cui);
				}
			}
			tx.success();
			tx.finish();
		} finally {
			cuiDetailsPstmt.close();
			cuiDetailsConnection.close();

			distinctCuiRs.close();
			cuiDetailsPstmt.close();
			distinctCuiConnection.close();
		}
	}

	public void writeAndMapConceptPredicates() throws SQLException {
		Connection getConceptPredicateConn = dataSource.getConnection();
		Statement getConceptRedicatesStmt = getConceptPredicateConn.createStatement();
		try {
			ResultSet rs = getConceptRedicatesStmt.executeQuery(GET_CONCEPT_PREDICATES);
			while (rs.next()) {
				Transaction tx = template.getGraphDatabaseService().beginTx();
				String text = rs.getString("VALUE");
				String invText = rs.getString("EXPL");
				Label label = labelRepository.getLabel(text, "ENG");
				if (label == null) {
					label = labelRepository.getLabel(invText, "ENG");
				}
				Concept conceptPredicate = null;
				// no label with text/inv text exists create new predicate
				if (label == null) {
					Label labelForText = labelRepository.save(new Label(text, "ENG"));
					Label labelForInvText = labelRepository.save(new Label(invText, "ENG"));
					conceptPredicate = conceptRepository.save(new Concept(ConceptType.PREDICATE));
					conceptPredicate.addLabelIfNoneExists(template, labelForText, LabelType.PREFERRED, MRREL);
					conceptPredicate.addLabelIfNoneExists(template, labelForInvText, LabelType.ALTERNATE, MRREL);
				} else {
					for (Concept relatedConcept : label.getRelatedConcepts()) {
						if (relatedConcept.getType() == ConceptType.PREDICATE) {
							conceptPredicate = relatedConcept;
						}
					}
				}
				Concept preConcept;
				// check if predicate maps to semantic type predicate
				SemanticTypePredicate semanticTypePredicate = predicateMapper.getMappedSemanticTypePredicate(text);
				if (semanticTypePredicate == null) {
					semanticTypePredicate = predicateMapper.getMappedSemanticTypePredicate(invText);
				}
				// if mapping not found
				if (semanticTypePredicate == null) {
					preConcept = getOrCreatePredicateConcept(text, invText);
				}
				if (semanticTypePredicate != null) {
					// found
					if (semanticTypePredicate.getRelationship() == SemanticTypePredicate.EQ_PROP) {
						// add the labels to the ST predicate
						Label labelForText = labelRepository.save(new Label(text, "ENG"));
						Label labelForInvText = labelRepository.save(new Label(invText, "ENG"));
						Label predicateLabel = labelRepository.getLabel(semanticTypePredicate.getRelatedTo(), "ENG");
						Iterable<Concept> concepts = predicateLabel.getRelatedConcepts();
						for (Concept concept : concepts) {
							if (concept.getType() == ConceptType.PREDICATE) {
								concept.addLabelIfNoneExists(template, labelForText, LabelType.ALTERNATE,
										"ERIK_TSV_FILE");
								concept.addLabelIfNoneExists(template, labelForInvText, LabelType.ALTERNATE,
										"ERIK_TSV_FILE");
							}
						}
					} else if (semanticTypePredicate.getRelationship() == SemanticTypePredicate.SUB_PROP) {
						// ConceptType.PREDICATE
						preConcept = getOrCreatePredicateConcept(text, invText);
						Label predicateLabel = labelRepository.getLabel(semanticTypePredicate.getRelatedTo(), "ENG");
						Iterable<Concept> concepts = predicateLabel.getRelatedConcepts();
						long relationshipType = notationRepository.getRelatedConcept(
								NotationSourceConstant.UMLS.toString(), IS_A).getNodeId();
						for (Concept predicateConcept : concepts) {
							if (predicateConcept.getType() == ConceptType.PREDICATE) {
								predicateConcept.addRelationshipIfNoneExists(template, preConcept, ""
										+ relationshipType, 0, "ERIK_TSV_FILE");
							}
						}
					}
				}

				tx.success();
				tx.finish();
			}
			rs.close();
		} finally {
			getConceptRedicatesStmt.close();
			getConceptPredicateConn.close();
		}

	}

	private Concept getOrCreatePredicateConcept(String text, String invText) {
		Label label = labelRepository.getLabel(text, "ENG");
		if (label == null) {
			label = labelRepository.getLabel(invText, "ENG");
		}
		Concept preConcept = null;
		if (label == null) {
			// create new predicate with these labels
			Label labelForText = labelRepository.save(new Label(text, "ENG"));
			Label labelForInvText = labelRepository.save(new Label(invText, "ENG"));
			preConcept = conceptRepository.save(new Concept(ConceptType.PREDICATE));
			preConcept.addLabelIfNoneExists(template, labelForText, LabelType.PREFERRED, MRREL);
			preConcept.addLabelIfNoneExists(template, labelForInvText, LabelType.ALTERNATE, MRREL);
		} else {
			Iterable<Concept> relatedConcepts = label.getRelatedConcepts();
			for (Concept relatedConcept : relatedConcepts) {
				if (relatedConcept.getType() == ConceptType.PREDICATE) {
					preConcept = relatedConcept;
				}
			}
		}
		return preConcept;
	}

	public void writeSemanticTypePredicates() throws SQLException {
		Connection getAllPredicateConnection = dataSource.getConnection();
		Statement getAllPredicateStmt = getAllPredicateConnection.createStatement();
		ResultSet rs = getAllPredicateStmt.executeQuery(GET_ALL_ST_PREDICATES);
		try {
			int ctr = 0;
			while (rs.next()) {
				Transaction tx = template.getGraphDatabaseService().beginTx();
				String notationCode = rs.getString("UI");
				String labeltext = rs.getString("STY_RL");
				String invLabelText = rs.getString("RIN");
				Concept concept = conceptRepository.save(new Concept(ConceptType.PREDICATE));
				Notation notation = notationRepository.save(new Notation(NotationSourceConstant.UMLS.toString(),
						notationCode));
				Label label = labelRepository.save(new Label(labeltext, "ENG"));
				Label invLabel = labelRepository.save(new Label(invLabelText, "ENG"));
				concept.addNotationIfNoneExists(template, notation, SRDEF);
				concept.addLabelIfNoneExists(template, label, LabelType.PREFERRED, SRDEF);
				concept.addLabelIfNoneExists(template, invLabel, LabelType.ALTERNATE, SRDEF);
				logger.info("{}", ++ctr);
				System.out.println("" + System.currentTimeMillis() + " ui:" + notationCode);
				tx.success();
				tx.finish();
			}
		} finally {
			rs.close();
			getAllPredicateStmt.close();
			getAllPredicateConnection.close();
		}
	}

	public void writeSemanticTypePredicateHierarchy() throws SQLException {
		Connection getAllPredicateConnection = dataSource.getConnection();
		Statement getAllPredicateStmt = getAllPredicateConnection.createStatement();
		ResultSet rs = getAllPredicateStmt.executeQuery(GET_UNIQUE_ST_PREDICATE_NOTATIONS);
		Connection predicateParentConn = dataSource.getConnection();
		PreparedStatement predicateParentPstmt = predicateParentConn.prepareStatement(GET_ST_PREDICATE_HIERARCHY);
		long relationshipType = notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(), IS_A)
				.getNodeId();
		try {
			while (rs.next()) {
				try {
					Transaction tx = template.getGraphDatabaseService().beginTx();
					String notationCode = rs.getString("UI");
					Concept srcConcept = notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(),
							notationCode);
					predicateParentPstmt.setString(1, notationCode);
					ResultSet predicateParentRs = predicateParentPstmt.executeQuery();
					while (predicateParentRs.next()) {
						String parentNotationCode = predicateParentRs.getString("UI3");
						Concept targetConcept = notationRepository.getRelatedConcept(
								NotationSourceConstant.UMLS.toString(), parentNotationCode);
						srcConcept.addRelationshipIfNoneExists(template, targetConcept, "" + relationshipType, 0,
								SRSTRE1);
					}
					tx.success();
					tx.finish();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		} finally {
			rs.close();
			predicateParentPstmt.close();
			predicateParentConn.close();
		}
	}

	public void writeConceptSchemes() throws SQLException {
		Connection conceptSchemeConnection = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConnection.createStatement();
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_ALL_CONCEPT_SCHEMES);
		try {
			int ctr = 0;
			while (rs.next()) {
				Transaction tx = template.getGraphDatabaseService().beginTx();
				String notationCode = rs.getString("UI");
				String labeltext = rs.getString("STY_RL");
				Concept concept = conceptRepository.save(new Concept(ConceptType.CONCEPT_SCHEME));
				Notation notation = notationRepository.save(new Notation(NotationSourceConstant.UMLS.toString(),
						notationCode));
				Label label = labelRepository.save(new Label(labeltext, "ENG"));
				concept.addNotationIfNoneExists(template, notation, SRDEF);
				concept.addLabelIfNoneExists(template, label, LabelType.PREFERRED, SRDEF);
				tx.success();
				tx.finish();
				logger.info("{}", ++ctr);
				System.out.println("" + System.currentTimeMillis() + " ui:" + notationCode);
			}
		} finally {
			rs.close();
			conceptSchemeStmt.close();
			conceptSchemeConnection.close();
		}
	}

	public void writeRlspsBetweenConceptSchemes() throws SQLException {
		Connection conceptSchemeConnection = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConnection.createStatement();
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_ALL_CONCEPT_SCHEME_RLSPS);
		try {
			int ctr = 0;
			while (rs.next()) {
				ctr++;
				Transaction tx = template.getGraphDatabaseService().beginTx();
				String fromNotation = rs.getString("UI1");
				String predicateNotation = rs.getString("UI2");
				String toNotation = rs.getString("UI3");
				Concept from = notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(),
						fromNotation);
				long relationshipType = notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(),
						predicateNotation).getNodeId();
				Concept to = notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(), toNotation);
				from.addRelationshipIfNoBidirectionalRlspExists(template, to, String.valueOf(relationshipType), 0,
						SRSTRE1);
				tx.success();
				tx.finish();
				logger.info("{}-{}:{}:{}", new Object[] { ctr, fromNotation, predicateNotation, toNotation });
				System.out.println("" + System.currentTimeMillis() + " ui:" + fromNotation + " " + predicateNotation
						+ " " + toNotation);
			}
		} finally {
			rs.close();
			conceptSchemeStmt.close();
			conceptSchemeConnection.close();
		}

	}

	public void writeRlspsBetweenConcepts() throws SQLException {

	}

	public void writeRlspsBetweenConceptsAndSchemes() throws SQLException {
		Connection conceptSchemeConn = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		conceptSchemeStmt.setFetchSize(Integer.MIN_VALUE);
		//
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_RLSP_CONCEPT_SCHEME);
		try {
			while (rs.next()) {
				Transaction tx = template.getGraphDatabaseService().beginTx();
				try {
					String cui = rs.getString("CUI");
					if (ignoredCuiReader.isIgnored(cui)) {
						continue;
					}
					String tui = rs.getString("TUI");
					Concept concept = notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(), cui);
					Concept conceptScheme = notationRepository.getRelatedConcept(
							NotationSourceConstant.UMLS.toString(), tui);
					concept.addRelationshipIfNoneExists(template, conceptScheme, "IN_SCHEME", 0, MRSTY);
					tx.success();
					tx.finish();
					System.out.println("" + System.currentTimeMillis() + " tui:" + tui + " cui:" + cui);
				} catch (Exception e) {
					logger.error("", e);
				}

			}

		} finally {
			rs.close();
			conceptSchemeStmt.close();
			conceptSchemeConn.close();
		}
	}

	private static LabelType getLabelType(String ts, String isPref, String stt) {
		if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
			return LabelType.PREFERRED;
		} else {
			return LabelType.ALTERNATE;
		}
	}

	@Autowired
	private DataSource dataSource;
	@Autowired
	private LabelRepository labelRepository;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private IgnoredCuiReader ignoredCuiReader;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private Neo4jTemplate template;
	@Autowired
	private SemanticTypeConceptPredicateMapper predicateMapper;

	private static final String GET_CUI_DETAILS = "select * from MRCONSO WHERE CUI = ?";
	private static final String GET_ALL_DISTINCT_CUIS = "select distinct(CUI) from MRCONSO";
	private static final String GET_ALL_CONCEPT_SCHEMES = "select STY_RL, UI from SRDEF where RT=\"STY\"";
	private static final String GET_ALL_ST_PREDICATES = "select STY_RL, RIN, UI from SRDEF where RT=\"RL\"";
	private static final String GET_UNIQUE_ST_PREDICATE_NOTATIONS = "select DISTINCT(UI) from SRDEF where RT=\"RL\"";
	private static final String GET_ST_PREDICATE_HIERARCHY = "select DISTINCT(UI3) from SRSTRE1 where UI1 = ?";
	private static final String GET_RLSP_CONCEPT_SCHEME = "select CUI, TUI from MRSTY";// 2,151,295
	private static final String GET_CONCEPT_PREDICATES = "select VALUE, EXPL from MRDOC where DOCKEY = \"RELA\" and type = \"rela_inverse\" AND VALUE IS NOT NULL";
	private static final String GET_ALL_CONCEPT_SCHEME_RLSPS = "SELECT * FROM SRSTRE1 WHERE UI1 != UI3 AND UI1 IN (SELECT UI FROM SRDEF WHERE RT=\"STY\") AND UI3 IN (SELECT UI FROM SRDEF WHERE RT=\"STY\")";// 6371
	private static final Logger logger = LoggerFactory.getLogger(UmlsToStoreWriter.class);
	/**
	 * change this for every new UMLS RELEASE
	 * 
	 */
	// FIXME move to properties file
	private static final String UMLS_VERSION = "UMLS2012AA";
	private static final String MRCONSO = UMLS_VERSION + "|MRCONSO";
	private static final String SRDEF = UMLS_VERSION + "|SRDEF";
	private static final String SRSTRE1 = UMLS_VERSION + "|SRSTRE1";
	private static final String MRSTY = UMLS_VERSION + "|MRSTY";
	private static final String MRREL = UMLS_VERSION + "|MRREL";
	/**
	 * 10000/4G XMX memory runtime memory allocated. Only helpful if you have
	 * slow disks, because its staggers writes
	 */
	private static final int txSize = 10000;
	private static final String IS_A = "T186";

}
