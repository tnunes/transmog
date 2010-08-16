package org.biosemantics.disambiguation.droid.umls.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipInput;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptSchemeService;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptService;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelService;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationService;
import org.biosemantics.disambiguation.knowledgebase.service.QueryService;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipCategory;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import au.com.bytecode.opencsv.CSVReader;

public class UmlsMySqlDataSourceImpl implements DataSource {

	private String url;
	private String userName;
	private String password;
	private Connection connection;
	private LabelService labelService;
	private NotationService notationService;
	private ConceptService conceptService;
	private QueryService queryService;
	private RelationshipService relationshipService;
	private ConceptSchemeService conceptSchemeService;
	private String predicateFile = "predicates.tsv";

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLabelService(LabelService labelService) {
		this.labelService = labelService;
	}

	public void setNotationService(NotationService notationService) {
		this.notationService = notationService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	public void setRelationshipService(RelationshipService relationshipService) {
		this.relationshipService = relationshipService;
	}

	public void setConceptSchemeService(ConceptSchemeService conceptSchemeService) {
		this.conceptSchemeService = conceptSchemeService;
	}

	public void setPredicateFile(String predicateFile) {
		this.predicateFile = predicateFile;
	}

	@Override
	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}

	@Override
	public void setBatchSize(int batchSize) {
		if (batchSize <= 0)
			throw new IllegalArgumentException("batch size can only be a positive number ");
		this.batchSize = batchSize;
	}

	private Map<String, Language> languageMap = new HashMap<String, Language>();
	private Map<String, Concept> predicateMap = new HashMap<String, Concept>();
	private Map<String, Concept> conceptSchemeMap = new HashMap<String, Concept>();

	private static final Logger logger = LoggerFactory.getLogger(UmlsMySqlDataSourceImpl.class);
	private static final String SCR = "scr";
	private GraphDatabaseService graphDatabaseService;
	private int batchSize = 10000;
	// private static final String GET_ALL_SEMANTIC_TYPES = "Select STY_RL FROM STY WHERE RT='STY'";
	private static final String GET_ALL_CONCEPTS = "select CUI, TS, ISPREF, STT, LAT, SUI,  STR, SAB, CODE from MRCONSO order by CUI";
	private static final String GET_ALL_RELATIONSHIPS = "select CUI1, CUI2, REL, RELA from MRREL order by CUI1";
	private static final String GET_ALL_CO_OCCURANCE_RELATIONSHIPS = "select CUI1, CUI2, COF from MRCOC order by CUI1";
	private static final String GET_ALL_CONCEPT_SCHEME = "select STY_RL, UI from SRDEF where RT='STY'";
	private static final String GET_ALL_CONCEPT_SCHEME_RLSP = "select distinct RL as RL from SRSTRE2";
	private static final String GET_CONCEPT_SCHEME_RELATIONS = "select STY1, RL, STY2 from SRSTRE2 order by STY1";

	public void init() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url, userName, password);
		Language[] languages = Language.values();
		for (Language language : languages) {
			languageMap.put(language.getIso62392Code(), language);
		}
	}

	@Override
	public void initialize() {
		try {
			StopWatch stopWatch = new StopWatch();
			logger.info("creating predicates from text file {} " + predicateFile);
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
			getAllConcepts();
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

		} catch (SQLException e) {
			logger.error("Sql Exception", e);
		}
	}

	private void createPredicateConcepts() {
		Transaction transaction = graphDatabaseService.beginTx();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			FileReader fileReader = new FileReader(new File(predicateFile));
			CSVReader csvReader = new CSVReader(fileReader, '\t');
			List<String[]> allLines = csvReader.readAll();
			if (allLines != null) {
				List<Label> labels = new ArrayList<Label>(1);
				List<Notation> notations = new ArrayList<Notation>(1);
				int ctr = 0;
				for (String[] line : allLines) {
					ctr++;
					if (line.length == 2) {
						String code = line[0].trim();
						Notation notation = notationService.createNotation(Domain.UMLS, code);
						notations.clear();
						notations.add(notation);
						String text = line[1].trim();
						Label label = labelService.createPreferredLabel(text, Language.EN);
						labels.clear();
						labels.add(label);
						Concept predicate = conceptService.createPredicate(labels, notations);
						predicateMap.put(code, predicate);

					} else {
						logger.warn("line number {} is unreadable.", ctr);
					}
				}
				stmt = connection.createStatement();
				rs = stmt.executeQuery(GET_ALL_CONCEPT_SCHEME_RLSP);
				while (rs.next()) {
					String rlsp = rs.getString("RL");
					if (predicateMap.containsKey(rlsp)) {
						//logger.info("found {} rlsp in predicate map ", rlsp);
					} else {
						logger.info("No match found for {} in the predicate map, creating new predicate concept", rlsp);
						labels.clear();
						labels.add(labelService.createPreferredLabel(rlsp, Language.EN));
						Concept predicate = conceptService.createPredicate(labels);
						predicateMap.put(rlsp, predicate);
					}
				}

			}
			transaction.success();
		} catch (IOException ex) {
			logger.error("IO exception when reading from predicate file", ex);
			transaction.failure();
		} catch (SQLException ex) {
			logger.error("SQL exception with query", ex);
			transaction.failure();
		} finally {
			if (transaction != null) {
				transaction.finish();
			}
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				logger.warn("exception when closing resultset / statement in finally block", e);
			}
		}

	}

	private void createConceptScheme() {
		Transaction transaction = graphDatabaseService.beginTx();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(GET_ALL_CONCEPT_SCHEME);
			List<Label> labels = new ArrayList<Label>(1);
			List<Notation> notations = new ArrayList<Notation>(1);
			while (rs.next()) {
				Label label = labelService.createPreferredLabel(rs.getString("STY_RL"), Language.EN);
				labels.clear();
				labels.add(label);
				Notation notation = notationService.createNotation(Domain.UMLS, rs.getString("UI"));
				notations.clear();
				notations.add(notation);
				Concept conceptScheme = conceptSchemeService.createConceptScheme(labels, notations);
				conceptSchemeMap.put(label.getText(), conceptScheme);
			}
			transaction.success();
		} catch (SQLException e) {
			logger.error("SQL exception creating concept scheme", e);
			transaction.failure();
		} finally {
			if (transaction != null) {
				transaction.finish();
			}
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				logger.warn("exception when closing resultset / statement in finally block", e);
			}
		}
	}

	private void createConceptSchemeRelationship() {
		Transaction transaction = graphDatabaseService.beginTx();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(GET_CONCEPT_SCHEME_RELATIONS);
			while (rs.next()) {
				String sty1 = rs.getString("STY1");
				String sty2 = rs.getString("STY2");
				String rl = rs.getString("RL");
				Concept source = conceptSchemeMap.get(sty1);
				Concept target = conceptSchemeMap.get(sty2);
				Concept predicate = predicateMap.get(rl);
				if (predicate == null) {
					logger.warn("null predicate concept {}", rl);
				}

				/*
				 * check against following entries in table. e.g.
				 * Acquired Abnormality	co-occurs_with	Acquired Abnormality
				 * We cannot have the same source and target for a relationship in neo4j
				 */
				if (source != null && target != null && !source.equals(target)) {
					ConceptRelationshipType conceptRelationshipType = getRelationshipType(rl);
					ConceptRelationshipInput input = new ConceptRelationshipInput().withSource(source)
							.withTarget(target).withPredicate(predicate)
							.withConceptRelationshipType(conceptRelationshipType)
							.withRelationshipCategory(RelationshipCategory.AUTHORITATIVE).withScore(Integer.MAX_VALUE);
					relationshipService.createRelationship(input);
				}
			}
			transaction.success();
		} catch (SQLException e) {
			logger.error("SQL exception creating concept scheme", e);
			transaction.failure();
		} finally {
			if (transaction != null) {
				transaction.finish();
			}
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				logger.warn("exception when closing resultset / statement in finally block", e);
			}
		}
	}

	private ConceptRelationshipType getRelationshipType(String rl) {
		ConceptRelationshipType conceptRelationshipType = ConceptRelationshipType.RELATED;

		if (rl.equalsIgnoreCase("isa")) {
			conceptRelationshipType = ConceptRelationshipType.HAS_BROADER_CONCEPT;
		}
		return conceptRelationshipType;
	}

	private void getFactualRelationships() throws SQLException {
		Transaction transaction = graphDatabaseService.beginTx();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery(GET_ALL_RELATIONSHIPS);
			int ctr = 0;
			String previousCui1 = "";
			Collection<Concept> sourceConcepts = null;
			while (rs.next()) {
				String cui1 = rs.getString("CUI1");
				String cui2 = rs.getString("CUI2");
				String rel = rs.getString("REL");
				String rela = rs.getString("RELA");
				if (!previousCui1.equals(cui1)) {
					// do not fetch source CUI till we are done with all relations for that source
					sourceConcepts = queryService.getConceptsByNotation(Domain.UMLS, cui1);
					previousCui1 = cui1;
				}
				Collection<Concept> targetConcepts = queryService.getConceptsByNotation(Domain.UMLS, cui2);
				ConceptRelationshipType conceptRelationshipType = getConceptRelationshipType(rel);
				Concept predicate = predicateMap.get(rela);
				for (Concept sourceConcept : sourceConcepts) {
					for (Concept targetConcept : targetConcepts) {
						ConceptRelationshipInput input = new ConceptRelationshipInput().withSource(sourceConcept)
								.withTarget(targetConcept).withConceptRelationshipType(conceptRelationshipType)
								.withRelationshipCategory(RelationshipCategory.AUTHORITATIVE)
								.withScore(Integer.MAX_VALUE);
						if (predicate != null) {
							input.setPredicate(predicate);
						}
						relationshipService.createRelationship(input);
					}
				}
				ctr++;
				if (ctr % batchSize == 0) {
					transaction.success();
					transaction.finish();
					transaction = graphDatabaseService.beginTx();
					logger.info("{} factual relationships added. Starting a new transaction.", ctr);
				}
			}
		} finally {
			if (transaction != null) {
				transaction.success();
				transaction.finish();
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	private void getCooccuranceRelationships() throws SQLException {
		Transaction transaction = graphDatabaseService.beginTx();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery(GET_ALL_CO_OCCURANCE_RELATIONSHIPS);
			int ctr = 0;
			String previousCui1 = "";
			Collection<Concept> sourceConcepts = null;
			while (rs.next()) {
				String cui1 = rs.getString("CUI1");
				String cui2 = rs.getString("CUI2");
				int cof = rs.getInt("COF");
				// do not fetch source CUI till we are done with all relations for that source
				if (!previousCui1.equals(cui1)) {
					sourceConcepts = queryService.getConceptsByNotation(Domain.UMLS, cui1);
					previousCui1 = cui1;
				}
				Collection<Concept> targetConcepts = queryService.getConceptsByNotation(Domain.UMLS, cui2);
				for (Concept sourceConcept : sourceConcepts) {
					for (Concept targetConcept : targetConcepts) {
						ConceptRelationshipInput input = new ConceptRelationshipInput().withSource(sourceConcept)
								.withTarget(targetConcept).withConceptRelationshipType(ConceptRelationshipType.RELATED)
								.withRelationshipCategory(RelationshipCategory.CO_OCCURANCE).withScore(cof);
						relationshipService.createRelationship(input);
					}
				}
				ctr++;
				if (ctr % batchSize == 0) {
					transaction.success();
					transaction.finish();
					transaction = graphDatabaseService.beginTx();
					logger.info("{} cooccurance relationships added. Starting a new transaction.", ctr);
				}
			}
		} finally {
			if (transaction != null) {
				transaction.success();
				transaction.finish();
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	private void getAllConcepts() throws SQLException {
		Transaction transaction = graphDatabaseService.beginTx();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery(GET_ALL_CONCEPTS);
			int ctr = 0;
			// start transaction;
			String previousCui = "";
			List<Label> labels = new ArrayList<Label>();
			List<Notation> notations = new ArrayList<Notation>();
			while (rs.next()) {
				String cui = rs.getString("CUI");
				if (ctr == 0) {
					previousCui = cui;
				}
				if (!cui.equals(previousCui)) {
					// create new concept
					if (notations.isEmpty()) {
						conceptService.createConcept(labels);
					} else {
						conceptService.createConcept(labels, notations);
					}
					labels.clear();
					notations.clear();
					previousCui = cui;
				}
				// get all data for same CUI
				String ts = rs.getString("TS");
				String isPref = rs.getString("ISPREF");
				String stt = rs.getString("STT");// TS, ISPREF, STT
				String str = rs.getString("STR");
				String lat = rs.getString("LAT");
				Language language = getLanguage(lat);
				Label label = null;
				if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
					// create preferred term
					label = labelService.createPreferredLabel(str, language);
				} else {
					label = labelService.createAlternateLabel(str, language);
				}
				labels.add(label);
				// create domain
				String sab = rs.getString("SAB");
				String code = rs.getString("CODE");
				Domain domain = getDomain(sab);
				Notation notation = notationService.createNotation(domain, code);
				notations.add(notation);
				ctr++;
				if (ctr % batchSize == 0) {
					transaction.success();
					transaction.finish();
					transaction = graphDatabaseService.beginTx();
					logger.info("{} concepts added. Starting a new transaction.", ctr);
				}
			}
		} finally {
			if (transaction != null) {
				transaction.success();
				transaction.finish();
			}
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		}
	}

	private Domain getDomain(String sab) {
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
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has been deprecated
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
		AQ 	Allowed qualifier
		CHD has child relationship in a Metathesaurus source vocabulary
		DEL Deleted concept
		PAR has parent relationship in a Metathesaurus source vocabulary
		QB 	can be qualified by.
		RB 	has a broader relationship
		RL 	the relationship is similar or "alike". the two concepts are similar or "alike". In the current edition of the Metathesaurus, most relationships with this attribute are mappings provided by a source, named in SAB and SL; hence concepts linked by this relationship may be synonymous, i.e. self-referential: CUI1 = CUI2. In previous releases, some MeSH Supplementary Concept relationships were represented in this way.
		RN 	has a narrower relationship
		RO 	has relationship other than synonymous, narrower, or broader
		RQ 	related and possibly synonymous.
		RU 	Related, unspecified
		SIB 	has sibling relationship in a Metathesaurus source vocabulary.
		SY 	source asserted synonymy.
		XR 	Not related, no mapping
		Empty relationship
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

}
