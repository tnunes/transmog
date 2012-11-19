package org.biosemantics.wsd.datasource.umls;

import java.io.*;
import java.sql.*;

import org.springframework.context.support.*;

public class UmlsDbToGraphWriter {

	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException, IOException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		applicationContext.registerShutdownHook();
		ConceptWriter conceptWriter = applicationContext.getBean(ConceptWriter.class);
		conceptWriter.writeSemanticTypes();
		conceptWriter.writeConcepts();
	}

}
