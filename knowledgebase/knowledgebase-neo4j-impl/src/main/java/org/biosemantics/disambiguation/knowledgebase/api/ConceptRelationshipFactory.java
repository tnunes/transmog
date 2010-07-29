package org.biosemantics.disambiguation.knowledgebase.api;

public interface ConceptRelationshipFactory {

	ConceptRelationship createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType, RelationshipSourceType relationshipSourceType,
			String description, int frequency);

	ConceptRelationship createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType, RelationshipSourceType relationshipSourceType);
}
