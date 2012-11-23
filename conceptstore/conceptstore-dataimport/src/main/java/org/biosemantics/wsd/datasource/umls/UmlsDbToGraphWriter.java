package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.neo4j.helpers.collection.MapUtil;

public class UmlsDbToGraphWriter {

	public static void main(String[] args) throws SQLException, IOException {
		ConfigReader configReader  = new ConfigReader();
		configReader.init();
		Properties properties = configReader.getProperties();
		
		UmlsImporter conceptWriter = new UmlsImporter();
		
		
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

	private static final Map<String, String> getGraphConfig() {
		return MapUtil.genericMap(new Object[] { "neostore.propertystore.db.index.keys.mapped_memory", "5M",
				"neostore.propertystore.db.index.mapped_memory", "5M", "neostore.nodestore.db.mapped_memory", "200M",
				"neostore.relationshipstore.db.mapped_memory", "1000M", "neostore.propertystore.db.mapped_memory",
				"1000M", "neostore.propertystore.db.strings.mapped_memory", "200M" });
	}

}
