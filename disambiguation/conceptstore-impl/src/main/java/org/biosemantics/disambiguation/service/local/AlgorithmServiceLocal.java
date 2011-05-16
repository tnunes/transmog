package org.biosemantics.disambiguation.service.local;

import org.neo4j.graphdb.Path;

public interface AlgorithmServiceLocal {
	
	Path dijkstra(String fromConceptUuid, String toConceptUuid);
	Iterable<Path> shortestPath(String fromConceptUuid, String toConceptUuid, int maxDepth);

}
