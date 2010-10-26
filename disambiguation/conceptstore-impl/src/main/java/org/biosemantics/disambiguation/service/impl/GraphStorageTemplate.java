package org.biosemantics.disambiguation.service.impl;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public interface GraphStorageTemplate {
	public String getDataStore();

	public Map<String, String> getConfiguration();

	public GraphDatabaseService getGraphDatabaseService();

	public Node getParentNode(DefaultRelationshipType defaultRelationshipTypes);

	public Node createNode();

	public long getNodeCount();

	public Relationship createRelationship(Node from, Node to, RelationshipType relationshipType);

	public long getRelationshipCount();

}