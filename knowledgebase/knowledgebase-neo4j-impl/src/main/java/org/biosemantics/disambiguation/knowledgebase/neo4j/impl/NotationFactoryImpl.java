package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.api.Domain;
import org.biosemantics.disambiguation.knowledgebase.api.Notation;
import org.biosemantics.disambiguation.knowledgebase.api.NotationFactory;
import org.biosemantics.disambiguation.knowledgebase.api.NotationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.api.TextIndexService;
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
		if (notationInputValidator == null)
			throw new NullArgumentException("notationInputValidator");
		this.notationInputValidator = notationInputValidator;
	}

	public void setTextIndexService(TextIndexService textIndexService) {
		if (textIndexService == null)
			throw new NullArgumentException("textIndexService");
		this.textIndexService = textIndexService;
	}

	@Override
	@Transactional
	public Notation createNotation(Domain domain, String code) {
		notationInputValidator.validateDomain(domain);
		notationInputValidator.validateCode(code);
		Iterable<Notation> notations = textIndexService.getNotationByCode(code);
		Notation found = null;
		if (notations != null) {
			for (Notation notation : notations) {
				if (notation.getCode().equals(code) && notation.getDomain().equals(domain)) {
					found = notation;
					break;
				}
			}
		}
		if (found == null) {
			// create new node if none exists
			Node node = graphDb.createNode();
			notationFactoryNode.createRelationshipTo(node, KnowledgebaseRelationshipType.NOTATION);
			found = new NotationImpl(node).withDomain(domain).withCode(code);
			// index new nodes only
			textIndexService.indexNotation(found);
		}
		return found;

	}

}
