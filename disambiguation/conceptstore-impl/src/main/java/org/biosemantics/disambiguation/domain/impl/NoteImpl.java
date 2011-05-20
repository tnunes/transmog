package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.domain.Source;
import org.neo4j.graphdb.Node;

public class NoteImpl implements Note {

	private static final long serialVersionUID = -4163123163985347673L;
	public static final String NOTE_TYPE_PROPERTY = "noteType";
	public static final String LANGUAGE_PROPERTY = "language";
	public static final String TEXT_PROPERTY = "text";
	public static final String DATE_TIME_PROPERTY = "dateTime";
	private Node underlyingNode;

	public NoteImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public NoteType getNoteType() {
		return NoteType.fromId((Integer) underlyingNode.getProperty(NOTE_TYPE_PROPERTY));
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}

	@Override
	public Language getLanguage() {
		return LanguageImpl.valueOf((String) underlyingNode.getProperty(LANGUAGE_PROPERTY));
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
	public Source[] getSources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreatedDateTime() {
		return (String) underlyingNode.getProperty(DATE_TIME_PROPERTY);
	}

}
