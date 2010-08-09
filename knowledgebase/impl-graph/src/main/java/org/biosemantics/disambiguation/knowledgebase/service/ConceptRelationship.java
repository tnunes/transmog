package org.biosemantics.disambiguation.knowledgebase.service;

public interface ConceptRelationship {

	String getId();

	int getScore();

	void setScore(int score);

	Concept getSource();

	Concept getTarget();

	String getPredicateConceptId();

	ConceptRelationshipType getConceptRelationshipType();

	RelationshipCategory getRelationshipCategory();

}