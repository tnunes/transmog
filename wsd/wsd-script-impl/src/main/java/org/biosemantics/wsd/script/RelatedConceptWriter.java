package org.biosemantics.wsd.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.bytecode.opencsv.CSVWriter;

public class RelatedConceptWriter {
	public static final String id = "C1274013";
	@Autowired
	private ConceptRepository conceptRepository;

	private String outputFile;

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	private void getAllRelatedConcepts() throws IOException {
		Concept concept = conceptRepository.getConceptById(id);
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(outputFile)));
		Iterable<Concept> rldConcepts = conceptRepository.getRelatedConcepts(concept);
		for (Concept rldConcept : rldConcepts) {
			String txt = conceptRepository.getPreferredLabel(rldConcept, "ENG").getText();
			csvWriter.writeNext(new String[] { rldConcept.getId(), txt, "RELATED" });
		}
		Iterable<Concept> hierConcepts = conceptRepository.getHierarchicalConcepts(concept);
		for (Concept hierConcept : hierConcepts) {
			String txt = conceptRepository.getPreferredLabel(hierConcept, "ENG").getText();
			csvWriter.writeNext(new String[] { hierConcept.getId(), txt, "HIERARCHICAL" });
		}
		csvWriter.flush();
		csvWriter.close();

	}
	
	public void writeAll() throws IOException {
		getAllRelatedConcepts();
	}

}
