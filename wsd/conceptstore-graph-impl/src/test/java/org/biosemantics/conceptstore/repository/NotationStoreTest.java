package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "../conceptstore-context.xml" })
public class NotationStoreTest {

	@Autowired
	private Neo4jTemplate template;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private NotationRepository notationRepository;

	@Test
	@Transactional
	public void persistedNotationShouldBeRetrievableFromGraphDb() {
		String source = "some src";
		String code = "some code";
		Notation notation = template.save(new Notation(source, code));
		Notation retrievedNotation = template.findOne(notation.getNodeId(), Notation.class);
		assertEquals("retrieved Notation matches persisted one", notation, retrievedNotation);
		assertEquals("retrieved Notation source matches", source, retrievedNotation.getSource());
		assertEquals("retrieved Notation code matches", code, retrievedNotation.getCode());
	}

	@Test
	@Transactional
	public void persistedNotationShouldBeRetrievableFromGraphDbUsingRepo() {
		String source = "some src";
		String code = "some code";
		Notation notation = notationRepository.save(new Notation(source, code));
		Notation retrievedNotation = notationRepository.findOne(notation.getNodeId());
		assertEquals("retrieved Notation matches persisted one", notation, retrievedNotation);
		assertEquals("retrieved Notation source matches", source, retrievedNotation.getSource());
		assertEquals("retrieved Notation code matches", code, retrievedNotation.getCode());
	}

	@Test
	@Transactional
	public void getAssociatedConceptForNotation() {
		String source = "some src";
		String code = "some code";
		Notation notation = notationRepository.save(new Notation(source, code));
		Concept concept = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept.addNotationIfNoneExists(template, notation, "somesrc");
		Notation retrievedNotation = notationRepository.findOne(notation.getNodeId());
		Iterable<Concept> concepts = retrievedNotation.getRelatedConcepts();
		for (Concept retrievedConcept : concepts) {
			assertEquals("retrieved Concept matches", concept, retrievedConcept);
		}
	}

	@Test
	@Transactional
	public void retrieveNotationFromRepository() {
		String source = "some src";
		String code = "some code";
		Notation notation = notationRepository.save(new Notation(source, code));
		Notation retrievedNotation = notationRepository.getNotation(source, code);
		assertEquals("retrieved Notation matches persisted one", notation, retrievedNotation);
		assertEquals("retrieved Notation source matches", source, retrievedNotation.getSource());
		assertEquals("retrieved Notation code matches", code, retrievedNotation.getCode());
	}

	@Test
	@Transactional
	public void duplicateNotationsShouldBeAvoided() {
		String source = "some src";
		String code = "some code";
		Notation notation1 = notationRepository.save(new Notation(source, code));
		Notation notation2 = notationRepository.save(new Notation(source, code));
		// just one node is created
		assertEquals("notations should be equal", notation1, notation2);
		assertEquals("only one notation should be inserted", 1, notationRepository.count());
	}
	
	@Test(expected=DataIntegrityViolationException.class)
	@Transactional
	public void duplicateNotationInsertionShouldFail() {
		String source = "some src";
		String code = "some code";
		Notation notation1 = notationRepository.save(new Notation(source, code));
		Notation notation2 = notationRepository.save(new Notation("src1", code));
		assertNotSame("notations should not be same", notation1, notation2);
		assertEquals("two notations should be inserted", 2, notationRepository.count());
		notation2.setSource(source);
		notationRepository.save(notation2);// fails with a DataIntegrityViolationException
	}
	
	@Test
	@Transactional
	public void duplicateCheckShouldBeCaseSensitive() {
		String source = "some src";
		String code = "some code";
		Notation notation1 = notationRepository.save(new Notation(source, code));
		Notation notation2 = notationRepository.save(new Notation(source.toUpperCase(), code));
		Notation notation3 = notationRepository.save(new Notation(source, code.toUpperCase()));
		assertNotSame("notations should not be same", notation1, notation2);
		assertNotSame("notations should not be same", notation1, notation3);
		assertEquals("three notations should be inserted", 3, notationRepository.count());
	}
	
	

	

}
