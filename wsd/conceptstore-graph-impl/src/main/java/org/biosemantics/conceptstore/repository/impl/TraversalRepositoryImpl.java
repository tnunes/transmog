package org.biosemantics.conceptstore.repository.impl;

import java.util.Collection;

import org.biosemantics.conceptstore.repository.TraversalRepository;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraversalRepositoryImpl implements TraversalRepository {

	public TraversalRepositoryImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDb = graphDatabaseService;
	}

	@Override
	public Iterable<Path> findShortestPath(long startConceptId, long endConceptId,
			Collection<Long> predicatesConceptIdsToFollow, int maxDepth) {
		// create expander here
		Expander expander = Traversal.emptyExpander();
		for (Long predicateConceptId : predicatesConceptIdsToFollow) {
			expander = expander.add(DynamicRelationshipType.withName(predicateConceptId.toString()));
		}
		PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(expander, maxDepth);
		Node start = graphDb.getNodeById(startConceptId);
		Node end = graphDb.getNodeById(endConceptId);
		Iterable<Path> paths = pathFinder.findAllPaths(start, end);
		return paths;
	}

	private GraphDatabaseService graphDb;
	private static final Logger logger = LoggerFactory.getLogger(TraversalRepositoryImpl.class);

}
