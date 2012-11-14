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
		UmlsToStoreWriter umlsToStoreWriter = applicationContext.getBean(UmlsToStoreWriter.class);
		PubmedFileRlspWriter pubmedFileRlspWriter = applicationContext.getBean(PubmedFileRlspWriter.class);
//		umlsToStoreWriter.createPredicatesForConcepts();
//		pubmedFileRlspWriter
//				.createMissingPredicates("/home/bharat/code/git/transmog/wsd/umls-datasource-impl/src/main/resources/predicate_pubmed_all.csv");
		pubmedFileRlspWriter.addPubmedRelationships(new File("/home/bharat/Erik"));
		System.out.println("DONE ERIK!");
		pubmedFileRlspWriter.writeRlspsBetweenConceptsFromCsvFile("/home/bharat/result.csv");
		System.out.println("DONE MRREL!");
	}

}
