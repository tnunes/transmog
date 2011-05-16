package org.biosemantics.disambiguation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.common.service.ConceptRelationshipStorageService;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.service.local.AlgorithmServiceLocal;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;

public class AlgorithmServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	private ConceptStorageServiceLocal conceptStorageServiceLocal;

	@Autowired
	private AlgorithmServiceLocal algorithmServiceLocal;

	@Autowired
	private ConceptRelationshipStorageService conceptRelationshipStorageService;

	@Test
	public void testShortestPath() {
		// create simple straight path (start->middle1->middle2->middle3->middle4->end)
		Concept start = TestUtility.createFullConcept();
		String startUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, start);
		Concept middle1 = TestUtility.createFullConcept();
		String middle1Uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, middle1);
		Concept middle2 = TestUtility.createFullConcept();
		String middle2Uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, middle2);
		Concept middle3 = TestUtility.createFullConcept();
		String middle3Uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, middle3);
		Concept end = TestUtility.createFullConcept();
		String endUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, end);
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(startUuid, middle1Uuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle1Uuid, middle2Uuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle2Uuid, middle3Uuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle3Uuid, endUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);

		// find shortest path (start->middle1->middle2->middle3->middle4->end)
		Iterable<Path> paths = algorithmServiceLocal.shortestPath(startUuid, endUuid, 15);
		Assert.assertNotNull(paths);
		for (Path path : paths) {
			Assert.assertEquals(4, path.length());
			List<Relationship> relationshipList = new ArrayList<Relationship>();
			for (Relationship relationship : path.relationships()) {
				relationshipList.add(relationship);
			}
			Assert.assertEquals(startUuid, relationshipList.get(0).getStartNode()
					.getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle1Uuid, relationshipList.get(0).getEndNode()
					.getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle1Uuid,
					relationshipList.get(1).getStartNode().getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle2Uuid, relationshipList.get(1).getEndNode()
					.getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle2Uuid,
					relationshipList.get(2).getStartNode().getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle3Uuid, relationshipList.get(2).getEndNode()
					.getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle3Uuid,
					relationshipList.get(3).getStartNode().getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(endUuid, relationshipList.get(3).getEndNode().getProperty(ConceptImpl.UUID_PROPERTY));
		}
	}

	@Test
	public void testShortestPathForMultiplePaths() {
		// create simple straight path (start->middle1->middle2->middle3->middle4->end)
		Concept start = TestUtility.createFullConcept();
		String startUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, start);
		Concept middle1 = TestUtility.createFullConcept();
		String middle1Uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, middle1);
		Concept middle2 = TestUtility.createFullConcept();
		String middle2Uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, middle2);
		Concept middle3 = TestUtility.createFullConcept();
		String middle3Uuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, middle3);
		Concept end = TestUtility.createFullConcept();
		String endUuid = conceptStorageServiceLocal.createConcept(ConceptType.CONCEPT, end);
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(startUuid, middle1Uuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle1Uuid, middle2Uuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle2Uuid, middle3Uuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle3Uuid, endUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);
		// create a shorter path than one above (start->middle1->end)
		conceptRelationshipImpl = new ConceptRelationshipImpl(middle1Uuid, endUuid, null,
				SemanticRelationshipCategory.RELATED, ConceptRelationshipCategory.AUTHORITATIVE, 1);
		conceptRelationshipStorageService.createRelationship(conceptRelationshipImpl);

		// find shortest path : in this case : (start->middle1->end)
		Iterable<Path> paths = algorithmServiceLocal.shortestPath(startUuid, endUuid, 15);
		Assert.assertNotNull(paths);
		for (Path path : paths) {
			Assert.assertEquals(2, path.length());
			List<Relationship> relationshipList = new ArrayList<Relationship>();
			for (Relationship relationship : path.relationships()) {
				relationshipList.add(relationship);
			}
			Assert.assertEquals(startUuid, relationshipList.get(0).getStartNode()
					.getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle1Uuid, relationshipList.get(0).getEndNode()
					.getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(middle1Uuid,
					relationshipList.get(1).getStartNode().getProperty(ConceptImpl.UUID_PROPERTY));
			Assert.assertEquals(endUuid, relationshipList.get(1).getEndNode().getProperty(ConceptImpl.UUID_PROPERTY));
		}
	}
}
