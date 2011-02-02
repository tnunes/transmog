package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
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

	public void setCode(String code) {
		checkNotNull(code);
		checkArgument(!code.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, code);
		underlyingNode.setProperty(CODE_PROPERTY, code);
	}

	@Override
	public String getDomainUuid() {
		return (String) underlyingNode.getProperty(DOMAIN_UUID_PROPERTY);
	}

	private void setDomainUuid(String domainUuid) {
		checkNotNull(domainUuid);
		checkArgument(!domainUuid.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, domainUuid);
		underlyingNode.setProperty(DOMAIN_UUID_PROPERTY, domainUuid);
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

	public NotationImpl withDomainUuid(String domainUuid) {
		setDomainUuid(domainUuid);
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
