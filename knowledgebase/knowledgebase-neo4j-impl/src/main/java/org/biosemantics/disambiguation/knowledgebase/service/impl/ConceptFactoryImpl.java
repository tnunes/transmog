package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptFactory;
import org.biosemantics.disambiguation.knowledgebase.service.IdGenerator;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.LabelInputValidator;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.service.TextIndexService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.transaction.annotation.Transactional;

public class ConceptFactoryImpl implements ConceptFactory {

	private final GraphDatabaseService graphDb;
	private final Node conceptFactoryNode;
	private LabelInputValidator labelInputValidator;
	private NotationInputValidator notationInputValidator;
	private IdGenerator idGenerator;
	private TextIndexService textIndexService;

	public ConceptFactoryImpl(GraphDatabaseService graphDatabaseService) {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDb");
		this.graphDb = graphDatabaseService;
		Transaction transaction = this.graphDb.beginTx();
		try {
			// create the sub node if none exists
			Relationship rel = this.graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.CONCEPTS, Direction.OUTGOING);
			if (rel == null) {
				conceptFactoryNode = this.graphDb.createNode();
				this.graphDb.getReferenceNode().createRelationshipTo(conceptFactoryNode,
						KnowledgebaseRelationshipType.CONCEPTS);

			} else {
				conceptFactoryNode = rel.getEndNode();
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	public void setLabelInputValidator(LabelInputValidator labelInputValidator) {
		if (labelInputValidator == null)
			throw new NullArgumentException("labelInputValidator");
		this.labelInputValidator = labelInputValidator;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		if (idGenerator == null)
			throw new NullArgumentException("idGenerator");
		this.idGenerator = idGenerator;
	}

	public void setNotationInputValidator(NotationInputValidator notationInputValidator) {
		if (notationInputValidator == null)
			throw new NullArgumentException("notationInputValidator");
		this.notationInputValidator = notationInputValidator;
	}
	
	

	public void setTextIndexService(TextIndexService textIndexService) {
		if(textIndexService == null)
			throw new NullArgumentException("textIndexService");
		this.textIndexService = textIndexService;
	}

	@Override
	@Transactional
	public Concept createConcept(Collection<Label> labels) {
		labelInputValidator.validateLabels(labels);
		String id = idGenerator.generateRandomId();
		Node node = graphDb.createNode();
		conceptFactoryNode.createRelationshipTo(node, KnowledgebaseRelationshipType.CONCEPT);
		ConceptImpl conceptImpl = new ConceptImpl(node).withId(id).withLabels(labels);
		textIndexService.indexConcept(conceptImpl);
		return conceptImpl;
	}

	@Override
	@Transactional
	public Concept createConcept(Collection<Label> labels, Collection<Notation> notations) {
		labelInputValidator.validateLabels(labels);
		String id = idGenerator.generateRandomId();
		notationInputValidator.validateNotations(notations);
		Node node = graphDb.createNode();
		conceptFactoryNode.createRelationshipTo(node, KnowledgebaseRelationshipType.CONCEPT);
		ConceptImpl conceptImpl = new ConceptImpl(node).withId(id).withLabels(labels).withNotations(notations);
		textIndexService.indexConcept(conceptImpl);
		return conceptImpl;
	}

}
