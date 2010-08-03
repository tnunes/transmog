package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipService;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipSourceType;
import org.biosemantics.disambiguation.knowledgebase.validation.ConceptRelationInputValidator;
import org.neo4j.graphdb.Relationship;
import org.springframework.transaction.annotation.Transactional;

public class ConceptRelationshipServiceImpl implements ConceptRelationshipService {

	private ConceptRelationInputValidator conceptRelationInputValidator;

	public void setConceptRelationInputValidator(ConceptRelationInputValidator conceptRelationInputValidator) {
		this.conceptRelationInputValidator = conceptRelationInputValidator;
	}

	@Transactional
	@Override
	public ConceptRelationship createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType, RelationshipSourceType relationshipSourceType,
			String description, int frequency) {
		conceptRelationInputValidator.validateConceptRelationshipType(conceptRelationshipType);
		conceptRelationInputValidator.validateRelationshipSourceType(relationshipSourceType);
		ConceptRelationshipImpl conceptRelationshipImpl = createConceptRelationship(source, target,
				conceptRelationshipType);
		conceptRelationshipImpl.setRelationshipSourceType(relationshipSourceType);
		conceptRelationshipImpl.setFrequency(frequency);
		if (!StringUtils.isBlank(description)) {
			conceptRelationshipImpl.setDescription(description);
		}
		return conceptRelationshipImpl;
	}

	@Transactional
	@Override
	public ConceptRelationship createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType, RelationshipSourceType relationshipSourceType) {
		conceptRelationInputValidator.validateConceptRelationshipType(conceptRelationshipType);
		conceptRelationInputValidator.validateRelationshipSourceType(relationshipSourceType);
		ConceptRelationshipImpl conceptRelationshipImpl = createConceptRelationship(source, target,
				conceptRelationshipType);
		conceptRelationshipImpl.setRelationshipSourceType(relationshipSourceType);
		return conceptRelationshipImpl;
	}

	@Transactional
	private ConceptRelationshipImpl createConceptRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType) {
		ConceptImpl sourceImpl = (ConceptImpl) source;
		ConceptImpl targetConcept = (ConceptImpl) target;
		Relationship relationship = sourceImpl.getUnderlyingNode().createRelationshipTo(
				targetConcept.getUnderlyingNode(), conceptRelationshipType);
		return new ConceptRelationshipImpl(relationship);
	}

}
