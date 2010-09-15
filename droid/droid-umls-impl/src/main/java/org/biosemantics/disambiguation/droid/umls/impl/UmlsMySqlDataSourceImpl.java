package org.biosemantics.disambiguation.droid.umls.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Label.LabelType;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipCategory;
import org.biosemantics.disambiguation.knowledgebase.service.local.IdGenerator;
import org.neo4j.index.lucene.LuceneFulltextIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import au.com.bytecode.opencsv.CSVReader;

public class UmlsMySqlDataSourceImpl implements DataSource {
	private static final String GET_CO_OCCURANCE_RLSP_SQL = "select CUI1, CUI2, COF from MRCOC";
	private static final String GET_ALL_RLSP_SQL = "select CUI1, CUI2, REL, RELA from MRREL";
	private static final String GRAPH_FOLDER_PATH = "/home/bhsingh/Code/workspace/transmog/droid/droid-umls-impl/concept-graph";
	private static final String GET_ALL_CONCEPT_SCHEME = "select STY_RL, UI from SRDEF where RT='STY'";
	private static final String GET_CONCEPT_SCHEME_RELATIONS = "select STY1, RL, STY2 from SRSTRE2 order by STY1";
	private static final String GET_ALL_CONCEPTS_SQL = "select CUI, SUI, TS, ISPREF, STT, LAT, STR, SAB, CODE from MRCONSO ORDER BY CUI";
	private static final String GET_ALL_CONCEPT_SCHEME_RLSP = "select distinct RL as RL from SRSTRE2";
	private static final String SCR = "scr";
	private static final Object FULL_TEXT_SEPARATOR = " ";
	private static final Logger logger = LoggerFactory.getLogger(UmlsMySqlDataSourceImpl.class);

	// key=UMLS language code value=knowledge base language code
	private Map<String, Language> languageMap = new HashMap<String, Language>();
	// key =REL from MRREL value=Concept
	private Map<String, Long> predicateMap = new HashMap<String, Long>();
	private Map<String, Long> conceptSchemeMap = new HashMap<String, Long>();
	// key=cui value=concept.getId()
	// private Map<String, String> cuiConceptIdMap = new HashMap<String,
	// String>();

	// MYSQL data
	private String url;
	private String userName;
	private String password;

	private Connection connection;
	private IntermediateCache intermediateCache;
	private IdGenerator idGenerator;
	private int batchSize = 10000;
	private String predicateFile = "predicates.tsv";

	private long labelSubNodeId;
	private long notationSubNodeId;
	private long conceptSubNodeId;
	private long predicateSubNodeId;
	private long conceptSchemeSubNodeId;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPredicateFile(String predicateFile) {
		this.predicateFile = predicateFile;
	}

	public void setIntermediateCache(IntermediateCache intermediateCache) {
		this.intermediateCache = intermediateCache;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Override
	public void setBatchSize(int batchSize) {
		if (batchSize <= 0)
			throw new IllegalArgumentException("batch size can only be a positive number ");
		this.batchSize = batchSize;
		logger.info("batch size set to {}", batchSize);
	}

	public void init() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url, userName, password);
		Language[] languages = Language.values();
		for (Language language : languages) {
			languageMap.put(language.getIso62392Code(), language);
		}
	}

	@Override
	public void initialize() throws SQLException {
		StopWatch stopWatch = new StopWatch();

		logger.info("creating subnodes");
		stopWatch.start();
		createSubNodes();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());

		logger.info("creating predicates from text file located at \"{}\" ", predicateFile);
		stopWatch.start();
		createPredicateConcepts();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());

		logger.info("starting concept scheme import from SRDEF table");
		stopWatch.start();
		createConceptScheme();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());

		logger.info("starting concept scheme relationship import from SRSTRE2 table");
		stopWatch.start();
		createConceptSchemeRelationship();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());

		logger.info("starting concept import from MRCONSO table");
		stopWatch.start();
		getConcepts();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());

		logger.info("starting concept relationship import from MRREL table");
		stopWatch.start();
		getFactualRelationships();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());

		logger.info("starting co-occurance relationship import from MRCOC table");
		stopWatch.start();
		getCooccuranceRelationships();
		stopWatch.stop();
		logger.info("process completed in {} ms", stopWatch.getLastTaskTimeMillis());
	}

	private void createSubNodes() {
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		try {
			long referenceNode = inserter.getReferenceNode();
			labelSubNodeId = inserter.createNode(null);
			inserter.createRelationship(referenceNode, labelSubNodeId, KnowledgebaseRelationshipType.LABELS, null);
			notationSubNodeId = inserter.createNode(null);
			inserter.createRelationship(referenceNode, notationSubNodeId, KnowledgebaseRelationshipType.NOTATIONS, null);
			conceptSubNodeId = inserter.createNode(null);
			inserter.createRelationship(referenceNode, conceptSubNodeId, KnowledgebaseRelationshipType.CONCEPTS, null);
			predicateSubNodeId = inserter.createNode(null);
			inserter.createRelationship(referenceNode, predicateSubNodeId, KnowledgebaseRelationshipType.PREDICATES,
					null);
			conceptSchemeSubNodeId = inserter.createNode(null);
			inserter.createRelationship(referenceNode, conceptSchemeSubNodeId,
					KnowledgebaseRelationshipType.CONCEPT_SCHEMES, null);
		} finally {
			inserter.shutdown();
		}
	}

	private void createPredicateConcepts() throws SQLException {

		PreparedStatement getDistinctPredicateStatement = connection.prepareStatement(GET_ALL_CONCEPT_SCHEME_RLSP,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		getDistinctPredicateStatement.setFetchSize(Integer.MIN_VALUE);
		// #---GRAPH DATASTORE CONFIGURATION---#
		int ctr = 0;
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		LuceneIndexBatchInserter indexService = new LuceneIndexBatchInserterImpl(inserter);
		LuceneFulltextIndexBatchInserter fulltextIndexService = new LuceneFulltextIndexBatchInserter(inserter);
		Map<String, Object> properties = new HashMap<String, Object>();
		StringBuilder fullText = new StringBuilder();
		ResultSet results = getDistinctPredicateStatement.executeQuery();
		try {
			while (results.next()) {
				// notations
				String code = results.getString("RL");
				properties.clear();
				properties.put("domain", Domain.UMLS.name());
				properties.put("code", code);
				long notationNodeId = inserter.createNode(properties);
				inserter.createRelationship(notationSubNodeId, notationNodeId, KnowledgebaseRelationshipType.NOTATION,
						null);
				indexService.index(notationNodeId, "notation_code", code);
				fullText.append(code).append(FULL_TEXT_SEPARATOR);
				// labels
				String text = code.replace("_", " ");
				properties.clear();
				properties.put("language", Language.EN.name());
				properties.put("text", text);
				properties.put("labelType", LabelType.PREFERRED.name());
				long labelNodeId = inserter.createNode(properties);
				inserter.createRelationship(labelSubNodeId, labelNodeId, KnowledgebaseRelationshipType.LABEL, null);
				indexService.index(notationNodeId, "label_text", code);
				fullText.append(text).append(FULL_TEXT_SEPARATOR);

				// create predicate
				properties.clear();
				String randomConceptId = idGenerator.generateRandomId();
				properties.put("id", randomConceptId);
				long predicateNodeId = inserter.createNode(properties);
				predicateMap.put(code, predicateNodeId);
				inserter.createRelationship(predicateSubNodeId, predicateNodeId,
						KnowledgebaseRelationshipType.PREDICATE, null);
				indexService.index(predicateNodeId, "concept_id", randomConceptId);
				fulltextIndexService.index(predicateNodeId, "concept_full_text", fullText);
				fullText.setLength(0);
				inserter.createRelationship(predicateNodeId, notationNodeId,
						KnowledgebaseRelationshipType.HAS_NOTATION, null);

				inserter.createRelationship(predicateNodeId, labelNodeId, KnowledgebaseRelationshipType.HAS_LABEL, null);
			}
		} finally {
			logger.info("closing result set and statement");
			if (results != null) {
				results.close();
			}
			if (getDistinctPredicateStatement != null) {
				getDistinctPredicateStatement.close();
			}
		}

		try {
			FileReader fileReader = new FileReader(new File(predicateFile));
			CSVReader csvReader = new CSVReader(fileReader, '\t');
			List<String[]> allLines = csvReader.readAll();
			if (allLines != null) {
				for (String[] line : allLines) {
					if (line.length == 2) {
						ctr++;
						// notations
						String code = line[0].trim();
						properties.clear();
						properties.put("domain", Domain.UMLS.name());
						properties.put("code", code);
						long notationNodeId = inserter.createNode(properties);
						inserter.createRelationship(notationSubNodeId, notationNodeId,
								KnowledgebaseRelationshipType.NOTATION, null);
						indexService.index(notationNodeId, "notation_code", code);
						fullText.append(code).append(FULL_TEXT_SEPARATOR);
						// labels
						String text = line[1].trim();
						properties.clear();
						properties.put("language", Language.EN.name());
						properties.put("text", text);
						properties.put("labelType", LabelType.PREFERRED.name());
						long labelNodeId = inserter.createNode(properties);
						inserter.createRelationship(labelSubNodeId, labelNodeId, KnowledgebaseRelationshipType.LABEL,
								null);
						indexService.index(notationNodeId, "label_text", code);
						fullText.append(text).append(FULL_TEXT_SEPARATOR);

						// create predicate
						properties.clear();
						String randomConceptId = idGenerator.generateRandomId();
						properties.put("id", randomConceptId);
						long predicateNodeId = inserter.createNode(properties);
						predicateMap.put(code, predicateNodeId);
						inserter.createRelationship(predicateSubNodeId, predicateNodeId,
								KnowledgebaseRelationshipType.PREDICATE, null);
						indexService.index(predicateNodeId, "concept_id", randomConceptId);
						fulltextIndexService.index(predicateNodeId, "concept_full_text", fullText);
						fullText.setLength(0);
						inserter.createRelationship(predicateNodeId, notationNodeId,
								KnowledgebaseRelationshipType.HAS_NOTATION, null);

						inserter.createRelationship(predicateNodeId, labelNodeId,
								KnowledgebaseRelationshipType.HAS_LABEL, null);
					} else {
						logger.warn("line number {} is unreadable.", ctr);
					}

				}
				logger.info("{} predicates added", ctr);
			}
		} catch (FileNotFoundException e) {
			logger.info("Predicates file not found", e);
		} catch (IOException e) {
			logger.info("IOException when reading lines from TSV file", e);
		} finally {
			logger.info("closing result set and statement");
			logger.info("optimising indexes ...");
			long start = System.currentTimeMillis();
			indexService.optimize();
			fulltextIndexService.optimize();
			logger.info("indexes optimised in {} (ms)", System.currentTimeMillis() - start);
			logger.info("shutdown store called ...");
			start = System.currentTimeMillis();
			indexService.shutdown();
			fulltextIndexService.shutdown();
			inserter.shutdown();
			logger.info("shutdown complete in {} (ms)", System.currentTimeMillis() - start);
		}

	}

	private void createConceptScheme() throws SQLException {
		PreparedStatement getConceptSchemeStatement = connection.prepareStatement(GET_ALL_CONCEPT_SCHEME,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		getConceptSchemeStatement.setFetchSize(Integer.MIN_VALUE);
		// #---GRAPH DATASTORE CONFIGURATION---#
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		LuceneIndexBatchInserter indexService = new LuceneIndexBatchInserterImpl(inserter);
		LuceneFulltextIndexBatchInserter fulltextIndexService = new LuceneFulltextIndexBatchInserter(inserter);
		Map<String, Object> properties = new HashMap<String, Object>();
		StringBuilder fullText = new StringBuilder();
		ResultSet results = getConceptSchemeStatement.executeQuery();
		try {
			while (results.next()) {
				String text = results.getString("STY_RL");
				String code = results.getString("UI");

				// notations
				properties.clear();
				properties.put("domain", Domain.UMLS.name());
				properties.put("code", code);
				long notationNodeId = inserter.createNode(properties);
				inserter.createRelationship(notationSubNodeId, notationNodeId, KnowledgebaseRelationshipType.NOTATION,
						null);
				indexService.index(notationNodeId, "notation_code", code);
				fullText.append(code).append(FULL_TEXT_SEPARATOR);
				// labels
				properties.clear();
				properties.put("language", Language.EN.name());
				properties.put("text", text);
				properties.put("labelType", LabelType.PREFERRED.name());
				long labelNodeId = inserter.createNode(properties);
				inserter.createRelationship(labelSubNodeId, labelNodeId, KnowledgebaseRelationshipType.LABEL, null);
				indexService.index(notationNodeId, "label_text", code);
				fullText.append(text).append(FULL_TEXT_SEPARATOR);

				// create predicate
				properties.clear();
				String randomConceptId = idGenerator.generateRandomId();
				properties.put("id", randomConceptId);
				long conceptSchemeNodeId = inserter.createNode(properties);
				inserter.createRelationship(conceptSchemeSubNodeId, conceptSchemeNodeId,
						KnowledgebaseRelationshipType.CONCEPT_SCHEME, null);
				indexService.index(conceptSchemeNodeId, "concept_id", randomConceptId);
				fulltextIndexService.index(conceptSchemeNodeId, "concept_full_text", fullText);
				fullText.setLength(0);
				inserter.createRelationship(conceptSchemeNodeId, notationNodeId,
						KnowledgebaseRelationshipType.HAS_NOTATION, null);
				inserter.createRelationship(conceptSchemeNodeId, labelNodeId, KnowledgebaseRelationshipType.HAS_LABEL,
						null);
				conceptSchemeMap.put(text, conceptSchemeNodeId);
			}
		} finally {
			logger.info("closing result set and statement");
			if (results != null) {
				results.close();
			}
			if (getConceptSchemeStatement != null) {
				getConceptSchemeStatement.close();
			}
			logger.info("optimising indexes ...");
			long start = System.currentTimeMillis();
			indexService.optimize();
			fulltextIndexService.optimize();
			logger.info("indexes optimised in {} (ms)", System.currentTimeMillis() - start);
			logger.info("shutdown store called ...");
			start = System.currentTimeMillis();
			indexService.shutdown();
			fulltextIndexService.shutdown();
			inserter.shutdown();
			logger.info("shutdown complete in {} (ms)", System.currentTimeMillis() - start);
		}
	}

	private void createConceptSchemeRelationship() throws SQLException {
		PreparedStatement getConceptStatement = connection.prepareStatement(GET_CONCEPT_SCHEME_RELATIONS,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		getConceptStatement.setFetchSize(Integer.MIN_VALUE);
		// #---GRAPH DATASTORE CONFIGURATION---#
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		Map<String, Object> properties = new HashMap<String, Object>();
		ResultSet results = getConceptStatement.executeQuery();
		try {
			while (results.next()) {
				String sty1 = results.getString("STY1");
				String sty2 = results.getString("STY2");
				String rl = results.getString("RL");
				Long source = conceptSchemeMap.get(sty1);
				Long target = conceptSchemeMap.get(sty2);
				Long predicate = predicateMap.get(rl);
				if (predicate == null) {
					logger.warn("null predicate concept {}", rl);
				}

				/*
				 * check against following entries in table. e.g. Acquired
				 * Abnormality co-occurs_with Acquired Abnormality We cannot
				 * have the same source and target for a relationship in neo4j
				 */
				if (source != null && target != null && !source.equals(target)) {
					properties.clear();
					// reuse a map for all rlsps.
					properties.put("id", idGenerator.generateRandomId());
					properties.put("score", String.valueOf(Integer.MAX_VALUE));
					properties.put("relationshipCategory", RelationshipCategory.AUTHORITATIVE.name());
					ConceptRelationshipType conceptRelationshipType = getRelationshipType(rl);
					if (predicate != null) {
						properties.put("predicateConceptId", predicate);
					}
					inserter.createRelationship(source, target, conceptRelationshipType, properties);
				}
			}
		} finally {
			logger.info("closing result set and statement");
			if (results != null) {
				results.close();
			}
			if (getConceptStatement != null) {
				getConceptStatement.close();
			}
			logger.info("shutdown store called ...");
			long start = System.currentTimeMillis();
			inserter.shutdown();
			logger.info("shutdown complete in {} (ms)", System.currentTimeMillis() - start);
		}
	}

	private void getConcepts() throws SQLException {
		PreparedStatement getConceptStatement = connection.prepareStatement(GET_ALL_CONCEPTS_SQL,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		getConceptStatement.setFetchSize(Integer.MIN_VALUE);
		int ctr = 0;
		int nodeCtr = 0;
		String previousCUI = null;
		// #---GRAPH DATASTORE CONFIGURATION---#
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		LuceneIndexBatchInserter indexService = new LuceneIndexBatchInserterImpl(inserter);
		LuceneFulltextIndexBatchInserter fulltextIndexService = new LuceneFulltextIndexBatchInserter(inserter);

		Map<String, Object> properties = new HashMap<String, Object>();
		Set<Long> labelIds = new HashSet<Long>();
		Set<Long> notationIds = new HashSet<Long>();
		StringBuilder fullText = new StringBuilder();
		ResultSet results = getConceptStatement.executeQuery();
		try {
			while (results.next()) {
				// get all data for same CUI
				String cui = results.getString("CUI");
				String sui = results.getString("SUI");
				String ts = results.getString("TS");
				String isPref = results.getString("ISPREF");
				String stt = results.getString("STT");// TS, ISPREF, STT
				String lat = results.getString("LAT");
				String str = results.getString("STR");
				String sab = results.getString("SAB");
				String code = results.getString("CODE");
				// set previous cui to the cui at start
				if (ctr == 0) {
					previousCUI = cui;
				}
				if (!previousCUI.equals(cui)) {
					// create UMLS notation (only once for a concept) use
					// previousCUI here
					properties.clear();
					properties.put("domain", Domain.UMLS.name());
					properties.put("code", previousCUI);
					long notationNodeId = inserter.createNode(properties);
					inserter.createRelationship(notationSubNodeId, notationNodeId,
							KnowledgebaseRelationshipType.NOTATION, null);
					nodeCtr++;
					intermediateCache.addNotationNode(Domain.UMLS.name(), previousCUI, notationNodeId);
					indexService.index(notationNodeId, "notation_code", previousCUI);
					fullText.append(previousCUI).append(FULL_TEXT_SEPARATOR);
					notationIds.add(notationNodeId);

					// create concept
					properties.clear();
					String randomConceptId = idGenerator.generateRandomId();
					fullText.append(randomConceptId).append(FULL_TEXT_SEPARATOR);
					properties.put("id", randomConceptId);
					long conceptNodeId = inserter.createNode(properties);
					intermediateCache.addConceptNode(cui, conceptNodeId);
					inserter.createRelationship(conceptSubNodeId, conceptNodeId, KnowledgebaseRelationshipType.CONCEPT,
							null);
					nodeCtr++;
					indexService.index(conceptNodeId, "concept_id", randomConceptId);
					fulltextIndexService.index(conceptNodeId, "concept_full_text", fullText);
					fullText.setLength(0);

					for (Long nodeId : notationIds) {
						inserter.createRelationship(conceptNodeId, nodeId, KnowledgebaseRelationshipType.HAS_NOTATION,
								null);
					}
					for (Long nodeId : labelIds) {
						inserter.createRelationship(conceptNodeId, nodeId, KnowledgebaseRelationshipType.HAS_LABEL,
								null);
					}
					notationIds.clear();
					labelIds.clear();
					previousCUI = cui;
				}

				long labelNodeId = intermediateCache.getLabelNodeId(sui);
				if (labelNodeId == 0) {
					Language language = getLanguage(lat);
					properties.clear();
					properties.put("language", language.name());
					// create label
					if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
						properties.put("labelType", LabelType.PREFERRED.name());
					} else {
						properties.put("labelType", LabelType.ALTERNATE.name());
					}
					properties.put("text", str);
					labelNodeId = inserter.createNode(properties);
					inserter.createRelationship(labelSubNodeId, labelNodeId, KnowledgebaseRelationshipType.LABEL, null);
					nodeCtr++;
					intermediateCache.addLabelNode(sui, labelNodeId);
					indexService.index(labelNodeId, "label_text", str);
					fullText.append(str).append(FULL_TEXT_SEPARATOR);
				}
				labelIds.add(labelNodeId);
				// reuse map
				properties.clear();
				Domain domain = getDomain(sab);
				long notationNodeId = intermediateCache.getNotationNodeId(domain.name(), code);
				if (notationNodeId == 0) {
					properties.clear();
					properties.put("domain", domain.name());
					properties.put("code", code);
					notationNodeId = inserter.createNode(properties);
					inserter.createRelationship(notationSubNodeId, notationNodeId,
							KnowledgebaseRelationshipType.NOTATION, null);
					nodeCtr++;
					intermediateCache.addNotationNode(domain.name(), code, notationNodeId);
					indexService.index(notationNodeId, "notation_code", code);
					fullText.append(code).append(FULL_TEXT_SEPARATOR);
				}
				notationIds.add(notationNodeId);
				// reuse map
				properties.clear();
				ctr++;
				if (ctr % batchSize == 0) {
					logger.info("created concepts/nodes = {} / {}", new Object[] { ctr, nodeCtr });
					nodeCtr = 0;
				}
			}
		} finally {
			logger.info("closing result set and statement");
			if (results != null) {
				results.close();
			}
			if (getConceptStatement != null) {
				getConceptStatement.close();
			}
			logger.info("optimising indexes ...");
			long start = System.currentTimeMillis();
			indexService.optimize();
			fulltextIndexService.optimize();
			logger.info("indexes optimised in {} (ms)", System.currentTimeMillis() - start);
			logger.info("shutdown store called ...");
			start = System.currentTimeMillis();
			indexService.shutdown();
			fulltextIndexService.shutdown();
			inserter.shutdown();
			logger.info("shutdown complete in {} (ms)", System.currentTimeMillis() - start);
		}
	}

	private void getFactualRelationships() throws SQLException {
		PreparedStatement getAllRlspStatement = connection.prepareStatement(GET_ALL_RLSP_SQL,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		getAllRlspStatement.setFetchSize(Integer.MIN_VALUE);
		int ctr = 0;
		// #---GRAPH DATASTORE CONFIGURATION---#
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		ResultSet results = getAllRlspStatement.executeQuery();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		try {
			while (results.next()) {
				String cui1 = results.getString("CUI1");
				String cui2 = results.getString("CUI2");
				String rel = results.getString("REL");
				String rela = results.getString("RELA");
				// ignore rlsp if source or target is not found
				if (StringUtils.isBlank(cui1) || StringUtils.isBlank(cui2)) {
					logger.debug("No cui1={} cui2={} found. Ignoring", new Object[] { cui1, cui2 });
					continue;
				}
				// ignore rlsp if source or target are the same
				if (cui1.equalsIgnoreCase(cui2)) {
					logger.debug("self relationship found. cui1 {} == cui2 {}. Ignoring", new Object[] { cui1, cui2 });
					continue;
				}
				long sourceConceptNodeId = intermediateCache.getConceptNodeByCui(cui1);
				if (sourceConceptNodeId == 0) {
					logger.warn("cui (source) {} not found in map ", cui1);
					continue;
				}
				long targetConceptNodeId = intermediateCache.getConceptNodeByCui(cui2);
				if (targetConceptNodeId == 0) {
					logger.warn("cui (target) {} not found in map ", cui2);
					continue;
				}
				// reuse a map for all rlsps.
				rlspProperties.put("id", idGenerator.generateRandomId());
				rlspProperties.put("score", String.valueOf(Integer.MAX_VALUE));
				rlspProperties.put("relationshipCategory", RelationshipCategory.AUTHORITATIVE.name());
				Long predicateConceptId = predicateMap.get(rela);
				if (predicateConceptId != null) {
					rlspProperties.put("predicateConceptId", predicateConceptId);
				}
				ConceptRelationshipType conceptRelationshipType = getConceptRelationshipType(rel);
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId, conceptRelationshipType,
						rlspProperties);
				// clear map as we need to reuse it
				rlspProperties.clear();
				ctr++;
				if (ctr % batchSize == 0) {
					logger.info("created {} rlsps.", new Object[] { ctr });
				}
			}
		} finally {
			logger.info("closing result set and statement");
			if (results != null) {
				results.close();
			}
			if (getAllRlspStatement != null) {
				getAllRlspStatement.close();
			}
			logger.info("optimising indexes ...");
			long start = System.currentTimeMillis();
			logger.info("shutdown store called ...");
			start = System.currentTimeMillis();
			inserter.shutdown();
			logger.info("shutdown complete in {} (ms)", System.currentTimeMillis() - start);
		}
	}

	private void getCooccuranceRelationships() throws SQLException {
		PreparedStatement getAllRlspStatement = connection.prepareStatement(GET_CO_OCCURANCE_RLSP_SQL,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		getAllRlspStatement.setFetchSize(Integer.MIN_VALUE);
		int ctr = 0;
		// #---GRAPH DATASTORE CONFIGURATION---#
		Map<String, String> graphProps = new HashMap<String, String>();
		graphProps.put("neostore.nodestore.db.mapped_memory", "900M");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "3G");
		graphProps.put("neostore.propertystore.db.mapped_memory", "900M");
		graphProps.put("neostore.propertystore.db.strings.mapped_memory", "1G");
		graphProps.put("neostore.relationshipstore.db.mapped_memory", "1G");
		BatchInserter inserter = new BatchInserterImpl(GRAPH_FOLDER_PATH, graphProps);
		ResultSet results = getAllRlspStatement.executeQuery();
		Map<String, Object> rlspProperties = new HashMap<String, Object>();
		try {
			while (results.next()) {
				String cui1 = results.getString("CUI1");
				String cui2 = results.getString("CUI2");
				int cof = results.getInt("COF");// min value for COF in database is 1
				// ignore rlsp if source or target is not found
				if (StringUtils.isBlank(cui1) || StringUtils.isBlank(cui2)) {
					logger.warn("No cui1={} cui2={} found. Ignoring", new Object[] { cui1, cui2 });
					continue;
				}
				// ignore rlsp if source or target are the same
				if (cui1.equalsIgnoreCase(cui2)) {
					logger.warn("self relationship found. cui1 {} == cui2 {}. Ignoring", new Object[] { cui1, cui2 });
					continue;
				}
				long sourceConceptNodeId = intermediateCache.getConceptNodeByCui(cui1);
				if (sourceConceptNodeId == 0) {
					logger.warn("cui (source) {} not found in map ", cui1);
					continue;
				}
				long targetConceptNodeId = intermediateCache.getConceptNodeByCui(cui2);
				if (targetConceptNodeId == 0) {
					logger.warn("cui (target) {} not found in map ", cui2);
					continue;
				}
				// reuse a map for all rlsps.
				rlspProperties.put("id", idGenerator.generateRandomId());
				rlspProperties.put("score", String.valueOf(cof));
				rlspProperties.put("relationshipCategory", RelationshipCategory.CO_OCCURANCE.name());
				inserter.createRelationship(sourceConceptNodeId, targetConceptNodeId, ConceptRelationshipType.RELATED,
						rlspProperties);
				// clear map as we need to reuse it
				rlspProperties.clear();
				ctr++;
				if (ctr % batchSize == 0) {
					logger.info("created {} rlsps.", new Object[] { ctr });
				}
			}
		} finally {
			logger.info("closing result set and statement");
			if (results != null) {
				results.close();
			}
			if (getAllRlspStatement != null) {
				getAllRlspStatement.close();
			}
			logger.info("optimising indexes ...");
			long start = System.currentTimeMillis();
			logger.info("shutdown store called ...");
			start = System.currentTimeMillis();
			inserter.shutdown();
			logger.info("shutdown complete in {} (ms)", System.currentTimeMillis() - start);
		}
	}

	private Domain getDomain(String sab) {
		/**
		 * All "." and "-" converted to "_" in domain names as they are illegal characters.
		 * 
		 */
		sab = sab.replace("-", "_");
		sab = sab.replace(".", "_");
		Domain domain = Domain.valueOf(sab);
		if (domain == null) {
			logger.warn("no domain found for string {}", sab);
		}
		return domain;
	}

	private Language getLanguage(String lat) {
		lat = lat.toLowerCase();
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has
		// been deprecated
		if (lat.equalsIgnoreCase(SCR)) {
			lat = Language.HR.getIso62392Code();
		}
		Language language = languageMap.get(lat);
		if (language == null) {
			logger.error("canot find language for string {}", lat);
		}
		return language;
	}

	private ConceptRelationshipType getConceptRelationshipType(String rel) {
		// available values for rel in UMLS:
		// http://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html
		/*
		 * 
		 * AQ Allowed qualifier CHD has child relationship in a Metathesaurus
		 * source vocabulary DEL Deleted concept PAR has parent relationship in
		 * a Metathesaurus source vocabulary QB can be qualified by. RB has a
		 * broader relationship RL the relationship is similar or "alike". the
		 * two concepts are similar or "alike". In the current edition of the
		 * Metathesaurus, most relationships with this attribute are mappings
		 * provided by a source, named in SAB and SL; hence concepts linked by
		 * this relationship may be synonymous, i.e. self-referential: CUI1 =
		 * CUI2. In previous releases, some MeSH Supplementary Concept
		 * relationships were represented in this way. RN has a narrower
		 * relationship RO has relationship other than synonymous, narrower, or
		 * broader RQ related and possibly synonymous. RU Related, unspecified
		 * SIB has sibling relationship in a Metathesaurus source vocabulary. SY
		 * source asserted synonymy. XR Not related, no mapping Empty
		 * relationship
		 */
		ConceptRelationshipType conceptRelationshipType = ConceptRelationshipType.RELATED;
		String relUpper = rel.toUpperCase().trim();
		if (relUpper.equals("CHD") || relUpper.equals("RN")) {
			conceptRelationshipType = ConceptRelationshipType.HAS_NARROWER_CONCEPT;
		} else if (relUpper.equals("PAR") || relUpper.equals("RB")) {
			conceptRelationshipType = ConceptRelationshipType.HAS_BROADER_CONCEPT;
		} else if (relUpper.equals("RQ") || relUpper.equals("SY") || relUpper.equals("RL")) {
			conceptRelationshipType = ConceptRelationshipType.CLOSE_MATCH;
		}
		return conceptRelationshipType;
	}

	private ConceptRelationshipType getRelationshipType(String rl) {
		ConceptRelationshipType conceptRelationshipType = ConceptRelationshipType.RELATED;

		if (rl.equalsIgnoreCase("isa")) {
			conceptRelationshipType = ConceptRelationshipType.HAS_BROADER_CONCEPT;
		}
		return conceptRelationshipType;
	}

}
