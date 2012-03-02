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
				float hierarchicalScore = 1F;
				Node unAmbiguousNode = getNode(unambiguousId);
				Path hierarchicalPath = getHierarchicalPaths(wordSenseNode, unAmbiguousNode);
				if (hierarchicalPath == null) {
					logger.debug("wordsense:{} unambiguousId:{} hierarchical path = null", new Object[] { wordSense,
							unambiguousId });
					hierarchicalScore = 0F;
				} else {
					int length = hierarchicalPath.length();
					logger.debug("wordsense:{} unambiguousId:{} path:{}", new Object[] { wordSense, unambiguousId,
							length });
					for (int i = 0; i < length; i++) {
						hierarchicalScore = hierarchicalScore * CONSTANT_WEIGHT;
					}

				}

				float relatedScore = 1F;
				Path relatedPath = getRelatedPaths(wordSenseNode, unAmbiguousNode);
				if (relatedPath == null) {
					logger.debug("wordsense:{} unambiguousId:{} related path = null", new Object[] { wordSense,
							unambiguousId });
					relatedScore = 0F;
				} else {
					int length = relatedPath.length();
					logger.debug("wordsense:{} unambiguousId:{} path:{}", new Object[] { wordSense, unambiguousId,
							length });
					for (int i = 0; i < length; i++) {
						relatedScore = relatedScore * CONSTANT_WEIGHT;
					}
				}
				scores.add(new Score(unambiguousId, hierarchicalScore, relatedScore));
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

	private static final int MAX_TREE_WALK = 10;
	private static final float CONSTANT_WEIGHT = 0.9F;
	private static final Logger logger = LoggerFactory.getLogger(SsiImpl.class);
}
