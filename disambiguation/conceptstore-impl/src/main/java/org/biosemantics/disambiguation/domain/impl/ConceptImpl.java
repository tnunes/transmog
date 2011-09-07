package org.biosemantics.disambiguation.domain.impl;

import static org.biosemantics.disambiguation.common.PropertyConstant.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.disambiguation.common.RelationshipTypeConstant;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptImpl implements Concept {

	private static final long serialVersionUID = 851453341589506946L;
	private static final Logger logger = LoggerFactory.getLogger(ConceptImpl.class);
	private Node underlyingNode;

	public ConceptImpl(Node node) {
		this.underlyingNode = node;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public String getUuid() {
		return (String) underlyingNode.getProperty(UUID.name());
	}

	/*
	 * (non-Javadoc)
	 * @see org.biosemantics.conceptstore.common.domain.Concept#getLabelsByType(org.biosemantics.conceptstore.common.domain.Label.LabelType)
	 * Need a way to speed up this method, the response time is unpredictable
	 */
	@Override
	public Collection<ConceptLabel> getLabels() {
		long start = System.currentTimeMillis();
		Collection<ConceptLabel> labels = new HashSet<ConceptLabel>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(RelationshipTypeConstant.HAS_LABEL,
				Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			LabelType labelType = LabelType.fromId(((Integer) relationship.getProperty(LABEL_TYPE.name())));
			labels.add(new ConceptLabelImpl(new LabelImpl(relationship.getEndNode()), labelType));
		}
		logger.trace("in getLabelsByType time taken: {}(ms)", (System.currentTimeMillis() - start));
		return labels;
	}

	@Override
	public Collection<Notation> getNotations() {
		final List<Notation> notations = new ArrayList<Notation>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(RelationshipTypeConstant.HAS_NOTATION,
				Direction.OUTGOING);
		for (Relationship rel : relationships) {
			notations.add(new NotationImpl(rel.getEndNode()));
		}
		return notations;
	}

	@Override
	public Collection<Note> getNotes() {
		final List<Note> notes = new ArrayList<Note>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(RelationshipTypeConstant.HAS_NOTE,
				Direction.OUTGOING);
		for (Relationship rel : relationships) {
			notes.add(new NoteImpl(rel.getEndNode()));
		}
		return notes;
	}

	@Override
	public String[] getTags() {
		return (String[]) underlyingNode.getProperty(TAGS.name());
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

	@Override
	public ConceptType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
