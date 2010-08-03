package org.biosemantics.disambiguation.knowledgebase.service;

public interface ConceptRelationshipService {

	ConceptRelationship createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType, RelationshipSourceType relationshipSourceType,
			String description, int frequency);

	ConceptRelationship createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType, RelationshipSourceType relationshipSourceType);
}
