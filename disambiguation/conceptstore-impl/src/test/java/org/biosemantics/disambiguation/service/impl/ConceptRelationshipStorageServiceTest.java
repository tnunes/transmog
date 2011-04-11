package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.Source.SourceType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.conceptstore.utils.domain.impl.SourceImpl;
import org.biosemantics.disambiguation.service.local.ConceptRelationshipStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.SourceStorageServiceLocal;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConceptRelationshipStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private ConceptRelationshipStorageServiceLocal conceptRelationshipStorageServiceLocal;

	@Autowired
	private ConceptStorageServiceLocal conceptStorageServiceLocal;

	@Autowired
	private SourceStorageServiceLocal sourceStorageServiceLocal;

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
	public void createRelationshipWithSources() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());

		String uuidSource1 = sourceStorageServiceLocal.createSource(new SourceImpl("http:/sdhfjdhfksdhkjhkjs",
				SourceType.RESOURCE));
		String uuidSource2 = sourceStorageServiceLocal.createSource(new SourceImpl("http:/sdhfjdhfksdhkjhkjs",
				SourceType.RESOURCE));
		String[] sources = new String[] { uuidSource1, uuidSource2 };
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.HYPOTHETICAL, 100, sources);
		String uuid = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		Assert.assertNotNull(uuid);
	}

	@Test
	public void checkRelationshipExists() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String uuidSource1 = sourceStorageServiceLocal.createSource(new SourceImpl("http:/sdhfjdhfksdhkjhkjs",
				SourceType.RESOURCE));
		String uuidSource2 = sourceStorageServiceLocal.createSource(new SourceImpl("http:/sdhfjdhfksdhkjhkjs",
				SourceType.RESOURCE));
		String[] sources = new String[] { uuidSource1, uuidSource2 };
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.HYPOTHETICAL, 100, sources);
		String uuid = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		Assert.assertNotNull(uuid);
		boolean exists = conceptRelationshipStorageServiceLocal.relationshipExists(conceptRelationshipImpl);
		Assert.assertTrue(exists);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createDuplicateRelationship() {
		String fromUuid = conceptStorageServiceLocal
				.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());
		String toUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, TestUtility.createFullConcept());

		String uuidSource1 = sourceStorageServiceLocal.createSource(new SourceImpl("http:/sdhfjdhfksdhkjhkjs",
				SourceType.RESOURCE));
		String uuidSource2 = sourceStorageServiceLocal.createSource(new SourceImpl("http:/sdhfjdhfksdhkjhkjs",
				SourceType.RESOURCE));
		String[] sources = new String[] { uuidSource1, uuidSource2 };
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(fromUuid, toUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.HYPOTHETICAL, 100, sources);
		String uuid = conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
		Assert.assertNotNull(uuid);
		conceptRelationshipStorageServiceLocal.createRelationship(conceptRelationshipImpl);
	}
}
