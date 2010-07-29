package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.api.ConceptRelationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.api.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.api.RelationshipSourceType;

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
