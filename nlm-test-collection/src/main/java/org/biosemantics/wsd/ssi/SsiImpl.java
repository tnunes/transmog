package org.biosemantics.wsd.ssi;

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

public class SsiImpl {

	public SsiScore getSsiScore(Collection<String> unambiguousIds, String wordSense) {
		Node wordSenseNode = getNode(wordSense);
		List<Score> scores = new ArrayList<Score>();
		for (String unambiguousId : unambiguousIds) {
			if (unambiguousId.equals(wordSense)) {
				logger.debug("same concept wordsense:{} unambiguousId:{} ignoring.", new Object[] { wordSense,
						unambiguousId });
			} else {
				int hierarchicalPathLength = 0;
				Node unAmbiguousNode = null;
				try {
					unAmbiguousNode = getNode(unambiguousId);
				} catch (Throwable e) {
					logger.error("error in getNode() for cui = " + unambiguousId, e);
					continue;
				}
				Path hierarchicalPath = getHierarchicalPaths(wordSenseNode, unAmbiguousNode);

				if (hierarchicalPath == null) {
					logger.debug("hierarchical path NULL wordsense:{} unambiguousId:{}", new Object[] { wordSense,
							unambiguousId });
				} else {
					hierarchicalPathLength = hierarchicalPath.length();
					logger.debug("Hierarchical wordsense:{} unambiguousId:{} path:{}", new Object[] { wordSense,
							unambiguousId, hierarchicalPathLength });
				}

				int relatedPathLength = 0;
				Path relatedPath = null;
				// Path relatedPath = getRelatedPaths(wordSenseNode, unAmbiguousNode);
				//
				// if (relatedPath == null) {
				// logger.debug("Related Path NULL wordsense:{} unambiguousId:{}", new Object[] { wordSense,
				// unambiguousId });
				// } else {
				// relatedPathLength = relatedPath.length();
				// logger.debug("Related path wordsense:{} unambiguousId:{} path:{}", new Object[] { wordSense,
				// unambiguousId,
				// relatedPathLength });
				//
				// }
				scores.add(new Score(unambiguousId, hierarchicalPathLength, relatedPathLength));
			}
		}
		return new SsiScore(wordSense, scores);
	}

	public List<SsiScore> getScore(Collection<String> unambiguousIds, Collection<String> ambiguousSet) {
		long start = System.currentTimeMillis();
		List<SsiScore> ssiScores = new ArrayList<SsiScore>();
		Map<String, Node> ambiguosCuiMap = new HashMap<String, Node>();
		for (String ambiguousCui : ambiguousSet) {
			ambiguosCuiMap.put(ambiguousCui, getNode(ambiguousCui));
		}
		for (String unambiguousCui : unambiguousIds) {
			Node unambiguousNode = null;
			try {
				unambiguousNode = getNode(unambiguousCui);
			} catch (Throwable e) {
				logger.error("", e);
			}
			if (unambiguousNode != null) {
				List<Score> scores = new ArrayList<Score>();
				for (Entry<String, Node> entry : ambiguosCuiMap.entrySet()) {
					Path hierarchicalPath = getHierarchicalPaths(entry.getValue(), unambiguousNode);
					int hierarchicalHops = -1;
					if (hierarchicalPath != null) {
						hierarchicalHops = hierarchicalPath.length();
					}
					Path relatedPath = getRelatedPaths(entry.getValue(), unambiguousNode);
					int relatedHops = -1;
					if (relatedPath != null) {
						relatedHops = relatedPath.length();
					}
					Score score = new Score(entry.getKey(), hierarchicalHops, relatedHops);
					scores.add(score);
				}
				SsiScore ssiScore = new SsiScore(unambiguousCui, scores);
				ssiScores.add(ssiScore);
			}
		}
		logger.info("completed in {} ms ", System.currentTimeMillis() - start);
		return ssiScores;
	}

	private Node getNode(String id) {
		Concept concept = conceptRepository.getConceptById(id);
		return neo4jTemplate.getNode(concept.getNodeId());
	}

	private Path getHierarchicalPaths(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "CHILD";
			}
		}, Direction.BOTH), MAX_TREE_WALK);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	private Path getRelatedPaths(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "RELATED";
			}
		}, Direction.BOTH), MAX_TREE_WALK);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private ConceptRepository conceptRepository;

	private static final int MAX_TREE_WALK = 6;
	private static final Logger logger = LoggerFactory.getLogger(SsiImpl.class);
}
