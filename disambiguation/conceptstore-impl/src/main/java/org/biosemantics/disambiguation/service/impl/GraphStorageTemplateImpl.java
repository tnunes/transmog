package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class GraphStorageTemplateImpl implements GraphStorageTemplate {

	private final String dataStore;
	private final GraphDatabaseService graphDatabaseService;
	private Map<String, String> configuration;
	private Map<DefaultRelationshipType, Node> parentNodes;
	private long nodeCount;
	private long rlspCount;

	public GraphStorageTemplateImpl(String dataStore, Map<String, String> configuration) {
		super();
		this.dataStore = checkNotNull(dataStore);
		this.configuration = checkNotNull(configuration);
		graphDatabaseService = new EmbeddedGraphDatabase(this.dataStore, this.configuration);
		parentNodes = new HashMap<DefaultRelationshipType, Node>();
	}

	public GraphStorageTemplateImpl(String dataStore) {
		super();
		this.dataStore = checkNotNull(dataStore);
		graphDatabaseService = new EmbeddedGraphDatabase(this.dataStore);
		parentNodes = new HashMap<DefaultRelationshipType, Node>();
	}

	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	public String getDataStore() {
		return dataStore;
	}

	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}

	public void init() {
		// mind the "S" in the end
		parentNodes.put(DefaultRelationshipType.LABELS, initParentNodes(DefaultRelationshipType.LABELS));
		parentNodes.put(DefaultRelationshipType.NOTATIONS, initParentNodes(DefaultRelationshipType.NOTATIONS));
		parentNodes.put(DefaultRelationshipType.NOTES, initParentNodes(DefaultRelationshipType.NOTES));
		parentNodes.put(DefaultRelationshipType.CONCEPTS, initParentNodes(DefaultRelationshipType.CONCEPTS));
		parentNodes.put(DefaultRelationshipType.PREDICATES, initParentNodes(DefaultRelationshipType.PREDICATES));
		parentNodes.put(DefaultRelationshipType.DOMAINS, initParentNodes(DefaultRelationshipType.DOMAINS));
		parentNodes.put(DefaultRelationshipType.CONCEPT_SCHEMES,
				initParentNodes(DefaultRelationshipType.CONCEPT_SCHEMES));

	}

	private Node initParentNodes(DefaultRelationshipType defaultRelationshipTypes) {
		Transaction transaction = graphDatabaseService.beginTx();
		Node subNode = null;
		try {
			Relationship relationship = graphDatabaseService.getReferenceNode().getSingleRelationship(
					defaultRelationshipTypes, Direction.OUTGOING);
			if (relationship == null) {
				// create new sub node
				subNode = graphDatabaseService.createNode();
				graphDatabaseService.getReferenceNode().createRelationshipTo(subNode, defaultRelationshipTypes);
			} else {
				// return existing subnode
				subNode = relationship.getEndNode();
			}
			transaction.success();
			return subNode;
		} finally {
			transaction.finish();
		}

	}

	public void destroy() {
		parentNodes.clear();
		parentNodes = null;
		configuration.clear();
		configuration = null;
		graphDatabaseService.shutdown();
	}

	@Override
	public Node getParentNode(DefaultRelationshipType defaultRelationshipTypes) {
		return parentNodes.get(defaultRelationshipTypes);
	}

	@Override
	public Node createNode() {
		nodeCount++;
		return graphDatabaseService.createNode();
	}

	@Override
	public long getNodeCount() {
		return nodeCount;
	}

	@Override
	public Relationship createRelationship(Node from, Node to, RelationshipType relationshipType) {
		rlspCount++;
		return from.createRelationshipTo(to, relationshipType);

	}

	@Override
	public long getRelationshipCount() {
		return rlspCount;
	}

}
