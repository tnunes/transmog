package org.biosemantics.wsd.domain;

import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

import com.google.common.base.Objects;

@NodeEntity
public class Label {

	public Label(String text, String language) {
		super();
		this.text = text;
		this.language = language;
	}

	public Label() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getNodeId() {
		return nodeId;
	}

	public String getText() {
		return text;
	}

	public String getLanguage() {
		return language;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Label) {
			final Label other = (Label) obj;
			return Objects.equal(text, other.text) && Objects.equal(language, other.language);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(text, language);
	}

	@GraphId
	private Long nodeId;
	@Indexed(indexName = "labelTxt", indexType=IndexType.FULLTEXT)
	private String text;
	private String language;
	@RelatedTo(type = "HAS_LABEL", direction = Direction.INCOMING)
	Set<Concept> concepts;
}
