package org.biosemantics.disambiguation.manager.dto;

import java.io.Serializable;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.RelationshipCategory;

public class RelationshipResult implements Serializable {
	private static final long serialVersionUID = -3669985656902984452L;

	private String predicateUuid;
	private String predicateLabelText;
	private String conceptUuid;
	private String conceptLabelText;
	private int weight;
	private ConceptRelationshipType conceptRelationshipType;
	private RelationshipCategory getRelationshipCategory;

	public String getPredicateUuid() {
		return predicateUuid;
	}

	public void setPredicateUuid(String predicateUuid) {
		this.predicateUuid = predicateUuid;
	}

	public String getPredicateLabelText() {
		return predicateLabelText;
	}

	public void setPredicateLabelText(String predicateLabelText) {
		this.predicateLabelText = predicateLabelText;
	}

	public String getConceptUuid() {
		return conceptUuid;
	}

	public void setConceptUuid(String conceptUuid) {
		this.conceptUuid = conceptUuid;
	}

	public String getConceptLabelText() {
		return conceptLabelText;
	}

	public void setConceptLabelText(String conceptLabelText) {
		this.conceptLabelText = conceptLabelText;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public ConceptRelationshipType getConceptRelationshipType() {
		return conceptRelationshipType;
	}

	public void setConceptRelationshipType(ConceptRelationshipType conceptRelationshipType) {
		this.conceptRelationshipType = conceptRelationshipType;
	}

	public RelationshipCategory getGetRelationshipCategory() {
		return getRelationshipCategory;
	}

	public void setGetRelationshipCategory(RelationshipCategory getRelationshipCategory) {
		this.getRelationshipCategory = getRelationshipCategory;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
