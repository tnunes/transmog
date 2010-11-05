package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.RelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.biosemantics.disambiguation.service.impl.ConceptRelationshipTypeImpl;
import org.neo4j.graphdb.Relationship;

public class RelationshipImpl implements org.biosemantics.conceptstore.common.domain.Relationship {

	private static final long serialVersionUID = 3506729852996059944L;
	public static final String UUID_PROPERTY = "uuid";
	public static final String SCORE_PROPERTY = "score";
	public static final String RELATIONSHIP_CATEGORY_PROPERTY = "relationshipCategory";
	public static final String PREDICATE_CONCEPT_UUID_PROPERTY = "predicateConceptUuid";

	private Relationship underlyingRelationship;

	public RelationshipImpl(Relationship relationship) {
		this.underlyingRelationship = relationship;
	}

	@Override
	public ConceptRelationshipType getConceptRelationshipType() {
		return ((ConceptRelationshipTypeImpl) underlyingRelationship.getType()).toConceptRelationshipType();
	}

	@Override
	public String getUuid() {
		return (String) underlyingRelationship.getProperty(UUID_PROPERTY);
	}

	public void setUuid(String uuid) {
		checkNotNull(uuid);
		checkArgument(uuid.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, uuid);
		underlyingRelationship.setProperty(UUID_PROPERTY, uuid);
	}

	@Override
	public int getWeight() {
		return Integer.valueOf((String) underlyingRelationship.getProperty(SCORE_PROPERTY));
	}

	public void setScore(int score) {
		underlyingRelationship.setProperty(SCORE_PROPERTY, score);
	}

	@Override
	public RelationshipCategory getRelationshipCategory() {
		return RelationshipCategory
				.valueOf((String) underlyingRelationship.getProperty(RELATIONSHIP_CATEGORY_PROPERTY));
	}

	public void setRelationshipCategory(RelationshipCategory relationshipSourceType) {
		checkNotNull(relationshipSourceType);
		underlyingRelationship.setProperty(RELATIONSHIP_CATEGORY_PROPERTY, relationshipSourceType.name());
	}

	@Override
	public Concept getSource() {
		return new ConceptImpl(underlyingRelationship.getStartNode());
	}

	@Override
	public Concept getTarget() {
		return new ConceptImpl(underlyingRelationship.getEndNode());
	}

	@Override
	public String getPredicateConceptUuid() {
		return (String) underlyingRelationship.getProperty(PREDICATE_CONCEPT_UUID_PROPERTY);
	}

	public void setPredicateConceptId(String predicateConceptId) {
		checkNotNull(predicateConceptId);
		checkArgument(predicateConceptId.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, predicateConceptId);
		underlyingRelationship.setProperty(PREDICATE_CONCEPT_UUID_PROPERTY, predicateConceptId);
	}

}
