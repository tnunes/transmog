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
		conceptWriter.writeRelaPredicates();
		conceptWriter
				.writeMissingPubmedPredicates("/Users/bhsingh/code/git/transmog/wsd/umls-datasource-impl/src/main/resources/predicate_pubmed_all.csv");
		conceptWriter.mapRelaPredicatesToSemanticTypePredicates();
		conceptWriter.writeConcepts();
		conceptWriter.writeRlspsBetweenConceptsAndSchemes();
		conceptWriter.writeNotNullRelaRlsps();
		conceptWriter.writePubmedRlsps(new File("/Users/bhsingh/code/data/Erik"), "UTF-8");
	}

}
