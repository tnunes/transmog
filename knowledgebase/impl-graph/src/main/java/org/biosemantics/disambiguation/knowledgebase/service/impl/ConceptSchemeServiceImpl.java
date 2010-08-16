package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.ConceptSchemeService;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.local.IdGenerator;
import org.biosemantics.disambiguation.knowledgebase.service.local.TextIndexService;
import org.biosemantics.disambiguation.knowledgebase.validation.LabelInputValidator;
import org.biosemantics.disambiguation.knowledgebase.validation.NotationInputValidator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class ConceptSchemeServiceImpl implements ConceptSchemeService {

	private final GraphDatabaseService graphDb;
	private final Node conceptSchemeFactoryNode;
	private TextIndexService textIndexService;
	private IdGenerator idGenerator;
	private LabelInputValidator labelInputValidator;
	private NotationInputValidator notationInputValidator;

	public ConceptSchemeServiceImpl(GraphDatabaseService graphDatabaseService) {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDatabaseService");
		this.graphDb = graphDatabaseService;
		// explicitly starting transaction as constructor is called by spring.
		Transaction transaction = this.graphDb.beginTx();
		try {
			Relationship relationship = graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.CONCEPT_SCHEMES, Direction.OUTGOING);
			if (relationship == null) {
				conceptSchemeFactoryNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo(conceptSchemeFactoryNode,
						KnowledgebaseRelationshipType.CONCEPT_SCHEMES);
			} else {
				conceptSchemeFactoryNode = relationship.getEndNode();
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	public void setTextIndexService(TextIndexService textIndexService) {
		this.textIndexService = textIndexService;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	public void setLabelInputValidator(LabelInputValidator labelInputValidator) {
		this.labelInputValidator = labelInputValidator;
	}

	public void setNotationInputValidator(NotationInputValidator notationInputValidator) {
		this.notationInputValidator = notationInputValidator;
	}

	@Override
	public void addTopConceptsToScheme(String schemeId, Collection<Concept> concepts) {
		if (concepts == null || concepts.isEmpty())
			throw new IllegalArgumentException("concepts cannot be null or empty");
		Concept concept = textIndexService.getConceptById(schemeId);
		if (concept == null)
			throw new IllegalArgumentException("schemeId not found");
		ConceptImpl conceptImpl = (ConceptImpl) concept;
		for (Concept targetConcept : concepts) {
			ConceptImpl targetConceptImpl = (ConceptImpl) targetConcept;
			conceptImpl.getUnderlyingNode().createRelationshipTo(targetConceptImpl.getUnderlyingNode(),
					KnowledgebaseRelationshipType.TOP_CONCEPT);
		}
	}

	@Override
	public Concept createConceptScheme(Collection<Label> labels) {
		labelInputValidator.validateLabels(labels);
		ConceptImpl conceptImpl = createConceptImpl(labels, null);
		conceptSchemeFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(),
				KnowledgebaseRelationshipType.CONCEPT_SCHEME);
		return conceptImpl;
	}

	@Override
	public Concept createConceptScheme(Collection<Label> labels, Collection<Notation> notations) {
		labelInputValidator.validateLabels(labels);
		notationInputValidator.validateNotations(notations);
		ConceptImpl conceptImpl = createConceptImpl(labels, notations);
		conceptSchemeFactoryNode.createRelationshipTo(conceptImpl.getUnderlyingNode(),
				KnowledgebaseRelationshipType.CONCEPT_SCHEME);
		return conceptImpl;
	}

	private ConceptImpl createConceptImpl(Collection<Label> labels, Collection<Notation> notations) {
		String id = idGenerator.generateRandomId();
		Node node = graphDb.createNode();
		ConceptImpl conceptImpl = new ConceptImpl(node).withId(id).withLabels(labels);
		if (notations != null) {
			conceptImpl.setNotations(notations);
		}
		textIndexService.indexConcept(conceptImpl);
		return conceptImpl;
	}
}
