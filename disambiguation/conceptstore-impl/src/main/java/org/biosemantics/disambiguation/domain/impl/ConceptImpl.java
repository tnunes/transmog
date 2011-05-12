package org.biosemantics.disambiguation.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.disambiguation.service.local.impl.DefaultRelationshipType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ConceptImpl implements Concept {

	private static final long serialVersionUID = 851453341589506946L;
	public static final String UUID_PROPERTY = "uuid";
	private static final Logger logger = LoggerFactory.getLogger(ConceptImpl.class);
	public static final String LABEL_TYPE_RLSP_PROPERTY = "LABEL_TYPE";
	private Node underlyingNode;

	public ConceptImpl(Node node) {
		this.underlyingNode = node;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public String getUuid() {
		return (String) underlyingNode.getProperty(UUID_PROPERTY);
	}

	/*
	 * (non-Javadoc)
	 * @see org.biosemantics.conceptstore.common.domain.Concept#getLabelsByType(org.biosemantics.conceptstore.common.domain.Label.LabelType)
	 * Need a way to speed up this method, the response time is unpredictable
	 */
	@Override
	public Collection<ConceptLabel> getLabels() {
		//long start = System.currentTimeMillis();
		Collection<ConceptLabel> labels = new HashSet<ConceptLabel>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(DefaultRelationshipType.HAS_LABEL,
				Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			System.err.println(relationship.getEndNode().getProperty(LabelImpl.TEXT_PROPERTY));
			System.err.println(relationship.getStartNode().getPropertyKeys());
			LabelType labelType = LabelType.valueOf((String) relationship.getProperty(LABEL_TYPE_RLSP_PROPERTY));
			labels.add(new ConceptLabelImpl(new LabelImpl(relationship.getEndNode()), labelType));
		}
		//logger.info("in getLabelsByType time taken: {}(ms)", (System.currentTimeMillis() - start));
		return labels;
	}

	@Override
	public Collection<Notation> getNotations() {
		final List<Notation> notations = new ArrayList<Notation>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(DefaultRelationshipType.HAS_NOTATION,
				Direction.OUTGOING);
		for (Relationship rel : relationships) {
			notations.add(new NotationImpl(rel.getEndNode()));
		}
		return notations;
	}

	@Override
	public Collection<Note> getNotes() {
		final List<Note> notes = new ArrayList<Note>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(DefaultRelationshipType.HAS_NOTE,
				Direction.OUTGOING);
		for (Relationship rel : relationships) {
			notes.add(new NoteImpl(rel.getEndNode()));
		}
		return notes;
	}

	@Override
	public boolean equals(final Object conceptImpl) {
		if (conceptImpl instanceof ConceptImpl) {
			return this.underlyingNode.equals(((ConceptImpl) conceptImpl).underlyingNode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingNode.hashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
