package org.biosemantics.disambiguation.domain.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.conceptstore.common.domain.DomainType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.neo4j.graphdb.Node;

public class NotationImpl implements Notation {

	private static final long serialVersionUID = -8506731919995962487L;

	private Node underlyingNode;

	public NotationImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public String getCode() {
		return (String) underlyingNode.getProperty(PropertyConstant.CODE.name());
	}

	@Override
	public String getDomain() {
		return (String) underlyingNode.getProperty(PropertyConstant.DOMAIN.name());
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.fromId((Integer) underlyingNode.getProperty(PropertyConstant.DOMAIN_TYPE.name()));
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
	public String[] getTags() {
		return (String[]) underlyingNode.getProperty(PropertyConstant.TAGS.name());
	}

}
