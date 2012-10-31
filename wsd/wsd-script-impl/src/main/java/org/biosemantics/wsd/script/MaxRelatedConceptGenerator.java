package org.biosemantics.wsd.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.biosemantics.wsd.repository.LabelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import au.com.bytecode.opencsv.CSVWriter;

public class MaxRelatedConceptGenerator {
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private LabelRepository labelRepository;

	private static final Logger logger = LoggerFactory.getLogger(MaxRelatedConceptGenerator.class);

	public void writeAll() throws IOException {
		Iterable<Concept> concepts = conceptRepository.findAllByQuery("id", "*");
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File("/Users/bhsingh/Desktop/relations.txt")));
		int ctr = 0;
		for (Concept concept : concepts) {
			Iterable<Concept> hierarchicalConcepts = conceptRepository.getHierarchicalConcepts(concept);
			int hCtr = 0;
			for (Concept hConcept : hierarchicalConcepts) {
				hCtr++;
			}
			Iterable<Concept> relatedConcepts = conceptRepository.getRelatedConcepts(concept);
			int rCtr = 0;
			for (Concept rConcept : relatedConcepts) {
				rCtr++;
			}
			if (rCtr > 100 || hCtr > 100) {

				csvWriter.writeNext(new String[] { concept.getId(),
						conceptRepository.getPreferredLabel(concept, "ENG").getText(), "" + rCtr, "" + hCtr });
			}
			logger.info("{}", ++ctr);
		}
		csvWriter.flush();
		csvWriter.close();
	}

}
