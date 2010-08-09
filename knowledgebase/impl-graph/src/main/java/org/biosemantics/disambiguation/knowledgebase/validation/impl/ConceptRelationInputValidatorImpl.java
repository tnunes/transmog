package org.biosemantics.disambiguation.knowledgebase.validation.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipInput;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipCategory;
import org.biosemantics.disambiguation.knowledgebase.validation.ConceptRelationInputValidator;

public class ConceptRelationInputValidatorImpl implements ConceptRelationInputValidator {

	@Override
	public void validateConceptRelationshipType(ConceptRelationshipType conceptRelationshipType) {
		if (conceptRelationshipType == null)
			throw new NullArgumentException("conceptRelationshipType");

	}

	@Override
	public void validateRelationshipSourceType(RelationshipCategory relationshipSourceType) {
		if (relationshipSourceType == null)
			throw new NullArgumentException("relationshipSourceType");

	}

	@Override
	public void validate(ConceptRelationshipInput conceptRelationshipInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateConcept(Concept concept) {
		if (concept == null)
			throw new NullArgumentException("concept");

	}

}
