package org.biosemantics.wsd.datasource.umls;

import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UmlsDbToGraphWriter {

	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		ConceptNodeWriter conceptNodeWriter = applicationContext.getBean(ConceptNodeWriter.class);
		conceptNodeWriter.writeAll();
		applicationContext.close();
		//hopefully release memory
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		applicationContext.registerShutdownHook();
		RelationshipWriter relationshipWriter = applicationContext.getBean(RelationshipWriter.class);
		relationshipWriter.writeAll();
		applicationContext.close();

	}

}
