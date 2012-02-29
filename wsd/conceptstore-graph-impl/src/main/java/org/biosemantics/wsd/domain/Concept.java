package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.template.Neo4jOperations;

import com.google.common.base.Objects;

@NodeEntity
public class Concept {

	public Concept(String id, ConceptType type) {
		super();
		this.id = id;
		this.type = type;
	}

	public Concept() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Related relatedTo(Neo4jOperations neo4jOperations, Concept otherConcept, int strength, String predicate,
			String source) {
		Related related = new Related(this, otherConcept, strength, predicate, source);
		neo4jOperations.save(related);
		return related;
	}
	
	public Child hasChild(Neo4jOperations neo4jOperations, Concept otherConcept, int strength, String predicate,
			String source) {
		Child child = new Child(this, otherConcept, strength, predicate, source);
		neo4jOperations.save(child);
		return child;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public String getId() {
		return id;
	}

	public ConceptType getType() {
		return type;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Concept) {
			final Concept other = (Concept) obj;
			return Objects.equal(id, other.id) && Objects.equal(type, other.type);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, type);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", getId()).add("type", getType()).toString();
	}

	@GraphId
	private Long nodeId;
	@Indexed
	private String id;
	@Indexed
	private ConceptType type;

}
