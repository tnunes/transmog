package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.neo4j.graphdb.Node;

public class NoteImpl implements Note {

	private static final long serialVersionUID = -4163123163985347673L;
	private static final String NOTE_TYPE_PROPERTY = "noteType";
	private static final String LANGUAGE_PROPERTY = "language";
	private static final String TEXT_PROPERTY = "text";
	private Node underlyingNode;

	public NoteImpl(Node node) {
		this.underlyingNode = node;
		underlyingNode.setProperty(NOTE_TYPE_PROPERTY, NoteType.DEFINITION);
	}

	@Override
	public NoteType getNoteType() {
		return NoteType.valueOf((String) underlyingNode.getProperty(NOTE_TYPE_PROPERTY));
	}

	public void setNoteType(NoteType noteType) {
		checkNotNull(noteType);
		underlyingNode.setProperty(NOTE_TYPE_PROPERTY, noteType.name());
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}

	public void setText(String text) {
		checkNotNull(text);
		checkArgument(text.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, text);
		underlyingNode.setProperty(TEXT_PROPERTY, text);
	}

	@Override
	public Language getLanguage() {
		return LanguageImpl.valueOf((String) underlyingNode.getProperty(LANGUAGE_PROPERTY));
	}

	public void setLanguage(Language language) {
		checkNotNull(language);
		underlyingNode.setProperty(LANGUAGE_PROPERTY, language.getLabel());
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
