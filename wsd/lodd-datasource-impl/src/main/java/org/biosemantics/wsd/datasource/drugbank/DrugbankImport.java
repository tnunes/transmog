package org.biosemantics.wsd.datasource.drugbank;

import java.sql.SQLException;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DrugbankImport {
	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "drugbank-import-context.xml" });
		applicationContext.registerShutdownHook();
		DrugbankNotationWriter notationWriter = applicationContext.getBean(DrugbankNotationWriter.class);
		notationWriter.writeAll();
		applicationContext.close();
	}

}
