package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.Note;
import org.biosemantics.disambiguation.knowledgebase.service.NoteService;
import org.biosemantics.disambiguation.knowledgebase.service.NoteType;
import org.biosemantics.disambiguation.knowledgebase.validation.ValidationUtilityService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class NoteServiceImpl implements NoteService {

	private final GraphDatabaseService graphDb;
	private final Node noteFactoryNode;
	private ValidationUtilityService validationUtilityService;

	public NoteServiceImpl(GraphDatabaseService graphDatabaseService) {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDatabaseService");
		this.graphDb = graphDatabaseService;
		// explicitly starting transaction as constructor is called by spring.
		Transaction transaction = this.graphDb.beginTx();
		try {
			Relationship relationship = graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.NOTES, Direction.OUTGOING);
			if (relationship == null) {
				noteFactoryNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo(noteFactoryNode, KnowledgebaseRelationshipType.NOTES);
			} else {
				noteFactoryNode = relationship.getEndNode();
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	public void setValidationUtilityService(ValidationUtilityService validationUtilityService) {
		this.validationUtilityService = validationUtilityService;
	}

	@Override
	public Note createDefinitionNote(String text, Language language) {
		if (validationUtilityService.isBlankString(text))
			throw new IllegalArgumentException("text cannot be blank");
		if (validationUtilityService.isNull(language))
			throw new NullArgumentException("language");
		Node node = graphDb.createNode();
		noteFactoryNode.createRelationshipTo(node, KnowledgebaseRelationshipType.NOTE);
		NoteImpl noteImpl = new NoteImpl(node).withNoteType(NoteType.DEFINITION).withLanguage(language).withText(text);
		return noteImpl;
	}

}
