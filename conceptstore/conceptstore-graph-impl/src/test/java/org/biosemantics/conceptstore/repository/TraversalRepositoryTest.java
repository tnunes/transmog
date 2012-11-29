package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.LabelType;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.LabelRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.NotationRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.TraversalRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Path;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.test.TestGraphDatabaseFactory;

public class TraversalRepositoryTest {

	protected GraphDatabaseService graphDb;

	/**
	 * Create temporary database for each unit test.
	 */
	@Before
	public void prepareTestDatabase() {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
	}

	/**
	 * Shutdown the database.
	 */
	@After
	public void destroyTestDatabase() {
		graphDb.shutdown();
	}

	@Test
	public void startWithConfiguration() {
		// START SNIPPET: startDbWithConfig
		Map<String, String> config = new HashMap<String, String>();
		config.put("neostore.nodestore.db.mapped_memory", "10M");
		config.put("string_block_size", "60");
		config.put("array_block_size", "300");
		GraphDatabaseService db = new ImpermanentGraphDatabase(config);
		// END SNIPPET: startDbWithConfig
		db.shutdown();
	}

	@Test
	public void shortestPathsShouldBeRetrievable() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		TraversalRepository traversalRepository = new TraversalRepositoryImpl(graphDb, conceptRepositoryImpl);
		Concept concept1 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Concept concept3 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Concept concept4 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		conceptRepositoryImpl.hasRlsp(concept1.getId(), concept2.getId(), "12", "12");
		conceptRepositoryImpl.hasRlsp(concept2.getId(), concept3.getId(), "23", "23");
		conceptRepositoryImpl.hasRlsp(concept3.getId(), concept4.getId(), "34", "34");
		Iterable<Path> paths = traversalRepository.findShortestPath(concept1.getId(), concept4.getId(), 3);
		assertNotNull(paths);
		for (Path path : paths) {
			assertEquals(3, path.length());
		}
	}

}
