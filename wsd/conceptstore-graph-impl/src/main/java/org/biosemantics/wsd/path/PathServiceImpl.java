package org.biosemantics.wsd.path;

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

public class PathServiceImpl implements PathService {

	@Override
	public Path getShortestRelatedPath(String fromId, String toId, int maxTraversal) {
		Node fromNode = getNode(fromId);
		Node toNode = getNode(toId);
		if (fromNode == null || toNode == null) {
			logger.error("fromNode:{} toNode:{}", new Object[] { fromNode, toNode });
			throw new IllegalArgumentException("one or both nodes not found");
		}
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "RELATED";
			}
		}, Direction.BOTH), maxTraversal);
		Path path = finder.findSinglePath(fromNode, toNode);
		return path;
	}

	@Override
	public Path getShortestHierarchicalPath(String fromId, String toId, int maxTraversal) {
		Node fromNode = getNode(fromId);
		Node toNode = getNode(toId);
		if (fromNode == null || toNode == null) {
			logger.error("fromNode:{} toNode:{}", new Object[] { fromNode, toNode });
			throw new IllegalArgumentException("one or both nodes not found");
		}
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "CHILD";
			}
		}, Direction.BOTH), maxTraversal);

		Path path = finder.findSinglePath(fromNode, toNode);
		return path;
	}

	private Node getNode(String id) {
		Concept concept = conceptRepository.getConceptById(id);
		return neo4jTemplate.getNode(concept.getNodeId());
	}

	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	private static final Logger logger = LoggerFactory.getLogger(PathServiceImpl.class);

}
