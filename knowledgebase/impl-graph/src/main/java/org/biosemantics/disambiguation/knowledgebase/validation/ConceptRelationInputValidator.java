package org.biosemantics.disambiguation.knowledgebase.validation;

import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipInput;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipCategory;

public interface ConceptRelationInputValidator {
	void validateConceptRelationshipType(ConceptRelationshipType conceptRelationshipType);

	void validateRelationshipSourceType(RelationshipCategory relationshipSourceType);

	void validate(ConceptRelationshipInput conceptRelationshipInput);

	void validateConcept(Concept concept);
}
