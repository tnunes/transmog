package org.biosemantics.disambiguation.service.local.impl;

import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.service.local.NotationStorageServiceLocal;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.impl.lucene.LuceneIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class NotationStorageServiceLocalImpl implements NotationStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node notationParentNode;
	private final Index<Node> index;
	private ValidationUtility validationUtility;
	public static final String NOTATION_INDEX = "notation";
	public static final String CODE_INDEX_KEY = "notation_code";

	private static final Logger logger = LoggerFactory.getLogger(NotationStorageServiceLocalImpl.class);

	public NotationStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		super();
		this.graphStorageTemplate = graphStorageTemplate;
		this.notationParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.NOTATIONS);
		this.index = graphStorageTemplate.getIndexManager().forNodes(NOTATION_INDEX);
		((LuceneIndex<Node>) this.index).setCacheCapacity(CODE_INDEX_KEY, 300000);
		logger.debug("setting cache for notation-code index to 300000");
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Override
	@Transactional
	public long createNotation(Notation notation) {
		validationUtility.validateNotation(notation);
		Node node = createNotationNode(notation);
		return node.getId();
	}

	@Override
	public Node createNotationNode(Notation notation) {
		// create new node if none exists
		Node notationNode = graphStorageTemplate.getGraphDatabaseService().createNode();
		notationParentNode.createRelationshipTo(notationNode, DefaultRelationshipType.NOTATION);
		notationNode.setProperty(NotationImpl.DOMAIN_UUID_PROPERTY, notation.getDomainUuid());
		notationNode.setProperty(NotationImpl.CODE_PROPERTY, notation.getCode());
		index.add(notationNode, CODE_INDEX_KEY, notation.getCode());
		return notationNode;
	}

	@Override
	public Notation getNotation(long id) {
		validationUtility.validateId(id);
		return new NotationImpl(this.graphStorageTemplate.getGraphDatabaseService().getNodeById(id));
	}

	@Override
	public Collection<Notation> getNotationsByCode(String code) {
		validationUtility.validateString(code, "code");
		IndexHits<Node> nodes = index.get(CODE_INDEX_KEY, code);
		Collection<Notation> notations = new HashSet<Notation>();
		for (Node node : nodes) {
			if (((String) node.getProperty(NotationImpl.CODE_PROPERTY)).equals(code)) {
				notations.add(new NotationImpl(node));
			}
		}
		return notations;
	}

	@Override
	public Node getNotationNode(long id) {
		return this.graphStorageTemplate.getGraphDatabaseService().getNodeById(id);
	}

	@Override
	public Node getNotationNode(String code, String domainUuid) {
		Node found = null;
		IndexHits<Node> nodes = index.get(CODE_INDEX_KEY, code);
		try {
			for (Node node : nodes) {
				if (((String) node.getProperty(NotationImpl.CODE_PROPERTY)).equals(code)
						&& domainUuid.equals((String) node.getProperty(NotationImpl.DOMAIN_UUID_PROPERTY))) {
					found = node;
					break;
				}
			}
		} finally {
			nodes.close();
		}
		return found;
	}

	@Override
	public Collection<Concept> getAllRelatedConcepts(String notationCode) {
		validationUtility.validateString(notationCode, "notationCode");
		IndexHits<Node> nodes = index.get(CODE_INDEX_KEY, notationCode);
		Collection<Concept> concepts = new HashSet<Concept>();
		for (Node node : nodes) {
			Iterable<Relationship> rlsps = node.getRelationships(DefaultRelationshipType.HAS_NOTATION,
					Direction.INCOMING);
			for (Relationship relationship : rlsps) {
				concepts.add(new ConceptImpl(relationship.getOtherNode(node)));
			}
		}
		return concepts;
	}

}
