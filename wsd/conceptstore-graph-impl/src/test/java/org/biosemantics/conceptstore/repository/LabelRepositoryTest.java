package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

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

public class LabelRepositoryTest {
	protected GraphDatabaseService graphDb;
	protected LabelRepository labelRepository;
	protected ConceptRepository conceptRepository;

	/**
	 * Create temporary database for each unit test.
	 */
	@Before
	public void prepareTestDatabase() {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		labelRepository = new LabelRepositoryImpl(graphDb);
		conceptRepository = new ConceptRepositoryImpl(graphDb);
	}

	/**
	 * Shutdown the database.
	 */
	@After
	public void destroyTestDatabase() {
		graphDb.shutdown();
	}

	@Test
	public void persistedLabelShouldBeRetrievableFromGraphDb() {
		String text = "someText";
		String lang = "lang";

		Label label = labelRepository.create(text, lang);
		Label retrievedLabel = labelRepository.getById(label.getId());
		assertEquals("retrieved Label matches persisted one", label, retrievedLabel);
		assertEquals("retrieved Label text matches", text, retrievedLabel.getText());
		assertEquals("retrieved Label language matches", lang, retrievedLabel.getLanguage());
	}

	@Test
	public void getAssociatedConceptForLabel() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label = labelRepository.create(text, lang);
		Concept concept = conceptRepository.create(ConceptType.CONCEPT);
		conceptRepository.hasLabelIfNoneExists(concept.getId(), label.getId(), LabelType.HIDDEN, "dddd");
		Label retrievedLabel = labelRepository.getById(label.getId());
		Iterable<Concept> concepts = retrievedLabel.getRelatedConcepts();
		for (Concept retrievedConcept : concepts) {
			assertEquals("retrieved Concept matches", concept, retrievedConcept);
		}
	}

	@Test
	public void retrieveLabelFromRepository() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.create(text, lang);
		Collection<Label> retrievedLabels = labelRepository.getByText(text);
		Label retrieved = null;
		for (Label label : retrievedLabels) {
			retrieved = label;
		}
		assertEquals(1, retrievedLabels.size());
		assertEquals(label1, retrieved);
		assertEquals(text, retrieved.getText());
		assertEquals(lang, retrieved.getLanguage());
	}

	@Test
	public void duplicateLabelsShouldBeAvoided() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.getOrCreate(text, lang);
		Label label2 = labelRepository.getOrCreate(text, lang);
		// just one node is created
		assertEquals("labels should be equal", label1, label2);
	}

	@Test
	public void duplicateCheckShouldBeCaseSensitive() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.getOrCreate(text, lang);
		Label label2 = labelRepository.getOrCreate(text.toUpperCase(), lang);
		Label label3 = labelRepository.getOrCreate(text, lang);
		Label label4 = labelRepository.getOrCreate(text, lang.toUpperCase());
		assertNotSame("labels should not be same", label1, label2);
		assertEquals(label1, label3);
		assertNotSame("labels should not be same", label3, label4);
	}

}
