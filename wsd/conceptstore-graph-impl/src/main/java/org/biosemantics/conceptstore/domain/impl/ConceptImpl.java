package org.biosemantics.conceptstore.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class ConceptImpl implements Concept {

	public ConceptImpl(Node node) {
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Concept#getLabels()
	 */
	@Override
	public Collection<Label> getLabels() {
		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, RlspType.HAS_LABEL);
		Set<Label> labels = new HashSet<Label>();
		for (Relationship relationship : relationships) {
			labels.add(new LabelImpl(relationship.getEndNode()));
		}
		return labels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Concept#getNotations()
	 */
	@Override
	public Collection<Notation> getNotations() {
		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, RlspType.HAS_NOTATION);
		List<Notation> notations = new ArrayList<Notation>();
		for (Relationship relationship : relationships) {
			notations.add(new NotationImpl(relationship.getEndNode()));
		}
		return notations;

	}

	@Override
	public Collection<Concept> getInSchemes() {
		Iterable<Relationship> relationships = node.getRelationships(RlspType.IN_SCHEME, Direction.OUTGOING);
		Set<Concept> concepts = new HashSet<Concept>();
		for (Relationship relationship : relationships) {
			concepts.add(new ConceptImpl(relationship.getOtherNode(node)));
		}
		return concepts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Concept#getType()
	 */
	@Override
	public ConceptType getType() {
		return ConceptType.valueOf((String) node.getProperty("type"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Concept#getId()
	 */
	@Override
	public long getId() {
		return node.getId();
	}

	public void setType(ConceptType type) {
		node.setProperty("type", type.toString());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ConceptImpl && node.equals(((ConceptImpl) obj).node);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("nodeId", node.getId()).add("type", getType()).toString();
	}

	public Node getNode() {
		return node;
	}

	private final Node node;

}
