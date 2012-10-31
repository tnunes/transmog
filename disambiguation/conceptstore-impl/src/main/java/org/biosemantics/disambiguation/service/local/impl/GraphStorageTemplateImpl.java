package org.biosemantics.disambiguation.service.local.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.biosemantics.disambiguation.common.RelationshipTypeConstant;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphStorageTemplateImpl implements GraphStorageTemplate {

	private final String dataStore;
	private EmbeddedGraphDatabase graphDatabaseService;
	private WrappingNeoServerBootstrapper server;
	private Map<String, String> configuration;
	private Map<RelationshipTypeConstant, Node> parentNodes;

	private static final Logger logger = LoggerFactory.getLogger(GraphStorageTemplateImpl.class);

	public GraphStorageTemplateImpl(String dataStore, boolean startServer, Map<String, String> configuration) {
		super();
		this.dataStore = checkNotNull(dataStore);
		this.configuration = checkNotNull(configuration);

		graphDatabaseService = new EmbeddedGraphDatabase(this.dataStore, this.configuration);
		logger.info("graph database data folder is \"{}\"",
				((EmbeddedGraphDatabase) graphDatabaseService).getStoreDir());
		logger.info("graph database configuration is \"{}\"", this.configuration);
		if (startServer) {
			server = new WrappingNeoServerBootstrapper(graphDatabaseService);
			server.start();
		}
		parentNodes = new HashMap<RelationshipTypeConstant, Node>();
	}

	public GraphStorageTemplateImpl(String dataStore, boolean startServer) {
		super();
		this.dataStore = checkNotNull(dataStore);
		graphDatabaseService = new EmbeddedGraphDatabase(this.dataStore);
		logger.info("graph database data folder is \"{}\"",
				((EmbeddedGraphDatabase) graphDatabaseService).getStoreDir());
		if (startServer) {
			server = new WrappingNeoServerBootstrapper(graphDatabaseService);
			server.start();
		}
		parentNodes = new HashMap<RelationshipTypeConstant, Node>();
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
		parentNodes.put(RelationshipTypeConstant.LABELS, initParentNodes(RelationshipTypeConstant.LABELS));
		parentNodes.put(RelationshipTypeConstant.NOTATIONS, initParentNodes(RelationshipTypeConstant.NOTATIONS));
		parentNodes.put(RelationshipTypeConstant.NOTES, initParentNodes(RelationshipTypeConstant.NOTES));
		parentNodes.put(RelationshipTypeConstant.CONCEPTS, initParentNodes(RelationshipTypeConstant.CONCEPTS));
	}

	private Node initParentNodes(RelationshipTypeConstant RelationshipTypeConstants) {
		Transaction transaction = graphDatabaseService.beginTx();
		Node subNode = null;
		try {
			Relationship relationship = graphDatabaseService.getReferenceNode().getSingleRelationship(
					RelationshipTypeConstants, Direction.OUTGOING);
			if (relationship == null) {
				// create new sub node
				subNode = graphDatabaseService.createNode();
				graphDatabaseService.getReferenceNode().createRelationshipTo(subNode, RelationshipTypeConstants);
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
		if (parentNodes != null) {
			parentNodes.clear();
			parentNodes = null;
		}
		if (configuration != null) {
			configuration.clear();
			configuration = null;
		}
		if (graphDatabaseService != null) {
			graphDatabaseService.shutdown();
		}
	}

	@Override
	public Node getParentNode(RelationshipTypeConstant relationshipTypeConstant) {
		return parentNodes.get(relationshipTypeConstant);
	}

	public IndexManager getIndexManager() {
		return this.graphDatabaseService.index();
	}

}
