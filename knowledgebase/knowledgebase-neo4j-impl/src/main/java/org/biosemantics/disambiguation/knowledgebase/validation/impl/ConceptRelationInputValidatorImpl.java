package org.biosemantics.disambiguation.knowledgebase.validation.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipSourceType;
import org.biosemantics.disambiguation.knowledgebase.validation.ConceptRelationInputValidator;

public class ConceptRelationInputValidatorImpl implements ConceptRelationInputValidator {

	@Override
	public void validateConceptRelationshipType(ConceptRelationshipType conceptRelationshipType) {
		if (conceptRelationshipType == null)
			throw new NullArgumentException("conceptRelationshipType");

	}

	@Override
	public void validateRelationshipSourceType(RelationshipSourceType relationshipSourceType) {
		if (relationshipSourceType == null)
			throw new NullArgumentException("relationshipSourceType");

	}

}
