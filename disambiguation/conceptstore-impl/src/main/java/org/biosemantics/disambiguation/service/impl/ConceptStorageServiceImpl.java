package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.neo4j.graphdb.Node;
import org.springframework.transaction.annotation.Transactional;

public class ConceptStorageServiceImpl implements ConceptStorageService {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node conceptParentNode;
	private final Node predicateParentNode;
	private final Node domainParentNode;
	private final Node conceptSchemeParentNode;

	private UuidGeneratorService uuidGeneratorService;

	public ConceptStorageServiceImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = checkNotNull(graphStorageTemplate);
		this.conceptParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.CONCEPTS);
		this.predicateParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.PREDICATES);
		this.domainParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.DOMAINS);
		this.conceptSchemeParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.CONCEPT_SCHEMES);
	}

	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Override
	@Transactional
	public Concept createConcept(Concept concept) {
		return createConceptInternal(concept, DefaultRelationshipType.CONCEPT);
	}

	@Override
	@Transactional
	public Concept createPredicate(Concept predicate) {
		return createConceptInternal(predicate, DefaultRelationshipType.PREDICATE);
	}

	@Override
	@Transactional
	public Concept createConceptScheme(Concept conceptScheme) {
		return createConceptInternal(conceptScheme, DefaultRelationshipType.CONCEPT_SCHEME);
	}

	@Override
	@Transactional
	public Concept createDomain(Concept domain) {
		return createConceptInternal(domain, DefaultRelationshipType.DOMAIN);
	}

	private Concept createConceptInternal(Concept concept, DefaultRelationshipType defaultRelationshipType) {
		Node node = graphStorageTemplate.createNode();
		Node parentNode = null;
		switch (defaultRelationshipType) {
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
		graphStorageTemplate.createRelationship(parentNode, node, defaultRelationshipType);
		ConceptImpl conceptImpl = new ConceptImpl(node).withUuid(uuidGeneratorService.generateRandomUuid());
		Collection<Label> labels = concept.getLabelsByType(LabelType.PREFERRED);
		if (labels != null) {
			conceptImpl.setLabels(LabelType.PREFERRED, labels);
		}
		labels = concept.getLabelsByType(LabelType.ALTERNATE);
		if (labels != null) {
			conceptImpl.setLabels(LabelType.ALTERNATE, labels);
		}
		labels = concept.getLabelsByType(LabelType.HIDDEN);
		if (labels != null) {
			conceptImpl.setLabels(LabelType.HIDDEN, labels);
		}
		if (concept.getNotations() != null) {
			conceptImpl.setNotations(concept.getNotations());
		}
		if (concept.getNotes() != null) {
			conceptImpl.setNotes(concept.getNotes());
		}
		return conceptImpl;
	}

}
