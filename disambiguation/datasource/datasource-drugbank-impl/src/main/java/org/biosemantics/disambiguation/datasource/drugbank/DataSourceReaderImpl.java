package org.biosemantics.disambiguation.datasource.drugbank;

import java.io.File;
import static com.google.common.base.Preconditions.*;
import java.io.FileReader;
import java.io.IOException;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.springframework.beans.factory.annotation.Required;

import ca.drugbank.generated.Druginteractions;
import ca.drugbank.generated.Drugs;
import ca.drugbank.generated.Enzymes;
import ca.drugbank.generated.Offer;
import ca.drugbank.generated.References;
import ca.drugbank.generated.Targets;

public class DataSourceReaderImpl implements DataSourceReader {

	private static final String DRUGBANK_SCHEMA__TEST_FILE = "/Users/bhsingh/Code/workspace/transmog/disambiguation/datasource/datasource-drugbank-impl/src/test/resources/drugbank_testset.nt";

	private File sourceLocation;
	private Model model;

	@Override
	@Required
	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = new File(checkNotNull(sourceLocation));
		if (!this.sourceLocation.exists() && !this.sourceLocation.isFile()) {
			throw new IllegalArgumentException("Make sure the file exits and is readable at \"" + sourceLocation + "\"");
		}
	}

	public void init() throws ModelRuntimeException, IOException {
		model = RDF2Go.getModelFactory().createModel();
		model.open();
		FileReader reader = new FileReader(sourceLocation);
		model.readFrom(reader, Syntax.Ntriples);
	}

	public Drugs[] getAllDrugs() {
		Drugs[] drugs = Drugs.getAllInstances_as(model).asArray();
		return drugs;
	}

	public Enzymes[] getAllEnzymns() {
		Enzymes[] enzymes = Enzymes.getAllInstances_as(model).asArray();
		return enzymes;
	}

	public Druginteractions[] getAllDrugInteractions() {
		Druginteractions[] druginteractions = Druginteractions.getAllInstances_as(model).asArray();
		return druginteractions;
	}

	public Offer[] getAllOffers() {
		Offer[] offers = Offer.getAllInstances_as(model).asArray();
		return offers;
	}

	public References[] getAllReferences() {
		References[] references = References.getAllInstances_as(model).asArray();
		return references;
	}

	public Targets[] getAllTargets() {
		Targets[] targets = Targets.getAllInstances_as(model).asArray();
		return targets;
	}

	public static void main(String[] args) throws Exception {
		// create the RDF2GO Model - By default creates a In-Memory model, make sure you have enough memory
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		// if the File already exists, the existing triples are read and added to the model
		File rdfStoreFile = new File(DRUGBANK_SCHEMA__TEST_FILE);
		if (rdfStoreFile.exists()) {
			try {
				FileReader reader = new FileReader(rdfStoreFile);
				model.readFrom(reader, Syntax.Ntriples);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// File will be created on save only
		}
		// model.dump();

		// get references for all objects of a certain class, by calling a static method upon this class
		Drugs[] drugsArray = Drugs.getAllInstances_as(model).asArray();
		// print all instances
		// while (resourceIterator.hasNext()) {
		// Resource resource = resourceIterator.next();
		// System.err.println(resource.getClass().getName());
		// Object object = model.getProperty(resource.asURI());
		// System.err.println(object);
		// }
		for (int i = 0; i < drugsArray.length; i++) {
			Drugs drugs = drugsArray[i];
			System.err.println(drugs.toString());
			ClosableIterator<Node> nodeIterator = drugs.getAllPrimaryAccessionNo_asNode();
			while (nodeIterator.hasNext()) {
				System.err.println(nodeIterator.next().toString());
			}
		}

	}

}
