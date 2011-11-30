package org.biosemantics.disambiguation.domain.impl;

import static org.biosemantics.disambiguation.common.PropertyConstant.UUID;
import static org.biosemantics.disambiguation.common.PropertyConstant.WEIGHT;

import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class ConceptRelationshipImpl implements ConceptRelationship {

	private static final long serialVersionUID = -1908002768195644773L;
	public static final String PREDICATE_CONCEPT_UUID_PROPERTY = "predicateConceptUuid";
	public static final String SOURCES_PROPERTY = "sources";

	private Relationship underlyingRelationship;

	public ConceptRelationshipImpl(Relationship relationship) {
		this.underlyingRelationship = relationship;
	}

	public Relationship getUnderlyingRelationship() {
		return underlyingRelationship;
	}

	@Override
	public String getUuid() {
		return (String) underlyingRelationship.getProperty(UUID.name());
	}

	@Override
	public double getWeight() {
		return (Double) underlyingRelationship.getProperty(WEIGHT.name());
	}

	@Override
	public String getPredicateConceptUuid() {
		return (String) underlyingRelationship.getProperty(PREDICATE_CONCEPT_UUID_PROPERTY);
	}

	@Override
	public String fromConcept() {
		return (String) underlyingRelationship.getStartNode().getProperty(UUID.name());
	}

	@Override
	public String toConcept() {
		return (String) underlyingRelationship.getEndNode().getProperty(UUID.name());
	}

	@Override
	public ConceptRelationshipType getType() {
		return ConceptRelationshipType.valueOf(underlyingRelationship.getType().name());
	}

	@Override
	public boolean equals(Object relationshipImpl) {
		if (relationshipImpl instanceof ConceptRelationshipImpl) {
			return this.underlyingRelationship.equals(((ConceptRelationshipImpl) relationshipImpl)
					.getUnderlyingRelationship());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingRelationship.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add(UUID.name(), getUuid()).add(WEIGHT.name(), getWeight())
				.add(PREDICATE_CONCEPT_UUID_PROPERTY, getPredicateConceptUuid()).toString();
	}

	@Override
	public String[] getTags() {
		return (String[]) underlyingRelationship.getProperty(PropertyConstant.TAGS.name());
	}

}
