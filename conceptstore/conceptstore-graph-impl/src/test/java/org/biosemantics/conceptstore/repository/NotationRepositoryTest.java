package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.repository.impl.ConceptRepositoryImpl;
import org.biosemantics.conceptstore.repository.impl.NotationRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

public class NotationRepositoryTest {

	protected GraphDatabaseService graphDb;
	protected NotationRepository notationRepository;
	protected ConceptRepository conceptRepository;

	/**
	 * Create temporary database for each unit test.
	 */
	@Before
	public void prepareTestDatabase() {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		notationRepository = new NotationRepositoryImpl(graphDb);
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
	public void persistedNotationShouldBeRetrievable() {
		String source = "some src";
		String code = "some code";
		Notation notation = notationRepository.create(source, code);
		Notation retrievedNotation = notationRepository.getById(notation.getId());
		assertEquals("retrieved Notation matches persisted one", notation, retrievedNotation);
		assertEquals("retrieved Notation source matches", source, retrievedNotation.getSource());
		assertEquals("retrieved Notation code matches", code, retrievedNotation.getCode());
	}

	@Test
	public void getAssociatedConceptForNotation() {
		String source = "some src";
		String code = "some code";
		Notation notation = notationRepository.create(source, code);
		Concept concept = conceptRepository.create(ConceptType.CONCEPT);
		conceptRepository.hasNotationIfNoneExists(concept.getId(), notation.getId(), "src");
		Notation retrievedNotation = notationRepository.getById(notation.getId());
		Iterable<Concept> concepts = retrievedNotation.getRelatedConcepts();
		for (Concept retrievedConcept : concepts) {
			assertEquals("retrieved Concept matches", concept, retrievedConcept);
		}
	}
	
	@Test
	public void duplicateNotationsShouldBeAllowed() {
		String source = "some src";
		String code = "some code";
		Notation notation1 = notationRepository.create(source, code);
		Notation notation2 = notationRepository.create(source, code);
		// just one node is created
		assertNotSame("notations should be equal", notation1, notation2);
	}

	@Test
	public void duplicateNotationsShouldBeAvoided() {
		String source = "some src";
		String code = "some code";
		Notation notation1 = notationRepository.getOrCreate(source, code);
		Notation notation2 = notationRepository.getOrCreate(source, code);
		// just one node is created
		assertEquals("notations should be equal", notation1, notation2);
	}

	@Test
	public void duplicateCheckShouldBeCaseSensitive() {
		String source = "some src";
		String code = "some code";
		Notation notation1 = notationRepository.create(source, code);
		Notation notation2 = notationRepository.create(source, code.toUpperCase());
		Notation notation3 = notationRepository.create(source, code.toUpperCase());
		assertNotSame("notations should not be same", notation1, notation2);
		assertNotSame("notations should not be same", notation1, notation3);
	}

}
