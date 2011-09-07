package org.biosemantics.disambiguation.domain.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.neo4j.graphdb.Node;

public class LabelImpl implements Label {

	private static final long serialVersionUID = 2625024353276251601L;
	private Node underlyingNode;

	public LabelImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public Language getLanguage() {
		return Language.valueOf((String) underlyingNode.getProperty(PropertyConstant.LANGUAGE.name()));
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(PropertyConstant.TEXT.name());
	}

	@Override
	public long getId() {
		return underlyingNode.getId();
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public String[] getTags() {
		return (String[]) underlyingNode.getProperty(PropertyConstant.TAGS.name());
	}

}
