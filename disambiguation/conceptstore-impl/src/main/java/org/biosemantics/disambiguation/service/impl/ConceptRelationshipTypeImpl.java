package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.neo4j.graphdb.RelationshipType;

// Since java does not allow us to extend enums we convert the enum in the interface to the values in the implementation. Inelegent! but to be fixed in JDK 7 
public enum ConceptRelationshipTypeImpl implements RelationshipType {
	HAS_NARROWER_CONCEPT, HAS_BROADER_CONCEPT, RELATED, CLOSE_MATCH, EXACT_MATCH;

	public static ConceptRelationshipTypeImpl fromConceptRelationshipType(
			ConceptRelationshipType conceptRelationshipType) {
		switch (conceptRelationshipType) {
		case HAS_NARROWER_CONCEPT:
			return ConceptRelationshipTypeImpl.HAS_NARROWER_CONCEPT;
		case HAS_BROADER_CONCEPT:
			return ConceptRelationshipTypeImpl.HAS_BROADER_CONCEPT;
		case RELATED:
			return ConceptRelationshipTypeImpl.RELATED;
		case CLOSE_MATCH:
			return ConceptRelationshipTypeImpl.CLOSE_MATCH;
		case EXACT_MATCH:
			return ConceptRelationshipTypeImpl.EXACT_MATCH;

		default:
			throw new IllegalStateException("No mapping offered for " + conceptRelationshipType.name());
		}
	}

	public ConceptRelationshipType toConceptRelationshipType() {
		switch (this) {
		case HAS_NARROWER_CONCEPT:
			return ConceptRelationshipType.HAS_NARROWER_CONCEPT;
		case HAS_BROADER_CONCEPT:
			return ConceptRelationshipType.HAS_BROADER_CONCEPT;
		case RELATED:
			return ConceptRelationshipType.RELATED;
		case CLOSE_MATCH:
			return ConceptRelationshipType.CLOSE_MATCH;
		case EXACT_MATCH:
			return ConceptRelationshipType.EXACT_MATCH;

		default:
			throw new IllegalStateException("No mapping offered for " + this.name());
		}
	}
}
