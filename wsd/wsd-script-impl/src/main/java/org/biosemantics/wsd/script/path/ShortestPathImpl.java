package org.biosemantics.wsd.script.path;

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

public class ShortestPathImpl {

	public void setMaxBreadth(int maxBreadth) {
		this.maxBreadth = maxBreadth;
	}

	public Path findShortestHierarchicalPath(String cui, String otherCui) {
		Node from = getNode(cui);
		Node to = getNode(otherCui);
		if (from == null || to == null) {
			logger.error("from:{} to:{}", new Object[] { from, to });
			throw new IllegalStateException("node not found");
		} else {
			return getHierarchicalPath(from, to);
		}
	}

	public Path findShortestRelatedPath(String cui, String otherCui) {
		Node from = getNode(cui);
		Node to = getNode(otherCui);
		if (from == null || to == null) {
			logger.error("from:{} to:{}", new Object[] { from, to });
			throw new IllegalStateException("node not found");
		} else {
			return getRelatedPath(from, to);
		}
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
		}, Direction.BOTH), maxBreadth);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	private Path getRelatedPath(Node from, Node to) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(new RelationshipType() {
			public String name() {
				return "RELATED";
			}
		}, Direction.BOTH), maxBreadth);
		Path path = finder.findSinglePath(from, to);
		return path;
	}

	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private ConceptRepository conceptRepository;
	private int maxBreadth = 6;
	private static final Logger logger = LoggerFactory.getLogger(ShortestPathImpl.class);

}
