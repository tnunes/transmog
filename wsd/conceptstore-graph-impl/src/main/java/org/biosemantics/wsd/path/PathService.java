package org.biosemantics.wsd.path;

import org.neo4j.graphdb.Path;

public interface PathService {

	Path getShortestRelatedPath(String fromId, String toId, int maxTraversal);

	Path getShortestHierarchicalPath(String fromId, String toId, int maxTraversal);

}
