package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.conceptstore.common.service.RelationshipStoreService;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.RelationshipImpl;
import org.neo4j.graphdb.Direction;

public class RelationshipStoreServiceImpl implements RelationshipStoreService {

	private UuidGeneratorService uuidGeneratorService;
	private boolean checkExists;

	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = checkNotNull(uuidGeneratorService);
	}

	public void setCheckExists(boolean checkExists) {
		this.checkExists = checkExists;
	}

	@Override
	public Relationship createRelationship(Relationship relationship) {
		Relationship createdRelationship = null;
		if (checkExists) {
			createdRelationship = findRelationship(relationship);
		}
		if (createdRelationship == null) {
			ConceptImpl sourceConceptImpl = (ConceptImpl) relationship.getSource();
			ConceptImpl targetConceptImpl = (ConceptImpl) relationship.getTarget();

			org.neo4j.graphdb.Relationship underlyingRelationship = sourceConceptImpl.getUnderlyingNode()
					.createRelationshipTo(
							targetConceptImpl.getUnderlyingNode(),
							ConceptRelationshipTypeImpl.fromConceptRelationshipType(relationship
									.getConceptRelationshipType()));
			RelationshipImpl relationshipImpl = new RelationshipImpl(underlyingRelationship);
			relationshipImpl.setUuid(uuidGeneratorService.generateRandomUuid());
			relationshipImpl.setPredicateConceptId(relationship.getPredicateConceptUuid());
			relationshipImpl.setScore(relationship.getWeight());
			relationshipImpl.setRelationshipCategory(relationship.getRelationshipCategory());
			createdRelationship = relationshipImpl;
		}
		return createdRelationship;
	}

	// FIXME need to find out what constitutes a .equals relationship
	/*
	 * Direction / Category / Relationship type
	 */
	private Relationship findRelationship(Relationship relationship) {
		Relationship found = null;
		ConceptImpl conceptImpl = (ConceptImpl) relationship.getSource();
		Iterable<org.neo4j.graphdb.Relationship> foundRlsps = conceptImpl.getUnderlyingNode().getRelationships(
				ConceptRelationshipTypeImpl.fromConceptRelationshipType(relationship.getConceptRelationshipType()),
				Direction.OUTGOING);
		if (foundRlsps != null) {
			for (org.neo4j.graphdb.Relationship foundRelationship : foundRlsps) {
				RelationshipImpl relationshipImpl = new RelationshipImpl(foundRelationship);
				if (relationship.getRelationshipCategory() == relationshipImpl.getRelationshipCategory()) {
					found = relationshipImpl;
					break;
				}
			}
		}
		return found;

	}
}
