package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.AbstractTransactionalDataSource;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptService;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelService;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationService;
import org.biosemantics.disambiguation.knowledgebase.service.QueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/knowledgebase-test-context.xml" })
public class QueryServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private QueryService queryService;
	@Autowired
	private ConceptService conceptService;
	@Autowired
	private NotationService notationService;
	@Autowired
	private LabelService labelService;

	@Test
	public void testGetByNotation() {
		// concept1
		Label prefLabel = labelService.createPreferredLabel("PREFERRED_LABEL", Language.EN);
		Label altLabel = labelService.createAlternateLabel("ALTERNATE LABEL", Language.EN);
		List<Label> labels = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		final String umls_code = "C00345678";
		Notation umlsNotation = notationService.createNotation(Domain.UMLS, umls_code);
		Notation spaceNotation = notationService.createNotation(Domain.CCPSS, "SPACE 1234");
		List<Notation> notations = new ArrayList<Notation>();
		notations.add(umlsNotation);
		notations.add(spaceNotation);
		Concept concept1 = conceptService.createConcept(labels, notations);

		prefLabel = labelService.createPreferredLabel("PREF_LABEL2", Language.EN);
		altLabel = labelService.createAlternateLabel("ALT_LABEL2", Language.EN);
		labels = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		Notation umlsNotation2 = notationService.createNotation(Domain.UMLS, umls_code + "0");
		Notation spaceNotation2 = notationService.createNotation(Domain.CCPSS, "SPACE 4321");
		notations = new ArrayList<Notation>();
		notations.add(umlsNotation2);
		notations.add(spaceNotation2);
		conceptService.createConcept(labels);

		Collection<Concept> concepts = queryService.getConceptsByNotationCode(umls_code);
		Assert.assertNotNull(concepts);
		Assert.assertFalse(concepts.isEmpty());
		Assert.assertEquals(1, concepts.size());
		// FIXME: relying on order here. need a way to clean the graph database before each run
		Concept retrieved = null;
		for (Concept concept : concepts) {
			retrieved = concept;
		}
		Assert.assertEquals(retrieved, concept1);
	}
}
