package org.biosemantics.wsd.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class SimilarityImpl {

	private static final int MAX_HOPS = 100;

	public Collection<PathScore> pathSimilarity(Collection<String> unambiguousIds, Collection<String> ambiguousSet) {
		long start = System.currentTimeMillis();
		List<PathScore> pathScores = new ArrayList<PathScore>();
		Map<String, Node> ambiguousCuiMap = new HashMap<String, Node>();
		for (String ambiguousCui : ambiguousSet) {
			ambiguousCuiMap.put(ambiguousCui, getNode(ambiguousCui));
		}
		for (String unambiguousCui : unambiguousIds) {
			Node unambiguousNode = null;
			try {
				unambiguousNode = getNode(unambiguousCui);
			} catch (Throwable e) {
				logger.error("could not find node for: {}", unambiguousCui);
				logger.error("", e);
			}
			if (unambiguousNode != null) {
				for (Entry<String, Node> entry : ambiguousCuiMap.entrySet()) {
					Iterable<Path> hierarchicalPaths = getHierarchicalPaths(entry.getValue(), unambiguousNode);
					int minHierHops = MAX_HOPS;
					int totalHierPaths = 0;
					if (hierarchicalPaths != null) {
						for (Path path : hierarchicalPaths) {
							minHierHops = path.length();
							totalHierPaths++;
						}
					}
					Iterable<Path> relatedPaths = getRelatedPaths(entry.getValue(), unambiguousNode);
					int minRelatedHops = MAX_HOPS;
					int totalRelatedPaths = 0;
					if (relatedPaths != null) {
						for (Path path : relatedPaths) {
							minRelatedHops = path.length();
							totalRelatedPaths++;
						}
					}
					PathScore pathScore = new PathScore(unambiguousCui, entry.getKey(), totalHierPaths, minHierHops,
							totalRelatedPaths, minRelatedHops);
					pathScores.add(pathScore);
				}
			}
		}
		logger.info("pathSimilarity calculated in {} ms ", System.currentTimeMillis() - start);
		return pathScores;
	}

	private Node getNode(String id) {
		Concept concept = conceptRepository.getConceptById(id);
		return neo4jTemplate.getNode(concept.getNodeId());
	}

	private Path getHierarchicalPath(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "CHILD";
			}
		}, Direction.BOTH), MAX_TREE_WALK);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	private Iterable<Path> getHierarchicalPaths(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "CHILD";
			}
		}, Direction.BOTH), MAX_TREE_WALK);
		Iterable<Path> paths = finder.findAllPaths(from, to);
		return paths;
	}

	private Path getRelatedPath(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "RELATED";
			}
		}, Direction.BOTH), MAX_TREE_WALK);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	private Iterable<Path> getRelatedPaths(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "RELATED";
			}
		}, Direction.BOTH), MAX_TREE_WALK);
		Iterable<Path> paths = finder.findAllPaths(from, to);
		return paths;
	}

	private static final int MAX_TREE_WALK = 6;
	private static final Logger logger = LoggerFactory.getLogger(SimilarityImpl.class);
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private ConceptRepository conceptRepository;
}
