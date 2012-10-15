package org.biosemantics.wsd.domain;

import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import com.google.common.base.Objects;

@NodeEntity
public class Notation {

	public Notation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Notation(String source, String code) {
		super();
		this.source = source;
		this.code = code;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public String getSource() {
		return source;
	}

	public String getCode() {
		return code;
	}

	public Set<Concept> getRelatedConcepts() {
		return concepts;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Notation) {
			final Notation other = (Notation) obj;
			return Objects.equal(source, other.source) && Objects.equal(code, other.code);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(source, code);
	}

	@GraphId
	private Long nodeId;
	private String source;
	@Indexed
	private String code;
	@RelatedTo(type = "HAS_NOTATION", direction = Direction.INCOMING)
	@Fetch
	private Set<Concept> concepts;

}
