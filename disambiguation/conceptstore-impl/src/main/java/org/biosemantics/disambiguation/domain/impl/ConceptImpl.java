package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.biosemantics.disambiguation.service.impl.DefaultRelationshipType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptImpl implements Concept {

	private static final long serialVersionUID = 851453341589506946L;
	public static final String UUID_PROPERTY = "uuid";
	public static final String LABEL_TYPE_PROPERTY = "labelType";
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
		return (String) underlyingNode.getProperty(UUID_PROPERTY);
	}

	public void setUuid(String uuid) {
		checkNotNull(uuid);
		checkArgument(!uuid.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, uuid);
		underlyingNode.setProperty(UUID_PROPERTY, uuid);
	}

	@Override
	public Collection<Label> getLabels() {
		final List<Label> labels = new ArrayList<Label>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(DefaultRelationshipType.HAS_LABEL);
		for (Relationship rel : relationships) {
			labels.add(new LabelImpl(rel.getEndNode()));
		}
		return labels;
	}

	/*
	 * (non-Javadoc)
	 * @see org.biosemantics.conceptstore.common.domain.Concept#getLabelsByType(org.biosemantics.conceptstore.common.domain.Label.LabelType)
	 * Need a way to speed up this method, the response time is unpredictable
	 */
	@Override
	public Collection<Label> getLabelsByType(LabelType labelType) {
		long start = System.currentTimeMillis();
		List<Label> labels = new ArrayList<Label>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(DefaultRelationshipType.HAS_LABEL,
				Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			if (LabelType.valueOf((String) relationship.getProperty(LABEL_TYPE_PROPERTY)) == labelType) {
				labels.add(new LabelImpl(relationship.getEndNode()));
			}
		}
		logger.info("in getLabelsByType time taken: {}(ms)", (System.currentTimeMillis() - start));
		return labels;
	}

	// @Override
	// public Collection<Label> getLabelsByType(LabelType labelType) {
	// List<Label> labels = new ArrayList<Label>();
	// long start = System.currentTimeMillis();
	//
	// Traverser traverser = underlyingNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
	// ReturnableEvaluator.ALL_BUT_START_NODE, DefaultRelationshipType.HAS_LABEL, Direction.OUTGOING);
	// logger.info("to get HAS_LABEL rlsps for a concept{}(ms)",(System.currentTimeMillis()-start));
	// start = System.currentTimeMillis();
	// Collection<Node> nodes = traverser.getAllNodes();
	// for (Node node : nodes) {
	// labels.add(new LabelImpl(node));
	// }
	// logger.info("iterate over rlsps for a  concept {}(ms)", (System.currentTimeMillis() - start));
	// return labels;
	// }

	public void setLabels(LabelType labelType, Collection<Label> labels) {
		checkNotNull(labels);
		checkArgument(!labels.isEmpty(), ErrorMessage.EMPTY_COLLECTION_MSG, "labels");
		for (Label label : labels) {
			LabelImpl labelImpl = (LabelImpl) label;
			Relationship relationship = underlyingNode.createRelationshipTo(labelImpl.getUnderlyingNode(),
					DefaultRelationshipType.HAS_LABEL);
			relationship.setProperty(LABEL_TYPE_PROPERTY, labelType.name());
		}
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

	public void setNotations(Collection<Notation> notations) {
		checkNotNull(notations);
		checkArgument(!notations.isEmpty(), ErrorMessage.EMPTY_COLLECTION_MSG, "notations");
		for (Notation notation : notations) {
			NotationImpl notationImpl = (NotationImpl) notation;
			underlyingNode.createRelationshipTo(notationImpl.getUnderlyingNode(), DefaultRelationshipType.HAS_NOTATION);
		}
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

	public void setNotes(Collection<Note> notes) {
		checkNotNull(notes);
		checkArgument(!notes.isEmpty(), ErrorMessage.EMPTY_COLLECTION_MSG, "notes");
		for (Note note : notes) {
			NoteImpl noteImpl = (NoteImpl) note;
			underlyingNode.createRelationshipTo(noteImpl.getUnderlyingNode(), DefaultRelationshipType.HAS_NOTE);
		}
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

	public ConceptImpl withUuid(String uuid) {
		setUuid(uuid);
		return this;
	}

	public ConceptImpl withLabels(LabelType labelType, Collection<Label> labels) {
		setLabels(labelType, labels);
		return this;
	}

	public ConceptImpl withNotations(Collection<Notation> notations) {
		setNotations(notations);
		return this;
	}

	public ConceptImpl withNotes(Collection<Note> notes) {
		setNotes(notes);
		return this;
	}

}
