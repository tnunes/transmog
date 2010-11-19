package org.biosemantics.disambiguation.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Required;

public class ConceptQueryServiceImpl implements ConceptQueryService {

	private IndexService indexService;
	private GraphStorageTemplate graphStorageTemplate;

	@Required
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	public void setGraphStorageTemplate(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
	}

	@Override
	public Collection<Concept> fullTextSearch(String text, int maxResults) {
		return indexService.fullTextSearch(text, maxResults);
	}

	@Override
	public Concept getConceptByUuid(String uuid) {
		return indexService.getConceptByUuid(uuid);
	}

	@Override
	public Collection<Concept> getConceptsByLabel(Label label) {
		Collection<Label> labels = indexService.getLabelsByText(label.getText());
		Label found = null;
		for (Label label2 : labels) {
			if (label2.getLanguage() == label.getLanguage()) {
				found = label;
				break;
			}
		}
		// FIXME should i throw an exception here?
		List<Concept> concepts = new ArrayList<Concept>();
		if (found != null) {
			LabelImpl labelImpl = (LabelImpl) found;
			Iterable<Relationship> relationships = labelImpl.getUnderlyingNode().getRelationships(
					DefaultRelationshipType.HAS_LABEL, Direction.INCOMING);
			for (Relationship relationship : relationships) {
				concepts.add(new ConceptImpl(relationship.getStartNode()));
			}
		}
		return concepts;
	}

	@Override
	public Collection<Concept> getConceptsByType(ConceptType conceptType) {
		// FIXME there is a lot of overlap between concept type and default relationship types. On defining a new
		// concept type we will have to define a new DefaultRelationshipType and same for deletion, which is not
		// optimum. See if we can remove this dependency.
		DefaultRelationshipType defaultRelationshipType = null;
		switch (conceptType) {
		case CONCEPT:
			defaultRelationshipType = DefaultRelationshipType.CONCEPTS;
			break;
		case DOMAIN:
			defaultRelationshipType = DefaultRelationshipType.DOMAINS;
			break;
		case CONCEPT_SCHEME:
			defaultRelationshipType = DefaultRelationshipType.CONCEPT_SCHEMES;
			break;
		case PREDICATE:
			defaultRelationshipType = DefaultRelationshipType.PREDICATES;
			break;
		default:
			break;
		}
		Node node = graphStorageTemplate.getParentNode(defaultRelationshipType);
		List<Concept> concepts = new ArrayList<Concept>();
		Iterable<Relationship> relationships = node.getRelationships(DefaultRelationshipType.CONCEPTS,
				Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			concepts.add(new ConceptImpl(relationship.getEndNode()));
		}
		return concepts;
	}
}
