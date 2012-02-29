package org.biosemantics.wsd.datasource.umls;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UmlsDbToGraphWriter {

	private ApplicationContext applicationContext;

	public UmlsDbToGraphWriter() throws SQLException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		ConceptNodeWriter conceptNodeWriter = applicationContext.getBean(ConceptNodeWriter.class);
		conceptNodeWriter.writeAll();
	}

	public static void main(String[] args) throws SQLException {
		UmlsDbToGraphWriter obj = new UmlsDbToGraphWriter();
	}

}
