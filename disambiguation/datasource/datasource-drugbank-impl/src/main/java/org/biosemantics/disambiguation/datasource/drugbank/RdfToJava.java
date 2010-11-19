package org.biosemantics.disambiguation.datasource.drugbank;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;

public class RdfToJava {

	private String schemaFile;
	private String packageName = "ca.drugbank.generated";
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

	public void generate(String[] args) throws Exception {
		CodeGenerator.generate(schemaFile, location, packageName, Reasoning.rdfs, true);
	}

}
