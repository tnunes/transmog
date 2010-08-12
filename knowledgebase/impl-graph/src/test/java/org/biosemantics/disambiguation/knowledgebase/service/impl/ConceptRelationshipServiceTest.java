package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.biosemantics.disambiguation.knowledgebase.AbstractTransactionalDataSource;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipInput;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptService;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelService;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipCategory;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/knowledgebase-test-context.xml" })
public class ConceptRelationshipServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private ConceptService conceptService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private RelationshipService relationshipService;

	@Test
	public void createRelationship() {
		Label prefLabel = labelService.createPreferredLabel("PREF_LABEL", Language.EN);
		Label altLabel = labelService.createAlternateLabel("ALT_LABEL", Language.EN);
		List<Label> labels = new ArrayList<Label>();
		labels.add(prefLabel);
		labels.add(altLabel);
		Concept source = conceptService.createConcept(labels);
		Concept target = conceptService.createConcept(labels);
		Concept predicate = conceptService.createPredicate(labels);
		ConceptRelationshipInput conceptRelationshipInput = new ConceptRelationshipInput().withSource(source)
				.withTarget(target).withPredicate(predicate)
				.withConceptRelationshipType(ConceptRelationshipType.EXACT_MATCH)
				.withRelationshipCategory(RelationshipCategory.HYPOTHETICAL);
		ConceptRelationship conceptRelationship = relationshipService.createRelationship(conceptRelationshipInput);
		Assert.assertEquals(ConceptRelationshipType.EXACT_MATCH, conceptRelationship.getConceptRelationshipType());
		Assert.assertEquals(RelationshipCategory.HYPOTHETICAL, conceptRelationship.getRelationshipCategory());
		Assert.assertNotNull(conceptRelationship.getId());
		Assert.assertEquals(predicate.getId(), conceptRelationship.getPredicateConceptId());
	}
}
