package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UmlsDbToGraphWriter {

	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException, IOException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		applicationContext.registerShutdownHook();
		MultithreadedFileImport multithreadedFileImport = applicationContext.getBean(MultithreadedFileImport.class);
		multithreadedFileImport.setFolder(new File("/Users/bhsingh/code/pubmed"));
		multithreadedFileImport.fire();
		// UmlsToStoreWriter umlsToStoreWriter =
		// applicationContext.getBean(UmlsToStoreWriter.class);
		// umlsToStoreWriter.writeUmlsToStore();
		// PubmedFileRlspWriter pubmedFileRlspWriter =
		// applicationContext.getBean(PubmedFileRlspWriter.class);
		// pubmedFileRlspWriter.setInputFile("/Users/bhsingh/Erik");
		// // pubmedFileRlspWriter.validatePredicates();
		// pubmedFileRlspWriter
		// .createMissingPredicates("/Users/bhsingh/code/git/transmog/wsd/umls-datasource-impl/src/main/resources/predicate_pubmed_all.csv");
		// pubmedFileRlspWriter.addPubmedRelationships();
	}

}
