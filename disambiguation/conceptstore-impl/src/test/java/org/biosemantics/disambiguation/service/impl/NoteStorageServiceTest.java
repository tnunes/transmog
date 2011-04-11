package org.biosemantics.disambiguation.service.impl;

import junit.framework.Assert;

import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.domain.Note.NoteType;
import org.biosemantics.conceptstore.utils.domain.impl.NoteImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.biosemantics.disambiguation.service.local.NoteStorageServiceLocal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NoteStorageServiceTest extends AbstractTransactionalDataSource {

	@Autowired
	NoteStorageServiceLocal noteStorageServiceLocal;

	@Test
	public void createNote() {
		Note note = noteStorageServiceLocal
				.createNote(new NoteImpl(
						NoteType.DEFINITION,
						LanguageImpl.EN,
						"an environment or material in which something develops; a surrounding medium or structure : free choices become the matrix of human life."));
		Assert.assertNotNull(note);
	}

	@Test(expected = NullPointerException.class)
	public void createNullNote() {
		noteStorageServiceLocal.createNote(null);
	}

	@Test(expected = NullPointerException.class)
	public void createNoteWithNullText() {
		noteStorageServiceLocal.createNote(new NoteImpl(NoteType.DEFINITION, LanguageImpl.EN, null));

	}

	@Test(expected = NullPointerException.class)
	public void createNoteWithNullLanguage() {
		noteStorageServiceLocal.createNote(new NoteImpl(NoteType.DEFINITION, null, "some"));

	}

	@Test(expected = NullPointerException.class)
	public void createNoteWithNullType() {
		noteStorageServiceLocal.createNote(new NoteImpl(null, LanguageImpl.EN, "some"));

	}

	@Test(expected = IllegalArgumentException.class)
	public void createLabelWithBlankText() {
		noteStorageServiceLocal.createNote(new NoteImpl(NoteType.DEFINITION, LanguageImpl.EN, " "));

	}

}
