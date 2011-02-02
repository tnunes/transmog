package org.biosemantics.disambiguation.datasource.dailymed;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;

public class RdfToJava {

	private String schemaFile;
	private String packageName = "gov.nih.nlm.dailymed";
	private String location = "src/main/java";

	public void setSchemaFile(String schemaFile) {
		this.schemaFile = schemaFile;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void generate() throws Exception {
		CodeGenerator.generate(schemaFile, location, packageName, Reasoning.rdfs, true);
	}
	
	public static void main(String[] args) throws Exception{
		RdfToJava rdfToJava = new RdfToJava();
		rdfToJava.setSchemaFile("/Users/bhsingh/Downloads/LODD/dailymed_dump.nt");
		rdfToJava.generate();
	}

}
