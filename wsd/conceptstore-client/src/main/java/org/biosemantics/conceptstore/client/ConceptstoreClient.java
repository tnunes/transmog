package org.biosemantics.conceptstore.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.LabelRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.NotationRepositoryImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptstoreClient {

	private static final String DB_PATH = "/Users/bhsingh/code/neo4j-community-1.8/data/graph.db";

	private GraphDatabaseService graphDb;
	private ConceptRepository conceptRepository;
	private LabelRepository labelRepository;
	private NotationRepository notationRepository;

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
		Map<String, String> config = new HashMap<String, String>();
		config.put("neostore.nodestore.db.mapped_memory", "10M");
		config.put("string_block_size", "60");
		config.put("array_block_size", "300");
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbPath).setConfig(config).newGraphDatabase();
		registerShutdownHook(graphDb);
		this.conceptRepository = new ConceptRepositoryImpl(graphDb);
		this.labelRepository = new LabelRepositoryImpl(graphDb);
		this.notationRepository = new NotationRepositoryImpl(graphDb);
	}

	public void doSomething() {
		Collection<Label> labels = labelRepository.getByText("cold");
		for (Label label : labels) {
			Collection<Concept> concepts = label.getRelatedConcepts();
			for (Concept concept : concepts) {
				for (Notation notation : concept.getNotations()) {
					System.out.println(notation);
				}
			}
		}
		
	}
	
	public void printAllPredicates() {
		Collection<Concept> concepts = conceptRepository.getByType(ConceptType.PREDICATE);
		for (Concept concept : concepts) {
			for (Label label : concept.getLabels()) {
				logger.debug("{}", label.getText());
			}
		}
		
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	public static void main(String[] args) {
		ConceptstoreClient client = new ConceptstoreClient();
		client.init(DB_PATH);
		client.doSomething();
		client.printAllPredicates();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ConceptstoreClient.class);

}