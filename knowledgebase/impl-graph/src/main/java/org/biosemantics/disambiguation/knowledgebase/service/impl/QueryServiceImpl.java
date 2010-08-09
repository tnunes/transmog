package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.QueryService;
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
}
