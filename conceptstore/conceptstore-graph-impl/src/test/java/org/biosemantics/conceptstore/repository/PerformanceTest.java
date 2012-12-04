package org.biosemantics.conceptstore.repository;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.LabelType;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.LabelRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTest {

	private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
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

	@Test(timeout=20000)
	public void retrieveLabelsForLargeNoOfConnections() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		Concept rootConcept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		LabelRepository labelRepositoryImpl = new LabelRepositoryImpl(graphDb);
		Label label = labelRepositoryImpl.create("text", "lang");
		conceptRepositoryImpl.hasLabel(rootConcept.getId(), label.getId(), LabelType.PREFERRED, new String[] { "one",
				"two" });
		for (int i = 0; i < 1000; i++) {
			Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
			conceptRepositoryImpl.hasRlsp(rootConcept.getId(), concept.getId(), String.valueOf(i), String.valueOf(i));
		}
		long start = System.currentTimeMillis();
		Collection<Label> labels = rootConcept.getLabels();
		for (Label foundLabel : labels) {
			logger.info("{} {}", new Object[] { foundLabel.getLanguage(), foundLabel.getText() });
		}
		long end = System.currentTimeMillis();
		logger.info("{} (ms)", end-start);
	}

}
