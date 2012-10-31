package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.neo4j.graphdb.Node;

public class NoteImpl implements Note {

	private static final long serialVersionUID = -4163123163985347673L;
	private Node underlyingNode;

	public NoteImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public NoteType getNoteType() {
		return NoteType.fromId((Integer) underlyingNode.getProperty(PropertyConstant.TYPE.name()));
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(PropertyConstant.TEXT.name());
	}

	@Override
	public Language getLanguage() {
		return Language.valueOf((String) underlyingNode.getProperty(PropertyConstant.LANGUAGE.name()));
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
	public String getCreatedDateTime() {
		return (String) underlyingNode.getProperty(PropertyConstant.DATE_TIME.name());
	}

	@Override
	public String[] getTags() {
		return (String[]) underlyingNode.getProperty(PropertyConstant.TAGS.name());
	}

}
