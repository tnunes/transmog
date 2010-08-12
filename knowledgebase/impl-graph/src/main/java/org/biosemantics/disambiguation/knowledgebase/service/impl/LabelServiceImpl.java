package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelService;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.service.Label.LabelType;
import org.biosemantics.disambiguation.knowledgebase.service.local.IdGenerator;
import org.biosemantics.disambiguation.knowledgebase.service.local.TextIndexService;
import org.biosemantics.disambiguation.knowledgebase.validation.LabelInputValidator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.transaction.annotation.Transactional;

public class LabelServiceImpl implements LabelService {

	private final GraphDatabaseService graphDb;
	private final Node labelFactoryNode;
	private TextIndexService textIndexService;
	private IdGenerator idGenerator;
	private LabelInputValidator labelInputValidator;

	public LabelServiceImpl(GraphDatabaseService graphDatabaseService) {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDatabaseService");
		this.graphDb = graphDatabaseService;
		// explicitly starting transaction as constructor is called by spring.
		Transaction transaction = this.graphDb.beginTx();
		try {
			Relationship relationship = graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.LABELS, Direction.OUTGOING);
			if (relationship == null) {
				labelFactoryNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo(labelFactoryNode, KnowledgebaseRelationshipType.LABELS);
			} else {
				labelFactoryNode = relationship.getEndNode();
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	public void setTextIndexService(TextIndexService textIndexService) {
		if (textIndexService == null)
			throw new NullArgumentException("textIndexService");
		this.textIndexService = textIndexService;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		if (idGenerator == null)
			throw new NullArgumentException("idGenerator");
		this.idGenerator = idGenerator;
	}
	
	

	public void setLabelInputValidator(LabelInputValidator labelInputValidator) {
		if(labelInputValidator == null)
			throw new NullArgumentException("labelInputValidator");
		this.labelInputValidator = labelInputValidator;
	}

	@Override
	@Transactional
	public Label createPreferredLabel(String text, Language language) {
		labelInputValidator.validateLanguage(language);
		labelInputValidator.validateText(text);
		return createLabel(LabelType.PREFERRED, text, language);
	}

	@Override
	@Transactional
	public Label createAlternateLabel(String text, Language language) {
		labelInputValidator.validateLanguage(language);
		labelInputValidator.validateText(text);
		return createLabel(LabelType.ALTERNATE, text, language);
	}

	@Transactional
	private Label createLabel(LabelType labelType, String text, Language language) {
		Iterable<Label> labels = textIndexService.getLabelsByText(text);
		Label found = null;
		if (labels != null) {
			for (Label label : labels) {
				if (label.getLabelType().equals(labelType) && label.getLanguage().equals(language)
						&& label.getText().equals(text)) {
					found = label;
					break;
				}
			}
		}
		if (found == null) {
			// create new node if none exists
			Node node = graphDb.createNode();
			labelFactoryNode.createRelationshipTo(node, KnowledgebaseRelationshipType.LABEL);
			String id = idGenerator.generateRandomId();
			found = new LabelImpl(node).withId(id).withLabelType(labelType).withLanguage(language).withText(text);
			// index the new node (old nodes are already indexed)
			textIndexService.indexLabel(found);
		}
		return found;
	}
}
