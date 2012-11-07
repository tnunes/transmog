package org.biosemantics.conceptstore.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.google.common.base.Objects;

@RelationshipEntity(type = "HAS_NOTATION", useShortNames = true)
public class HasNotation {

	public HasNotation(Concept concept, Notation notation, String... sources) {
		super();
		this.concept = concept;
		this.notation = notation;
		addSources(sources);
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

	public Long getRelationshipId() {
		return relationshipId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HasNotation) {
			final HasNotation other = (HasNotation) obj;
			return (Objects.equal(relationshipId, other.relationshipId) && Objects.equal(concept, other.concept) && Objects
					.equal(notation, other.notation));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(relationshipId, concept, notation);
	}

	@StartNode
	private Concept concept;
	@EndNode
	private Notation notation;
	private List<String> sources = new ArrayList<String>();
	@GraphId
	private Long relationshipId;

}
