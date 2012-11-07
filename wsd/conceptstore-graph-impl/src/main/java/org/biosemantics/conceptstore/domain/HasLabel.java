package org.biosemantics.conceptstore.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.google.common.base.Objects;

@RelationshipEntity(type = "HAS_LABEL", useShortNames = true)
public class HasLabel {

	public HasLabel() {
		super();
		this.sources = new ArrayList<String>();
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

	public void setType(LabelType type) {
		this.type = type;
	}

	public List<String> getSources() {
		// returning coppied list: effective java
		if (sources != null && !sources.isEmpty()) {
			return new ArrayList<String>(sources);
		}
		return new ArrayList<String>();
	}

	public void addSources(String... sources) {
		if (sources != null) {
			if (this.sources == null) {
				this.sources = new ArrayList<String>();
			}
			for (String source : sources) {
				if (!StringUtils.isBlank(source)) {
					this.sources.add(source);
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HasLabel) {
			final HasLabel other = (HasLabel) obj;
			return Objects.equal(relationshipId, other.relationshipId) && Objects.equal(type, other.type)
					&& (Objects.equal(concept, other.concept) && Objects.equal(label, other.label));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(relationshipId, concept, label, type);
	}

	@StartNode
	private Concept concept;
	@EndNode
	private Label label;
	private LabelType type;
	private List<String> sources;
	@GraphId
	private Long relationshipId;

}
