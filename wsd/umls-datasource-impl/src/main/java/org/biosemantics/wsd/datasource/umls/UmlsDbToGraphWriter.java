package org.biosemantics.wsd.datasource.umls;

import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UmlsDbToGraphWriter {

	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		applicationContext.registerShutdownHook();
		ConceptNodeWriter conceptNodeWriter = applicationContext.getBean(ConceptNodeWriter.class);
		conceptNodeWriter.writeAll();
		RelationshipWriter relationshipWriter = applicationContext.getBean(RelationshipWriter.class);
		relationshipWriter.writeAll();
		applicationContext.close();
	}

}
