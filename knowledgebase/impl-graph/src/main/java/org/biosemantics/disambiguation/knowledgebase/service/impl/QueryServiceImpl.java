package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.QueryService;
import org.biosemantics.disambiguation.knowledgebase.service.local.TextIndexService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class QueryServiceImpl implements QueryService {

	private TextIndexService textIndexService;

	public void setTextIndexService(TextIndexService textIndexService) {
		this.textIndexService = textIndexService;
	}

	@Override
	public Collection<Concept> getConceptsByNotation(Domain domain, String code) {
		List<Concept> concepts = new ArrayList<Concept>();
		NotationImpl notationImpl = (NotationImpl) textIndexService.getNotationsByDomainAndCode(domain, code);
		if (notationImpl != null) {
			Traverser traverser = notationImpl.getUnderlyingNode().traverse(Order.BREADTH_FIRST,
					StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
					KnowledgebaseRelationshipType.HAS_NOTATION, Direction.INCOMING);
			for (Node conceptNode : traverser) {
				concepts.add(new ConceptImpl(conceptNode));
			}
		}
		return concepts;
	}

	@Override
	public Concept getConceptById(String id) {
		if (StringUtils.isBlank(id))
			throw new IllegalArgumentException("id cannot be blank");
		Concept concept = textIndexService.getConceptById(id);
		if (concept == null)
			throw new IllegalArgumentException("No concept found with id " + id);
		return concept;
	}

	@Override
	public Collection<Concept> getConceptsByLabelText(String labelText) {
		if (StringUtils.isBlank(labelText))
			throw new IllegalArgumentException("labelText cannot be blank");
		List<Concept> concepts = new ArrayList<Concept>();
		Collection<Label> labels = textIndexService.getLabelsByText(labelText);
		for (Label label : labels) {
			LabelImpl labelImpl = (LabelImpl) label;
			if (labelImpl != null) {
				Traverser traverser = labelImpl.getUnderlyingNode().traverse(Order.BREADTH_FIRST,
						StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
						KnowledgebaseRelationshipType.HAS_LABEL, Direction.INCOMING);
				for (Node conceptNode : traverser) {
					concepts.add(new ConceptImpl(conceptNode));
				}
			}
		}
		return concepts;
	}

	@Override
	public Collection<Concept> getConceptsByFullTextSearch(String text, int maxResults) {
		if (StringUtils.isBlank(text))
			throw new IllegalArgumentException("text cannot be blank");
		if (maxResults <= 0)
			throw new IllegalArgumentException("maxResults cannot be zero or a negative value");
		Collection<Concept> concepts = textIndexService.fullTextSearch(text, maxResults);
		if (concepts == null) {
			return new ArrayList<Concept>();
		} else {
			return concepts;
		}
	}
}
