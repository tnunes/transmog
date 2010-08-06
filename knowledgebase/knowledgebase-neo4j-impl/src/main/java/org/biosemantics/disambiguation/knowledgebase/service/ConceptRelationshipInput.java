package org.biosemantics.disambiguation.knowledgebase.service;

public class ConceptRelationshipInput {
	private Concept source;
	private Concept target;
	private Concept predicate;
	private RelationshipCategory relationshipCategory;
	private ConceptRelationshipType conceptRelationshipType;
	private int score;

	public Concept getSource() {
		return source;
	}

	public void setSource(Concept source) {
		this.source = source;
	}

	public Concept getTarget() {
		return target;
	}

	public void setTarget(Concept target) {
		this.target = target;
	}

	public Concept getPredicate() {
		return predicate;
	}

	public void setPredicate(Concept predicate) {
		this.predicate = predicate;
	}

	public RelationshipCategory getRelationshipCategory() {
		return relationshipCategory;
	}

	public void setRelationshipCategory(RelationshipCategory relationshipCategory) {
		this.relationshipCategory = relationshipCategory;
	}

	public ConceptRelationshipType getConceptRelationshipType() {
		return conceptRelationshipType;
	}

	public void setConceptRelationshipType(ConceptRelationshipType conceptRelationshipType) {
		this.conceptRelationshipType = conceptRelationshipType;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public ConceptRelationshipInput withSource(Concept source) {
		setSource(source);
		return this;
	}

	public ConceptRelationshipInput withTarget(Concept target) {
		setTarget(target);
		return this;
	}

	public ConceptRelationshipInput withPredicate(Concept predicate) {
		setPredicate(predicate);
		return this;
	}

	public ConceptRelationshipInput withRelationshipCategory(RelationshipCategory relationshipCategory) {
		setRelationshipCategory(relationshipCategory);
		return this;
	}

	public ConceptRelationshipInput withConceptRelationshipType(ConceptRelationshipType conceptRelationshipType) {
		setConceptRelationshipType(conceptRelationshipType);
		return this;
	}
	
	public ConceptRelationshipInput withScore(int score) {
		setScore(score);
		return this;
	}

}
