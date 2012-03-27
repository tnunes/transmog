package org.biosemantics.wsd.ssi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
//				Path relatedPath = getRelatedPaths(wordSenseNode, unAmbiguousNode);
//				
//				if (relatedPath == null) {
//					logger.debug("Related Path NULL wordsense:{} unambiguousId:{}", new Object[] { wordSense,
//							unambiguousId });
//				} else {
//					relatedPathLength = relatedPath.length();
//					logger.debug("Related path wordsense:{} unambiguousId:{} path:{}", new Object[] { wordSense, unambiguousId,
//							relatedPathLength });
//
//				}
				scores.add(new Score(unambiguousId, hierarchicalPathLength, relatedPathLength));
			}
		}
		return new SsiScore(wordSense, scores);
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
	private static final float CONSTANT_WEIGHT = 0.9F;
	private static final Logger logger = LoggerFactory.getLogger(SsiImpl.class);
}
