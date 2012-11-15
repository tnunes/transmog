package org.biosemantics.conceptstore.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.google.common.base.Objects;

@RelationshipEntity
public class Rlsp {

	public Rlsp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Concept getFromConcept() {
		return fromConcept;
	}

	public Concept getToConcept() {
		return toConcept;
	}

	public int getStrength() {
		return strength;
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

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rlsp) {
			final Rlsp other = (Rlsp) obj;
			return (Objects.equal(fromConcept, other.fromConcept) && Objects.equal(toConcept, other.toConcept));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(fromConcept, toConcept);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("from", fromConcept).add("to", toConcept).add("strength", strength)
				.add("sources", sources).toString();
	}

	@StartNode
	private Concept fromConcept;
	@EndNode
	private Concept toConcept;
	private int strength;
	private Set<String> sources;
	@GraphId
	private Long relationshipId;

}
