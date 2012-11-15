package org.biosemantics.conceptstore.domain;

import java.util.*;

import org.apache.commons.lang.*;
import org.springframework.data.neo4j.annotation.*;

import com.google.common.base.*;

@RelationshipEntity(type = "HAS_NOTATION", useShortNames = true)
public class HasNotation {

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

	public Set<String> getSources() {
		// returning copied Set: effective java
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
	private Set<String> sources;
	@GraphId
	private Long relationshipId;

}
