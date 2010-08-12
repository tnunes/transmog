package org.biosemantics.disambiguation.knowledgebase;

import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractTransactionalDataSource {

	@Autowired
	private GraphDatabaseService graphDb;

	private Transaction transaction;

	@Before
	public void beforeTest() {
		transaction = graphDb.beginTx();
	}

	@After
	public void afterTest() {
		transaction.failure();
		transaction.finish();
	}
	
}
