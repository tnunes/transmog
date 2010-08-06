package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipInput;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipService;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.validation.ConceptRelationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.validation.IdGenerator;
import org.neo4j.graphdb.Relationship;
import org.springframework.transaction.annotation.Transactional;

public class RelationshipServiceImpl implements RelationshipService {

	private ConceptRelationInputValidator conceptRelationInputValidator;
	private IdGenerator idGenerator;

	public void setConceptRelationInputValidator(ConceptRelationInputValidator conceptRelationInputValidator) {
		this.conceptRelationInputValidator = conceptRelationInputValidator;
	}

	// @Transactional
	// @Override
	// public ConceptRelationship createConceptRelationship(Concept source, Concept target,
	// ConceptRelationshipType conceptRelationshipType, RelationshipCategory relationshipSourceType,
	// String description, int frequency) {
	// conceptRelationInputValidator.validateConceptRelationshipType(conceptRelationshipType);
	// conceptRelationInputValidator.validateRelationshipSourceType(relationshipSourceType);
	// ConceptRelationshipImpl conceptRelationshipImpl = createConceptRelationship(source, target,
	// conceptRelationshipType);
	// conceptRelationshipImpl.setRelationshipSourceType(relationshipSourceType);
	// conceptRelationshipImpl.setFrequency(frequency);
	// if (!StringUtils.isBlank(description)) {
	// conceptRelationshipImpl.setDescription(description);
	// }
	// return conceptRelationshipImpl;
	// }

	// @Transactional
	// @Override
	// public ConceptRelationship createConceptRelationship(Concept source, Concept target,
	// ConceptRelationshipType conceptRelationshipType, RelationshipCategory relationshipSourceType) {
	// conceptRelationInputValidator.validateConceptRelationshipType(conceptRelationshipType);
	// conceptRelationInputValidator.validateRelationshipSourceType(relationshipSourceType);
	// ConceptRelationshipImpl conceptRelationshipImpl = createConceptRelationship(source, target,
	// conceptRelationshipType);
	// conceptRelationshipImpl.setRelationshipSourceType(relationshipSourceType);
	// return conceptRelationshipImpl;
	// }

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Transactional
	@Override
	public ConceptRelationshipImpl createRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType) {
		conceptRelationInputValidator.validateConceptRelationshipType(conceptRelationshipType);
		conceptRelationInputValidator.validateConcept(source);
		conceptRelationInputValidator.validateConcept(target);
		ConceptImpl sourceImpl = (ConceptImpl) source;
		ConceptImpl targetConcept = (ConceptImpl) target;
		Relationship relationship = sourceImpl.getUnderlyingNode().createRelationshipTo(
				targetConcept.getUnderlyingNode(), conceptRelationshipType);
		ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(relationship);
		return conceptRelationshipImpl;
	}

	@Override
	@Transactional
	public ConceptRelationship createRelationship(ConceptRelationshipInput conceptRelationshipInput) {
		conceptRelationInputValidator.validate(conceptRelationshipInput);
		ConceptRelationshipImpl conceptRelationshipImpl = createRelationship(conceptRelationshipInput.getSource(),
				conceptRelationshipInput.getTarget(), conceptRelationshipInput.getConceptRelationshipType());
		conceptRelationshipImpl.setId(idGenerator.generateRandomId());
		conceptRelationshipImpl.setScore(conceptRelationshipInput.getScore());
		conceptRelationshipImpl.setPredicateConceptId(conceptRelationshipInput.getPredicate().getId());
		conceptRelationshipImpl.setRelationshipCategory(conceptRelationshipInput.getRelationshipCategory());
		return conceptRelationshipImpl;
	}

}
