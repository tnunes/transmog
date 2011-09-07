package org.biosemantics.disambiguation.service.local.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.biosemantics.disambiguation.common.RelationshipTypeConstant;
import org.biosemantics.disambiguation.service.local.NoteStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Required;

public class NoteStorageServiceLocalImpl implements NoteStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node noteParentNode;
	private ValidationUtility validationUtility;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public NoteStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
		this.noteParentNode = this.graphStorageTemplate.getParentNode(RelationshipTypeConstant.NOTES);
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Override
	public long createNote(Note note) {
		validationUtility.validateNote(note);
		return createNoteNode(note).getId();
	}

	@Override
	public Node createNoteNode(Note note) {
		Node noteNode = graphStorageTemplate.getGraphDatabaseService().createNode();
		noteParentNode.createRelationshipTo(noteNode, RelationshipTypeConstant.NOTE);
		noteNode.setProperty(PropertyConstant.TYPE.name(), note.getNoteType().getId());
		noteNode.setProperty(PropertyConstant.LANGUAGE.name(), note.getLanguage().name());
		noteNode.setProperty(PropertyConstant.TEXT.name(), note.getText());
		String nowDateTime = sdf.format(new Date());
		noteNode.setProperty(PropertyConstant.DATE_TIME.name(), nowDateTime);
		return noteNode;
	}
}
