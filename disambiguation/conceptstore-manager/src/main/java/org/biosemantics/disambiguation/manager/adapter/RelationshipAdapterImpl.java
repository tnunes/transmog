package org.biosemantics.disambiguation.manager.adapter;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.RelationshipImpl;
import org.biosemantics.disambiguation.manager.common.CommonUtility;
import org.biosemantics.disambiguation.manager.dto.RelationshipResult;

public class RelationshipAdapterImpl implements RelationshipAdapter {

	private ConceptQueryService conceptQueryService;

	public void setConceptQueryService(ConceptQueryService conceptQueryService) {
		this.conceptQueryService = conceptQueryService;
	}

	@Override
	public RelationshipResult adapt(Concept concept, Relationship relationship) {
		RelationshipResult relationshipResult = new RelationshipResult();
		RelationshipImpl relationshipImpl = (RelationshipImpl) relationship;
		Concept startConcept = new ConceptImpl(relationshipImpl.getUnderlyingRelationship().getStartNode());
		Concept endConcept = new ConceptImpl(relationshipImpl.getUnderlyingRelationship().getEndNode());
		Concept otherConcept = concept.equals(startConcept) ? endConcept : startConcept;
		String conceptLabelText = CommonUtility.getPreferredLabel(otherConcept.getLabelsByType(LabelType.PREFERRED))
				.getText();
		relationshipResult.setConceptLabelText(conceptLabelText);
		relationshipResult.setConceptUuid(otherConcept.getUuid());
		relationshipResult.setConceptRelationshipType(relationshipImpl.getConceptRelationshipType());
		relationshipResult.setGetRelationshipCategory(relationshipImpl.getRelationshipCategory());
		relationshipResult.setWeight(relationshipImpl.getWeight());
		String predicateUuid = relationship.getPredicateConceptUuid();
		if (predicateUuid != null && !predicateUuid.isEmpty()) {
			relationshipResult.setPredicateUuid(predicateUuid);
			Concept predicateConcept = conceptQueryService.getConceptByUuid(predicateUuid);
			if (predicateConcept != null) {
				String predicateLabelText = CommonUtility.getPreferredLabel(
						otherConcept.getLabelsByType(LabelType.PREFERRED)).getText();
				relationshipResult.setPredicateLabelText(predicateLabelText);
			}
		}
		return relationshipResult;
	}

}
