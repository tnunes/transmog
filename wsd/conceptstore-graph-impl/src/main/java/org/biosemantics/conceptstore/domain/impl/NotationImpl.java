package org.biosemantics.conceptstore.domain.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Notation;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class NotationImpl implements Notation {

	public NotationImpl(Node node) {
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.domain.impl.Notation#getRelatedConcepts()
	 */
	@Override
	public Collection<Concept> getRelatedConcepts() {
		Set<Concept> concepts = new HashSet<Concept>();
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, RlspType.HAS_NOTATION);
		for (Relationship relationship : relationships) {
			concepts.add(new ConceptImpl(relationship.getStartNode()));
		}
		return concepts;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Notation#getId()
	 */
	public long getId() {
		return node.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Notation#getSource()
	 */
	@Override
	public String getSource() {
		return (String) node.getProperty("source");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Notation#getCode()
	 */
	@Override
	public String getCode() {
		return (String) node.getProperty("code");
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NotationImpl && node.equals(((NotationImpl) obj).node);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("nodeId", node.getId()).add("source", getSource())
				.add("code", getCode()).toString();
	}

	private Node node;

}
