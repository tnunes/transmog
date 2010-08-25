package org.biosemantics.disambiguation.knowledgebase.service;

public interface Note {
	
	NoteType getNoteType();
	String getText();
	Language getLanguage();

}
