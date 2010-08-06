package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipCategory;
import org.neo4j.graphdb.Relationship;

public class ConceptRelationshipImpl implements ConceptRelationship {

	protected Relationship underlyingRelationship;
	private static final String ID_PROPERTY = "id";
	private static final String SCORE_PROPERTY = "score";
	private static final String RLSP_SOURCE_TYPE_PROPERTY = "relationshipSourceType";
	private static final String PREDICATE_CONCEPT_ID_PROPERTY = "predicateConceptId";

	public ConceptRelationshipImpl(Relationship relationship) {
		this.underlyingRelationship = relationship;
	}

	@Override
	public ConceptRelationshipType getConceptRelationshipType() {
		return (ConceptRelationshipType) underlyingRelationship.getType();
	}

	@Override
	public String getId() {
		return (String) underlyingRelationship.getProperty(ID_PROPERTY);
	}

	public void setId(String id) {
		if (id == null)
			throw new NullArgumentException("id");
		underlyingRelationship.setProperty(ID_PROPERTY, id);
	}

	@Override
	public int getScore() {
		return Integer.valueOf((String) underlyingRelationship.getProperty(SCORE_PROPERTY));
	}

	@Override
	public void setScore(int score) {
		underlyingRelationship.setProperty(SCORE_PROPERTY, score);
	}

	@Override
	public RelationshipCategory getRelationshipCategory() {
		return RelationshipCategory.valueOf((String) underlyingRelationship.getProperty(RLSP_SOURCE_TYPE_PROPERTY));
	}

	public void setRelationshipCategory(RelationshipCategory relationshipSourceType) {
		if (relationshipSourceType == null)
			throw new NullArgumentException("relationshipSourceType");
		underlyingRelationship.setProperty(RLSP_SOURCE_TYPE_PROPERTY, relationshipSourceType.name());
	}

	@Override
	public Concept getSource() {
		return new ConceptImpl(underlyingRelationship.getStartNode());
	}

	@Override
	public Concept getTarget() {
		return new ConceptImpl(underlyingRelationship.getEndNode());
	}

	@Override
	public String getPredicateConceptId() {
		return (String) underlyingRelationship.getProperty(PREDICATE_CONCEPT_ID_PROPERTY);
	}

	public void setPredicateConceptId(String predicateConceptId) {
		underlyingRelationship.setProperty(PREDICATE_CONCEPT_ID_PROPERTY, predicateConceptId);
	}

}
