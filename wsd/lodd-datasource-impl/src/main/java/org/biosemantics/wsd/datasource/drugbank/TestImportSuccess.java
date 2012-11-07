package org.biosemantics.wsd.datasource.drugbank;

import java.sql.SQLException;

import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestImportSuccess {
	private static ClassPathXmlApplicationContext applicationContext;
	
	public static void main(String[] args) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "drugbank-import-context.xml" });
		applicationContext.registerShutdownHook();
		NotationRepository notationRepository = applicationContext.getBean(NotationRepository.class);
		Iterable<Notation> notations = notationRepository.findAllByPropertyValue("code", "DB00002");
		for (Notation notation : notations) {
			System.err.println(notation);
		}
		applicationContext.close();
	}

}
