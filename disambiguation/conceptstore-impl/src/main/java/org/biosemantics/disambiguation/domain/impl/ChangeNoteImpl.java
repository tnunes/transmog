package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Note;
import org.joda.time.DateTime;

public class ChangeNoteImpl implements Note {

	private static final long serialVersionUID = 2504882105565095145L;

	public enum ChangeType {
		CONCEPT_CREATED, RELATIONSHIP_CREATED
	}

	private Language language;
	private ChangeType changeType;
	private DateTime changeAt;
	private String changeBy;

	public NoteType getNoteType() {
		return NoteType.CHANGE_NOTE;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public DateTime getChangeAt() {
		return changeAt;
	}

	public void setChangeAt(DateTime changeAt) {
		this.changeAt = changeAt;
	}

	public String getChangeBy() {
		return changeBy;
	}

	public void setChangeBy(String changeBy) {
		this.changeBy = changeBy;
	}

	@Override
	public String getText() {
		// TODO custom toString() needed here
		return toString();
	}

}
