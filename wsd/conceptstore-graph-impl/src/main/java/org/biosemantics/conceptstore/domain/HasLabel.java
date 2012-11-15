package org.biosemantics.conceptstore.domain;

import java.util.*;

import org.apache.commons.lang.*;
import org.springframework.data.neo4j.annotation.*;

import com.google.common.base.*;

@RelationshipEntity(type = "HAS_LABEL", useShortNames = true)
public class HasLabel {

	public HasLabel() {
		super();
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

	public Set<String> getSources() {
		// returning coppied list: effective java
		if (sources != null && !sources.isEmpty()) {
			return new HashSet<String>(sources);
		}
		return new HashSet<String>();
	}

	public void addSources(String... sources) {
		if (sources != null) {
			if (this.sources == null) {
				this.sources = new HashSet<String>();
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
	private Set<String> sources;
	@GraphId
	private Long relationshipId;

}
