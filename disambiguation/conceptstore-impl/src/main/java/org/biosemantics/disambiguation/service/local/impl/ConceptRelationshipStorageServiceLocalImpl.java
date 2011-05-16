package org.biosemantics.disambiguation.service.local.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.disambiguation.service.local.ConceptRelationshipStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class ConceptRelationshipStorageServiceLocalImpl implements ConceptRelationshipStorageServiceLocal {

	private static final Logger logger = LoggerFactory.getLogger(ConceptRelationshipStorageServiceLocalImpl.class);
	private static final String RLSP_INDEX = "relationship";
	private static final String UUID_INDEX_KEY = "rlsp_uuid";
	private UuidGeneratorService uuidGeneratorService;
	private final GraphStorageTemplate graphStorageTemplate;
	private final RelationshipIndex index;
	private ConceptStorageServiceLocal conceptStorageServiceLocal;
	private ValidationUtility validationUtility;

	public static final RelationshipType relatedRlspType = new RelationshipType() {
		@Override
		public String name() {
			return SemanticRelationshipCategory.RELATED.name();
		}
	};
	public static final RelationshipType hasBroaderRlspType = new RelationshipType() {
		@Override
		public String name() {
			return SemanticRelationshipCategory.HAS_BROADER_CONCEPT.name();
		}
	};
	public static final RelationshipType hasNarrowerRlspType = new RelationshipType() {
		@Override
		public String name() {
			return SemanticRelationshipCategory.HAS_NARROWER_CONCEPT.name();
		}
	};

	public ConceptRelationshipStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
		this.index = this.graphStorageTemplate.getIndexManager().forRelationships(RLSP_INDEX);
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = checkNotNull(uuidGeneratorService);
	}

	@Required
	public void setConceptStorageServiceLocal(ConceptStorageServiceLocal conceptStorageServiceLocal) {
		this.conceptStorageServiceLocal = conceptStorageServiceLocal;
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Transactional
	@Override
	public String createRelationship(final ConceptRelationship conceptRelationship) {
		// no optimisation new relationship created without any checks, calling code must ensure relationshipExists() is
		// called to ensure uniqueness
		validationUtility.validateRelationship(conceptRelationship);
		Node startNode = conceptStorageServiceLocal.getConceptNode(conceptRelationship.fromConcept());
		Node endNode = conceptStorageServiceLocal.getConceptNode(conceptRelationship.toConcept());
		final String uuid = uuidGeneratorService.generateRandomUuid();
		Relationship relationship = startNode.createRelationshipTo(endNode, new RelationshipType() {
			@Override
			public String name() {
				return conceptRelationship.getSemanticRelationshipCategory().name();
			}
		});
		relationship.setProperty(ConceptRelationshipImpl.UUID_PROPERTY, uuid);
		relationship.setProperty(ConceptRelationshipImpl.WEIGHT_PROERTY, conceptRelationship.getWeight());
		if (!StringUtils.isBlank(conceptRelationship.getPredicateConceptUuid())) {
			relationship.setProperty(ConceptRelationshipImpl.PREDICATE_CONCEPT_UUID_PROPERTY,
					conceptRelationship.getPredicateConceptUuid());
		}
		relationship.setProperty(ConceptRelationshipImpl.RLSP_CATEGORY_PROPERTY, conceptRelationship
				.getConceptRelationshipCategory().name());
		// add sources
		if (conceptRelationship.getSources() != null && conceptRelationship.getSources().length > 0) {
			relationship.setProperty(ConceptRelationshipImpl.SOURCES_PROPERTY, conceptRelationship.getSources());
		}

		index.add(relationship, UUID_INDEX_KEY, uuid);
		return uuid;

	}

	@Transactional
	@Override
	public ConceptRelationship getConceptRelationship(String uuid) {
		validationUtility.validateString(uuid, "uuid");
		return new ConceptRelationshipImpl(getRelationship(uuid));
	}

	@Transactional
	@Override
	public Collection<ConceptRelationship> getAllRelationshipsForConcept(String uuid) {
		Node node = conceptStorageServiceLocal.getConceptNode(uuid);
		Iterable<Relationship> relationships = node.getRelationships(relatedRlspType, hasNarrowerRlspType,
				hasBroaderRlspType);
		List<ConceptRelationship> conceptRelationships = new ArrayList<ConceptRelationship>();
		for (Relationship relationship : relationships) {
			conceptRelationships.add(new ConceptRelationshipImpl(relationship));
		}
		return conceptRelationships;
	}

	@Transactional
	@Override
	public Relationship getRelationship(String uuid) {
		return index.get(UUID_INDEX_KEY, uuid).getSingle();
	}

	@Transactional
	@Override
	public Collection<ConceptRelationship> getAllDirectRelationships(String firstConceptUuid, String secondConceptUuid) {
		Node firstNode = conceptStorageServiceLocal.getConceptNode(firstConceptUuid);
		Node secondNode = conceptStorageServiceLocal.getConceptNode(secondConceptUuid);
		Collection<ConceptRelationship> conceptRelationships = new HashSet<ConceptRelationship>();
		for (Relationship relationship : firstNode.getRelationships()) {
			if (relationship.getOtherNode(firstNode).equals(secondNode)) {
				conceptRelationships.add(new ConceptRelationshipImpl(relationship));
			}
		}
		return conceptRelationships;
	}

	@Transactional
	@Override
	public boolean relationshipExists(ConceptRelationship conceptRelationship) {
		Node startNode = conceptStorageServiceLocal.getConceptNode(conceptRelationship.fromConcept());
		Node endNode = conceptStorageServiceLocal.getConceptNode(conceptRelationship.toConcept());
		for (Relationship foundRelationship : startNode.getRelationships()) {
			// get all relationships for one node: check if there is a rlsp with other node
			if (foundRelationship.getOtherNode(startNode).equals(endNode)) {
				// rlsp exists between the concepts: check if it is the same type
				if (foundRelationship.getType().name()
						.equals(conceptRelationship.getSemanticRelationshipCategory().name())
						&& ((String) foundRelationship.getProperty(ConceptRelationshipImpl.RLSP_CATEGORY_PROPERTY))
								.equals(conceptRelationship.getConceptRelationshipCategory().name())) {
					return true;
				}
				if (conceptRelationship.getSemanticRelationshipCategory() == SemanticRelationshipCategory.HAS_BROADER_CONCEPT) {
					if (foundRelationship.getStartNode().equals(endNode)
							&& foundRelationship.getEndNode().equals(startNode)
							&& foundRelationship.getType().name()
									.equals(SemanticRelationshipCategory.HAS_NARROWER_CONCEPT.name())) {
						return true;
					}
				}
				if (conceptRelationship.getSemanticRelationshipCategory() == SemanticRelationshipCategory.HAS_NARROWER_CONCEPT) {
					if (foundRelationship.getStartNode().equals(endNode)
							&& foundRelationship.getEndNode().equals(startNode)
							&& foundRelationship.getType().name()
									.equals(SemanticRelationshipCategory.HAS_BROADER_CONCEPT.name())) {
						return true;
					}
				}

			}
		}
		return false;
	}
}
