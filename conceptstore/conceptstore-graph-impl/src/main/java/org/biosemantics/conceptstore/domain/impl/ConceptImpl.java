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
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

public class ConceptImpl implements Concept {

	public ConceptImpl(Node node) {
		this.node = node;
		relationshipIndex = node.getGraphDatabase().index().forRelationships("Rlsp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Concept#getLabels()
	 */
	@Override
	public Collection<Label> getLabels() {
		long start = System.currentTimeMillis();
		Set<Label> labels = new HashSet<Label>();
		IndexHits<Relationship> relationships = relationshipIndex.get("rlspType", RlspType.HAS_LABEL.toString(),
				this.node, null);
		for (Relationship relationship : relationships) {
			labels.add(new LabelImpl(relationship.getEndNode()));
		}
		logger.trace("time taken: {} ()", System.currentTimeMillis() - start);
		return labels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.Concept#getNotations()
	 */
	@Override
	public Collection<Notation> getNotations() {
		long start = System.currentTimeMillis();
		Set<Notation> notations = new HashSet<Notation>();
		IndexHits<Relationship> relationships = relationshipIndex.get("rlspType", RlspType.HAS_NOTATION.toString(),
				this.node, null);
		for (Relationship relationship : relationships) {
			notations.add(new NotationImpl(relationship.getEndNode()));
		}
		logger.trace("time taken: {} ()", System.currentTimeMillis() - start);
		return notations;

	}

	@Override
	public Collection<Concept> getInSchemes() {
		long start = System.currentTimeMillis();
		Set<Concept> schemes = new HashSet<Concept>();
		IndexHits<Relationship> relationships = relationshipIndex.get("rlspType", RlspType.IN_SCHEME.toString(),
				this.node, null);
		for (Relationship relationship : relationships) {
			schemes.add(new ConceptImpl(relationship.getEndNode()));
		}
		logger.trace("time taken: {} ()", System.currentTimeMillis() - start);
		return schemes;
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
	public Long getId() {
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
	private RelationshipIndex relationshipIndex;
	private static final Logger logger = LoggerFactory.getLogger(ConceptImpl.class);
}
