package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.Source;
import org.neo4j.graphdb.Node;

public class SourceImpl implements Source {

	private Node underlyingNode;
	public static final String SOURCE_TYPE_PROPERTY = "sourceType";
	public static final String VALUE_PROPERTY = "value";
	public static final String UUID_PROPERTY = "uuid";

	public SourceImpl(Node node) {
		super();
		this.underlyingNode = node;
	}

	@Override
	public String getValue() {
		return (String) underlyingNode.getProperty(VALUE_PROPERTY);
	}

	@Override
	public SourceType getSourceType() {
		return SourceType.valueOf((String) underlyingNode.getProperty(SOURCE_TYPE_PROPERTY));
	}

	@Override
	public String getUuid() {
		return (String) underlyingNode.getProperty(UUID_PROPERTY);
	}

	@Override
	public boolean equals(final Object labelImpl) {
		if (labelImpl instanceof LabelImpl) {
			return this.underlyingNode.equals(((LabelImpl) labelImpl).getUnderlyingNode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingNode.hashCode();
	}

}
