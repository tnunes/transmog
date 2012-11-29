package org.biosemantics.conceptstore.repository;

import java.util.Collection;

import org.neo4j.graphdb.Path;

public interface TraversalRepository {
	/**
	 * 
	 * @param startConceptId
	 * @param endConceptId
	 * @param predicatesConceptIdsToFollow
	 * @param maxDepth
	 * @return
	 * @since 1.7
	 */
	public abstract Iterable<Path> findShortestPath(long startConceptId, long endConceptId,
			Collection<Long> predicatesConceptIdsToFollow, int maxDepth);

	/**
	 * 
	 * @param startConceptId
	 * @param endConceptId
	 * @param maxDepth
	 * @return
	 * @since 1.9
	 */
	public abstract Iterable<Path> findShortestPath(long startConceptId, long endConceptId, int maxDepth);

}