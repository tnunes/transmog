package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
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
		// writeSemanticTypePredicates();
		// System.out.println("1111111111111111111111");
		// writeSemanticTypePredicateHierarchy();
		// System.out.println("2222222222222222222222");
		// writeAndMapConceptPredicates();
		// System.out.println("333333333333333333333");
		// writeConceptSchemes();
		// System.out.println("4444444444444444444444");
		// writeRlspsBetweenConceptSchemes();
		// System.out.println("5555555555555555555555555");
		// writeConcepts();
		System.out.println("666666666666666666666666");
		writeRlspsBetweenConceptsAndSchemes();
		System.out.println("77777777777777777777777");
		// writeRlspsBetweenConcepts();
		// System.out.println("8888888888888888888888888");
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
		Concept preConcept = null;
		Label label = labelRepository.getLabel(text, "ENG");
		if (label == null) {
			label = labelRepository.getLabel(invText, "ENG");
		}
		if (label != null) {
			Iterable<Concept> relatedConcepts = label.getRelatedConcepts();
			for (Concept relatedConcept : relatedConcepts) {
				if (relatedConcept.getType() == ConceptType.PREDICATE) {
					preConcept = relatedConcept;
				}
			}
		}
		if (preConcept == null) {
			// create new predicate with these labels
			Label labelForText = labelRepository.save(new Label(text, "ENG"));
			Label labelForInvText = labelRepository.save(new Label(invText, "ENG"));
			preConcept = conceptRepository.save(new Concept(ConceptType.PREDICATE));
			preConcept.addLabelIfNoneExists(template, labelForText, LabelType.PREFERRED, MRREL);
			preConcept.addLabelIfNoneExists(template, labelForInvText, LabelType.ALTERNATE, MRREL);
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
		Map<String, Long> predicateMap = new HashMap<String, Long>();
		Connection uniqueRelConn = dataSource.getConnection();
		Statement uniqueRelStmt = uniqueRelConn.createStatement();
		ResultSet uniqueRelRs = uniqueRelStmt.executeQuery(GET_ALL_REL);
		Transaction tx1 = template.getGraphDatabaseService().beginTx();
		try {
			while (uniqueRelRs.next()) {
				String rel = uniqueRelRs.getString("REL");
				Notation notation = notationRepository.save(new Notation(NotationSourceConstant.UMLS.toString(), rel));
				Concept concept = conceptRepository.save(new Concept(ConceptType.PREDICATE));
				concept.addNotationIfNoneExists(template, notation, MRREL);
				// Concept concept =
				// notationRepository.getRelatedConcept(NotationSourceConstant.UMLS.toString(),
				// rel);
				predicateMap.put(rel, concept.getNodeId());
			}
			uniqueRelRs.close();
			tx1.success();
			tx1.finish();
		} finally {
			uniqueRelStmt.close();
			uniqueRelConn.close();
		}
		System.out.println("created all distinct rel predicates, now checking is all RELA predicates are available");
		Connection uniqueRelaConn = dataSource.getConnection();
		Statement uniqueRelaStmt = uniqueRelaConn.createStatement();
		ResultSet uniqueRelaRs = uniqueRelaStmt.executeQuery(GET_ALL_RELA);
		Transaction tx2 = template.getGraphDatabaseService().beginTx();
		try {
			while (uniqueRelaRs.next()) {
				String rela = uniqueRelaRs.getString("RELA");
				if (!StringUtils.isEmpty(rela)) {
					Iterable<Concept> concepts = labelRepository.getLabel(rela, "ENG").getRelatedConcepts();
					Concept concept = null;
					for (Concept foundConcept : concepts) {
						if (foundConcept.getType() == ConceptType.PREDICATE) {
							concept = foundConcept;
						}
					}
					if (concept == null || concept.getType() != ConceptType.PREDICATE) {
						throw new IllegalStateException(
								"concept cannot be null or not a predicate for predicate rela = " + rela + " concept:"
										+ concept);
					} else {
						predicateMap.put(rela, concept.getNodeId());
					}
				}
			}
			uniqueRelaRs.close();
			tx2.success();
			tx2.finish();
		} finally {
			uniqueRelaStmt.close();
			uniqueRelaConn.close();
		}
		System.out.println("check complete begining inserts");
		Connection conceptRlspConn = dataSource.getConnection();
		Statement conceptRlspStmt = conceptRlspConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		conceptRlspStmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = conceptRlspStmt.executeQuery(GET_RLSP_CONCEPTS);
		int ctr = 0;
		try {
			Transaction tx = template.getGraphDatabaseService().beginTx();
			while (rs.next()) {
				String rela = rs.getString("RELA");
				String rel = rs.getString("REL");
				String sourceCui = rs.getString("CUI2");
				String targetCui = rs.getString("CUI1");
				if (ignoredCuiReader.isIgnored(sourceCui) || ignoredCuiReader.isIgnored(targetCui)) {
					continue;
				}
				String predicateNotationCode = null;
				if (StringUtils.isBlank(rela)) {
					predicateNotationCode = rel;
				} else {
					predicateNotationCode = rela;
				}
				Notation srcNotation = notationRepository.findByPropertyValue("code", sourceCui);
				Notation targetNotation = notationRepository.findByPropertyValue("code", targetCui);
				Long relationshipType = predicateMap.get(predicateNotationCode);
				if (srcNotation != null && targetNotation != null) {
					Concept srcConcept = null;
					for (Concept concept : srcNotation.getRelatedConcepts()) {
						srcConcept = concept;
					}
					Concept targetConcept = null;
					for (Concept concept : targetNotation.getRelatedConcepts()) {
						targetConcept = concept;
					}
					if (srcConcept != null && targetConcept != null && relationshipType != null) {
						srcConcept.addRelationshipIfNoBidirectionalRlspExists(template, targetConcept,
								relationshipType.toString(), 0, MRREL);
					} else {
						System.err.println("sourceCui:" + sourceCui + " targetCui:" + targetCui + " rel" + rel
								+ " rela:" + rela);
					}
				}
				if (++ctr % txSize == 0) {
					tx.success();
					tx.finish();
					tx = template.getGraphDatabaseService().beginTx();
					System.out.println("millis:" + System.currentTimeMillis() + " ctr:" + ctr);
				}
			}
			tx.success();
			tx.finish();
		} finally {
			rs.close();
			conceptRlspStmt.close();
			conceptRlspConn.close();
		}
	}

	public void writeRlspsBetweenConceptsAndSchemes() throws SQLException {
		Connection conceptSchemeConn = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		conceptSchemeStmt.setFetchSize(Integer.MIN_VALUE);
		//
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_RLSP_CONCEPT_SCHEME);
		int ctr = 0;
		Transaction tx = template.getGraphDatabaseService().beginTx();
		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (ignoredCuiReader.isIgnored(cui)) {
					continue;
				}
				String tui = rs.getString("TUI");
				Notation srcNotation = notationRepository.findByPropertyValue("code", cui);
				Notation targetNotation = notationRepository.findByPropertyValue("code", tui);
				if (srcNotation != null && targetNotation != null) {
					Concept srcConcept = null;
					for (Concept concept : srcNotation.getRelatedConcepts()) {
						srcConcept = concept;
					}
					Concept conceptScheme = null;
					for (Concept concept : targetNotation.getRelatedConcepts()) {
						conceptScheme = concept;
					}
					if (srcConcept != null && conceptScheme != null) {
						srcConcept.addRelationshipIfNoneExists(template, conceptScheme, "IN_SCHEME", 0, MRSTY);
					} else {
						System.err.println("millis:" + System.currentTimeMillis() + " tui:" + tui + " cui:" + cui
								+ " srcConcept:" + srcConcept + " conceptScheme:" + conceptScheme);
					}
				}
				if (++ctr % txSize == 0) {
					tx.success();
					tx.finish();
					tx = template.getGraphDatabaseService().beginTx();
					System.out.println("millis:" + System.currentTimeMillis() + " ctr:" + ctr);
				}
			}
			tx.success();
			tx.finish();
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
	private static final String GET_ALL_REL = "select DISTINCT(REL) from MRREL";// ???
	private static final String GET_ALL_RELA = "select DISTINCT(RELA) from MRREL";// ???
	private static final String GET_RLSP_CONCEPTS = "select CUI1, CUI2, REL, RELA from MRREL where CUI1 != CUI2";// ???
	private static final String GET_CONCEPT_PREDICATES = "select VALUE, EXPL from MRDOC where DOCKEY = \"RELA\" and type = \"rela_inverse\" AND VALUE IS NOT NULL";//623
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
	private static final int txSize = 1000;
	private static final String IS_A = "T186";

}
