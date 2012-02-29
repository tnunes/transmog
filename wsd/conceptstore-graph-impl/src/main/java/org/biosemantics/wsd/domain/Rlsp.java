package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.StartNode;

import com.google.common.base.Objects;

public abstract class Rlsp {

	public Rlsp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Rlsp(Concept fromConcept, Concept toConcept, int strength, String predicate, String source) {
		super();
		this.fromConcept = fromConcept;
		this.toConcept = toConcept;
		this.strength = strength;
		this.predicate = predicate;
		this.source = source;
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

	public String getPredicate() {
		return predicate;
	}

	public String getSource() {
		return source;
	}

	public Long getRelationshipId() {
		return relationshipId;
	}

	@StartNode
	private Concept fromConcept;
	@EndNode
	private Concept toConcept;
	private int strength;
	private String predicate;
	private String source;
	@GraphId
	private Long relationshipId;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Related) {
			final Related other = (Related) obj;
			return Objects.equal(source, other.getSource())
					&& (Objects.equal(fromConcept, other.getFromConcept()) && Objects.equal(toConcept,
							other.getToConcept()));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(fromConcept, toConcept, strength, predicate, source);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("from", getFromConcept()).add("to", getToConcept())
				.add("strength", getStrength()).add("predicate", getPredicate()).add("source", getSource()).toString();
	}

}
