package org.biosemantics.conceptstore.domain;

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
		this.id = this.source + this.code;
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

	public void setSource(String source) {
		this.source = source;
		// recalculate id
		this.id = this.source + this.code;
	}

	public void setCode(String code) {
		this.code = code;
		// recalculate id
		this.id = this.source + this.code;
	}

	public Iterable<Concept> getRelatedConcepts() {
		return concepts;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Notation) {
			final Notation other = (Notation) obj;
			return Objects.equal(nodeId, other.nodeId) && Objects.equal(source, other.source)
					&& Objects.equal(code, other.code);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nodeId, source, code);
	}

	// fails with a DataIntegrityViolationException
	@Indexed(unique = true)
	private String id;
	@GraphId
	private Long nodeId;
	private String source;
	@Indexed
	private String code;
	@RelatedTo(type = "HAS_NOTATION", direction = Direction.INCOMING)
	@Fetch
	private Iterable<Concept> concepts;

}
