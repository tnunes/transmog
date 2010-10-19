package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.biosemantics.disambiguation.service.impl.DefaultRelationshipType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class ConceptImpl implements Concept {

	private static final long serialVersionUID = 851453341589506946L;

	private static final String UUID_PROPERTY = "uuid";

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
		checkArgument(! uuid.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, uuid);
		underlyingNode.setProperty(UUID_PROPERTY, uuid);
	}

	@Override
	public Collection<Label> getLabels() {
		final List<Label> labels = new ArrayList<Label>();
		Iterable<Relationship> relationships = underlyingNode.getRelationships(DefaultRelationshipType.HAS_LABEL,
				Direction.OUTGOING);
		for (Relationship rel : relationships) {
			labels.add(new LabelImpl(rel.getEndNode()));
		}
		return labels;
	}

	public void setLabels(Collection<Label> labels) {
		checkNotNull(labels);
		checkArgument(! labels.isEmpty(), ErrorMessage.EMPTY_COLLECTION_MSG, "labels");
		for (Label label : labels) {
			LabelImpl labelImpl = (LabelImpl) label;
			underlyingNode.createRelationshipTo(labelImpl.getUnderlyingNode(), DefaultRelationshipType.HAS_LABEL);
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
		checkArgument(! notations.isEmpty(), ErrorMessage.EMPTY_COLLECTION_MSG, "notations");
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
		checkArgument(! notes.isEmpty(), ErrorMessage.EMPTY_COLLECTION_MSG, "notes");
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

	public ConceptImpl withLabels(Collection<Label> labels) {
		setLabels(labels);
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
