package org.biosemantics.wsd.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.bytecode.opencsv.CSVWriter;

public class ConnectedConceptFrequencyGenerator {

	@Autowired
	private ConceptRepository conceptRepository;
	private static final Logger logger = LoggerFactory.getLogger(AmbiguousTermFrequencyGenerator.class);

	private String outputFile;
	private CSVWriter csvWriter;

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void init() throws IOException {
		csvWriter = new CSVWriter(new FileWriter(new File(outputFile)));
	}

	public void writeAll() {
		Iterable<Concept> concepts = conceptRepository.findAllByQuery("id", "*");
		int ctr = 0;
		for (Concept concept : concepts) {
			ctr++;
			Set<Concept> unique = new HashSet<Concept>();
			Iterable<Concept> relatedConcepts = conceptRepository.getRelatedConcepts(concept);
			for (Concept relatedConcept : relatedConcepts) {
				unique.add(relatedConcept);
			}
			if (unique.size() > 1) {
				csvWriter.writeNext(new String[] { concept.getId(), "" + unique.size() });
			}
			if (ctr % 100000 == 0) {
				logger.info("{} concepts read", ctr);
			}
			
		}
		logger.info("total labels = {}", ctr);

	}
	
	public void destroy() throws IOException {
		csvWriter.flush();
		csvWriter.close();
	}

}
