package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.biosemantics.disambiguation.service.impl.DefaultRelationshipType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class NotationImpl implements Notation {

	private static final long serialVersionUID = -8506731919995962487L;

	private static final String CODE_PROPERTY = "code";

	private Node underlyingNode;

	public NotationImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public String getCode() {
		return (String) underlyingNode.getProperty(CODE_PROPERTY);
	}

	public void setCode(String code) {
		checkNotNull(code);
		checkArgument(!code.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, code);
		underlyingNode.setProperty(CODE_PROPERTY, code);
	}

	@Override
	public Concept getDomain() {
		Relationship relationship = checkNotNull(underlyingNode.getSingleRelationship(
				DefaultRelationshipType.HAS_DOMAIN, Direction.OUTGOING));
		return new ConceptImpl(relationship.getOtherNode(underlyingNode));
	}

	private void setDomain(Concept concept) {
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		underlyingNode.createRelationshipTo(conceptImpl.getUnderlyingNode(), DefaultRelationshipType.HAS_DOMAIN);
	}

	@Override
	public boolean equals(final Object notationImpl) {
		if (notationImpl instanceof NotationImpl) {
			return this.underlyingNode.equals(((NotationImpl) notationImpl).underlyingNode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingNode.hashCode();
	}

	public NotationImpl withDomain(Concept domain) {
		setDomain(domain);
		return this;
	}

	public NotationImpl withCode(String code) {
		setCode(code);
		return this;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

}
