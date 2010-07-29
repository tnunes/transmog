package org.biosemantics.disambiguation.knowledgebase.api;

public interface ConceptRelationInputValidator {
	void validateConceptRelationshipType(ConceptRelationshipType conceptRelationshipType);
	void validateRelationshipSourceType(RelationshipSourceType relationshipSourceType);
}
