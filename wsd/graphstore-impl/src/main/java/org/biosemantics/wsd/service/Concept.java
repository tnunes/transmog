package org.biosemantics.wsd.service;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

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

	public Long getNodeId() {
		return nodeId;
	}

	public String getId() {
		return id;
	}

	public ConceptType getType() {
		return type;
	}
	
	public void setType(ConceptType type) {
		this.type = type;

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
	private ConceptType type;

	
}
