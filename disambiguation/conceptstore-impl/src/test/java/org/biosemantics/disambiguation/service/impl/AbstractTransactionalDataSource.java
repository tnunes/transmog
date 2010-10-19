package org.biosemantics.disambiguation.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from files in the root of the classpath
@ContextConfiguration({ "/conceptstore-disambiguation-test-context.xml" })
public abstract class AbstractTransactionalDataSource {

	@Autowired
	private GraphStorageTemplate graphStorageTemplate;

	private Transaction transaction;

	@Before
	public void beforeTest() {
		transaction = graphStorageTemplate.getGraphDatabaseService().beginTx();
	}

	@After
	public void afterTest() {
		transaction.failure();
		transaction.finish();
	}
	
}
