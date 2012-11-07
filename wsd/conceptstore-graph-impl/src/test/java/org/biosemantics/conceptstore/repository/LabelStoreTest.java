package org.biosemantics.conceptstore.repository;

import static org.junit.Assert.*;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.LabelType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.LabelRepository;
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
public class LabelStoreTest {

	@Autowired
	private Neo4jTemplate template;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private LabelRepository labelRepository;

	@Test
	@Transactional
	public void persistedLabelShouldBeRetrievableFromGraphDb() {
		String text = "someText";
		String lang = "lang";
		Label label = template.save(new Label(text, lang));
		Label retrievedLabel = template.findOne(label.getNodeId(), Label.class);
		assertEquals("retrieved Label matches persisted one", label, retrievedLabel);
		assertEquals("retrieved Label text matches", text, retrievedLabel.getText());
		assertEquals("retrieved Label language matches", lang, retrievedLabel.getLanguage());
	}

	@Test
	@Transactional
	public void persistedLabelShouldBeRetrievableFromGraphDbUsingRepo() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label = labelRepository.save(new Label(text, lang));
		Label retrievedLabel = labelRepository.findOne(label.getNodeId());
		assertEquals("retrieved Label matches persisted one", label, retrievedLabel);
		assertEquals("retrieved Label text matches", text, retrievedLabel.getText());
		assertEquals("retrieved Label language matches", lang, retrievedLabel.getLanguage());
	}

	@Test
	@Transactional
	public void getAssociatedConceptForLabel() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label = labelRepository.save(new Label(text, lang));
		Concept concept = conceptRepository.save(new Concept(ConceptType.CONCEPT));
		concept.addLabelIfNoneExists(template, label, LabelType.HIDDEN, "somesrc");
		Label retrievedLabel = labelRepository.findOne(label.getNodeId());
		Iterable<Concept> concepts = retrievedLabel.getRelatedConcepts();
		for (Concept retrievedConcept : concepts) {
			assertEquals("retrieved Concept matches", concept, retrievedConcept);
		}
	}

	@Test
	@Transactional
	public void retrieveLabelFromRepository() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.save(new Label(text, lang));
		labelRepository.save(new Label("someOtherText1", lang));
		Label retrievedLabel = labelRepository.getLabel(text, lang);
		assertEquals("retrieved Label matches persisted one", label1, retrievedLabel);
		assertEquals("retrieved Label text matches", text, retrievedLabel.getText());
		assertEquals("retrieved Label language matches", lang, retrievedLabel.getLanguage());
	}

	@Test
	@Transactional
	public void duplicateLabelsShouldBeAvoided() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.save(new Label(text, lang));
		Label label2 = labelRepository.save(new Label(text, lang));
		// just one node is created
		assertEquals("labels should be equal", label1, label2);
		assertEquals("only one label should be inserted", 1, labelRepository.count());
	}

	@Test(expected = DataIntegrityViolationException.class)
	@Transactional
	public void duplicateLabelInsertionShouldFail() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.save(new Label(text, lang));
		Label label2 = labelRepository.save(new Label("text2", "lang2"));
		assertNotSame("labels should not be same", label1, label2);
		assertEquals("two labels should be inserted", 2, labelRepository.count());
		label2.setLanguage(lang);
		label2.setText(text);
		labelRepository.save(label2);// fails with a
										// DataIntegrityViolationException

	}

	@Test
	@Transactional
	public void duplicateCheckShouldBeCaseSensitive() {
		String text = "someOtherText";
		String lang = "somelang";
		Label label1 = labelRepository.save(new Label(text, lang));
		Label label2 = labelRepository.save(new Label(text.toUpperCase(), lang));
		Label label3 = labelRepository.save(new Label(text, lang.toUpperCase()));
		assertNotSame("labels should not be same", label1, label2);
		assertNotSame("labels should not be same", label1, label3);
		assertEquals("three labels should be inserted", 3, labelRepository.count());

	}

}
