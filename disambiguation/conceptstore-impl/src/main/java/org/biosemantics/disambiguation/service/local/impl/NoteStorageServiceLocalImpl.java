package org.biosemantics.disambiguation.service.local.impl;

import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.NoteImpl;
import org.biosemantics.disambiguation.service.local.NoteStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Required;

public class NoteStorageServiceLocalImpl implements NoteStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node noteParentNode;
	private ValidationUtility validationUtility;

	public NoteStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
		this.noteParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.NOTES);
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Override
	public Note createNote(Note note) {
		validationUtility.validateNote(note);
		return new NoteImpl(createNoteNode(note));
	}

	@Override
	public Node createNoteNode(Note note) {
		Node noteNode = graphStorageTemplate.getGraphDatabaseService().createNode();
		graphStorageTemplate.createRelationship(noteParentNode, noteNode, DefaultRelationshipType.NOTE);
		noteNode.setProperty(NoteImpl.NOTE_TYPE_PROPERTY, note.getNoteType().name());
		noteNode.setProperty(NoteImpl.LANGUAGE_PROPERTY, note.getLanguage().getLabel());
		noteNode.setProperty(NoteImpl.TEXT_PROPERTY, note.getText());
		return noteNode;
	}
}
