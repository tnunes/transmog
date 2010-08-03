package org.biosemantics.disambiguation.knowledgebase.service;

public interface ConceptRelationship {

	ConceptRelationshipType getConceptRelationshipType();
	
	RelationshipSourceType getRelationshipSourceType();

	String getId();

	String getDescription();

	int getFrequency();

	void setFrequency(int frequency);

}