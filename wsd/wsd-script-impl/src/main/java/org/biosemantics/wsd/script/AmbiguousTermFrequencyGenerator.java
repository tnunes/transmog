package org.biosemantics.wsd.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Label;
import org.biosemantics.wsd.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import au.com.bytecode.opencsv.CSVWriter;

public class AmbiguousTermFrequencyGenerator {

	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private LabelRepository labelRepository;

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
		for (Label label : labels) {
			Iterable<Concept> concepts = labelRepository.getRelatedConcepts(label);
			int ctr = 0;
			
			for (Concept concept : concepts) {
				ctr++;
			}
			csvWriter.writeNext(new String[] { label.getText(), ""+ctr });

		}
	}

	public void destroy() throws IOException {
		csvWriter.flush();
		csvWriter.close();
	}

}
