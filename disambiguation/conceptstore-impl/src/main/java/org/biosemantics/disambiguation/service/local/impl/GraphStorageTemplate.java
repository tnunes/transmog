package org.biosemantics.disambiguation.service.local.impl;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexManager;

public interface GraphStorageTemplate {
	public String getDataStore();

	public Map<String, String> getConfiguration();

	public GraphDatabaseService getGraphDatabaseService();

	public Node getParentNode(DefaultRelationshipType defaultRelationshipType);


	public IndexManager getIndexManager();

}
