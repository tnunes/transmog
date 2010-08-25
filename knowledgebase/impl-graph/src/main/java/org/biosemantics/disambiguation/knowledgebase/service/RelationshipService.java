package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

import org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptRelationshipImpl;

public interface RelationshipService {
	
	ConceptRelationship createRelationship(ConceptRelationshipInput conceptRelationshipInput);

	ConceptRelationshipImpl createRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType);
	
	Collection<ConceptRelationship> getConceptRelationships(String conceptId);
}
