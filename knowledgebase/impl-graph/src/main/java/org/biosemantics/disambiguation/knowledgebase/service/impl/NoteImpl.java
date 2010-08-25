package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.Note;
import org.biosemantics.disambiguation.knowledgebase.service.NoteType;
import org.neo4j.graphdb.Node;

public class NoteImpl implements Note {

	private static final String NOTE_TYPE_PROPERTY = "noteType";
	private static final String LANGUAGE_PROPERTY = "language";
	private static final String TEXT_PROPERTY = "text";

	private Node underlyingNode;

	public NoteImpl(Node node) {
		super();
		this.underlyingNode = node;
	}

	@Override
	public NoteType getNoteType() {
		return NoteType.valueOf((String) underlyingNode.getProperty(NOTE_TYPE_PROPERTY));
	}

	public void setNoteType(NoteType noteType) {
		if (noteType == null)
			throw new NullArgumentException("noteType");
		underlyingNode.setProperty(NOTE_TYPE_PROPERTY, noteType.name());
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}

	public void setText(String text) {
		if (StringUtils.isBlank(text))
			throw new IllegalArgumentException("text cannot be blank");
		underlyingNode.setProperty(TEXT_PROPERTY, text);
	}

	@Override
	public Language getLanguage() {
		return Language.valueOf((String) underlyingNode.getProperty(LANGUAGE_PROPERTY));
	}

	public void setLanguage(Language language) {
		if (language == null)
			throw new NullArgumentException("language");
		underlyingNode.setProperty(LANGUAGE_PROPERTY, language.name());
	}

	@Override
	public boolean equals(final Object noteImpl) {
		if (noteImpl instanceof NoteImpl) {
			return this.underlyingNode.equals(((NoteImpl) noteImpl).getUnderlyingNode());
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

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	public NoteImpl withNoteType(NoteType noteType) {
		setNoteType(noteType);
		return this;
	}

	public NoteImpl withLanguage(Language language) {
		setLanguage(language);
		return this;
	}

	public NoteImpl withText(String text) {
		setText(text);
		return this;
	}

}
