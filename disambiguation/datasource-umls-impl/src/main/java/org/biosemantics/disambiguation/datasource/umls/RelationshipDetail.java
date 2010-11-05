package org.biosemantics.disambiguation.datasource.umls;

import java.io.Serializable;

public class RelationshipDetail implements Serializable {
	private static final long serialVersionUID = -20933668459370437L;
	private String sourceConcept;
	private String targetConcept;
	private String predicateConcept;
	private String relationhipType;
	private int strength;

	public RelationshipDetail(String sourceConcept, String targetConcept, String predicateConcept) {
		super();
		this.sourceConcept = sourceConcept;
		this.targetConcept = targetConcept;
		this.predicateConcept = predicateConcept;
	}

	public RelationshipDetail(String sourceConcept, String targetConcept, String predicateConcept,
			String relationhipType) {
		super();
		this.sourceConcept = sourceConcept;
		this.targetConcept = targetConcept;
		this.predicateConcept = predicateConcept;
		this.relationhipType = relationhipType;
	}

	public RelationshipDetail(String sourceConcept, String targetConcept, int strength) {
		super();
		this.sourceConcept = sourceConcept;
		this.targetConcept = targetConcept;
		this.strength = strength;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public String getPredicateConcept() {
		return predicateConcept;
	}

	public void setPredicateConcept(String predicateConcept) {
		this.predicateConcept = predicateConcept;
	}

	public String getRelationhipType() {
		return relationhipType;
	}

	public void setRelationhipType(String relationhipType) {
		this.relationhipType = relationhipType;
	}

	public String getSourceConcept() {
		return sourceConcept;
	}

	public void setSourceConcept(String sourceConcept) {
		this.sourceConcept = sourceConcept;
	}

	public String getTargetConcept() {
		return targetConcept;
	}

	public void setTargetConcept(String targetConcept) {
		this.targetConcept = targetConcept;
	}

}
