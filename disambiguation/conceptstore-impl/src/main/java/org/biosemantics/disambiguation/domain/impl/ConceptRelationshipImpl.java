package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class ConceptRelationshipImpl implements ConceptRelationship {

	private static final long serialVersionUID = -1908002768195644773L;
	public static final String UUID_PROPERTY = "uuid";
	public static final String WEIGHT_PROERTY = "weight";
	public static final String PREDICATE_CONCEPT_UUID_PROPERTY = "predicateConceptUuid";
	public static final String RLSP_CATEGORY_PROPERTY = "rlspCategory";
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
		return (String) underlyingRelationship.getProperty(UUID_PROPERTY);
	}

	@Override
	public int getWeight() {
		return (Integer)underlyingRelationship.getProperty(WEIGHT_PROERTY);
	}

	@Override
	public String getPredicateConceptUuid() {
		return (String) underlyingRelationship.getProperty(PREDICATE_CONCEPT_UUID_PROPERTY);
	}

	@Override
	public String fromConcept() {
		return (String) underlyingRelationship.getStartNode().getProperty(ConceptImpl.UUID_PROPERTY);
	}

	@Override
	public String toConcept() {
		return (String) underlyingRelationship.getEndNode().getProperty(ConceptImpl.UUID_PROPERTY);
	}

	@Override
	public SemanticRelationshipCategory getSemanticRelationshipCategory() {
		return SemanticRelationshipCategory.valueOf(underlyingRelationship.getType().name());
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
		return Objects.toStringHelper(this).add(UUID_PROPERTY, getUuid()).add(WEIGHT_PROERTY, getWeight())
				.add(PREDICATE_CONCEPT_UUID_PROPERTY, getPredicateConceptUuid()).toString();
	}

	@Override
	public ConceptRelationshipCategory getConceptRelationshipCategory() {
		return ConceptRelationshipCategory.valueOf((String) underlyingRelationship.getProperty(RLSP_CATEGORY_PROPERTY));
	}

	@Override
	public String[] getSources() {
		// TODO Auto-generated method stub
		return null;
	}

}
