package org.biosemantics.wsd.datasource.umls;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UmlsDbToGraphWriter {

	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException, IOException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		applicationContext.registerShutdownHook();
		// UmlsToStoreWriter umlsToStoreWriter =
		// applicationContext.getBean(UmlsToStoreWriter.class);
		// umlsToStoreWriter.writeUmlsToStore();
		PubmedFileRlspWriter pubmedFileRlspWriter = applicationContext.getBean(PubmedFileRlspWriter.class);
		pubmedFileRlspWriter.setInputFile("/Users/bhsingh/Erik");
		pubmedFileRlspWriter.validateFile();
		// pubmedFileRlspWriter.addPubmedRelationships();
	}

}
