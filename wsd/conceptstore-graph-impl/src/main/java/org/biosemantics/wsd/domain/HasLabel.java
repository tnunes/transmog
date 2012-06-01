package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.google.common.base.Objects;

@RelationshipEntity(type = "HAS_LABEL", useShortNames = true)
public class HasLabel {

	public HasLabel(Concept concept, Label label, LabelType type, String source) {
		super();
		this.concept = concept;
		this.label = label;
		this.type = type;
		this.source = source;
	}

	public HasLabel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Concept getConcept() {
		return concept;
	}

	public Label getLabel() {
		return label;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	public LabelType getType() {
		return type;
	}

	public String getSource() {
		return source;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HasLabel) {
			final HasLabel other = (HasLabel) obj;
			return Objects.equal(type, other.getType())
					&& (Objects.equal(concept, other.getConcept()) && Objects.equal(label, other.getLabel()));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(concept, label, type, source);
	}

	@StartNode
	private Concept concept;
	@EndNode
	private Label label;
	private LabelType type;
	private String source;
	@GraphId
	private Long relationshipId;

}
