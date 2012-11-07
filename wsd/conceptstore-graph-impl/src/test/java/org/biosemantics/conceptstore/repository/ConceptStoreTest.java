package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.HasLabel;
import org.biosemantics.conceptstore.domain.HasNotation;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.LabelType;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.RlspType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "../conceptstore-context.xml" })
public class ConceptStoreTest {

	@Autowired
	private Neo4jTemplate template;

	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private LabelRepository labelRepository;
	@Autowired
	private NotationRepository notationRepository;

	@Test
	@Transactional
	public void persistedConceptShouldBeRetrievableFromGraphDb() {
		Concept concept = template.save(new Concept(ConceptType.CONCEPT));
		Concept retrievedConcept = template.findOne(concept.getNodeId(), Concept.class);
		assertEquals("retrieved Concept matches persisted one", concept, retrievedConcept);
		assertEquals("retrieved Concept type matches", concept.getType(), retrievedConcept.getType());
	}

	@Test
	@Transactional
	public void persistedConceptShouldBeRetrievableFromGraphDbUsingRepo() {
		Concept concept = conceptRepository.save(new Concept(ConceptType.PREDICATE));
		Concept retrievedConcept = conceptRepository.findOne(concept.getNodeId());
		assertEquals("retrieved concept matches persisted one", concept, retrievedConcept);
		assertEquals("retrieved concept type matches", concept.getType(), retrievedConcept.getType());
	}

	@Test
	@Transactional
	public void labelsForConceptShouldBeRetrievable() {
		Concept concept = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Label label1 = labelRepository.save(new Label("label1", "ENG"));
		concept.addLabelIfNoneExists(template, label1, LabelType.PREFERRED, "someSource");
		Concept retrievedConcept = conceptRepository.findOne(concept.getNodeId());
		Iterable<Label> labels = retrievedConcept.getLabels();
		for (Label retrievedLabel : labels) {
			assertEquals("label name not matched ", label1.getText(), retrievedLabel.getText());
		}
	}

	@Test
	@Transactional
	public void persistedNotationsShouldBeRetrievable() {
		Concept concept = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Notation notation = notationRepository.save(new Notation("source", "c123456$#"));
		concept.addNotationIfNoneExists(template, notation, "someSource");
		Concept retrievedConcept = conceptRepository.findOne(concept.getNodeId());
		Iterable<Notation> notations = retrievedConcept.getNotations();
		for (Notation retrievedNotation : notations) {
			assertEquals("notation code not matched ", retrievedNotation.getCode(), notation.getCode());
		}
	}

	@Test
	@Transactional
	public void allowDuplicateRelationships() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Concept concept2 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept1.addRelationship(template, concept2, "relationshipType", 0, "");
		concept1.addRelationship(template, concept2, "relationshipType", 0, "");
		Iterable<Concept> retrievedConcepts = conceptRepository.getRelatedConcepts(concept1);
		int ctr = 0;
		for (Concept concept : retrievedConcepts) {
			ctr++;
		}
		assertEquals("duplicate relationships between same concepts ", 2, ctr);
	}

	@Test
	@Transactional
	public void duplicateRelationshipsShouldBeAvoided() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Concept concept2 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept1.addRelationshipIfNoneExists(template, concept2, "135", 0, "");
		concept1.addRelationshipIfNoneExists(template, concept2, "135", 0, "");
		Iterable<Concept> retrievedConcepts = conceptRepository.getRelatedConcepts(concept1);
		int ctr = 0;
		for (Concept concept : retrievedConcepts) {
			ctr++;
		}
		assertEquals("duplicate relationships between same concepts ", 1, ctr);
	}

	@Test
	@Transactional
	public void bidirectionalRelationshipsShouldNotBeAllowed() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Concept concept2 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept1.addRelationshipIfNoBidirectionalRlspExists(template, concept2, "135", 0, "");
		concept2.addRelationshipIfNoBidirectionalRlspExists(template, concept1, "135", 0, "");
		Iterable<Concept> retrievedConcepts = conceptRepository.getRelatedConcepts(concept1);
		int ctr = 0;
		for (Concept concept : retrievedConcepts) {
			ctr++;
		}
		assertEquals("duplicate relationships between same concepts ", 1, ctr);
	}

	@Test
	@Transactional
	public void bidirectionalRelationshipsShouldNotBeAllowed2() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Concept concept2 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept1.addRelationshipIfNoBidirectionalRlspExists(template, concept2, "135", 0, "");
		concept1.addRelationshipIfNoBidirectionalRlspExists(template, concept2, "135", 0, "");
		Iterable<Concept> retrievedConcepts = conceptRepository.getRelatedConcepts(concept1);
		int ctr = 0;
		for (Concept concept : retrievedConcepts) {
			ctr++;
		}
		assertEquals("duplicate relationships between same concepts ", 1, ctr);
	}

	@Test
	@Transactional
	public void duplicateHasLabelShouldBeAvoided() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Label label1 = labelRepository.save(new Label("text", "lang"));
		HasLabel hasLabel1 = concept1.addLabelIfNoneExists(template, label1, LabelType.ALTERNATE, "src1");
		HasLabel hasLabel2 = concept1.addLabelIfNoneExists(template, label1, LabelType.ALTERNATE, "src1");
		assertEquals("same hasLabel", hasLabel1, hasLabel2);

	}

	@Test
	@Transactional
	public void duplicateHasLabelRlspsShouldBeAvoided() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Label label1 = labelRepository.save(new Label("text", "lang"));
		HasLabel hasLabel1 = concept1.addLabelIfNoneExists(template, label1, LabelType.ALTERNATE, "src1");
		HasLabel hasLabel2 = concept1.addLabelIfNoneExists(template, label1, LabelType.ALTERNATE, "src2");
		assertEquals("same hasLabel", hasLabel1, hasLabel2);
		Iterable<HasLabel> hasLabels = template.getRelationshipsBetween(concept1, label1, HasLabel.class, "HAS_LABEL");
		int ctr = 0;
		for (HasLabel hasLabel : hasLabels) {
			ctr++;
		}
		assertEquals("duplicate relationships between concept and label ", 1, ctr);
	}

	@Test
	@Transactional
	public void labelsWithDifferentLabelTypesForSameConceptShouldNotBeAllowed() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Label label1 = labelRepository.save(new Label("text", "lang"));
		HasLabel hasLabel1 = concept1.addLabelIfNoneExists(template, label1, LabelType.ALTERNATE, "src1");
		HasLabel hasLabel2 = concept1.addLabelIfNoneExists(template, label1, LabelType.PREFERRED, "src2");
		assertNotSame("not same hasLabel", hasLabel1, hasLabel2);
		Iterable<HasLabel> hasLabels = template.getRelationshipsBetween(concept1, label1, HasLabel.class, "HAS_LABEL");
		int ctr = 0;
		for (HasLabel hasLabel : hasLabels) {
			ctr++;
		}
		assertEquals("2 relationships between concept and label ", 1, ctr);
	}

	@Test
	@Transactional
	public void duplicateHasNotationRlspsShouldBeAvoided() {
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		Notation notation = notationRepository.save(new Notation("assdds", "asdasd"));
		HasNotation hasNotation1 = concept1.addNotationIfNoneExists(template, notation, "src 1");
		HasNotation hasNotation2 = concept1.addNotationIfNoneExists(template, notation, "src 1");
		assertEquals("same hasNotation", hasNotation1, hasNotation2);
		Iterable<HasNotation> hasNotations = template.getRelationshipsBetween(concept1, notation, HasNotation.class,
				"HAS_NOTATION");
		int ctr = 0;
		for (HasNotation hasNotation : hasNotations) {
			ctr++;
		}
		assertEquals("duplicate relationships between concept and notation ", 1, ctr);
	}

	@Test
	@Transactional
	public void multipleSourcesMustBeStoredAndRetrieved() {
		String[] sources = new String[] { "src1", "src2", "src3" };
		String source = "some source";
		String code = "some code";
		Notation notation1 = notationRepository.save(new Notation(source, code));
		Label label1 = labelRepository.save(new Label("text", "lang"));
		Concept concept1 = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept1.addLabelIfNoneExists(template, label1, LabelType.PREFERRED, sources);
		concept1.addNotationIfNoneExists(template, notation1, sources);
		HasLabel hasLabel = template.getRelationshipBetween(concept1, label1, HasLabel.class,
				RlspType.HAS_LABEL.toString());
		assertEquals(hasLabel.getSources().size(), 3);
		for (String string : sources) {
			assertTrue("label must contain source", hasLabel.getSources().contains(string));
		}
		HasNotation hasNotation = template.getRelationshipBetween(concept1, notation1, HasNotation.class,
				RlspType.HAS_NOTATION.toString());
		assertEquals(hasNotation.getSources().size(), 3);
		for (String string : sources) {
			assertTrue("notation must contain source", hasNotation.getSources().contains(string));
		}
	}

}
