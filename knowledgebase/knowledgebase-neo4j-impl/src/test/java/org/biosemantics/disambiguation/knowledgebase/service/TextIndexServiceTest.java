package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.service.Label.LabelType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/knowledgebase-neo4j-impl-context.xml" })
public class TextIndexServiceTest {
	
	@Autowired
	private TextIndexService textIndexService;
	@Autowired
	private LabelFactory labelFactory;
	@Autowired
	private NotationFactory notationFactory;
	@Autowired
	private ConceptFactory conceptFactory;

	@Test
	public void testGetLabelsByText(){
		Label l1 = labelFactory.createPreferredLabel("l1", Language.EN);
		Label l2 = labelFactory.createAlternateLabel("l2", Language.EN);
		
		List<Label> labels = new ArrayList<Label>();
		labels.add(l1);
		labels.add(l2);
		conceptFactory.createConcept(labels);
		Collection<Label> foundLabels = textIndexService.getLabelsByText("l2");
		Assert.assertFalse(foundLabels.isEmpty());
		Assert.assertEquals(1, foundLabels.size());
		for (Label found : foundLabels) {
			Assert.assertEquals(found.getLabelType(), LabelType.ALTERNATE);
			Assert.assertEquals(found.getText(), "l2");
			Assert.assertEquals(found.getLanguage(), Language.EN);
		}
		
	}
	
	@Test
	public void testMultiWordLabels(){
		Label l1 = labelFactory.createPreferredLabel("The Borg", Language.EN);
		Label l2 = labelFactory.createAlternateLabel("The Federation", Language.EN);
		
		List<Label> labels = new ArrayList<Label>();
		labels.add(l1);
		labels.add(l2);
		conceptFactory.createConcept(labels);
		Collection<Label> foundLabels = textIndexService.getLabelsByText("The Federation");
		Assert.assertFalse(foundLabels.isEmpty());
		Assert.assertEquals(1, foundLabels.size());
		for (Label found : foundLabels) {
			Assert.assertEquals(found.getLabelType(), LabelType.ALTERNATE);
			Assert.assertEquals(found.getText(), "The Federation");
			Assert.assertEquals(found.getLanguage(), Language.EN);
		}
		
	}
	
	@Test
	public void testUnicodeLabels(){
		Label l1 = labelFactory.createPreferredLabel("Resistance is futile", Language.EN);
		Label l2 = labelFactory.createAlternateLabel("The Federation starship enterprise", Language.EN);
		
		List<Label> labels = new ArrayList<Label>();
		labels.add(l1);
		labels.add(l2);
		conceptFactory.createConcept(labels);
		Collection<Label> foundLabels = textIndexService.getLabelsByText("The Federation starship enterprise");
		Assert.assertFalse(foundLabels.isEmpty());
		Assert.assertEquals(1, foundLabels.size());
		for (Label found : foundLabels) {
			Assert.assertEquals(found.getLabelType(), LabelType.ALTERNATE);
			Assert.assertEquals(found.getText(), "The Federation starship enterprise");
			Assert.assertEquals(found.getLanguage(), Language.EN);
		}
		
	}
	
	@Test
	public void testGetNotationByText(){
		Notation n1 = notationFactory.createNotation(Domain.MTH, "1234567890");
		Notation n2 = notationFactory.createNotation(Domain.MTH, "ABCDEFGH");
		Label l1 = labelFactory.createPreferredLabel("Resistance is futile", Language.EN);
		Label l2 = labelFactory.createAlternateLabel("The Federation starship enterprise", Language.EN);
		List<Label> labels = new ArrayList<Label>();
		labels.add(l1);
		labels.add(l2);
		List<Notation> notations  = new ArrayList<Notation>();
		notations.add(n1);
		notations.add(n2);
		conceptFactory.createConcept(labels, notations);
		Collection<Notation> foundNotation = textIndexService.getNotationByCode(n2.getCode());
		Assert.assertFalse(foundNotation.isEmpty());
		Assert.assertEquals(1, foundNotation.size());
		for (Notation found : foundNotation) {
			Assert.assertEquals(found.getCode(), n2.getCode());
			Assert.assertEquals(found.getDomain(), Domain.MTH);
		}
	}
	
	@Test
	public void testGetLabelById(){
		Label l1 = labelFactory.createPreferredLabel("Resistance is futile", Language.EN);
		Label l2 = labelFactory.createAlternateLabel("The Federation starship enterprise", Language.EN);
		List<Label> labels = new ArrayList<Label>();
		labels.add(l1);
		labels.add(l2);
		conceptFactory.createConcept(labels);
		l1.getText();
		Label foundLabel = textIndexService.getLabelById(l1.getId());
		Assert.assertNotNull(foundLabel);
		Assert.assertEquals(foundLabel, l1);
		Assert.assertEquals(foundLabel.getText(), l1.getText());
		Assert.assertEquals(foundLabel.getLabelType(), LabelType.PREFERRED);
		Assert.assertEquals(foundLabel.getLanguage(), Language.EN);
	}
	
	@Test
	public void testGetConceptById(){
		Label l1 = labelFactory.createPreferredLabel("Resistance is futile", Language.EN);
		Label l2 = labelFactory.createAlternateLabel("The Federation starship enterprise", Language.EN);
		List<Label> labels = new ArrayList<Label>();
		labels.add(l1);
		labels.add(l2);
		Notation n1 = notationFactory.createNotation(Domain.MTH, "1234567890");
		Notation n2 = notationFactory.createNotation(Domain.MTH, "ABCDEFGH");
		List<Notation> notations  = new ArrayList<Notation>();
		notations.add(n1);
		notations.add(n2);
		Concept concept = conceptFactory.createConcept(labels, notations);
		Concept foundConcept = textIndexService.getConceptById(concept.getId());
		Assert.assertEquals(foundConcept, concept);
	}
	
	
}
