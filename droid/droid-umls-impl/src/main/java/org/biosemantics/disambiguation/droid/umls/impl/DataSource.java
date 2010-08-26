package org.biosemantics.disambiguation.droid.umls.impl;

import org.neo4j.graphdb.GraphDatabaseService;

public interface DataSource {
	void setBatchSize(int batchSize);
	void initialize() throws Exception;
	void setGraphDatabaseService(GraphDatabaseService graphDatabaseService);
}
