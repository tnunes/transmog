package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.*;

public class UmlsDbToGraphWriter {

	private static ClassPathXmlApplicationContext applicationContext;

	public static void main(String[] args) throws SQLException, IOException {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "umls-import-context.xml" });
		applicationContext.registerShutdownHook();
		PubmedFileImporter pubmedFileImporter = applicationContext.getBean(PubmedFileImporter.class);
		TaskExecutor taskExecutor = applicationContext.getBean(TaskExecutor.class);

		MultithreadedFileImport multithreadedFileImport = new MultithreadedFileImport(taskExecutor, pubmedFileImporter);
		multithreadedFileImport.setFolder(new File("/Users/bhsingh/code/pubmed"));
		multithreadedFileImport.createCache();
		multithreadedFileImport.fire();

	}

}
