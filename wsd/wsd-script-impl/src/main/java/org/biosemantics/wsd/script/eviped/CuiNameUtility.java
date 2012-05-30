package org.biosemantics.wsd.script.eviped;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CuiNameUtility {

	public String findPreferredTerm(String cui) {
		Concept concept = conceptRepository.getConceptById(cui);
		if (concept != null) {
			return conceptRepository.getPreferredLabel(concept, "ENG").getText();
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("script-run-context.xml");
		applicationContext.registerShutdownHook();
		CuiNameUtility cuiNameUtility = applicationContext.getBean(CuiNameUtility.class);
		CSVReader csvReader = new CSVReader(new FileReader(new File("/Users/bhsingh/Desktop/Workbook3.csv")));
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File("/Users/bhsingh/Desktop/Workbook4.csv")));
		List<String[]> lines = csvReader.readAll();
		for (String[] columns : lines) {
			String indications = columns[8];
			if (indications.trim().isEmpty()) {
				continue;
			} else {
				String[] indicationCuis = indications.split("\\|");
				if (indicationCuis != null && indicationCuis.length > 0) {
					for (String indicationCui : indicationCuis) {
						String preferredName = cuiNameUtility.findPreferredTerm(indicationCui.trim());
						if (preferredName != null) {
							csvWriter.writeNext(new String[] { columns[2], columns[4], columns[6],
									indicationCui.trim(), preferredName });
						} else {
							logger.info("did not find preferred name for {}", indicationCui.trim());
						}

					}
				}
			}
		}
		csvReader.close();
		csvWriter.flush();
		csvWriter.close();

	}

	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private ConceptRepository conceptRepository;
	private static int maxBreadth = 6;
	private static final Logger logger = LoggerFactory.getLogger(CuiNameUtility.class);

}
