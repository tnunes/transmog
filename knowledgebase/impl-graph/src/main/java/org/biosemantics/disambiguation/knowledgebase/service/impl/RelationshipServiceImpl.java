package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipInput;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipService;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.local.IdGenerator;
import org.biosemantics.disambiguation.knowledgebase.service.local.TextIndexService;
import org.biosemantics.disambiguation.knowledgebase.validation.ConceptRelationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.validation.ValidationUtilityService;
import org.neo4j.graphdb.Relationship;
import org.springframework.transaction.annotation.Transactional;

public class RelationshipServiceImpl implements RelationshipService {

	private ValidationUtilityService validationUtilityService;
	private IdGenerator idGenerator;
	private TextIndexService textIndexService;

	public void setValidationUtilityService(ValidationUtilityService validationUtilityService) {
		this.validationUtilityService = validationUtilityService;
	}

	public void setTextIndexService(TextIndexService textIndexService) {
		this.textIndexService = textIndexService;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Transactional
	@Override
	public ConceptRelationshipImpl createRelationship(Concept source, Concept target,
			ConceptRelationshipType conceptRelationshipType) {
		if (validationUtilityService.isNull(source))
			throw new NullArgumentException("source");
		if (validationUtilityService.isNull(target))
			throw new NullArgumentException("target");
		if (validationUtilityService.isNull(conceptRelationshipType))
			throw new NullArgumentException("conceptRelationshipType");
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
		if (validationUtilityService.isNull(conceptRelationshipInput))
			throw new NullArgumentException("conceptRelationshipInput");
		ConceptRelationshipImpl conceptRelationshipImpl = createRelationship(conceptRelationshipInput.getSource(),
				conceptRelationshipInput.getTarget(), conceptRelationshipInput.getConceptRelationshipType());
		conceptRelationshipImpl.setId(idGenerator.generateRandomId());
		conceptRelationshipImpl.setScore(conceptRelationshipInput.getScore());
		// FIXME: study do we allow this? null predicates?
		if (conceptRelationshipInput.getPredicate() != null) {
			conceptRelationshipImpl.setPredicateConceptId(conceptRelationshipInput.getPredicate().getId());
		}
		conceptRelationshipImpl.setRelationshipCategory(conceptRelationshipInput.getRelationshipCategory());
		return conceptRelationshipImpl;
	}

	@Override
	public Collection<ConceptRelationship> getConceptRelationships(String conceptId) {
		if (validationUtilityService.isBlankString(conceptId))
			throw new IllegalArgumentException("conceptId");
		Concept concept = textIndexService.getConceptById(conceptId);
		List<ConceptRelationship> conceptRelationships = new ArrayList<ConceptRelationship>();
		if (concept != null) {
			ConceptImpl conceptImpl = (ConceptImpl) concept;
			Iterable<Relationship> relationships = conceptImpl.getUnderlyingNode().getRelationships(
					ConceptRelationshipType.CLOSE_MATCH, ConceptRelationshipType.EXACT_MATCH,
					ConceptRelationshipType.HAS_BROADER_CONCEPT, ConceptRelationshipType.HAS_NARROWER_CONCEPT,
					ConceptRelationshipType.RELATED);
			for (Relationship relationship : relationships) {
				conceptRelationships.add(new ConceptRelationshipImpl(relationship));
			}
		}
		return conceptRelationships;
	}

}
