package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationship;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.RelationshipSourceType;
import org.neo4j.graphdb.Relationship;

public class ConceptRelationshipImpl implements ConceptRelationship {

	protected Relationship underlyingRelationship;
	private static final String ID_PROPERTY = "id";
	private static final String FREQUENCY_PROPERTY = "frequency";
	private static final String DESCRIPTION_PROPERTY = "description";
	private static final String RLSP_SOURCE_TYPE_PROPERTY = "relationshipSourceType";

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
	public String getDescription() {
		return (String) underlyingRelationship.getProperty(DESCRIPTION_PROPERTY);
	}

	public void setDescription(String description) {
		if (description == null)
			throw new NullArgumentException("description");
		underlyingRelationship.setProperty(DESCRIPTION_PROPERTY, description);
	}

	@Override
	public int getFrequency() {
		return Integer.valueOf((String) underlyingRelationship.getProperty(FREQUENCY_PROPERTY));
	}

	@Override
	public void setFrequency(int frequency) {
		underlyingRelationship.setProperty(FREQUENCY_PROPERTY, frequency);
	}

	@Override
	public RelationshipSourceType getRelationshipSourceType() {
		return RelationshipSourceType.valueOf((String) underlyingRelationship.getProperty(RLSP_SOURCE_TYPE_PROPERTY));
	}

	public void setRelationshipSourceType(RelationshipSourceType relationshipSourceType) {
		if (relationshipSourceType == null)
			throw new NullArgumentException("relationshipSourceType");
		underlyingRelationship.setProperty(RLSP_SOURCE_TYPE_PROPERTY, relationshipSourceType);
	}
}
