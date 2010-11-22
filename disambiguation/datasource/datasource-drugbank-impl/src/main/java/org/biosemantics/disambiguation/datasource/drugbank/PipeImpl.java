package org.biosemantics.disambiguation.datasource.drugbank;

import org.biosemantics.disambiguation.service.impl.GraphStorageTemplate;
import org.biosemantics.disambiguation.service.impl.GraphStorageTemplateImpl;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.drugbank.generated.Drugs;

public class PipeImpl {

	private static final String[] CONTEXT = new String[] { "datasource-drugbank-impl-test-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(PipeImpl.class);

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT);
		DataSourceReaderImpl dataSourceReader = (DataSourceReaderImpl) applicationContext
				.getBean(DataSourceReader.class);
		DataSourceWriterImpl dataSourceWriter = (DataSourceWriterImpl) applicationContext
				.getBean(DataSourceWriter.class);
		GraphStorageTemplateImpl graphStorageTemplateImpl = (GraphStorageTemplateImpl) applicationContext
				.getBean(GraphStorageTemplate.class);
		DomainIterator domainIterator = applicationContext.getBean(DomainIterator.class);
		Transaction tx = graphStorageTemplateImpl.getGraphDatabaseService().beginTx();
		try {
			while (domainIterator.hasNext()) {
				dataSourceWriter.writeDomain(domainIterator.next());
			}

			Drugs[] drugsArray = dataSourceReader.getAllDrugs();
			logger.info("total number of drugs is {}", drugsArray.length);
			int ctr = 0;
			for (Drugs drugs : drugsArray) {
				long start = System.currentTimeMillis();
				dataSourceWriter.writeConcept(drugs);
				logger.info("{} written in {}(ms)", new Object[] { ctr++, (System.currentTimeMillis() - start) });
			}
			tx.success();
		} catch (Exception e) {
			logger.error("Error in drugbank parsing ", e);
			tx.failure();
		} finally {
			logger.info("finish called on tx");
			tx.finish();
		}
		logger.info("done");
		graphStorageTemplateImpl.getGraphDatabaseService().shutdown();
		logger.info("shutdown");
	}
}
