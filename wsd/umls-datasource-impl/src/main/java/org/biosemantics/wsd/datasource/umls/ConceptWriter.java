package org.biosemantics.wsd.datasource.umls;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

import javax.sql.*;

import org.apache.commons.lang.*;
import org.biosemantics.conceptstore.domain.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.*;
import org.neo4j.helpers.collection.*;
import org.neo4j.unsafe.batchinsert.*;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

public class ConceptWriter {

	public void init() {
		inserter = BatchInserters.inserter(neo4jFolder, config);
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		labelIndex = indexProvider.nodeIndex("Label", MapUtil.stringMap("type", "exact"));
		labelIndex.setCacheCapacity("text", 100000);
		labelIndex.setCacheCapacity("id", 100000);
		notationIndex = indexProvider.nodeIndex("Notation", MapUtil.stringMap("type", "exact"));
		notationIndex.setCacheCapacity("id", 100000);
		notationIndex.setCacheCapacity("code", 100000);
		conceptIndex = indexProvider.nodeIndex("Concept", MapUtil.stringMap("type", "exact"));
		notationIndex.setCacheCapacity("type", 100000);
	}

	public void writeSemanticTypes() throws SQLException {
		Connection conceptSchemeConnection = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConnection.createStatement();
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_ALL_ST_DEF);
		int ctr = 0;
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		Map<String, Long> uiNodeMap = new HashMap<String, Long>();
		Map<String, Long> exisitingLabelMap = new HashMap<String, Long>();
		try {
			while (rs.next()) {
				String recordType = rs.getString("RT");
				String notationCode = rs.getString("UI");
				String labelText = rs.getString("STY_RL");
				Long labelNode = null;
				if (exisitingLabelMap.containsKey(labelText)) {
					labelNode = exisitingLabelMap.get(labelText);
				} else {
					nodeProperties.put("language", ENG);
					nodeProperties.put("text", labelText);
					nodeProperties.put("id", labelText + ENG);
					labelNode = inserter.createNode(nodeProperties);
					labelIndex.add(labelNode, nodeProperties);
					nodeProperties.clear();
					exisitingLabelMap.put(labelText, labelNode);
				}
				// label done
				nodeProperties.put("source", NotationSourceConstant.UMLS.toString());
				nodeProperties.put("code", notationCode);
				nodeProperties.put("id", NotationSourceConstant.UMLS.toString() + notationCode);
				Long notationNode = inserter.createNode(nodeProperties);
				notationIndex.add(notationNode, nodeProperties);
				nodeProperties.clear();
				// notation done
				if (recordType.equalsIgnoreCase("STY")) {
					// semantic type
					nodeProperties.put("type", ConceptType.CONCEPT_SCHEME.toString());
					Long conceptNode = inserter.createNode(nodeProperties);
					conceptIndex.add(conceptNode, nodeProperties);
					nodeProperties.clear();
					// concept done
					rlspProperties.put("sources", new String[] { SRDEF });
					inserter.createRelationship(conceptNode, notationNode, hasNotation, rlspProperties);
					rlspProperties.put("type", LabelType.ALTERNATE.toString());
					inserter.createRelationship(conceptNode, labelNode, hasLabel, rlspProperties);
					rlspProperties.clear();
					// rlsps done
					uiNodeMap.put(notationCode, conceptNode);

				} else if (recordType.equalsIgnoreCase("RL")) {
					// rlsp between semantic type
					String invLabelText = rs.getString("RIN");
					Long invLabelNode = null;
					if (exisitingLabelMap.containsKey(invLabelText)) {
						invLabelNode = exisitingLabelMap.get(invLabelText);
					} else {
						nodeProperties.put("language", ENG);
						nodeProperties.put("text", invLabelText);
						nodeProperties.put("id", invLabelText + ENG);
						invLabelNode = inserter.createNode(nodeProperties);
						labelIndex.add(invLabelNode, nodeProperties);
						nodeProperties.clear();
						exisitingLabelMap.put(invLabelText, invLabelNode);
					}
					// inv label done
					nodeProperties.put("type", ConceptType.PREDICATE.toString());
					Long conceptNode = inserter.createNode(nodeProperties);
					conceptIndex.add(conceptNode, nodeProperties);
					nodeProperties.clear();
					// concept done
					rlspProperties.put("sources", new String[] { SRDEF });
					inserter.createRelationship(conceptNode, notationNode, hasNotation, rlspProperties);
					rlspProperties.put("type", LabelType.ALTERNATE.toString());
					inserter.createRelationship(conceptNode, labelNode, hasLabel, rlspProperties);
					inserter.createRelationship(conceptNode, invLabelNode, hasLabel, rlspProperties);
					rlspProperties.clear();
					// rlsps done
					uiNodeMap.put(notationCode, conceptNode);
				}
				logger.debug("{}", ++ctr);
			}
			rs.close();
		} finally {
			conceptSchemeStmt.close();
			conceptSchemeConnection.close();
			labelIndex.flush();
			notationIndex.flush();
			conceptIndex.flush();
		}
		logger.info("187 ui inserted:{}", uiNodeMap.size());
		// add relationships for predicates and semantic types
		Connection predicateParentConn = dataSource.getConnection();
		PreparedStatement predicateParentPstmt = predicateParentConn.prepareStatement(GET_ST_PREDICATE_HIERARCHY);
		try {
			for (Entry<String, Long> entry : uiNodeMap.entrySet()) {
				predicateParentPstmt.setString(1, entry.getKey());
				ResultSet predicateParentRs = predicateParentPstmt.executeQuery();
				while (predicateParentRs.next()) {
					String parentNotationCode = predicateParentRs.getString("UI3");
					String predicateNotationNode = predicateParentRs.getString("UI2");
					Long parentNode = uiNodeMap.get(parentNotationCode);
					Long predicateNode = uiNodeMap.get(predicateNotationNode);
					RelationshipType predicate = DynamicRelationshipType.withName(predicateNode.toString());
					rlspProperties.put("sources", new String[] { SRSTRE1 });
					inserter.createRelationship(entry.getValue(), parentNode, predicate, rlspProperties);
					rlspProperties.clear();
				}
				predicateParentRs.close();
			}
		} finally {
			predicateParentPstmt.close();
			predicateParentConn.close();
		}
	}

	public void writeConcepts() throws SQLException {
		writeCuis();
		writeSuis();
		linkSuiAndCui();
	}

	private static LabelType getLabelType(String ts, String isPref, String stt) {
		if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
			return LabelType.PREFERRED;
		} else {
			return LabelType.ALTERNATE;
		}
	}

	public void writeCuis() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = stmt.executeQuery(GET_ALL_DISTINCT_CUIS);
		int ctr = 0;
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();

		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (!ignoredCuiReader.isIgnored(cui)) {
					nodeProperties.put("source", NotationSourceConstant.UMLS.toString());
					nodeProperties.put("code", cui);
					nodeProperties.put("id", NotationSourceConstant.UMLS.toString() + cui);
					Long notationNode = inserter.createNode(nodeProperties);
					notationIndex.add(notationNode, nodeProperties);
					nodeProperties.clear();
					// notation done
					nodeProperties.put("type", ConceptType.CONCEPT.toString());
					Long conceptNode = inserter.createNode(nodeProperties);
					conceptIndex.add(conceptNode, nodeProperties);
					nodeProperties.clear();
					// concept done
					rlspProperties.put("sources", new String[] { MRCONSO });
					inserter.createRelationship(conceptNode, notationNode, hasNotation, rlspProperties);
					rlspProperties.clear();
					// rlsp done
					logger.debug("{}", ++ctr);
				} else {
					logger.info("ignoring cui:{}" + cui);
				}
			}
			rs.close();
		} finally {
			rs.close();
			connection.close();
			notationIndex.flush();
			conceptIndex.flush();
		}
		logger.info("cuis inserted:{}", ctr);
	}

	public void writeSuis() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = stmt.executeQuery(GET_ALL_DISTINCT_SUIS);
		int ctr = 0;
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		try {
			while (rs.next()) {
				String str = rs.getString("STR");
				String sui = rs.getString("SUI");
				IndexHits<Long> hits = labelIndex.get("text", str);
				Long labelNode = null;
				if (hits != null && hits.size() > 0) {
					labelNode = hits.getSingle();
					logger.info("{} {}", new Object[] { str, hits.getSingle() });
				} else {
					nodeProperties.put("language", ENG);
					nodeProperties.put("text", str);
					nodeProperties.put("id", str + ENG);
					labelNode = inserter.createNode(nodeProperties);
					labelIndex.add(labelNode, nodeProperties);
					nodeProperties.clear();
				}
				suiMap.put(sui, labelNode);
				logger.debug("{}", ++ctr);
			}
			rs.close();
		} finally {
			stmt.close();
			connection.close();
			labelIndex.flush();
		}
		logger.info("6427110 suis inserted:{}", suiMap.size());
	}

	public void linkSuiAndCui() throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SUI_FOR_CUI);
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		IndexHits<Long> hits = notationIndex.query("code", "C*");
		logger.info("cuis found:{}", hits.size());
		int ctr = 0;
		try {
			for (Long hit : hits) {
				Map<String, Object> cuiNotationProps = inserter.getNodeProperties(hit);
				String cui = (String) cuiNotationProps.get("code");
				pstmt.setString(1, cui);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String sui = rs.getString("SUI");
					rlspProperties.put("type", LabelType.ALTERNATE);
					Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(hit);
					Long conceptNode = null;
					for (BatchRelationship batchRelationship : batchRlsps) {
						if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
							conceptNode = batchRelationship.getEndNode();
							break;
						}
					}
					inserter.createRelationship(conceptNode, suiMap.get(sui), hasLabel, rlspProperties);
					rlspProperties.clear();
				}
				rs.close();
				logger.debug("{}", ++ctr);
			}

		} finally {
			pstmt.close();
			conn.close();
			// clear sui map here as it is no longer needed
			suiMap.clear();
			suiMap = null;
		}
		logger.info("sui and cui mapped cui:{}", ctr);
	}

	public void writeAndMapConceptPredicates() throws SQLException {
		Connection getConceptPredicateConn = dataSource.getConnection();
		Statement getConceptRedicatesStmt = getConceptPredicateConn.createStatement();
		try {
			ResultSet rs = getConceptRedicatesStmt.executeQuery(GET_ALL_RELA_PREDICATES);
			while (rs.next()) {
				String text = rs.getString("VALUE");
				String invText = rs.getString("EXPL");
				Long textNode = null;
				IndexHits<Long> textHits = labelIndex.get("text", text);
				if (textHits != null && textHits.size() > 0) {
					textNode = textHits.getSingle();
				}
				Long invTextNode = null;
				IndexHits<Long> invTextHits = labelIndex.get("text", invText);
				if (invTextHits != null && invTextHits.size() > 0) {
					invTextNode = invTextHits.getSingle();
				}
				if(textNode == null && invTextNode == null){
					//create predicate
				}else if(textNode == null || invTextNode == null){
					// if linked to predicate add label
					
					// if not linked to predicate: create predicate
					
				}else{
					//if linked  to predicate do nothing
					
					//if not linked to predicate - create predicate and link
				}
				

				Long invTextNode = null;

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

	public void writeConceptPredicates() throws SQLException {
		Map<String, Long> predicateMap = new HashMap<String, Long>();
		Connection uniqueRelConn = dataSource.getConnection();
		Statement uniqueRelStmt = uniqueRelConn.createStatement();
		ResultSet uniqueRelRs = uniqueRelStmt.executeQuery(GET_ALL_REL);
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

	}

	public void destroy() {
		logger.debug("shutdown invoked");
		labelIndex.flush();
		notationIndex.flush();
		conceptIndex.flush();
		indexProvider.shutdown();
		inserter.shutdown();
		logger.debug("shutdown complete");
	}

	@Required
	public void setNeo4jFolder(String neo4jFolder) {
		this.neo4jFolder = neo4jFolder;
	}

	@Required
	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	private String neo4jFolder;
	private Map<String, String> config;
	private BatchInserter inserter;
	private BatchInserterIndexProvider indexProvider;
	private BatchInserterIndex labelIndex;
	private BatchInserterIndex notationIndex;
	private BatchInserterIndex conceptIndex;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private IgnoredCuiReader ignoredCuiReader;

	private Map<String, Long> suiMap = new HashMap<String, Long>();

	private static final String GET_ALL_DISTINCT_CUIS = "select distinct(CUI) from MRCONSO";
	private static final String GET_ALL_DISTINCT_SUIS = "select distinct(SUI), STR from MRCONSO";
	private static final String SUI_FOR_CUI = "select DISTINCT(SUI) from MRCONSO WHERE CUI = ?";
	// SemanticType
	private static final String GET_ALL_ST_DEF = "select * from SRDEF";
	private static final String GET_ST_PREDICATE_HIERARCHY = "select DISTINCT(UI3), UI2 from SRSTRE1 where UI1 = ? and UI1 != UI3";

	private static final String GET_RLSP_CONCEPT_SCHEME = "select CUI, TUI from MRSTY";// 2,151,295
	private static final String GET_ALL_REL = "select DISTINCT(REL) from MRREL";// ???
	private static final String GET_ALL_RELA = "select DISTINCT(RELA) from MRREL";// ???
	private static final String GET_RLSP_CONCEPTS = "select CUI1, CUI2, REL, RELA from MRREL where CUI1 != CUI2";// ???
	private static final String GET_ALL_RELA_PREDICATES = "select VALUE, EXPL from MRDOC where DOCKEY = \"RELA\" and type = \"rela_inverse\" AND VALUE IS NOT NULL";// 623
	private static final String GET_ALL_CONCEPT_SCHEME_RLSPS = "SELECT * FROM SRSTRE1 WHERE UI1 != UI3 AND UI1 IN (SELECT UI FROM SRDEF WHERE RT=\"STY\") AND UI3 IN (SELECT UI FROM SRDEF WHERE RT=\"STY\")";// 6371
	private static final String GET_ALL_ST_PREDICATES = "select STY_RL, RIN, UI from SRDEF where RT=\"RL\"";// 54

	private static final String UMLS_VERSION = "UMLS2012AA";
	private static final String MRCONSO = UMLS_VERSION + "|MRCONSO";
	private static final String SRDEF = UMLS_VERSION + "|SRDEF";
	private static final String SRSTRE1 = UMLS_VERSION + "|SRSTRE1";
	private static final String MRSTY = UMLS_VERSION + "|MRSTY";
	private static final String MRREL = UMLS_VERSION + "|MRREL";
	private static final String ENG = "ENG";

	private static final Logger logger = LoggerFactory.getLogger(ConceptWriter.class);
	private final RelationshipType hasLabel = DynamicRelationshipType.withName(RlspType.HAS_LABEL.toString());
	private final RelationshipType hasNotation = DynamicRelationshipType.withName(RlspType.HAS_NOTATION.toString());
}
