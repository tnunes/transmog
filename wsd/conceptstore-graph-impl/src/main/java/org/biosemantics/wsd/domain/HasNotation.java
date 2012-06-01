package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.google.common.base.Objects;

@RelationshipEntity(type = "HAS_NOTATION", useShortNames = true)
public class HasNotation {

	public HasNotation(Concept concept, Notation notation, String source) {
		super();
		this.concept = concept;
		this.notation = notation;
		this.source = source;
	}

	public HasNotation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Concept getConcept() {
		return concept;
	}

	public Notation getNotation() {
		return notation;
	}

	public String getSource() {
		return source;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HasNotation) {
			final HasNotation other = (HasNotation) obj;
			return (Objects.equal(concept, other.getConcept()) && Objects.equal(notation, other.getNotation()));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(concept, notation, source);
	}

	@StartNode
	private Concept concept;
	@EndNode
	private Notation notation;
	private String source;
	@GraphId
	private Long relationshipId;

}
