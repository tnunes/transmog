package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.disambiguation.service.local.ConceptRelationshipStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConceptRelationshipStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private ConceptRelationshipStorageServiceLocal conceptRelationshipStorageServiceLocal;

	@Autowired
	private ConceptStorageServiceLocal conceptStorageServiceLocal;

	@Test
	public void createRelationship() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.HYPOTHETICAL, 100);
		String uuid = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		Assert.assertNotNull(uuid);
	}

	@Test
	public void checkRelationshipExists() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.HYPOTHETICAL, 100);
		String uuid = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		Assert.assertNotNull(uuid);
		boolean exists = conceptRelationshipStorageServiceLocal.relationshipExists(conceptRelationshipImpl);
		Assert.assertTrue(exists);
	}

	@Test
	public void createDuplicateRelationship() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.HYPOTHETICAL, 100);
		boolean exists = conceptRelationshipStorageServiceLocal.relationshipExists(conceptRelationshipImpl);
		Assert.assertFalse(exists);
		conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		exists = conceptRelationshipStorageServiceLocal.relationshipExists(conceptRelationshipImpl);
		Assert.assertTrue(exists);
	}

	@Test
	public void checkDuplicateHasBroaderRelationship() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());

		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.HAS_BROADER_CONCEPT, ConceptRelationshipCategory.HYPOTHETICAL, 100, null);
		boolean exists = conceptRelationshipStorageServiceLocal.relationshipExists(conceptRelationshipImpl);
		Assert.assertFalse(exists);
		conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);

		ConceptRelationshipImpl oppositeRelationship = new ConceptRelationshipImpl(toUuid, fromUuid, null,
				SemanticRelationshipCategory.HAS_NARROWER_CONCEPT, ConceptRelationshipCategory.HYPOTHETICAL, 100, null);
		exists = conceptRelationshipStorageServiceLocal.relationshipExists(oppositeRelationship);
		Assert.assertTrue(exists);
	}

	@Test
	public void checkDuplicateHasNarrowerRelationship() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());

		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.HAS_NARROWER_CONCEPT, ConceptRelationshipCategory.AUTHORITATIVE, 100, null);
		boolean exists = conceptRelationshipStorageServiceLocal.relationshipExists(conceptRelationshipImpl);
		Assert.assertFalse(exists);
		conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);

		ConceptRelationshipImpl oppositeRelationship = new ConceptRelationshipImpl(toUuid, fromUuid, null,
				SemanticRelationshipCategory.HAS_BROADER_CONCEPT, ConceptRelationshipCategory.AUTHORITATIVE, 100, null);
		exists = conceptRelationshipStorageServiceLocal.relationshipExists(oppositeRelationship);
		Assert.assertTrue(exists);
	}

	@Test
	public void testDuplicateRelationshipCreation() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());

		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.HAS_NARROWER_CONCEPT, ConceptRelationshipCategory.AUTHORITATIVE, 100, null);
		String uuid1 = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		String uuid2 = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		String uuid3 = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		Assert.assertNotSame(uuid1, uuid2);
		Assert.assertNotSame(uuid2, uuid3);
		Assert.assertNotSame(uuid1, uuid3);
	}
}
