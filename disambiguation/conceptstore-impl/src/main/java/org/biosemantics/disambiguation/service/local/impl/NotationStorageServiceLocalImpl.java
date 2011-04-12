package org.biosemantics.disambiguation.service.local.impl;

import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.service.local.NotationStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class NotationStorageServiceLocalImpl implements NotationStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node notationParentNode;
	private final Index<Node> index;
	private ValidationUtility validationUtility;
	private UuidGeneratorService uuidGeneratorService;
	public static final String NOTATION_INDEX = "notation";
	public static final String UUID_INDEX_KEY = "notation_uuid";
	public static final String CODE_INDEX_KEY = "notation_code";

	public NotationStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		super();
		this.graphStorageTemplate = graphStorageTemplate;
		this.notationParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.NOTATIONS);
		this.index = graphStorageTemplate.getIndexManager().forNodes(NOTATION_INDEX);
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Override
	@Transactional
	public String createNotation(Notation notation) {
		validationUtility.validateNotation(notation);
		Node node = createNotationNode(notation);
		return (String) node.getProperty(NotationImpl.UUID_PROPERTY);
	}

	@Override
	public Node createNotationNode(Notation notation) {
		String uuid = uuidGeneratorService.generateRandomUuid();
		// create new node if none exists
		Node notationNode = graphStorageTemplate.createNode();
		graphStorageTemplate.createRelationship(notationParentNode, notationNode, DefaultRelationshipType.NOTATION);
		notationNode.setProperty(NotationImpl.UUID_PROPERTY, uuid);
		notationNode.setProperty(NotationImpl.DOMAIN_UUID_PROPERTY, notation.getDomainUuid());
		notationNode.setProperty(NotationImpl.CODE_PROPERTY, notation.getCode());
		index.add(notationNode, UUID_INDEX_KEY, uuid);
		index.add(notationNode, CODE_INDEX_KEY, notation.getCode());
		return notationNode;
	}

	@Override
	public Notation getNotation(String uuid) {
		validationUtility.validateString(uuid, "uuid");
		return new NotationImpl(getNotationNode(uuid));
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
	public Node getNotationNode(String uuid) {
		return index.get(UUID_INDEX_KEY, uuid).getSingle();
	}

	@Override
	public Node getNotationNode(String code, String domainUuid) {
		IndexHits<Node> nodes = index.get(CODE_INDEX_KEY, code);
		for (Node node : nodes) {
			if (((String) node.getProperty(NotationImpl.CODE_PROPERTY)).equals(code)
					&& domainUuid.equals((String) node.getProperty(NotationImpl.DOMAIN_UUID_PROPERTY))) {
				return node;
			}
		}
		return null;
	}

}
