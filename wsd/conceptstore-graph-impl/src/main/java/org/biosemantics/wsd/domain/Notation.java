package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.google.common.base.Objects;

@NodeEntity
public class Notation {

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
	@Indexed(indexName = "notationSource")
	private String source;
	@Indexed(indexName = "notationCode")
	private String code;

}
