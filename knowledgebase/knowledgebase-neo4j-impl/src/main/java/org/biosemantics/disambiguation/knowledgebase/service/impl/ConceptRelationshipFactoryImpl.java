package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.api.Concept;
import org.biosemantics.disambiguation.knowledgebase.api.ConceptRelationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.api.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.api.ConceptRelationshipFactory;
import org.biosemantics.disambiguation.knowledgebase.api.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.api.RelationshipSourceType;
import org.neo4j.graphdb.Relationship;
import org.springframework.transaction.annotation.Transactional;

public class ConceptRelationshipFactoryImpl implements ConceptRelationshipFactory {

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
