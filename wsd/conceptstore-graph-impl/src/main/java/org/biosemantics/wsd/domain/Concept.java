package org.biosemantics.wsd.domain;

import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
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

	public InScheme inScheme(Neo4jOperations neo4jOperations, Concept scheme, int strength, String predicate,
			String source) {
		InScheme inScheme = new InScheme(this, scheme, strength, predicate, source);
		neo4jOperations.save(inScheme);
		return inScheme;
	}

	public Child hasChild(Neo4jOperations neo4jOperations, Concept otherConcept, int strength, String predicate,
			String source) {
		Child child = new Child(this, otherConcept, strength, predicate, source);
		neo4jOperations.save(child);
		return child;
	}

	public HasLabel hasLabel(Neo4jOperations neo4jOperations, Label label, LabelType labelType, String source) {
		HasLabel hasLabel = new HasLabel(this, label, labelType, source);
		neo4jOperations.save(hasLabel);
		return hasLabel;
	}

	public HasNotation hasNotation(Neo4jOperations neo4jOperations, Notation notation, String source) {
		HasNotation hasNotation = new HasNotation(this, notation, source);
		neo4jOperations.save(hasNotation);
		return hasNotation;
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
	@Indexed(indexName = "conceptType")
	private ConceptType type;
	@RelatedTo(type = "HAS_LABEL", direction = Direction.OUTGOING)
	Set<Label> labels;

}
