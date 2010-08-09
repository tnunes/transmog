package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class ConceptImpl implements Concept {
	private static final String ID_PROPERTY = "id";

	private Node underlyingNode;

	public ConceptImpl(Node node) {
		this.underlyingNode = node;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public String getId() {
		return (String) underlyingNode.getProperty(ID_PROPERTY);
	}

	public void setId(String id) {
		if (id == null)
			throw new NullArgumentException("id");
		underlyingNode.setProperty(ID_PROPERTY, id);
	}

	@Override
	public Collection<Label> getLabels() {
		final List<Label> labels = new ArrayList<Label>();
		for (Relationship rel : underlyingNode.getRelationships(KnowledgebaseRelationshipType.HAS_LABEL,
				Direction.OUTGOING)) {
			labels.add(new LabelImpl(rel.getEndNode()));
		}
		return labels;
	}

	public void setLabels(Collection<Label> labels) {
		assert (labels != null);
		for (Label label : labels) {
			LabelImpl labelImpl = (LabelImpl) label;
			underlyingNode.createRelationshipTo(labelImpl.getUnderlyingNode(), KnowledgebaseRelationshipType.HAS_LABEL);
		}
	}

	@Override
	public Collection<Notation> getNotations() {
		final List<Notation> notations = new ArrayList<Notation>();
		for (Relationship rel : underlyingNode.getRelationships(KnowledgebaseRelationshipType.HAS_NOTATION,
				Direction.OUTGOING)) {
			notations.add(new NotationImpl(rel.getEndNode()));
		}
		return notations;
	}

	public void setNotations(Collection<Notation> notations) {
		assert (notations != null);
		for (Notation notation : notations) {
			NotationImpl notationImpl = (NotationImpl) notation;
			underlyingNode.createRelationshipTo(notationImpl.getUnderlyingNode(),
					KnowledgebaseRelationshipType.HAS_NOTATION);
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

	public ConceptImpl withId(String id) {
		setId(id);
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
}
