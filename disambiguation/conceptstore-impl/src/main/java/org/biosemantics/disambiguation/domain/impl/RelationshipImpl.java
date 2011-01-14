package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.RelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.biosemantics.disambiguation.service.impl.ConceptRelationshipTypeImpl;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class RelationshipImpl implements org.biosemantics.conceptstore.common.domain.Relationship {

	private static final long serialVersionUID = 3506729852996059944L;
	public static final String UUID_PROPERTY = "uuid";
	public static final String WEIGHT_PROERTY = "weight";
	public static final String RELATIONSHIP_CATEGORY_PROPERTY = "relationshipCategory";
	public static final String PREDICATE_CONCEPT_UUID_PROPERTY = "predicateConceptUuid";

	private Relationship underlyingRelationship;

	public RelationshipImpl(Relationship relationship) {
		this.underlyingRelationship = relationship;
	}

	public Relationship getUnderlyingRelationship() {
		return underlyingRelationship;
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
		checkArgument(!uuid.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, uuid);
		underlyingRelationship.setProperty(UUID_PROPERTY, uuid);
	}

	@Override
	public int getWeight() {
		return Integer.valueOf((String) underlyingRelationship.getProperty(WEIGHT_PROERTY));
	}

	public void setWeight(int weight) {
		underlyingRelationship.setProperty(WEIGHT_PROERTY, weight);
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
	public Concept getStartConcept() {
		return new ConceptImpl(underlyingRelationship.getStartNode());
	}

	@Override
	public Concept getEndConcept() {
		return new ConceptImpl(underlyingRelationship.getEndNode());
	}

	@Override
	public String getPredicateConceptUuid() {
		return (String) underlyingRelationship.getProperty(PREDICATE_CONCEPT_UUID_PROPERTY);
	}

	public void setPredicateConceptId(String predicateConceptId) {
		checkNotNull(predicateConceptId);
		checkArgument(!predicateConceptId.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, predicateConceptId);
		underlyingRelationship.setProperty(PREDICATE_CONCEPT_UUID_PROPERTY, predicateConceptId);
	}

	@Override
	public boolean equals(Object relationshipImpl) {
		if (relationshipImpl instanceof RelationshipImpl) {
			return this.underlyingRelationship
					.equals(((RelationshipImpl) relationshipImpl).getUnderlyingRelationship());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingRelationship.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add(UUID_PROPERTY, getUuid()).add(WEIGHT_PROERTY, getWeight())
				.add(RELATIONSHIP_CATEGORY_PROPERTY, getRelationshipCategory())
				.add(PREDICATE_CONCEPT_UUID_PROPERTY, getPredicateConceptUuid()).toString();
	}

}
