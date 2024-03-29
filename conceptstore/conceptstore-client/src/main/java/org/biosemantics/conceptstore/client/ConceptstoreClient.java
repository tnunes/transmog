package org.biosemantics.conceptstore.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.biosemantics.conceptstore.repository.TraversalRepository;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.LabelRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.NotationRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.TraversalRepositoryImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptstoreClient {

	private static final String DB_PATH = "/Users/bhsingh/code/neo4j-community-1.8/data/graph.db";
	private GraphDatabaseService graphDb;
	private ConceptRepository conceptRepository;
	private LabelRepository labelRepository;
	private NotationRepository notationRepository;
	private TraversalRepository traversalRepository;

	/**
	 * Initializes the conceptstore project
	 * 
	 * @param dbPath
	 *            Absolute path to the the database folder, generally the
	 *            "graph.db" folder. e.g. /Users/bhsingh/graph.db
	 */
	public void init(String dbPath) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook(graphDb);
		this.conceptRepository = new ConceptRepositoryImpl(graphDb);
		this.labelRepository = new LabelRepositoryImpl(graphDb);
		this.notationRepository = new NotationRepositoryImpl(graphDb);
		this.traversalRepository = new TraversalRepositoryImpl(graphDb, this.conceptRepository);
	}

	/**
	 * Initializes the conceptstore project with graph database configuration.
	 * For documentation on configuration see
	 * 
	 * @see http://docs.neo4j.org/chunked/stable/configuration-introduction.html
	 * @param dbPath
	 *            Absolute path to the the database folder, generally the
	 *            "graph.db" folder. e.g. /Users/bhsingh/graph.db
	 */
	public void initWithConfig(String dbPath) {
		Map<String, String> config = MapUtil.stringMap("neostore.propertystore.db.index.keys.mapped_memory", "5M",
				"neostore.propertystore.db.index.mapped_memory", "5M", "neostore.nodestore.db.mapped_memory", "200M",
				"neostore.relationshipstore.db.mapped_memory", "1000M", "neostore.propertystore.db.mapped_memory",
				"1000M", "neostore.propertystore.db.strings.mapped_memory", "200M");
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbPath).setConfig(config).newGraphDatabase();
		registerShutdownHook(graphDb);
		this.conceptRepository = new ConceptRepositoryImpl(graphDb);
		this.labelRepository = new LabelRepositoryImpl(graphDb);
		this.notationRepository = new NotationRepositoryImpl(graphDb);
		this.traversalRepository = new TraversalRepositoryImpl(graphDb, this.conceptRepository);
	}

	public void printNotationsForLabel(String labelText) {
		Collection<Label> labels = labelRepository.getByText(labelText);
		for (Label label : labels) {
			Collection<Concept> concepts = label.getRelatedConcepts();
			for (Concept concept : concepts) {
				for (Notation notation : concept.getNotations()) {
					System.out.println(notation);
				}
			}
		}
	}

	/**
	 * API requested by Erik in release 1.7
	 * 
	 * @param umlsCui
	 */
	public void printAllRelationshipsForNotationCode(String umlsCui) {
		Collection<Notation> notations = notationRepository.getByCode(umlsCui);
		for (Notation notation : notations) {
			Collection<Concept> concepts = notation.getRelatedConcepts();
			for (Concept concept : concepts) {
				Collection<HasRlsp> hasRlsps = conceptRepository.getAllHasRlspsForConcept(concept.getId());
				logger.debug("{} relationships found for cui: {}", new Object[] { hasRlsps.size(), umlsCui });
				for (HasRlsp hasRlsp : hasRlsps) {
					// logger.debug("text representation of relationship: {}",
					// hasRlsp);
					Concept otherConcept = hasRlsp.getOtherConcept(concept.getId());
					long start = System.currentTimeMillis();
					logger.debug("{}-{}-{}",
							new Object[] { concept.getLabels(), hasRlsp.getLabels(), otherConcept.getLabels() });
					logger.info("{}", (System.currentTimeMillis() - start));
				}
			}
		}
	}

	/**
	 * API requested by Erik in release 1.7
	 * 
	 * @param text
	 * @return
	 */
	public void printPredicateChildren(String text) {
		Collection<Label> labels = labelRepository.getByText(text);
		Set<Concept> concepts = new HashSet<Concept>();
		for (Label label : labels) {
			for (Concept concept : label.getRelatedConcepts()) {
				if (concept.getType() == ConceptType.PREDICATE) {
					concepts.add(concept);
				}
			}
		}
		logger.debug("{} predicate concepts found for label '{}' concept are {}", new Object[] { concepts.size(), text,
				concepts });
		Collection<Long> ids = new HashSet<Long>();
		for (Concept concept : concepts) {
			ids.addAll(conceptRepository.getAllChildPredicates(concept.getId()));
		}
		logger.debug("{} are children for {} ", new Object[] { ids, text });
	}

	/**
	 * API requested by Erik in release 1.7
	 * 
	 * @param cui1
	 * @param cui2
	 * @param predicatesToFollow
	 * @param maxDepth
	 * @return
	 */
	public Iterable<Path> getShortestPaths(String cui1, String cui2, Collection<Long> predicatesToFollow, int maxDepth) {
		Long startConceptId = null;
		Collection<Notation> startNotations = notationRepository.getByCode(cui1);
		for (Notation notation : startNotations) {
			for (Concept concept : notation.getRelatedConcepts()) {
				if (concept.getType() != ConceptType.PREDICATE) {
					startConceptId = concept.getId();
					break;
				}
			}
		}
		Long endConceptId = null;
		Collection<Notation> endNotations = notationRepository.getByCode(cui2);
		for (Notation notation : endNotations) {
			for (Concept concept : notation.getRelatedConcepts()) {
				if (concept.getType() != ConceptType.PREDICATE) {
					endConceptId = concept.getId();
					break;
				}
			}
		}
		return traversalRepository.findShortestPath(startConceptId, endConceptId, predicatesToFollow, maxDepth);
	}

	/**
	 * Requested by Kang in 1.9
	 * 
	 * @param cui1
	 * @param cui2
	 * @param maxDepth
	 * @return
	 */
	public Iterable<Path> getAllShortestPaths(String cui1, String cui2, int maxDepth) {
		Long startConceptId = null;
		Collection<Notation> startNotations = notationRepository.getByCode(cui1);
		for (Notation notation : startNotations) {
			for (Concept concept : notation.getRelatedConcepts()) {
				if (concept.getType() != ConceptType.PREDICATE) {
					startConceptId = concept.getId();
					break;
				}
			}
		}
		Long endConceptId = null;
		Collection<Notation> endNotations = notationRepository.getByCode(cui2);
		for (Notation notation : endNotations) {
			for (Concept concept : notation.getRelatedConcepts()) {
				if (concept.getType() != ConceptType.PREDICATE) {
					endConceptId = concept.getId();
					break;
				}
			}
		}

		return traversalRepository.findShortestPath(startConceptId, endConceptId, maxDepth);

	}

	/**
	 * 
	 */
	public void printAllPredicates() {
		Collection<Concept> concepts = conceptRepository.getByType(ConceptType.PREDICATE);
		for (Concept concept : concepts) {
			for (Label label : concept.getLabels()) {
				logger.debug("{}", label.getText());
			}
		}

	}

	/**
	 * Registers a shutdown hook for the Neo4j instance so that it shuts down
	 * nicely when the VM exits (even if you "Ctrl-C" the running example before
	 * it's completed)
	 * 
	 * @param graphDb
	 */
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	public static void main(String[] args) {
		ConceptstoreClient client = new ConceptstoreClient();
		client.initWithConfig(DB_PATH);
		client.printAllPredicates();
		client.printNotationsForLabel("cold");
		client.printAllRelationshipsForNotationCode("C0234192");
		client.printPredicateChildren("associated_with");
		// C0009450 (11032) -- occures_before (424) (417/532) --c1998063
		// (2966926) -- finding_site_of (978) -- C0039493 (43008)
		Set<Long> predicateIds = new HashSet<Long>();
		predicateIds.add(424L);
		predicateIds.add(978L);
		Iterable<Path> paths = client.getShortestPaths("C0009450", "C0039493", predicateIds, 3);
		for (Path path : paths) {
			logger.debug("{} | {}", new Object[] { path.length(), path.toString() });
		}
		Iterable<Path> paths2 = client.getAllShortestPaths("C0009450", "C0039493", 3);
		for (Path path : paths2) {
			logger.debug("{} | {}", new Object[] { path.length(), path.toString() });
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(ConceptstoreClient.class);

}