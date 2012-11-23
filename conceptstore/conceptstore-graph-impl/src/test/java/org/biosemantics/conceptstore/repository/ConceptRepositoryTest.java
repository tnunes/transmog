package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.InScheme;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.LabelType;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.LabelRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.NotationRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.test.TestGraphDatabaseFactory;

public class ConceptRepositoryTest {

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
	public void persistedConceptShouldBeRetrievableFromGraphDb() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Concept retrievedConcept = conceptRepositoryImpl.getById(concept.getId());
		assertEquals("retrieved Concept matches persisted one", concept, retrievedConcept);
		assertEquals("retrieved Concept type matches", concept.getType(), retrievedConcept.getType());
	}

	@Test
	public void labelsForConceptShouldBeRetrievable() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		LabelRepository labelRepositoryImpl = new LabelRepositoryImpl(graphDb);
		Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Label label = labelRepositoryImpl.create("text", "lang");
		conceptRepositoryImpl.hasLabel(concept.getId(), label.getId(), LabelType.PREFERRED,
				new String[] { "one", "two" });
		Collection<Label> foundLabels = concept.getLabels();
		assertNotNull("retrieved labels should not be null", foundLabels);
		assertEquals(1, foundLabels.size());
		for (Label foundLabel : foundLabels) {
			assertEquals(label, foundLabel);
			assertEquals("text", foundLabel.getText());
			assertEquals("lang", foundLabel.getLanguage());
		}
	}

	@Test
	public void persistedNotationsShouldBeRetrievable() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		NotationRepository notationRepositoryImpl = new NotationRepositoryImpl(graphDb);
		Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Notation notation = notationRepositoryImpl.create("src", "code");
		conceptRepositoryImpl.hasNotation(concept.getId(), notation.getId(), new String[] { "one", "two" });
		Collection<Notation> foundNotations = concept.getNotations();
		assertNotNull(foundNotations);
		assertEquals(1, foundNotations.size());
		for (Notation foundNotation : foundNotations) {
			assertEquals(notation, foundNotation);
			assertEquals("src", foundNotation.getSource());
			assertEquals("code", foundNotation.getCode());
		}
	}

	@Test
	public void allowDuplicateRelationships() {
		ConceptRepository conceptRepository = new ConceptRepositoryImpl(graphDb);
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT_SCHEME);
		HasRlsp hasRlsp = conceptRepository.hasRlsp(concept1.getId(), concept2.getId(), "xxx", new String[] {
				"Nirvana", "Aerosmith" });
		assertEquals("xxx", hasRlsp.getType());
		HasRlsp hasRlsp2 = conceptRepository.hasRlsp(concept1.getId(), concept2.getId(), "xxx", new String[] {
				"Nirvana", "Aerosmith" });
		assertNotSame(hasRlsp2, hasRlsp);

	}

	@Test
	public void duplicateRelationshipsShouldBeAvoided() {
		ConceptRepository conceptRepository = new ConceptRepositoryImpl(graphDb);
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT_SCHEME);
		HasRlsp hasRlsp = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx",
				new String[] { "Nirvana", "Aerosmith" });
		assertEquals("xxx", hasRlsp.getType());
		HasRlsp hasRlsp2 = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx",
				new String[] { "Nirvana", "Aerosmith" });
		assertEquals(hasRlsp2, hasRlsp);
	}

	@Test
	public void bidirectionalRelationshipsShouldNotBeAllowed() {
		ConceptRepository conceptRepository = new ConceptRepositoryImpl(graphDb);
		Concept concept1 = conceptRepository.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepository.create(ConceptType.CONCEPT_SCHEME);
		HasRlsp hasRlsp = conceptRepository.hasRlspIfNoneExists(concept1.getId(), concept2.getId(), "xxx",
				new String[] { "Nirvana", "Aerosmith" });
		assertEquals("xxx", hasRlsp.getType());
		HasRlsp hasRlsp2 = conceptRepository.hasRlspIfNoBidirectionalRlspExists(concept2.getId(), concept1.getId(),
				"xxx", new String[] { "Nirvana", "Aerosmith" });
		assertEquals(hasRlsp2, hasRlsp);
	}

	@Test
	public void duplicateHasLabelShouldBeAvoided() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		LabelRepository labelRepositoryImpl = new LabelRepositoryImpl(graphDb);
		Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Label label = labelRepositoryImpl.create("text", "lang");
		conceptRepositoryImpl.hasLabelIfNoneExists(concept.getId(), label.getId(), LabelType.PREFERRED, new String[] {
				"one", "two" });
		conceptRepositoryImpl.hasLabelIfNoneExists(concept.getId(), label.getId(), LabelType.PREFERRED, new String[] {
				"three", "four" });
		Collection<Label> foundLabels = concept.getLabels();
		assertNotNull("retrieved labels should not be null", foundLabels);
		assertEquals(1, foundLabels.size());
		for (Label foundLabel : foundLabels) {
			assertEquals(label, foundLabel);
			assertEquals("text", foundLabel.getText());
			assertEquals("lang", foundLabel.getLanguage());
		}

	}

	@Test
	public void labelsWithDifferentLabelTypesForSameConceptShouldNotBeAllowed() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		LabelRepository labelRepositoryImpl = new LabelRepositoryImpl(graphDb);
		Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Label label = labelRepositoryImpl.create("text", "lang");
		conceptRepositoryImpl.hasLabelIfNoneExists(concept.getId(), label.getId(), LabelType.PREFERRED, new String[] {
				"one", "two" });
		conceptRepositoryImpl.hasLabelIfNoneExists(concept.getId(), label.getId(), LabelType.ALTERNATE, new String[] {
				"three", "four" });
		Collection<Label> foundLabels = concept.getLabels();
		assertNotNull("retrieved labels should not be null", foundLabels);
		assertEquals(1, foundLabels.size());
		for (Label foundLabel : foundLabels) {
			assertEquals(label, foundLabel);
			assertEquals("text", foundLabel.getText());
			assertEquals("lang", foundLabel.getLanguage());
		}

	}

	@Test
	public void duplicateHasNotationShouldBeAvoided() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		NotationRepository notationRepositoryImpl = new NotationRepositoryImpl(graphDb);
		Concept concept = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Notation notation = notationRepositoryImpl.create("src", "code");
		conceptRepositoryImpl.hasNotationIfNoneExists(concept.getId(), notation.getId(), new String[] { "one", "two" });
		conceptRepositoryImpl.hasNotationIfNoneExists(concept.getId(), notation.getId(),
				new String[] { "three", "four" });
		Collection<Notation> foNotations = concept.getNotations();
		assertNotNull("retrieved labels should not be null", foNotations);
		assertEquals(1, foNotations.size());
		for (Notation notation2 : foNotations) {
			assertEquals(notation, notation2);
			assertEquals("src", notation2.getSource());
			assertEquals("code", notation2.getCode());
		}
	}

	@Test
	public void checkHasRlspsDoNotIncludeOtherRlspTypes() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		NotationRepository notationRepositoryImpl = new NotationRepositoryImpl(graphDb);
		Concept concept1 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepositoryImpl.create(ConceptType.CONCEPT_SCHEME);
		Concept concept3 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Notation notation = notationRepositoryImpl.create("src", "code");
		conceptRepositoryImpl
				.hasNotationIfNoneExists(concept1.getId(), notation.getId(), new String[] { "one", "two" });
		conceptRepositoryImpl.hasNotationIfNoneExists(concept2.getId(), notation.getId(), new String[] { "three",
				"four" });
		HasRlsp hasRlsp = conceptRepositoryImpl.hasRlspIfNoneExists(concept1.getId(), concept3.getId(), "123456",
				new String[] { "some1", "some2" });
		conceptRepositoryImpl.addInScheme(concept1.getId(), concept2.getId(), new String[] { "three", "four" });
		Collection<HasRlsp> hasRlsps = conceptRepositoryImpl.getAllHasRlspsForConcept(concept1.getId());
		assertEquals(1, hasRlsps.size());
		for (HasRlsp foundRlsp : hasRlsps) {
			assertEquals(hasRlsp, foundRlsp);
		}
	}

	@Test
	public void inSchemeIsAddedAndRetrievable() {
		ConceptRepository conceptRepositoryImpl = new ConceptRepositoryImpl(graphDb);
		NotationRepository notationRepositoryImpl = new NotationRepositoryImpl(graphDb);
		Concept concept1 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		Concept concept2 = conceptRepositoryImpl.create(ConceptType.CONCEPT_SCHEME);
		Notation notation = notationRepositoryImpl.create("src", "code");
		conceptRepositoryImpl
				.hasNotationIfNoneExists(concept1.getId(), notation.getId(), new String[] { "one", "two" });
		conceptRepositoryImpl.hasNotationIfNoneExists(concept2.getId(), notation.getId(), new String[] { "three",
				"four" });
		Concept concept3 = conceptRepositoryImpl.create(ConceptType.CONCEPT);
		conceptRepositoryImpl
				.hasNotationIfNoneExists(concept1.getId(), notation.getId(), new String[] { "one", "two" });
		conceptRepositoryImpl.hasNotationIfNoneExists(concept2.getId(), notation.getId(), new String[] { "three",
				"four" });
		conceptRepositoryImpl.hasRlspIfNoneExists(concept1.getId(), concept3.getId(), "123456", new String[] { "some1",
				"some2" });
		conceptRepositoryImpl.addInScheme(concept1.getId(), concept2.getId(), new String[] { "three", "four" });
		Collection<Concept> foundConceptSchemes = concept1.getInSchemes();
		assertEquals(1, foundConceptSchemes.size());
		for (Concept foundConceptScheme : foundConceptSchemes) {
			assertEquals(foundConceptScheme, concept2);
			assertNotSame(foundConceptScheme, concept3);
			assertNotSame(foundConceptScheme, concept1);
		}
	}
}
