package org.biosemantics.disambiguation.service.local.impl;

import static org.biosemantics.disambiguation.common.IndexConstant.*;
import static org.biosemantics.disambiguation.common.PropertyConstant.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.domain.extn.ChildConcept;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.common.RelationshipTypeConstant;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.LabelStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.NotationStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.NoteStorageServiceLocal;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

public class ConceptStorageServiceLocalImpl implements ConceptStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node conceptParentNode;
	private final Index<Node> index;
	private final Index<Node> fulltextIndex;
	private UuidGeneratorService uuidGeneratorService;
	private LabelStorageServiceLocal labelStorageServiceLocal;
	private NotationStorageServiceLocal notationStorageServiceLocal;
	private NoteStorageServiceLocal noteStorageServiceLocal;
	private ValidationUtility validationUtility;
	private boolean optimise = true;

	public static final String DELIMITER = " ";

	// private static final Logger logger = LoggerFactory.getLogger(ConceptStorageServiceLocalImpl.class);

	public ConceptStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		// FIXME two enums relationshipTypes and ConceptTypes with certain overlap between them.
		this.graphStorageTemplate = graphStorageTemplate;
		this.conceptParentNode = this.graphStorageTemplate.getParentNode(RelationshipTypeConstant.CONCEPTS);
		this.index = graphStorageTemplate.getIndexManager().forNodes(CONCEPT_INDEX.name());
		this.fulltextIndex = graphStorageTemplate.getIndexManager().forNodes(CONCEPT_FULL_TEXT_KEY.name(),
				MapUtil.stringMap("provider", "lucene", "type", "fulltext"));
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Required
	public void setLabelStorageServiceLocal(LabelStorageServiceLocal labelStorageServiceLocal) {
		this.labelStorageServiceLocal = labelStorageServiceLocal;
	}

	@Required
	public void setNotationStorageServiceLocal(NotationStorageServiceLocal notationStorageServiceLocal) {
		this.notationStorageServiceLocal = notationStorageServiceLocal;
	}

	@Required
	public void setNoteStorageServiceLocal(NoteStorageServiceLocal noteStorageServiceLocal) {
		this.noteStorageServiceLocal = noteStorageServiceLocal;
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Transactional
	@Override
	public String createConcept(ConceptType conceptType, Concept concept) {
		validationUtility.validateConcept(concept);
		final String uuid = uuidGeneratorService.generateRandomUuid();
		Collection<String> fullTextStrings = new HashSet<String>();
		fullTextStrings.add(uuid);
		Node conceptNode = graphStorageTemplate.getGraphDatabaseService().createNode();
		conceptNode.setProperty(UUID.name(), uuid);
		index.add(conceptNode, CONCEPT_UUID_KEY.name(), uuid);
		index.add(conceptNode, CONCEPT_TYPE_KEY.name(), concept.getType().name());
		conceptParentNode.createRelationshipTo(conceptNode, RelationshipTypeConstant.CONCEPT);
		// create labels
		for (ConceptLabel label : concept.getLabels()) {
			Node labelNode = null;
			// if optimise is set to true only create labels if needed otherwise link with existing labels
			if (optimise) {
				labelNode = labelStorageServiceLocal.getLabelNode(label.getText(), label.getLanguage());
			}
			if (labelNode == null) {
				labelNode = labelStorageServiceLocal.createLabelNode(label);
			}
			Relationship relationship = conceptNode.createRelationshipTo(labelNode, RelationshipTypeConstant.HAS_LABEL);
			relationship.setProperty(LABEL_TYPE.name(), label.getLabelType().getId());
			fullTextStrings.add(label.getText());
		}
		// create notations
		if (!CollectionUtils.isEmpty(concept.getNotations())) {
			for (Notation notation : concept.getNotations()) {
				// if optimise is set to true only create notations if needed otherwise link with existing notations
				Node notationNode = null;
				if (optimise) {
					notationNode = notationStorageServiceLocal.getNotationNode(notation.getCode(),
							notation.getDomainUuid());
				}
				if (notationNode == null) {
					notationNode = notationStorageServiceLocal.createNotationNode(notation);
				}
				conceptNode.createRelationshipTo(notationNode, RelationshipTypeConstant.HAS_NOTATION);
				fullTextStrings.add(notation.getCode());
			}
		}

		// create notes
		if (!CollectionUtils.isEmpty(concept.getNotes())) {
			for (Note note : concept.getNotes()) {
				// notes are not optimised as the probability of getting the same note is too low
				Node noteNode = noteStorageServiceLocal.createNoteNode(note);
				conceptNode.createRelationshipTo(noteNode, RelationshipTypeConstant.HAS_NOTE);
				fullTextStrings.add(note.getText());
			}
		}

		StringBuilder fullText = new StringBuilder();
		for (String string : fullTextStrings) {
			fullText.append(string).append(DELIMITER);
		}
		fulltextIndex.add(conceptNode, CONCEPT_FULL_TEXT_KEY.name(), fullText.toString());
		return uuid;
	}

	@Override
	public Concept getConcept(String uuid) {
		return new ConceptImpl(getConceptNode(uuid));
	}

	@Override
	public Node getConceptNode(String uuid) {
		return index.get(CONCEPT_UUID_KEY.name(), uuid).getSingle();
	}

	@Override
	public Collection<Concept> getConceptsByFullTextQuery(String fulltextQuery, int maxResults) {
		IndexHits<Node> nodes = fulltextIndex.query(CONCEPT_FULL_TEXT_KEY.name(), fulltextQuery);
		Collection<Concept> concepts = new HashSet<Concept>(maxResults);
		int ctr = 0;
		for (Node node : nodes) {
			ctr++;
			if (ctr > maxResults) {
				break;
			} else {
				concepts.add(new ConceptImpl(node));
			}
		}
		return concepts;
	}

	@Override
	public Collection<String> getConceptsByNotation(Notation notation) {
		Collection<String> uuids = new HashSet<String>();
		Node node = notationStorageServiceLocal.getNotationNode(notation.getCode(), notation.getDomainUuid());
		if (node == null) {
			throw new IllegalArgumentException("no notation found for " + notation.toString());
		} else {
			Iterable<Relationship> relationships = node.getRelationships(RelationshipTypeConstant.HAS_NOTATION);
			for (Relationship relationship : relationships) {
				uuids.add((String) relationship.getOtherNode(node).getProperty(UUID.name()));
			}
		}
		return uuids;

	}

	@Override
	public Collection<ChildConcept> getAllChildren(String uuid) {
		Node conceptNode = getConceptNode(uuid);
		Collection<ChildConcept> childConcepts = new ArrayList<ChildConcept>();
		Traverser children = conceptNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
				ReturnableEvaluator.ALL_BUT_START_NODE, ConceptRelationshipType.HAS_NARROWER_CONCEPT,
				Direction.OUTGOING, ConceptRelationshipType.HAS_BROADER_CONCEPT, Direction.INCOMING);
		for (Node child : children) {
			TraversalPosition currentPosition = children.currentPosition();
			ChildConcept childConcept = new ChildConcept(new ConceptImpl(child), currentPosition.depth());
			childConcepts.add(childConcept);
		}
		return childConcepts;
	}

}
