package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptService;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.Note;
import org.biosemantics.disambiguation.knowledgebase.service.local.IdGenerator;
import org.biosemantics.disambiguation.knowledgebase.service.local.TextIndexService;
import org.biosemantics.disambiguation.knowledgebase.validation.LabelInputValidator;
import org.biosemantics.disambiguation.knowledgebase.validation.NotationInputValidator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.transaction.annotation.Transactional;

public class ConceptServiceImpl implements ConceptService {

	private final GraphDatabaseService graphDb;
	private final Node conceptFactoryNode;
	private final Node predicateFactoryNode;
	private LabelInputValidator labelInputValidator;
	private NotationInputValidator notationInputValidator;
	private IdGenerator idGenerator;
	private TextIndexService textIndexService;

	public ConceptServiceImpl(GraphDatabaseService graphDatabaseService) {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDb");
		this.graphDb = graphDatabaseService;
		Transaction transaction = this.graphDb.beginTx();
		try {
			// create the concepts sub node if none exists
			Relationship rel = this.graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.CONCEPTS, Direction.OUTGOING);
			if (rel == null) {
				conceptFactoryNode = this.graphDb.createNode();
				this.graphDb.getReferenceNode().createRelationshipTo(conceptFactoryNode,
						KnowledgebaseRelationshipType.CONCEPTS);

			} else {
				conceptFactoryNode = rel.getEndNode();
			}

			// create the predicates sub node if none exists
			Relationship predicates = this.graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.PREDICATES, Direction.OUTGOING);
			if (predicates == null) {
				predicateFactoryNode = this.graphDb.createNode();
				this.graphDb.getReferenceNode().createRelationshipTo(conceptFactoryNode,
						KnowledgebaseRelationshipType.PREDICATES);

			} else {
				predicateFactoryNode = predicates.getEndNode();
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
		if (textIndexService == null)
			throw new NullArgumentException("textIndexService");
		this.textIndexService = textIndexService;
	}

	@Override
	@Transactional
	public Concept createConcept(Collection<Label> labels) {
		labelInputValidator.validateLabels(labels);
		ConceptImpl conceptImpl = createConceptImpl(labels, null, null);
		conceptFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(), KnowledgebaseRelationshipType.CONCEPT);
		return conceptImpl;
	}

	@Override
	@Transactional
	public Concept createConcept(Collection<Label> labels, Collection<Notation> notations) {
		labelInputValidator.validateLabels(labels);
		notationInputValidator.validateNotations(notations);
		ConceptImpl conceptImpl = createConceptImpl(labels, notations, null);
		conceptFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(), KnowledgebaseRelationshipType.CONCEPT);
		return conceptImpl;
	}

	@Override
	@Transactional
	public Concept createPredicate(Collection<Label> labels) {
		labelInputValidator.validateLabels(labels);
		ConceptImpl conceptImpl = createConceptImpl(labels, null, null);
		predicateFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(),
				KnowledgebaseRelationshipType.PREDICATE);
		return conceptImpl;
	}

	@Override
	@Transactional
	public Concept createPredicate(Collection<Label> labels, Collection<Notation> notations) {
		labelInputValidator.validateLabels(labels);
		notationInputValidator.validateNotations(notations);
		ConceptImpl conceptImpl = createConceptImpl(labels, notations, null);
		predicateFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(),
				KnowledgebaseRelationshipType.PREDICATE);
		return conceptImpl;
	}

	private ConceptImpl createConceptImpl(Collection<Label> labels, Collection<Notation> notations, Collection<Note> notes) {
		String id = idGenerator.generateRandomId();
		Node node = graphDb.createNode();
		ConceptImpl conceptImpl = new ConceptImpl(node).withId(id).withLabels(labels);
		if(notations != null){
			conceptImpl.setNotations(notations);
		}
		if(notes != null){
			conceptImpl.setNotes(notes);
		}
		textIndexService.indexConcept(conceptImpl);
		return conceptImpl;
	}

	@Override
	public Concept createConcept(Collection<Label> labels, Collection<Notation> notations, Collection<Note> notes) {
		labelInputValidator.validateLabels(labels);
		notationInputValidator.validateNotations(notations);
		ConceptImpl conceptImpl = createConceptImpl(labels, notations, notes);
		predicateFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(),
				KnowledgebaseRelationshipType.PREDICATE);
		return conceptImpl;
	}

}
