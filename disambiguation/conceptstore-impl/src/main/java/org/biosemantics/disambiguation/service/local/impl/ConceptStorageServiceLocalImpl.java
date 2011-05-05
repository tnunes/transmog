package org.biosemantics.disambiguation.service.local.impl;

import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.LabelStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.NotationStorageServiceLocal;
import org.biosemantics.disambiguation.service.local.NoteStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

public class ConceptStorageServiceLocalImpl implements ConceptStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node conceptParentNode;
	private final Node predicateParentNode;
	private final Node domainParentNode;
	private final Node conceptSchemeParentNode;
	private final Index<Node> index;
	private final Index<Node> fulltextIndex;
	private UuidGeneratorService uuidGeneratorService;
	private LabelStorageServiceLocal labelStorageServiceLocal;
	private NotationStorageServiceLocal notationStorageServiceLocal;
	private NoteStorageServiceLocal noteStorageServiceLocal;
	private ValidationUtility validationUtility;
	private boolean optimise = true;

	public static final String CONCEPT_INDEX = "concept";
	public static final String CONCEPT_FULLTEXT_INDEX = "concept_fulltext";
	public static final String UUID_INDEX_KEY = "concept_uuid";
	public static final String FULLTEXT_INDEX_KEY = "concept_all_text";
	public static final String DELIMITER = " ";
	//private static final Logger logger = LoggerFactory.getLogger(ConceptStorageServiceLocalImpl.class);

	public ConceptStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		// FIXME two enums relationshipTypes and ConceptTypes with certain overlap between them.
		this.graphStorageTemplate = graphStorageTemplate;
		this.conceptParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.CONCEPTS);
		this.predicateParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.PREDICATES);
		this.domainParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.DOMAINS);
		this.conceptSchemeParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.CONCEPT_SCHEMES);
		this.index = graphStorageTemplate.getIndexManager().forNodes(CONCEPT_INDEX);
		this.fulltextIndex = graphStorageTemplate.getIndexManager().forNodes(CONCEPT_FULLTEXT_INDEX,
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
		Node parentNode = getParentNode(conceptType);
		conceptNode.setProperty(ConceptImpl.UUID_PROPERTY, uuid);
		index.add(conceptNode, UUID_INDEX_KEY, uuid);
		parentNode.createRelationshipTo(conceptNode, DefaultRelationshipType.CONCEPT);
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
			Relationship relationship = conceptNode.createRelationshipTo(labelNode, DefaultRelationshipType.HAS_LABEL);
			relationship.setProperty(ConceptImpl.LABEL_TYPE_RLSP_PROPERTY, label.getLabelType().name());
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
				conceptNode.createRelationshipTo(notationNode, DefaultRelationshipType.HAS_NOTATION);
				fullTextStrings.add(notation.getCode());
			}
		}

		// create notes
		if (!CollectionUtils.isEmpty(concept.getNotes())) {
			for (Note note : concept.getNotes()) {
				// notes are not optimised as the probability of getting the same note is too low
				Node noteNode = noteStorageServiceLocal.createNoteNode(note);
				conceptNode.createRelationshipTo(noteNode, DefaultRelationshipType.HAS_NOTE);
				fullTextStrings.add(note.getText());
			}
		}

		StringBuilder fullText = new StringBuilder();
		for (String string : fullTextStrings) {
			fullText.append(string).append(DELIMITER);
		}
		fulltextIndex.add(conceptNode, FULLTEXT_INDEX_KEY, fullText.toString());
		return uuid;
	}

	private Node getParentNode(ConceptType conceptType) {
		Node parentNode = null;
		switch (conceptType) {
		case CONCEPT:
			parentNode = conceptParentNode;
			break;
		case PREDICATE:
			parentNode = predicateParentNode;
			break;
		case CONCEPT_SCHEME:
			parentNode = conceptSchemeParentNode;
			break;
		case DOMAIN:
			parentNode = domainParentNode;
			break;
		}
		return parentNode;
	}

	@Override
	public Concept getConcept(String uuid) {
		return new ConceptImpl(getConceptNode(uuid));
	}

	@Override
	public Node getConceptNode(String uuid) {
		return index.get(UUID_INDEX_KEY, uuid).getSingle();
	}

	@Override
	public Collection<Concept> getConceptsByFullTextQuery(String fulltextQuery, int maxResults) {
		IndexHits<Node> nodes = fulltextIndex.query(FULLTEXT_INDEX_KEY, fulltextQuery);
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
		Iterable<Relationship> relationships = node.getRelationships(DefaultRelationshipType.HAS_NOTATION);
		for (Relationship relationship : relationships) {
			uuids.add((String) relationship.getOtherNode(node).getProperty(ConceptImpl.UUID_PROPERTY));
		}
		return uuids;

	}

}
