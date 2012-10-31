package org.biosemantics.wsd.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Label;
import org.biosemantics.wsd.repository.LabelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import au.com.bytecode.opencsv.CSVWriter;

public class AmbiguousTermFrequencyGenerator {

	@Autowired
	private LabelRepository labelRepository;
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
		Iterable<Label> labels = labelRepository.findAllByQuery("text", "*");
		int ctr = 0;
		for (Label label : labels) {
			Iterable<Concept> concepts = labelRepository.getRelatedConcepts(label);
			ctr++;
			Set<Concept> unique = new HashSet<Concept>();
			for (Concept concept : concepts) {
				unique.add(concept);
			}
			if (unique.size() > 1) {
				csvWriter.writeNext(new String[] { label.getText(), "" + unique.size() });
			}
			if (ctr % 100000 == 0) {
				logger.info("{} labels read", ctr);
			}
		}
		logger.info("total labels = {}", ctr);
	}

	public void destroy() throws IOException {
		csvWriter.flush();
		csvWriter.close();
	}

}
