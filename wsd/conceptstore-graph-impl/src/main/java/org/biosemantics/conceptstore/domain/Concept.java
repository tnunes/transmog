package org.biosemantics.conceptstore.domain;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import com.google.common.base.Objects;

@NodeEntity
public class Concept {

	public Concept(ConceptType type) {
		super();
		this.type = type;
	}

	public Concept() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Rlsp addRelationshipIfNoneExists(Neo4jTemplate template, Concept otherConcept, String relationshipType,
			int strength, String... sources) {
		Rlsp rlsp = template.createRelationshipBetween(this, otherConcept, Rlsp.class, relationshipType, false);
		rlsp.setStrength(strength);
		rlsp.addSources(sources);
		return template.save(rlsp);
	}

	public Rlsp addRelationshipIfNoBidirectionalRlspExists(Neo4jTemplate template, Concept otherConcept,
			String relationshipType, int strength, String... sources) {
		Rlsp rlsp = template.getRelationshipBetween(this, otherConcept, Rlsp.class, relationshipType);
		if (rlsp == null) {
			rlsp = template.getRelationshipBetween(otherConcept, this, Rlsp.class, relationshipType);
		}
		if (rlsp == null) {
			rlsp = template.createRelationshipBetween(this, otherConcept, Rlsp.class, relationshipType, false);
			rlsp.setStrength(strength);
			rlsp.addSources(sources);
			return template.save(rlsp);
		}
		return rlsp;
	}

	public Rlsp addRelationship(Neo4jTemplate template, Concept otherConcept, String relationshipType, int strength,
			String... sources) {
		Rlsp rlsp = template.createRelationshipBetween(this, otherConcept, Rlsp.class, relationshipType, true);
		rlsp.setStrength(strength);
		rlsp.addSources(sources);
		return template.save(rlsp);
	}

	public HasLabel addLabelIfNoneExists(Neo4jTemplate template, Label label, LabelType labelType, String... sources) {
		HasLabel hasLabel = template.createRelationshipBetween(this, label, HasLabel.class,
				RlspType.HAS_LABEL.toString(), false);
		hasLabel.addSources(sources);
		hasLabel.setType(labelType);
		return template.save(hasLabel);
	}

	public HasNotation addNotationIfNoneExists(Neo4jTemplate template, Notation notation, String... sources) {
		HasNotation hasNotation = template.createRelationshipBetween(this, notation, HasNotation.class,
				RlspType.HAS_NOTATION.toString(), false);
		hasNotation.addSources(sources);
		return template.save(hasNotation);
	}

	public Long getNodeId() {
		return nodeId;
	}

	public ConceptType getType() {
		return type;
	}

	public Iterable<Label> getLabels() {
		return labels;
	}

	public Iterable<Notation> getNotations() {
		return notations;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Concept) {
			final Concept other = (Concept) obj;
			return Objects.equal(nodeId, other.nodeId) && Objects.equal(type, other.type);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nodeId, type);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("nodeId", getNodeId()).add("type", getType()).toString();
	}

	@GraphId
	private Long nodeId;
	@Indexed
	private ConceptType type;
	@Fetch
	@RelatedTo(type = "HAS_LABEL", direction = Direction.OUTGOING)
	private Iterable<Label> labels;
	@Fetch
	@RelatedTo(type = "HAS_NOTATION", direction = Direction.OUTGOING)
	private Iterable<Notation> notations;

}
