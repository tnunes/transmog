package org.biosemantics.conceptstore.domain.impl;

import java.util.*;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Label;
import org.neo4j.graphdb.*;

import com.google.common.base.*;

public class LabelImpl implements Label {

	public LabelImpl(Node node) {
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Label#getId()
	 */
	@Override
	public long getId() {
		return node.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Label#getText()
	 */
	@Override
	public String getText() {
		return (String) node.getProperty("text");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Label#getLanguage()
	 */
	@Override
	public String getLanguage() {
		return (String) node.getProperty("language");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Label#getRelatedConcepts()
	 */
	@Override
	public Collection<Concept> getRelatedConcepts() {
		Set<Concept> concepts = new HashSet<Concept>();
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, RlspType.HAS_LABEL);
		for (Relationship relationship : relationships) {
			concepts.add(new ConceptImpl(relationship.getStartNode()));
		}
		return concepts;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LabelImpl && node.equals(((LabelImpl) obj).node);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("nodeId", node.getId()).add("text", getText())
				.add("language", getLanguage()).toString();
	}

	private Node node;

}
