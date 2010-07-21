package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationFactory;
import org.biosemantics.disambiguation.knowledgebase.service.NotationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.service.TextIndexService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.transaction.annotation.Transactional;

public class NotationFactoryImpl implements NotationFactory {

	private final GraphDatabaseService graphDb;
	private final Node notationFactoryNode;
	private NotationInputValidator notationInputValidator;
	private TextIndexService textIndexService;

	public NotationFactoryImpl(GraphDatabaseService graphDatabaseService) {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDb");
		this.graphDb = graphDatabaseService;
		Transaction transaction = this.graphDb.beginTx();
		try {
			// create the sub node if none exists
			Relationship rel = this.graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.NOTATIONS, Direction.OUTGOING);
			if (rel == null) {
				notationFactoryNode = this.graphDb.createNode();
				this.graphDb.getReferenceNode().createRelationshipTo(notationFactoryNode,
						KnowledgebaseRelationshipType.NOTATIONS);

			} else {
				notationFactoryNode = rel.getEndNode();
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	public void setNotationInputValidator(NotationInputValidator notationInputValidator) {
		if(notationInputValidator == null)
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
	public Notation createNotation(Domain domain, String text) {
		notationInputValidator.validateDomain(domain);
		notationInputValidator.validateText(text);
		Node node = graphDb.createNode();
		notationFactoryNode.createRelationshipTo(node, KnowledgebaseRelationshipType.NOTATION);
		NotationImpl notationImpl = new  NotationImpl(node).withDomain(domain).withText(text);
		textIndexService.indexNotation(notationImpl);
		return notationImpl;
	}

}
