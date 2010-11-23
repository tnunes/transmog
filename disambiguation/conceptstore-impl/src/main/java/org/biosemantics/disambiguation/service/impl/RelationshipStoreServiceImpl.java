package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.conceptstore.common.service.RelationshipStoreService;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.RelationshipImpl;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class RelationshipStoreServiceImpl implements RelationshipStoreService {

	private static final Logger logger = LoggerFactory.getLogger(RelationshipStoreServiceImpl.class);
	private UuidGeneratorService uuidGeneratorService;
	private IndexService indexService;
	private boolean checkExists = true;

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = checkNotNull(uuidGeneratorService);
	}

	@Required
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
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

	@Override
	public Relationship getRelationshipByUuid(String uuid) {
		throw new UnsupportedOperationException(
				"getRelationshipByUuid not supported yet, will be supported when we move to neo4j 1.2");
	}

	@Override
	public Collection<Relationship> getAllRelationshipsForConcept(String uuid) {
		Concept concept = indexService.getConceptByUuid(uuid);
		if (concept == null) {
			logger.warn("no concept in store for uuid {}", uuid);
		}
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		Iterable<org.neo4j.graphdb.Relationship> storeRelationships = conceptImpl.getUnderlyingNode().getRelationships(
				ConceptRelationshipTypeImpl.CLOSE_MATCH, ConceptRelationshipTypeImpl.EXACT_MATCH,
				ConceptRelationshipTypeImpl.HAS_BROADER_CONCEPT, ConceptRelationshipTypeImpl.HAS_NARROWER_CONCEPT,
				ConceptRelationshipTypeImpl.RELATED);
		Set<Relationship> relationships = new HashSet<Relationship>();
		for (org.neo4j.graphdb.Relationship storeRelationship : storeRelationships) {
			relationships.add(new RelationshipImpl(storeRelationship));
		}
		return relationships;
	}
}
