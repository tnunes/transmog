/**
 * 
 */
package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.biosemantics.disambiguation.knowledgebase.AbstractTransactionalDataSource;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptService;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelService;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration( { "/knowledgebase-test-context.xml" })
public class ConceptServiceTest extends AbstractTransactionalDataSource {
	
	@Autowired
	private ConceptService conceptService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private NotationService notationService;
	

	/**
	 * Test method for {@link org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptServiceImpl#createConcept(java.util.Collection)}.
	 */
	@Test
	public void createConceptWithLabel() {
		Label prefLabel = labelService.createPreferredLabel("PREF_LABEl", Language.EN);
		Label altLabel = labelService.createAlternateLabel("ALT_LABEL", Language.EN);
		List<Label> labels  = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		conceptService.createConcept(labels);
	}

	/**
	 * Test method for {@link org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptServiceImpl#createConcept(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void createConceptWithLabelNotation() {
		Label prefLabel = labelService.createPreferredLabel("PREF_LABEl", Language.EN);
		Label altLabel = labelService.createAlternateLabel("ALT_LABEL", Language.EN);
		List<Label> labels  = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		Notation umlsNotation = notationService.createNotation(Domain.MTH, "C00345678");
		List<Notation> notations  = new ArrayList<Notation>();
		notations.add(umlsNotation);
		conceptService.createConcept(labels, notations);
	}

}
