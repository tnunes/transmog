package org.biosemantics.conceptstore.repository.impl;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.TraversalRepository;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraversalRepositoryImpl implements TraversalRepository {

	public TraversalRepositoryImpl(GraphDatabaseService graphDatabaseService, ConceptRepository conceptRepository) {
		this.graphDb = graphDatabaseService;
		this.conceptRepository = conceptRepository;
		Collection<Concept> concepts = conceptRepository.getByType(ConceptType.PREDICATE);
		predicateExpander = Traversal.emptyExpander();
		for (Concept concept : concepts) {
			RelationshipType rlspType = DynamicRelationshipType.withName(concept.getId().toString());
			predicateExpander = predicateExpander.add(rlspType, Direction.BOTH);
		}
		logger.info("predicate expander created and cached");
	}

	@Override
	public Iterable<Path> findShortestPath(long startConceptId, long endConceptId,
			Collection<Long> predicatesConceptIdsToFollow, int maxDepth) {
		// create expander here
		long startTime = System.currentTimeMillis();
		Expander expander = Traversal.emptyExpander();
		for (Long predicateConceptId : predicatesConceptIdsToFollow) {
			expander = expander.add(DynamicRelationshipType.withName(predicateConceptId.toString()), Direction.BOTH);
		}
		PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(expander, maxDepth);
		Node start = graphDb.getNodeById(startConceptId);
		Node end = graphDb.getNodeById(endConceptId);
		Iterable<Path> paths = pathFinder.findAllPaths(start, end);
		logger.debug("time taken for calculating shortest paths: {} (millisec)", (System.currentTimeMillis() - startTime));
		return paths;
	}

	@Override
	public Iterable<Path> findShortestPath(long startConceptId, long endConceptId, int maxDepth) {
		long startTime = System.currentTimeMillis();
		PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(predicateExpander, maxDepth);
		Node start = graphDb.getNodeById(startConceptId);
		Node end = graphDb.getNodeById(endConceptId);
		Iterable<Path> paths = pathFinder.findAllPaths(start, end);
		logger.debug("time taken for calculating shortest paths: {} (millisec)", (System.currentTimeMillis() - startTime));
		return paths;
	}

	private GraphDatabaseService graphDb;
	private ConceptRepository conceptRepository;
	private Expander predicateExpander;
	private static final Logger logger = LoggerFactory.getLogger(TraversalRepositoryImpl.class);

}
