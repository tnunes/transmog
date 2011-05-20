package org.biosemantics.disambiguation.domain.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Source;
import org.neo4j.graphdb.Node;

public class NotationImpl implements Notation {

	private static final long serialVersionUID = -8506731919995962487L;
	public static final String CODE_PROPERTY = "code";
	public static final String DOMAIN_UUID_PROPERTY = "domainUuid";

	private Node underlyingNode;

	public NotationImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public String getCode() {
		return (String) underlyingNode.getProperty(CODE_PROPERTY);
	}

	@Override
	public String getDomainUuid() {
		return (String) underlyingNode.getProperty(DOMAIN_UUID_PROPERTY);
	}

	@Override
	public long getId() {
		return underlyingNode.getId();
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

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public Source[] getSources() {
		// TODO Auto-generated method stub
		return null;
	}

}
