package org.biosemantics.disambiguation.service.local.impl;

import static org.biosemantics.disambiguation.common.PropertyConstant.WEIGHT;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.disambiguation.service.local.AlgorithmServiceLocal;
import org.biosemantics.disambiguation.service.local.ConceptStorageServiceLocal;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.Traversal;
import org.springframework.beans.factory.annotation.Required;

public class AlgorithmServiceLocalImpl implements AlgorithmServiceLocal {

	private ConceptStorageServiceLocal conceptStorageServiceLocal;

	@Required
	public void setConceptStorageServiceLocal(ConceptStorageServiceLocal conceptStorageServiceLocal) {
		this.conceptStorageServiceLocal = conceptStorageServiceLocal;
	}

	@Override
	public Path dijkstra(String fromConceptUuid, String toConceptUuid) {
		Node from = conceptStorageServiceLocal.getConceptNode(fromConceptUuid);
		Node to = conceptStorageServiceLocal.getConceptNode(toConceptUuid);
		PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(
				Traversal.expanderForTypes(ConceptRelationshipType.RELATED, Direction.BOTH),
				new CostEvaluator<Double>() {

					@Override
					public Double getCost(Relationship relationship, Direction direction) {
						return Double.valueOf(1 / (Double) relationship.getProperty(WEIGHT.name()));
					}
				});
		WeightedPath path = finder.findSinglePath(from, to);
		return path;
	}

	@Override
	public Iterable<Path> shortestPath(String fromConceptUuid, String toConceptUuid, int maxDepth) {
		Node from = conceptStorageServiceLocal.getConceptNode(fromConceptUuid);
		Node to = conceptStorageServiceLocal.getConceptNode(toConceptUuid);
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(
				ConceptRelationshipType.RELATED, Direction.BOTH, ConceptRelationshipType.HAS_BROADER_CONCEPT,
				Direction.BOTH, ConceptRelationshipType.HAS_NARROWER_CONCEPT, Direction.BOTH,
				ConceptRelationshipType.IN_SCHEME, Direction.BOTH), maxDepth);
		Iterable<Path> paths = finder.findAllPaths(from, to);
		return paths;
	}

	@Override
	public Path shortestSinglePath(String fromConceptUuid, String toConceptUuid, int maxDepth) {
		Node from = conceptStorageServiceLocal.getConceptNode(fromConceptUuid);
		Node to = conceptStorageServiceLocal.getConceptNode(toConceptUuid);
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(
				ConceptRelationshipType.RELATED, Direction.BOTH, ConceptRelationshipType.HAS_BROADER_CONCEPT,
				Direction.BOTH, ConceptRelationshipType.HAS_NARROWER_CONCEPT, Direction.BOTH,
				ConceptRelationshipType.IN_SCHEME, Direction.BOTH), maxDepth);
		return finder.findSinglePath(from, to);
	}
}
