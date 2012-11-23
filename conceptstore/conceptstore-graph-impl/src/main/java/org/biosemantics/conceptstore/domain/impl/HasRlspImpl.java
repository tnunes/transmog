package org.biosemantics.conceptstore.domain.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

public class HasRlspImpl implements HasRlsp {

	public HasRlspImpl(Relationship relationship) {
		this.relationship = relationship;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.HasRlsp#getSources()
	 */
	@Override
	public Collection<String> getSources() {
		return new HashSet<String>(Arrays.asList((String[]) relationship.getProperty("sources")));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.HasRlsp#getType()
	 */
	@Override
	public String getType() {
		return relationship.getType().name();
	}

	@Override
	public Concept getStartConcept() {
		return new ConceptImpl(relationship.getStartNode());
	}

	@Override
	public Concept getEndConcept() {
		return new ConceptImpl(relationship.getEndNode());
	}

	@Override
	public Concept getOtherConcept(long id) {
		Node foundNode = relationship.getGraphDatabase().getNodeById(id);
		if (foundNode == null) {
			throw new IllegalArgumentException("node not found for id " + id);
		}
		return new ConceptImpl(relationship.getOtherNode(foundNode));
	}

	@Override
	public Collection<Label> getLabels() {
		Collection<Label> labels = null;
		try {
			Long id = Long.valueOf(relationship.getType().name());
			Node node = relationship.getGraphDatabase().getNodeById(id);
			labels = new ConceptImpl(node).getLabels();
		} catch (Exception e) {
			logger.error("getLabels() failed with exception ", e);
		}
		return labels;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof HasRlspImpl && relationship.equals(((HasRlspImpl) obj).relationship);
	}

	@Override
	public int hashCode() {
		return relationship.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("relationshipId", relationship.getId())
				.add("type", relationship.getType()).add("labels", getLabels()).add("startConcept", getStartConcept()).add("endConcept", getEndConcept()).add("sources", getSources()).toString();
	}

	private Relationship relationship;
	private static final Logger logger = LoggerFactory.getLogger(HasRlspImpl.class);

}
