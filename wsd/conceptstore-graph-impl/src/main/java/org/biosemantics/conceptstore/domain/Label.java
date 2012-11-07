package org.biosemantics.conceptstore.domain;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import com.google.common.base.Objects;

@NodeEntity
public class Label {

	public Label(String text, String language) {
		super();
		this.text = text;
		this.language = language;
		this.id = this.text + this.language;
	}

	public Label() {
		super();
	}

	public Long getNodeId() {
		return nodeId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		// recalculate id
		this.id = this.text + this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
		// recalculate id
		this.id = this.text + this.language;
	}

	public String getLanguage() {
		return language;
	}

	public Iterable<Concept> getRelatedConcepts() {
		return concepts;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Label) {
			final Label other = (Label) obj;
			return Objects.equal(nodeId, other.nodeId) && Objects.equal(text, other.text)
					&& Objects.equal(language, other.language);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nodeId, text, language);
	}

	// fails with a DataIntegrityViolationException
	@Indexed(unique = true)
	private String id;
	@GraphId
	private Long nodeId;
	@Indexed
	private String text;
	private String language;
	@RelatedTo(type = "HAS_LABEL", direction = Direction.INCOMING)
	@Fetch
	private Iterable<Concept> concepts;
}
