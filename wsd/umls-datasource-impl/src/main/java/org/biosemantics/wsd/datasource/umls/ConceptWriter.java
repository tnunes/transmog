package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.LabelType;
import org.biosemantics.conceptstore.domain.impl.NotationSourceConstant;
import org.biosemantics.conceptstore.domain.impl.RlspType;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.BatchRelationship;
import org.neo4j.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import au.com.bytecode.opencsv.CSVReader;

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
		relationshipTypeIndex = indexProvider.relationshipIndex("type", MapUtil.stringMap("type", "exact"));
		relationshipTypeIndex.setCacheCapacity("type", 100000);
	}

	public void writeSemanticTypes() throws SQLException {
		Connection conceptSchemeConnection = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConnection.createStatement();
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_ALL_ST_DEF);
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		Map<String, Object> indexProperties = new HashMap<String, Object>();
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
					indexProperties.put("text", labelText);
					labelNode = inserter.createNode(nodeProperties);
					labelIndex.add(labelNode, indexProperties);
					indexProperties.clear();
					nodeProperties.clear();
					exisitingLabelMap.put(labelText, labelNode);
				}
				// label done
				nodeProperties.put("source", NotationSourceConstant.UMLS.toString());
				nodeProperties.put("code", notationCode);
				indexProperties.put("code", notationCode);
				Long notationNode = inserter.createNode(nodeProperties);
				notationIndex.add(notationNode, indexProperties);
				indexProperties.clear();
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
						indexProperties.put("text", invLabelText);
						invLabelNode = inserter.createNode(nodeProperties);
						labelIndex.add(invLabelNode, nodeProperties);
						indexProperties.clear();
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

	// private static LabelType getLabelType(String ts, String isPref, String
	// stt) {
	// if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") &&
	// stt.equalsIgnoreCase("PF")) {
	// return LabelType.PREFERRED;
	// } else {
	// return LabelType.ALTERNATE;
	// }
	// }

	private void writeCuis() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = stmt.executeQuery(GET_ALL_DISTINCT_CUIS);
		int ctr = 0;
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		Map<String, Object> indexProperties = new HashMap<String, Object>();
		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (!ignoredCuiReader.isIgnored(cui)) {
					nodeProperties.put("source", NotationSourceConstant.UMLS.toString());
					nodeProperties.put("code", cui);
					Long notationNode = inserter.createNode(nodeProperties);
					indexProperties.put("code", cui);
					notationIndex.add(notationNode, indexProperties);
					nodeProperties.clear();
					indexProperties.clear();
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

	private void writeSuis() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = stmt.executeQuery(GET_ALL_DISTINCT_SUIS);
		int ctr = 0;
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> indexProperties = new HashMap<String, Object>();

		try {
			while (rs.next()) {
				String str = rs.getString("STR");
				String sui = rs.getString("SUI");
				IndexHits<Long> hits = labelIndex.get("text", str);
				Long labelNode = null;
				if (hits != null && hits.size() > 0) {
					labelNode = hits.getSingle();
					logger.info("{} {}", new Object[] { str, labelNode });
				} else {
					nodeProperties.put("language", ENG);
					nodeProperties.put("text", str);
					labelNode = inserter.createNode(nodeProperties);
					indexProperties.put("text", str);
					labelIndex.add(labelNode, indexProperties);
					indexProperties.clear();
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

	private void linkSuiAndCui() throws SQLException {
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
					rlspProperties.put("type", LabelType.ALTERNATE.toString());
					Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(hit);
					Long conceptNode = null;
					for (BatchRelationship batchRelationship : batchRlsps) {
						if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
							conceptNode = batchRelationship.getStartNode();
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

	public void writeRelaPredicates() throws SQLException {
		Connection getConceptPredicateConn = dataSource.getConnection();
		Statement getConceptRedicatesStmt = getConceptPredicateConn.createStatement();
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		Map<String, Object> indexProperties = new HashMap<String, Object>();
		try {
			ResultSet rs = getConceptRedicatesStmt.executeQuery(GET_ALL_RELA_PREDICATES);
			while (rs.next()) {
				String text = rs.getString("VALUE");

				Long textNode = null;
				IndexHits<Long> textHits = labelIndex.get("text", text);
				if (textHits != null && textHits.size() > 0) {
					textNode = textHits.getSingle();
				}
				String invText = rs.getString("EXPL");

				Long invTextNode = null;
				IndexHits<Long> invTextHits = labelIndex.get("text", invText);
				if (invTextHits != null && invTextHits.size() > 0) {
					invTextNode = invTextHits.getSingle();
				}
				if (textNode == null && invTextNode == null) {

					// create predicate and labels
					nodeProperties.put("language", ENG);
					nodeProperties.put("text", text);

					Long newTextNode = inserter.createNode(nodeProperties);
					indexProperties.put("text", text);
					labelIndex.add(newTextNode, indexProperties);
					indexProperties.clear();
					nodeProperties.clear();

					Long newInvTextNode = null;
					if (!text.equalsIgnoreCase(invText)) {
						nodeProperties.put("language", ENG);
						nodeProperties.put("text", invText);
						indexProperties.put("text", invText);
						newInvTextNode = inserter.createNode(nodeProperties);
						labelIndex.add(newInvTextNode, indexProperties);
						indexProperties.clear();
						nodeProperties.clear();
					}

					nodeProperties.put("type", ConceptType.PREDICATE.toString());
					Long conceptNode = inserter.createNode(nodeProperties);
					conceptIndex.add(conceptNode, nodeProperties);
					nodeProperties.clear();

					rlspProperties.put("sources", new String[] { SRDEF });
					rlspProperties.put("type", LabelType.ALTERNATE.toString());
					inserter.createRelationship(conceptNode, newTextNode, hasLabel, rlspProperties);
					if (!text.equalsIgnoreCase(invText)) {
						inserter.createRelationship(conceptNode, newInvTextNode, hasLabel, rlspProperties);
					}
					rlspProperties.clear();

				} else if (textNode == null || invTextNode == null) {
					String missingLabelText = text;
					if (!StringUtils.isBlank(missingLabelText)) {
						missingLabelText = invText;
					}
					Long availableNode = textNode;
					if (availableNode == null) {
						availableNode = invTextNode;
					}
					nodeProperties.put("language", ENG);
					nodeProperties.put("text", missingLabelText);
					Long newTextNode = inserter.createNode(nodeProperties);
					indexProperties.put("text", missingLabelText);
					labelIndex.add(newTextNode, indexProperties);
					nodeProperties.clear();
					indexProperties.clear();

					Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(availableNode);
					Long conceptNode = null;
					for (BatchRelationship batchRelationship : batchRlsps) {
						if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_LABEL.toString())) {
							conceptNode = batchRelationship.getStartNode();
							break;
						}
					}

					rlspProperties.put("sources", new String[] { SRDEF });
					rlspProperties.put("type", LabelType.ALTERNATE.toString());
					inserter.createRelationship(conceptNode, newTextNode, hasLabel, rlspProperties);
					rlspProperties.clear();

				} else {
					// if linked to predicate do nothing
					// if not linked to predicate - create predicate and link

				}
				labelIndex.flush();
				notationIndex.flush();
			}
		} finally {
			getConceptRedicatesStmt.close();
			getConceptPredicateConn.close();
		}
	}

	public void mapRelaPredicatesToSemanticTypePredicates() throws SQLException {
		Map<String, SemanticTypePredicate> mapping = predicateMapper.getMappingMap();
		Map<String, Object> rlspProps = new HashMap<String, Object>();
		rlspProps.put("sources", new String[] { ERIK_TSV_FILE });
		for (Entry<String, SemanticTypePredicate> entry : mapping.entrySet()) {
			String key = entry.getKey();
			IndexHits<Long> fromHits = labelIndex.get("text", key);
			Long fromHit = null;
			if (fromHits != null && fromHits.size() > 0) {
				fromHit = fromHits.getSingle();
			}
			if (entry.getValue().getRelatedTo() == null) {
				System.out.println("here");
			}
			IndexHits<Long> toHits = labelIndex.get("text", entry.getValue().getRelatedTo());
			Long toHit = null;
			if (toHits != null && toHits.size() > 0) {
				toHit = toHits.getSingle();
			}

			if (fromHit != null && toHit != null) {
				if (entry.getValue().getRelationship() == SemanticTypePredicate.EQ_PROP) {
					inserter.createRelationship(fromHit, toHit, sameAs, rlspProps);
				} else {
					inserter.createRelationship(fromHit, toHit, subPropOf, rlspProps);
				}
			} else {
				logger.error("{} {}", new Object[] { fromHit, toHit });
			}

		}
	}

	public void writeRlspsBetweenConceptsAndSchemes() throws SQLException {
		Connection conceptSchemeConn = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		conceptSchemeStmt.setFetchSize(Integer.MIN_VALUE);
		Map<String, Object> rlspProps = new HashMap<String, Object>();
		rlspProps.put("sources", new String[] { MRSTY });
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_RLSP_CONCEPT_SCHEME);
		int ctr = 0;
		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (ignoredCuiReader.isIgnored(cui)) {
					continue;
				}
				String tui = rs.getString("TUI");
				IndexHits<Long> cuiHits = notationIndex.get("code", cui);
				Long cuiNotationNode = cuiHits.getSingle();
				Long cuiConceptNode = null;
				Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(cuiNotationNode);
				for (BatchRelationship batchRelationship : batchRlsps) {
					if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
						cuiConceptNode = batchRelationship.getStartNode();
						break;
					}
				}
				IndexHits<Long> tuiHits = notationIndex.get("code", tui);
				Long tuiNotationNode = tuiHits.getSingle();
				Long tuiConceptNode = null;
				Iterable<BatchRelationship> tuiRlsps = inserter.getRelationships(tuiNotationNode);
				for (BatchRelationship batchRelationship : tuiRlsps) {
					if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
						tuiConceptNode = batchRelationship.getStartNode();
						break;
					}
				}
				inserter.createRelationship(cuiConceptNode, tuiConceptNode, inScheme, rlspProps);
				ctr++;
			}
			logger.info("{} concept to concept scheme rlsps created", ctr);
		} finally {
			conceptSchemeStmt.close();
			conceptSchemeConn.close();
		}

	}

	public void writeRelPredicates() throws SQLException {

	}

	public void writeNotNullRelaRlsps() throws SQLException {
		// load all predicates in map
		// Map<String, Long> predicatesMap = new HashMap<String, Long>();

		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = stmt.executeQuery(GET_NOT_NULL_RELA);
		Map<String, Object> sourcesMap = new HashMap<String, Object>();
		sourcesMap.put("sources", new String[] { MRREL });
		int ctr = 0;
		try {
			while (rs.next()) {
				String cui1 = rs.getString("CUI1");
				String cui2 = rs.getString("CUI2");
				String rela = rs.getString("RELA");
				if (ignoredCuiReader.isIgnored(cui1) || ignoredCuiReader.isIgnored(cui2)) {
					continue;
				}
				Long srcConceptNode = null;
				IndexHits<Long> cui1Hits = notationIndex.get("code", cui1);
				if (cui1Hits != null && cui1Hits.size() == 1) {
					Long srcNodeId = cui1Hits.getSingle();
					Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(srcNodeId);
					for (BatchRelationship batchRelationship : batchRlsps) {
						if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
							srcConceptNode = batchRelationship.getStartNode();
							break;
						}
					}
				} else {
					continue;
				}

				Long tgtConceptNode = null;
				IndexHits<Long> cui2Hits = notationIndex.get("code", cui2);
				if (cui2Hits != null && cui2Hits.size() == 1) {
					Long tgtNodeId = cui2Hits.getSingle();
					Iterable<BatchRelationship> tgtNotationRlsps = inserter.getRelationships(tgtNodeId);
					for (BatchRelationship batchRelationship : tgtNotationRlsps) {
						if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
							tgtConceptNode = batchRelationship.getStartNode();
							break;
						}
					}
				} else {
					continue;
				}
				if (srcConceptNode != null && tgtConceptNode != null) {
					IndexHits<Long> labelHits = labelIndex.get("text", rela);
					Long labelNode = labelHits.getSingle();
					Long predicateConceptNode = null;
					Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(labelNode);
					for (BatchRelationship batchRelationship : batchRlsps) {
						if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_LABEL.toString())) {
							predicateConceptNode = batchRelationship.getStartNode();
							break;
						}
					}
					inserter.createRelationship(srcConceptNode, tgtConceptNode,
							DynamicRelationshipType.withName(String.valueOf(predicateConceptNode)), sourcesMap);
					ctr++;

				}
				logger.debug("{}", ctr);
			}
			logger.info("rela relationships added: {}", ctr);
			rs.close();
		} finally {
			stmt.close();
			connection.close();
		}

	}

	public void writeMissingPubmedPredicates(String csvFile) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(csvFile));
		List<String[]> lines = csvReader.readAll();
		Map<String, Object> nodeProperties = new HashMap<String, Object>();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		Map<String, Object> indexProperties = new HashMap<String, Object>();
		Map<String, Object> sources = new HashMap<String, Object>();
		sources.put("sources", new String[] { ERIK_TSV_FILE });
		logger.info("{} predicates read", lines.size());
		int ctr = 0;
		try {
			for (String[] columns : lines) {
				ctr++;
				String predText = columns[0].trim();
				IndexHits<Long> labelIndexHits = labelIndex.get("text", predText);
				if (labelIndexHits != null && labelIndexHits.size() > 0) {
					logger.info("existing predicate = {}", predText);
				} else {
					nodeProperties.put("language", ENG);
					nodeProperties.put("text", predText);
					indexProperties.put("text", predText);
					Long newTextNode = inserter.createNode(nodeProperties);
					labelIndex.add(newTextNode, indexProperties);
					nodeProperties.clear();
					indexProperties.clear();

					nodeProperties.put("type", ConceptType.PREDICATE.toString());
					Long conceptNode = inserter.createNode(nodeProperties);
					conceptIndex.add(conceptNode, nodeProperties);
					nodeProperties.clear();

					rlspProperties.put("sources", new String[] { ERIK_TSV_FILE });
					rlspProperties.put("type", LabelType.ALTERNATE.toString());
					inserter.createRelationship(conceptNode, newTextNode, hasLabel, rlspProperties);
					rlspProperties.clear();
					if (predText.startsWith("neg_")) {
						predText = predText.substring(4);
						IndexHits<Long> hits = labelIndex.get("text", predText);
						Long hit = null;
						if (hits != null && hits.size() > 0) {
							hit = hits.getSingle();
							inserter.createRelationship(hit, conceptNode,
									DynamicRelationshipType.withName(RlspType.IS_INVERSE_OF.name()), sources);
						}
					}
				}
			}
			logger.info("predicates created: {}", ctr);
		} finally {
			labelIndex.flush();
			conceptIndex.flush();
			csvReader.close();
		}
	}

	public void writePubmedRlsps(File file, String encoding) throws IOException {
		LineIterator iterator = FileUtils.lineIterator(file, encoding);
		Set<String> missingCuis = new HashSet<String>();
		Map<String, Object> sources = new HashMap<String, Object>();
		int ctr = 0;
		try {
			while (iterator.hasNext()) {
				String line = iterator.nextLine();
				String[] columns = line.split(" ");
				if (columns.length == 4) {
					String srcCui = columns[0].trim();
					String predicateText = columns[1].trim();
					String tgtCui = columns[2].trim();
					String pmid = columns[3].trim();
					if (!ignoredCuiReader.isIgnored(srcCui) && !ignoredCuiReader.isIgnored(tgtCui)) {
						Long srcConceptNode = null;
						IndexHits<Long> fromCuiHits = notationIndex.get("code", srcCui);
						if (fromCuiHits != null && fromCuiHits.size() == 1) {
							Long srcNodeId = fromCuiHits.getSingle();
							Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(srcNodeId);
							for (BatchRelationship batchRelationship : batchRlsps) {
								if (batchRelationship.getType().name()
										.equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
									srcConceptNode = batchRelationship.getStartNode();
									break;
								}
							}
						} else {
							missingCuis.add(srcCui);
							continue;
						}

						Long tgtConceptNode = null;
						IndexHits<Long> toCuiHits = notationIndex.get("code", tgtCui);
						if (toCuiHits != null && toCuiHits.size() == 1) {
							Long tgtNodeId = toCuiHits.getSingle();
							Iterable<BatchRelationship> tgtNotationRlsps = inserter.getRelationships(tgtNodeId);
							for (BatchRelationship batchRelationship : tgtNotationRlsps) {
								if (batchRelationship.getType().name()
										.equalsIgnoreCase(RlspType.HAS_NOTATION.toString())) {
									tgtConceptNode = batchRelationship.getStartNode();
									break;
								}
							}
						} else {
							missingCuis.add(tgtCui);
							continue;
						}
						if (srcConceptNode != null && tgtConceptNode != null) {
							IndexHits<Long> labelHits = labelIndex.get("text", predicateText);
							Long labelNode = labelHits.getSingle();
							Long predicateConceptNode = null;
							Iterable<BatchRelationship> batchRlsps = inserter.getRelationships(labelNode);
							for (BatchRelationship batchRelationship : batchRlsps) {
								if (batchRelationship.getType().name().equalsIgnoreCase(RlspType.HAS_LABEL.toString())) {
									predicateConceptNode = batchRelationship.getStartNode();
									break;
								}
							}
							sources.put("sources", new String[] { pmid });
							inserter.createRelationship(srcConceptNode, tgtConceptNode,
									DynamicRelationshipType.withName(String.valueOf(predicateConceptNode)), sources);
							sources.clear();
							ctr++;
						}
					} else {
						logger.info("ignored {}  or {}", new Object[] { srcCui, tgtCui });
					}

				} else {
					logger.error("length != 4");
				}
				logger.debug("{}", ctr);
			}
			//
			logger.info("pubmed rlsps written {}", ctr);
			for (String string : missingCuis) {
				logger.error("{}", string);
			}
		} finally {
			LineIterator.closeQuietly(iterator);
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
	private BatchInserterIndex relationshipTypeIndex;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private IgnoredCuiReader ignoredCuiReader;
	@Autowired
	private SemanticTypeConceptPredicateMapper predicateMapper;

	private Map<String, Long> suiMap = new HashMap<String, Long>();

	private static final String GET_ALL_DISTINCT_CUIS = "select distinct(CUI) from MRCONSO";
	private static final String GET_ALL_DISTINCT_SUIS = "select distinct(SUI), STR from MRCONSO";//
	private static final String SUI_FOR_CUI = "select DISTINCT(SUI) from MRCONSO WHERE CUI = ?";
	// SemanticType
	private static final String GET_ALL_ST_DEF = "select * from SRDEF";
	private static final String GET_ST_PREDICATE_HIERARCHY = "select DISTINCT(UI3), UI2 from SRSTRE1 where UI1 = ? and UI1 != UI3";

	private static final String GET_RLSP_CONCEPT_SCHEME = "select CUI, TUI from MRSTY";// 2,151,295
	private static final String GET_NOT_NULL_RELA = "select CUI1, CUI2, RELA from MRREL where CUI1 != CUI2 AND RELA IS NOT NULL";// ???
	private static final String GET_ALL_RELA_PREDICATES = "select VALUE, EXPL from MRDOC where DOCKEY = \"RELA\" and type = \"rela_inverse\" AND VALUE IS NOT NULL";// 623

	private static final String UMLS_VERSION = "UMLS2012AA";
	private static final String MRCONSO = UMLS_VERSION + "|MRCONSO";
	private static final String SRDEF = UMLS_VERSION + "|SRDEF";
	private static final String SRSTRE1 = UMLS_VERSION + "|SRSTRE1";
	private static final String MRSTY = UMLS_VERSION + "|MRSTY";
	private static final String MRREL = UMLS_VERSION + "|MRREL";
	private static final String ENG = "ENG";
	private static final String ERIK_TSV_FILE = "ERIK_TSV_FILE";

	private static final Logger logger = LoggerFactory.getLogger(ConceptWriter.class);
	private final RelationshipType hasLabel = DynamicRelationshipType.withName(RlspType.HAS_LABEL.toString());
	private final RelationshipType hasNotation = DynamicRelationshipType.withName(RlspType.HAS_NOTATION.toString());
	private final RelationshipType sameAs = DynamicRelationshipType.withName(RlspType.SAME_AS.toString());
	private final RelationshipType subPropOf = DynamicRelationshipType.withName(RlspType.SUB_PROP_OF.toString());
	private final RelationshipType inScheme = DynamicRelationshipType.withName(RlspType.IN_SCHEME.toString());
}
