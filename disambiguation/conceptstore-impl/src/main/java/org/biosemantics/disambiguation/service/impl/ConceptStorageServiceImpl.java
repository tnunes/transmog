package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Note;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.biosemantics.conceptstore.common.service.NoteStorageService;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ConceptValidator;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

public class ConceptStorageServiceImpl implements ConceptStorageService {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node conceptParentNode;
	private final Node predicateParentNode;
	private final Node domainParentNode;
	private final Node conceptSchemeParentNode;

	private UuidGeneratorService uuidGeneratorService;
	private LabelStorageService labelStorageService;
	private NotationStorageService notationStorageService;
	private NoteStorageService noteStorageService;
	private ConceptValidator conceptValidator;

	private IndexService indexService;

	public ConceptStorageServiceImpl(GraphStorageTemplate graphStorageTemplate) {
		// FIXME two enums relationshipTypes and ConceptTypes with certain overlap between them.
		this.graphStorageTemplate = checkNotNull(graphStorageTemplate);
		this.conceptParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.CONCEPTS);
		this.predicateParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.PREDICATES);
		this.domainParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.DOMAINS);
		this.conceptSchemeParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.CONCEPT_SCHEMES);
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Required
	public void setLabelStorageService(LabelStorageService labelStorageService) {
		this.labelStorageService = labelStorageService;
	}

	@Required
	public void setNotationStorageService(NotationStorageService notationStorageService) {
		this.notationStorageService = notationStorageService;
	}

	@Required
	public void setNoteStorageService(NoteStorageService noteStorageService) {
		this.noteStorageService = noteStorageService;
	}

	@Required
	public void setConceptValidator(ConceptValidator conceptValidator) {
		this.conceptValidator = conceptValidator;
	}

	@Required
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	@Override
	@Transactional
	public Concept createConcept(ConceptType conceptType, Concept concept) {
		conceptValidator.validate(concept);
		Node node = graphStorageTemplate.createNode();
		Node parentNode = getParentNode(conceptType);
		graphStorageTemplate.createRelationship(parentNode, node, DefaultRelationshipType.CONCEPT);
		ConceptImpl conceptImpl = new ConceptImpl(node).withUuid(uuidGeneratorService.generateRandomUuid());
		Collection<Label> labels = concept.getLabelsByType(LabelType.PREFERRED);
		if (!CollectionUtils.isEmpty(labels)) {
			Collection<Label> createdLabels = createLabels(labels);
			conceptImpl.setLabels(LabelType.PREFERRED, createdLabels);
		}
		labels = concept.getLabelsByType(LabelType.ALTERNATE);
		if (!CollectionUtils.isEmpty(labels)) {
			Collection<Label> createdLabels = createLabels(labels);
			conceptImpl.setLabels(LabelType.ALTERNATE, createdLabels);
		}
		labels = concept.getLabelsByType(LabelType.HIDDEN);
		if (!CollectionUtils.isEmpty(labels)) {
			Collection<Label> createdLabels = createLabels(labels);
			conceptImpl.setLabels(LabelType.HIDDEN, createdLabels);
		}
		Collection<Notation> notations = concept.getNotations();
		// create only is something is provided
		if (!CollectionUtils.isEmpty(notations)) {
			Collection<Notation> createdNotations = createNotations(notations);
			conceptImpl.setNotations(createdNotations);
		}
		// create only is something is provided
		Collection<Note> notes = concept.getNotes();
		if (!CollectionUtils.isEmpty(notes)) {
			conceptImpl.setNotes(createNotes(notes));
		}
		return conceptImpl;
	}

	@Override
	public Concept appendConcept(String uuid, Concept concept) {
		ConceptImpl conceptImpl = (ConceptImpl) indexService.getConceptByUuid(uuid);
		if (conceptImpl == null) {
			throw new IllegalArgumentException("No concept found in store with uuid = " + uuid);
		}
		Collection<Label> labels = concept.getLabelsByType(LabelType.PREFERRED);
		if (!CollectionUtils.isEmpty(labels)) {
			Collection<Label> createdLabels = createLabels(labels);
			conceptImpl.setLabels(LabelType.PREFERRED, createdLabels);
		}
		labels = concept.getLabelsByType(LabelType.ALTERNATE);
		if (!CollectionUtils.isEmpty(labels)) {
			Collection<Label> createdLabels = createLabels(labels);
			conceptImpl.setLabels(LabelType.ALTERNATE, createdLabels);
		}
		labels = concept.getLabelsByType(LabelType.HIDDEN);
		if (!CollectionUtils.isEmpty(labels)) {
			Collection<Label> createdLabels = createLabels(labels);
			conceptImpl.setLabels(LabelType.HIDDEN, createdLabels);
		}
		Collection<Notation> notations = concept.getNotations();
		// create only is something is provided
		if (!CollectionUtils.isEmpty(notations)) {
			Collection<Notation> createdNotations = createNotations(notations);
			conceptImpl.setNotations(createdNotations);
		}
		// create only is something is provided
		Collection<Note> notes = concept.getNotes();
		if (!CollectionUtils.isEmpty(notes)) {
			conceptImpl.setNotes(createNotes(notes));
		}
		return conceptImpl;

	}

	private Collection<Notation> createNotations(Collection<Notation> notations) {
		Collection<Notation> createdNotations = new ArrayList<Notation>(notations.size());
		for (Notation notation : notations) {
			createdNotations.add(notationStorageService.createNotation(notation));
		}
		return createdNotations;
	}

	private Collection<Label> createLabels(Collection<Label> labels) {
		Collection<Label> createdLabels = new ArrayList<Label>(labels.size());
		for (Label label : labels) {
			createdLabels.add(labelStorageService.createLabel(label));
		}
		return createdLabels;
	}

	private Collection<Note> createNotes(Collection<Note> notes) {
		Collection<Note> createdNotes = new ArrayList<Note>(notes.size());
		for (Note note : notes) {
			createdNotes.add(noteStorageService.createDefinition(note));
		}
		return createdNotes;
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
		default:
			break;
		}
		return parentNode;
	}

}
