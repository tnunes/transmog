/**
 * 
 */
package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration( { "/knowledgebase-neo4j-impl-context.xml" })
public class ConceptFactoryUnitTest {
	
	@Autowired
	private ConceptFactory conceptFactory;
	@Autowired
	private LabelFactory labelFactory;
	@Autowired
	private NotationFactory notationFactory;
	

	/**
	 * Test method for {@link org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptFactoryImpl#createConcept(java.util.Collection)}.
	 */
	@Test
	public void createConceptWithLabel() {
		Label prefLabel = labelFactory.createPreferredLabel("PREF_LABEl", Language.EN);
		Label altLabel = labelFactory.createAlternateLabel("ALT_LABEL", Language.EN);
		List<Label> labels  = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		conceptFactory.createConcept(labels);
	}

	/**
	 * Test method for {@link org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptFactoryImpl#createConcept(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void createConceptWithLabelNotation() {
		Label prefLabel = labelFactory.createPreferredLabel("PREF_LABEl", Language.EN);
		Label altLabel = labelFactory.createAlternateLabel("ALT_LABEL", Language.EN);
		List<Label> labels  = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		Notation umlsNotation = notationFactory.createNotation(Domain.MTH, "C00345678");
		List<Notation> notations  = new ArrayList<Notation>();
		notations.add(umlsNotation);
		conceptFactory.createConcept(labels, notations);
	}

}
