package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.biosemantics.conceptstore.utils.validation.NotationValidator;
import org.biosemantics.disambiguation.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Node;
import org.springframework.transaction.annotation.Transactional;

public class NotationStorageServiceImpl implements NotationStorageService {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node notationParentNode;
	private boolean checkExists;
	private IndexService indexService;
	private NotationValidator notationValidator;

	public NotationStorageServiceImpl(GraphStorageTemplate graphStorageTemplate) {
		super();
		this.graphStorageTemplate = checkNotNull(graphStorageTemplate);
		this.notationParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.NOTATIONS);
		this.checkExists = true;
	}

	public void setCheckExists(boolean checkExists) {
		this.checkExists = checkExists;
	}

	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	public void setNotationValidator(NotationValidator notationValidator) {
		this.notationValidator = notationValidator;
	}

	@Override
	@Transactional
	public Notation createNotation(Notation notation) {
		Notation createdNotation = null;
		if (checkExists) {
			// check if node exits in data store
			createdNotation = findNotation(notation);
		}
		if (createdNotation == null) {
			// create new node if none exists
			Node node = graphStorageTemplate.createNode();
			graphStorageTemplate.createRelationship(notationParentNode, node, DefaultRelationshipType.NOTATION);
			createdNotation = new NotationImpl(node).withCode(notation.getCode()).withDomain(notation.getDomain());
			indexService.indexNotation(createdNotation);
		}
		return createdNotation;
	}

	private Notation findNotation(Notation notation) {
		Notation found = null;
		Iterable<Notation> notations = indexService.getNotationByCode(notation.getCode());
		if (notations != null) {
			for (Notation foundNotation : notations) {
				if (foundNotation.getDomain().equals(notation.getDomain())) {
					found = foundNotation;
					break;
				}
			}
		}
		return found;
	}

	@Override
	public Collection<Notation> getNotationsByCode(String code) {
		return indexService.getNotationByCode(code);
	}

}
