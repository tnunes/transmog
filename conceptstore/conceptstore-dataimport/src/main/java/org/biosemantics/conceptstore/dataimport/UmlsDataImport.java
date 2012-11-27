package org.biosemantics.conceptstore.dataimport;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class UmlsDataImport implements DataImport {

	public UmlsDataImport(BatchInserter batchInserter, BatchInserterIndex labelIndex, BatchInserterIndex notationIndex,
			BatchInserterIndex conceptIndex, BatchInserterIndex relationshipIndex, DataSource dataSource)
			throws IOException {
		this.inserter = batchInserter;
		this.labelIndex = labelIndex;
		this.notationIndex = notationIndex;
		this.conceptIndex = conceptIndex;
		this.relationshipIndex = relationshipIndex;
		this.dataSource = dataSource;
		this.ignoredCuiReader = new IgnoredCuiReader();
		ignoredCuiReader.init();
	}

	@Override
	public void importData() throws Exception {
		writeSemanticTypes();
		mapPredicates();
		writeRelaPredicates();
		writeConcepts();
		writeRlspsBetweenConceptsAndSchemes();
		writeNotNullRelaRlsps();
	}

	private void writeSemanticTypes() throws SQLException {
		Connection conceptSchemeConnection = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConnection.createStatement();
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_ALL_ST_DEF);
		Map<String, Object> props = new HashMap<String, Object>();
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
					labelNode = createLabelNode(labelText, ENG, props);
					exisitingLabelMap.put(labelText, labelNode);
				}
				Long notationNode = createNotationNode(NotationSourceConstant.UMLS.toString(), notationCode, props);
				if (recordType.equalsIgnoreCase("STY")) {
					Long conceptNode = createConceptNode(ConceptType.CONCEPT_SCHEME.toString(), props);
					props.put("sources", new String[] { SRDEF });
					createRelationship(conceptNode, notationNode, RlspType.HAS_NOTATION, props);
					props.put("sources", new String[] { SRDEF });
					props.put("type", LabelType.ALTERNATE.toString());
					createRelationship(conceptNode, labelNode, RlspType.HAS_LABEL, props);
					uiNodeMap.put(notationCode, conceptNode);

				} else if (recordType.equalsIgnoreCase("RL")) {
					// rlsp between semantic type
					String invLabelText = rs.getString("RIN");
					Long invLabelNode = null;
					if (exisitingLabelMap.containsKey(invLabelText)) {
						invLabelNode = exisitingLabelMap.get(invLabelText);
					} else {
						invLabelNode = createLabelNode(invLabelText, ENG, props);
						exisitingLabelMap.put(invLabelText, invLabelNode);
					}
					// inv label done
					Long conceptNode = createConceptNode(ConceptType.PREDICATE.toString(), props);
					if (notationCode.equalsIgnoreCase("T186")) {
						IS_A = DynamicRelationshipType.withName(conceptNode.toString());
					}
					// concept done
					props.put("sources", new String[] { SRDEF });
					createRelationship(conceptNode, notationNode, RlspType.HAS_NOTATION, props);
					props.put("sources", new String[] { SRDEF });
					props.put("type", LabelType.ALTERNATE.toString());
					createRelationship(conceptNode, labelNode, RlspType.HAS_LABEL, props);
					props.put("sources", new String[] { SRDEF });
					props.put("type", LabelType.ALTERNATE.toString());
					createRelationship(conceptNode, invLabelNode, RlspType.HAS_LABEL, props);
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
		logger.info("(187) ui inserted:{}", uiNodeMap.size());
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
					props.put("sources", new String[] { SRSTRE1 });
					createRelationship(entry.getValue(), parentNode, predicate, props);
				}
				predicateParentRs.close();
			}
		} finally {
			predicateParentPstmt.close();
			predicateParentConn.close();
		}
	}

	private void writeConcepts() throws SQLException {
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
		Map<String, Object> props = new HashMap<String, Object>();
		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (!ignoredCuiReader.isIgnored(cui)) {
					Long notationNode = createNotationNode(NotationSourceConstant.UMLS.toString(), cui, props);
					// notation done
					Long conceptNode = createConceptNode(ConceptType.CONCEPT.toString(), props);
					// concept done
					props.put("sources", new String[] { MRCONSO });
					createRelationship(conceptNode, notationNode, RlspType.HAS_NOTATION, props);
					// rlsp done
					if (++ctr % 10000 == 0) {
						logger.debug("{}", ctr);
					}
				} else {
					logger.info("ignoring cui:{}", cui);
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
		Map<String, Object> props = new HashMap<String, Object>();
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
					labelNode = createLabelNode(str, ENG, props);
				}
				suiMap.put(sui, labelNode);
				if (++ctr % 10000 == 0) {
					logger.debug("{}", ctr);
				}
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
		Map<String, Object> props = new HashMap<String, Object>();
		IndexHits<Long> hits = notationIndex.query("code", "C*");
		logger.info("cuis found:{}", hits.size());
		int ctr = 0;
		try {
			for (Long hit : hits) {
				Map<String, Object> cuiNotationProps = inserter.getNodeProperties(hit);
				String cui = (String) cuiNotationProps.get("code");
				pstmt.setString(1, cui);
				ResultSet rs = pstmt.executeQuery();
				Long cuiConceptNode = getConceptNodeForNotationNode(hit);
				while (rs.next()) {
					String sui = rs.getString("SUI");
					Long suiNode = suiMap.get(sui);
					props.put("sources", new String[] { MRCONSO });
					props.put("type", LabelType.ALTERNATE.toString());
					createRelationship(cuiConceptNode, suiNode, RlspType.HAS_LABEL, props);
				}
				rs.close();
				if (++ctr % 10000 == 0) {
					logger.debug("{}", ctr);
				}
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

	private void writeRelaPredicates() throws SQLException {
		Connection getConceptPredicateConn = dataSource.getConnection();
		Statement getConceptRedicatesStmt = getConceptPredicateConn.createStatement();
		Map<String, Object> props = new HashMap<String, Object>();
		try {
			ResultSet rs = getConceptRedicatesStmt.executeQuery(GET_ALL_RELA_PREDICATES);
			while (rs.next()) {
				String text = rs.getString("VALUE");
				Long textNode = getLabelNode(text, ENG);
				String invText = rs.getString("EXPL");
				Long invTextNode = getLabelNode(invText, ENG);
				if (textNode == null && invTextNode == null) {
					textNode = createLabelNode(text, ENG, props);
					invTextNode = createLabelNode(invText, ENG, props);
					Long conceptNode = createConceptNode(ConceptType.PREDICATE.toString(), props);
					createRelationship(conceptNode, textNode, RlspType.HAS_LABEL, props);
					createRelationship(conceptNode, invTextNode, RlspType.HAS_LABEL, props);

				} else if (textNode == null || invTextNode == null) {
					String nullLabelNodeText = null;
					Long notNullLabelNode = null;
					if (textNode == null) {
						nullLabelNodeText = text;
						notNullLabelNode = invTextNode;
					} else {
						nullLabelNodeText = invText;
						notNullLabelNode = textNode;
					}
					Long notNullConcept = getConceptNodeForLabelNode(notNullLabelNode);
					Long labelNode = createLabelNode(nullLabelNodeText, ENG, props);
					createRelationship(notNullConcept, labelNode, RlspType.HAS_LABEL, props);
				} else {
					// if linked to predicate do nothing
					// if not linked to predicate - create predicate and link
					long textConceptNode = getConceptNodeForLabelNode(textNode);
					long invConceptNode = getConceptNodeForLabelNode(invTextNode);
					if (textConceptNode != invConceptNode) {
						throw new IllegalStateException("inverse labels point to different concepts " + text + " "
								+ invText);
					} else {
						Map<String, Object> properties = inserter.getNodeProperties(textConceptNode);
						if (!((String) properties.get("type")).equalsIgnoreCase(ConceptType.PREDICATE.toString())) {
							throw new IllegalStateException("labels associated with concept that is not a predicate "
									+ text);
						}
					}
				}
				labelIndex.flush();
				notationIndex.flush();
			}
		} finally {
			getConceptRedicatesStmt.close();
			getConceptPredicateConn.close();
		}
	}

	public void mapPredicates() throws SQLException, IOException {
		Map<String, Object> props = new HashMap<String, Object>();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("predicate-mapping.txt");
		if (is == null) {
			logger.info("no predicate-mapping.txt file found");
		} else {
			CSVReader csvReader = new CSVReader(new InputStreamReader(is), '\t');
			try {
				List<String[]> lines = csvReader.readAll();
				for (String[] columns : lines) {
					String meta = columns[0].trim();
					String invMeta = columns[4].trim();
					String rlsp = columns[1].trim();
					String semNet = columns[2].trim();
					if (!StringUtils.isBlank(rlsp) && rlsp.equalsIgnoreCase("eqProp")) {
						labelIndex.flush();
						Long semNetLabelNode = getLabelNode(semNet, ENG);
						if (semNetLabelNode == null) {
							throw new IllegalStateException("semantic network label node not found for text " + semNet);
						} else {
							Long metaNode = getLabelNode(meta, ENG);
							if (metaNode == null) {
								metaNode = createLabelNode(meta, ENG, props);
							}
							Long invMetaNode = getLabelNode(invMeta, ENG);
							if (invMetaNode == null) {
								invMetaNode = createLabelNode(invMeta, ENG, props);
							}
							Long semNetConceptNode = getConceptNodeForLabelNode(semNetLabelNode);
							createRelationship(semNetConceptNode, metaNode, RlspType.HAS_LABEL, props);
							createRelationship(semNetConceptNode, invMetaNode, RlspType.HAS_LABEL, props);
						}
					}
				}
				for (String[] columns : lines) {
					String meta = columns[0].trim();
					String invMeta = columns[4].trim();
					String rlsp = columns[1].trim();
					String semNet = columns[2].trim();
					if (!StringUtils.isBlank(rlsp) && rlsp.equalsIgnoreCase("subProp")) {
						labelIndex.flush();
						Long semNetLabelNode = getLabelNode(semNet, ENG);
						if (semNetLabelNode == null) {
							throw new IllegalStateException("semantic network label node not found for text " + semNet);
						} else {
							Long metaNode = getLabelNode(meta, ENG);
							Long invMetaNode = getLabelNode(invMeta, ENG);
							Long semNetConceptNode = getConceptNodeForLabelNode(semNetLabelNode);
							if (metaNode == null && invMetaNode == null) {
								// create new concept add as IS_A to the
								// semnatic
								// type
								metaNode = createLabelNode(meta, ENG, props);
								invMetaNode = createLabelNode(invMeta, ENG, props);
								Long conceptNode = createConceptNode(ConceptType.PREDICATE.toString(), props);
								createRelationship(conceptNode, metaNode, RlspType.HAS_LABEL, props);
								createRelationship(conceptNode, invMetaNode, RlspType.HAS_LABEL, props);
								createRelationship(conceptNode, semNetConceptNode, IS_A, props);
							} else if (metaNode == null || invMetaNode == null) {
								String nullLabelNodeText = null;
								Long notNullLabelNode = null;
								if (invMetaNode == null) {
									nullLabelNodeText = invMeta;
									notNullLabelNode = metaNode;
								} else {
									nullLabelNodeText = meta;
									notNullLabelNode = invMetaNode;
								}
								if (notNullLabelNode == 619) {
									System.out.println("here");
								}
								Long notNullConcept = getConceptNodeForLabelNode(notNullLabelNode);
								if (notNullConcept == null) {
									System.out.println(inserter.getNodeProperties(notNullLabelNode));
								}
								Long labelNode = createLabelNode(nullLabelNodeText, ENG, props);
								createRelationship(notNullConcept, labelNode, RlspType.HAS_LABEL, props);
								createRelationship(notNullConcept, semNetConceptNode, IS_A, props);

							} else if (metaNode != null && invMetaNode != null) {
								Long metaConceptNode = getConceptNodeForLabelNode(metaNode);
								Long invMetaConceptNode = getConceptNodeForLabelNode(invMetaNode);
								if (metaConceptNode != invMetaConceptNode) {
									throw new IllegalStateException("inverse labels point to different concepts "
											+ meta + " " + invMeta);
								} else {
									createRelationship(metaConceptNode, semNetConceptNode, IS_A, props);
								}
							}

						}
					}
				}
			} finally {
				csvReader.close();
			}
		}
	}

	private Long getConceptNodeForLabelNode(Long labelNode) {
		Long conceptNode = null;
		Iterable<BatchRelationship> relationships = inserter.getRelationships(labelNode);
		for (BatchRelationship batchRelationship : relationships) {
			if (batchRelationship.getType().name().equals(RlspType.HAS_LABEL.toString())) {
				conceptNode = batchRelationship.getStartNode();
				break;
			}
		}
		return conceptNode;
	}

	private Long getConceptNodeForNotationNode(Long notationNode) {
		Long conceptNode = null;
		Iterable<BatchRelationship> relationships = inserter.getRelationships(notationNode);
		for (BatchRelationship batchRelationship : relationships) {
			if (batchRelationship.getType().name().equals(RlspType.HAS_NOTATION.toString())) {
				conceptNode = batchRelationship.getStartNode();
				break;
			}
		}
		return conceptNode;
	}

	private Long getLabelNode(String labelText, String language) {
		Long labelNode = null;
		IndexHits<Long> hits = labelIndex.get("text", labelText);
		for (Long hit : hits) {
			Map<String, Object> props = inserter.getNodeProperties(hit);
			if (((String) props.get("language")).equalsIgnoreCase(language)) {
				labelNode = hit;
			}
		}
		return labelNode;
	}

	private Long createLabelNode(String labelText, String language, Map<String, Object> props) {
		Long labelNode = null;
		props.put("text", labelText);
		props.put("language", language);
		labelNode = inserter.createNode(props);
		props.clear();
		props.put("text", labelText);
		labelIndex.add(labelNode, props);
		props.clear();
		return labelNode;
	}

	private Long getNotationNode(String code) {
		Long notationNode = null;
		IndexHits<Long> hits = notationIndex.get("code", code);
		for (Long hit : hits) {
			notationNode = hit;
		}
		return notationNode;
	}

	private Long createRelationship(Long from, Long to, RelationshipType rlspType, Map<String, Object> props) {
		Long relaNode = inserter.createRelationship(from, to, rlspType, props);
		props.clear();
		props.put("rlspType", rlspType.toString());
		relationshipIndex.add(relaNode, props);
		props.clear();
		return relaNode;
	}

	private Long createNotationNode(String source, String code, Map<String, Object> props) {
		Long notationNode = null;
		props.put("source", source);
		props.put("code", code);
		notationNode = inserter.createNode(props);
		props.clear();
		props.put("code", code);
		notationIndex.add(notationNode, props);
		props.clear();
		return notationNode;
	}

	private Long createConceptNode(String conceptType, Map<String, Object> props) {
		props.put("type", conceptType);
		Long conceptNode = inserter.createNode(props);
		conceptIndex.add(conceptNode, props);
		props.clear();
		return conceptNode;
	}

	public void writeRlspsBetweenConceptsAndSchemes() throws SQLException {
		Connection conceptSchemeConn = dataSource.getConnection();
		Statement conceptSchemeStmt = conceptSchemeConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		conceptSchemeStmt.setFetchSize(Integer.MIN_VALUE);
		Map<String, Object> props = new HashMap<String, Object>();
		ResultSet rs = conceptSchemeStmt.executeQuery(GET_RLSP_CONCEPT_SCHEME);
		int ctr = 0;
		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (ignoredCuiReader.isIgnored(cui)) {
					continue;
				}
				String tui = rs.getString("TUI");
				Long cuiNotationNode = getNotationNode(cui);
				Long cuiConceptNode = getConceptNodeForNotationNode(cuiNotationNode);
				Long tuiNotationNode = getNotationNode(tui);
				Long tuiConceptNode = getConceptNodeForNotationNode(tuiNotationNode);
				props.put("sources", new String[] { MRSTY });
				createRelationship(cuiConceptNode, tuiConceptNode, RlspType.IN_SCHEME, props);
				if (++ctr % 10000 == 0) {
					logger.debug("{}", ctr);
				}
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
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = stmt.executeQuery(GET_NOT_NULL_RELA);
		Map<String, Object> props = new HashMap<String, Object>();
		int ctr = 0;
		try {
			while (rs.next()) {
				String cui1 = rs.getString("CUI1");
				String cui2 = rs.getString("CUI2");
				String rela = rs.getString("RELA");
				if (ignoredCuiReader.isIgnored(cui1) || ignoredCuiReader.isIgnored(cui2)) {
					continue;
				}

				Long cui1NotationNode = getNotationNode(cui1);
				Long srcConceptNode = getConceptNodeForNotationNode(cui1NotationNode);
				Long cui2NotationNode = getNotationNode(cui2);
				Long tgtConceptNode = getConceptNodeForNotationNode(cui2NotationNode);
				if (srcConceptNode != null && tgtConceptNode != null) {
					Long relaLabelNode = getLabelNode(rela, ENG);
					Long relaConceptNode = getConceptNodeForLabelNode(relaLabelNode);
					RelationshipType rlsptype = DynamicRelationshipType.withName(relaConceptNode.toString());
					props.put("sources", new String[] { MRREL });
					createRelationship(cui2NotationNode, cui1NotationNode, rlsptype, props);
				}
				if (++ctr % 10000 == 0) {
					logger.debug("{}", ctr);
				}
			}
			logger.info("all non null rela relationships added: {}", ctr);
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
					inserter.createRelationship(conceptNode, newTextNode, RlspType.HAS_LABEL, rlspProperties);
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
							sources.put("sources", new String[] { "PMID|" + pmid });
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

	private BatchInserter inserter;
	private BatchInserterIndex labelIndex;
	private BatchInserterIndex notationIndex;
	private BatchInserterIndex conceptIndex;
	private BatchInserterIndex relationshipIndex;
	private DataSource dataSource;
	private IgnoredCuiReader ignoredCuiReader;
	private static RelationshipType IS_A = null;

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

	private static final Logger logger = LoggerFactory.getLogger(UmlsDataImport.class);

}
