package org.biosemantics.wsd.datasource.lodd;

import java.sql.SQLException;

import org.biosemantics.wsd.datasource.dailymed.DailymedNotationWriter;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoddImport {
	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException, RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "drugbank-import-context.xml" });
		applicationContext.registerShutdownHook();
		// DrugbankNotationWriter notationWriter =
		// applicationContext.getBean(DrugbankNotationWriter.class);
		// notationWriter.writeAll();
		DailymedNotationWriter dailymedNotationWriter = applicationContext.getBean(DailymedNotationWriter.class);
		dailymedNotationWriter.writeAll();
		applicationContext.close();
	}

}
