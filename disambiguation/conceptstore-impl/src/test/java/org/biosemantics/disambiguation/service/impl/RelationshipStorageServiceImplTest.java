package org.biosemantics.disambiguation.service.impl;

import junit.framework.Assert;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.conceptstore.common.domain.RelationshipCategory;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.RelationshipStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.RelationshipImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RelationshipStorageServiceImplTest extends AbstractTransactionalDataSource {

	private static final String PREFERRED_TXT = "PREFERRED";
	@Autowired
	private RelationshipStorageService relationshipStorageService;
	@Autowired
	private ConceptStorageService conceptStorageService;
	private Concept sourceConcept;
	private Concept targetConcept;
	private Concept predicate;

	@Before
	public void init() {
		ConceptImpl conceptImpl = new ConceptImpl();
		LabelImpl labelImpl = new LabelImpl(PREFERRED_TXT, LanguageImpl.EN);
		conceptImpl.addLabelByType(LabelType.PREFERRED, labelImpl);
		sourceConcept = conceptStorageService.createConcept(ConceptType.CONCEPT, conceptImpl);
		targetConcept = conceptStorageService.createConcept(ConceptType.CONCEPT, conceptImpl);
		predicate = conceptStorageService.createConcept(ConceptType.CONCEPT, conceptImpl);
	}

	@Test
	public void testCreateRelationship() {
		RelationshipImpl relationshipImpl = new RelationshipImpl();
		relationshipImpl.setStartConcept(sourceConcept);
		relationshipImpl.setEndConcept(targetConcept);
		relationshipImpl.setPredicate(predicate);
		relationshipImpl.setConceptRelationshipType(ConceptRelationshipType.CLOSE_MATCH);
		relationshipImpl.setWeight(100);
		relationshipImpl.setRelationshipCategory(RelationshipCategory.AUTHORITATIVE);
		relationshipStorageService.createRelationship(relationshipImpl);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSelfRelationship() {
		RelationshipImpl relationshipImpl = new RelationshipImpl();
		relationshipImpl.setStartConcept(sourceConcept);
		relationshipImpl.setEndConcept(sourceConcept);
		relationshipImpl.setPredicate(predicate);
		relationshipImpl.setConceptRelationshipType(ConceptRelationshipType.CLOSE_MATCH);
		relationshipImpl.setWeight(100);
		relationshipImpl.setRelationshipCategory(RelationshipCategory.AUTHORITATIVE);
		relationshipStorageService.createRelationship(relationshipImpl);
	}

	@Test
	public void testGetRelationship() {
		RelationshipImpl relationshipImpl = new RelationshipImpl();
		relationshipImpl.setStartConcept(sourceConcept);
		relationshipImpl.setEndConcept(targetConcept);
		relationshipImpl.setPredicate(predicate);
		relationshipImpl.setConceptRelationshipType(ConceptRelationshipType.CLOSE_MATCH);
		relationshipImpl.setWeight(100);
		relationshipImpl.setRelationshipCategory(RelationshipCategory.AUTHORITATIVE);
		Relationship relationship = relationshipStorageService.createRelationship(relationshipImpl);
		Relationship relationship2 = relationshipStorageService.createRelationship(relationshipImpl);
		Relationship found = relationshipStorageService.getRelationshipByUuid(relationship.getUuid());
		Assert.assertEquals(found, relationship);
		Assert.assertNotSame(found, relationship2);
	}

	@Test
	public void testCreateMultipleRelationship() {
		RelationshipImpl relationshipImpl = new RelationshipImpl();
		relationshipImpl.setStartConcept(sourceConcept);
		relationshipImpl.setEndConcept(targetConcept);
		relationshipImpl.setPredicate(predicate);
		relationshipImpl.setConceptRelationshipType(ConceptRelationshipType.CLOSE_MATCH);
		relationshipImpl.setWeight(100);
		relationshipImpl.setRelationshipCategory(RelationshipCategory.AUTHORITATIVE);
		Relationship relationship1 = relationshipStorageService.createRelationship(relationshipImpl);
		Relationship relationship2 = relationshipStorageService.createRelationship(relationshipImpl);
		Relationship relationship3 = relationshipStorageService.createRelationship(relationshipImpl);
		Assert.assertNotSame(relationship1, relationship2);
		Assert.assertNotSame(relationship1, relationship3);
		Assert.assertNotSame(relationship2, relationship3);
	}
	
	

}
