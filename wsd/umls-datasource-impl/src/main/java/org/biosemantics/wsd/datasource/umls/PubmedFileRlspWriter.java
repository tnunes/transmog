package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.LabelType;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.NotationSourceConstant;
import org.biosemantics.conceptstore.domain.RlspType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

@Component
public class PubmedFileRlspWriter {

	private File inputFile;

	public void setInputFile(String inputFile) {
		this.inputFile = new File(inputFile);
		if (!this.inputFile.exists()) {
			throw new IllegalStateException("input file does not exist");
		}
	}

	public void addPubmedRelationships() throws IOException {
		LineIterator iterator = FileUtils.lineIterator(inputFile, "UTF-8");
		System.out.println(System.currentTimeMillis() + " iterating file");
		Transaction tx = template.getGraphDatabaseService().beginTx();
		int ctr = 0;
		try {
			while (iterator.hasNext()) {
				String line = iterator.nextLine();
				String[] columns = line.split(" ");
				if (columns.length == 4) {
					String srcCui = columns[0].trim();
					String predicateText = columns[1].trim();
					String tgtCui = columns[2].trim();
					String pmid = columns[3].trim();
					if (!ignoredCuiReader.isIgnored(srcCui) && !ignoredCuiReader.isIgnored(tgtCui)) {
						Concept srcConcept = null;
						Concept tgtConcept = null;
						Long predConceptNodeId = null;
						Notation srcNotation = notationRepository.getNotation(NotationSourceConstant.UMLS.toString(),
								srcCui);
						if (srcNotation != null) {
							for (Concept concept : srcNotation.getRelatedConcepts()) {
								srcConcept = concept;
							}
						}

						Notation tgtNotation = notationRepository.getNotation(NotationSourceConstant.UMLS.toString(),
								tgtCui);
						if (tgtNotation != null) {
							for (Concept concept : tgtNotation.getRelatedConcepts()) {
								tgtConcept = concept;
							}
						}
						if (predicateNodeIdMap.containsKey(predicateText)) {
							predConceptNodeId = predicateNodeIdMap.get(predicateText);
						} else {
							Label predLabel = labelRepository.getLabel(predicateText, "ENG");
							if (predLabel != null) {
								for (Concept concept : predLabel.getRelatedConcepts()) {
									if (concept.getType() == ConceptType.PREDICATE) {
										predConceptNodeId = concept.getNodeId();
										predicateNodeIdMap.put(predicateText, predConceptNodeId);
									}
								}
							}
						}
						if (srcConcept != null && tgtConcept != null && predConceptNodeId != null) {
							srcConcept.addRelationshipIfNoneExists(template, tgtConcept,
									String.valueOf(predConceptNodeId), 0, "PMID:" + pmid);
							if (++ctr % txSize == 0) {
								tx.success();
								tx.finish();
								tx = template.getGraphDatabaseService().beginTx();
								System.out.println("millis:" + System.currentTimeMillis() + " ctr:" + ctr);
							}
						} else {
							System.err.println("src:" + srcCui + ":" + srcConcept + " pred:" + predicateText + ":"
									+ predConceptNodeId + " tgt:" + tgtCui + ":" + tgtConcept);
						}
					}
				} else {
					System.err.println("no 4 columns for line =" + line);
				}
			}
			// 78 seconds with no graph activity
			tx.success();
			tx.finish();
			System.out.println(System.currentTimeMillis() + " done iterating file");
		} finally {
			LineIterator.closeQuietly(iterator);
		}
	}

	public void validatePredicates() throws IOException {
		LineIterator iterator = FileUtils.lineIterator(inputFile, "UTF-8");
		Set<String> predicates = new HashSet<String>();
		System.out.println(System.currentTimeMillis() + " iterating file");
		while (iterator.hasNext()) {
			String line = iterator.nextLine();
			String[] columns = line.split(" ");
			if (columns.length == 4) {
				predicates.add(columns[1].trim());
			}
		}
		System.out.println(System.currentTimeMillis() + " done iterating file");
		System.out.println("unique predicates = " + predicates.size());
		CSVWriter csvWriter = new CSVWriter(new FileWriter("/Users/bhsingh/Desktop/predicate.csv"));
		int ctr = 0;
		for (String strPedicate : predicates) {
			Iterable<Label> labels = labelRepository.findAllByPropertyValue("text", strPedicate);
			boolean found = false;
			for (Label label : labels) {
				if (label.getText().equalsIgnoreCase(strPedicate)) {
					found = true;
					break;
				}
			}
			csvWriter.writeNext(new String[] { strPedicate, String.valueOf(found) });
			System.out.println(++ctr);
		}
		csvWriter.flush();
		csvWriter.close();
	}

	public void createMissingPredicates(String csvFile) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(csvFile));
		List<String[]> lines = csvReader.readAll();
		Transaction tx = template.getGraphDatabaseService().beginTx();
		for (String[] columns : lines) {
			if (columns[1].trim().equalsIgnoreCase("FALSE")) {
				String predText = columns[0].trim();
				Concept predicateConcept = null;
				Label label = labelRepository.save(new Label(predText, "ENG"));
				predicateConcept = conceptRepository.save(new Concept(ConceptType.PREDICATE));
				predicateConcept.addLabelIfNoneExists(template, label, LabelType.PREFERRED, "PUBMED_RLSP_FILE");
				if (predText.startsWith("neg_")) {
					predText = predText.substring(4);
					Label foundLabel = labelRepository.findByPropertyValue(predText, "ENG");
					if (foundLabel != null) {
						// create rlsp
						Iterable<Concept> concepts = foundLabel.getRelatedConcepts();
						for (Concept concept : concepts) {
							if (concept.getType() == ConceptType.PREDICATE) {
								concept.addRelationshipIfNoneExists(template, predicateConcept,
										RlspType.IS_INVERSE_OF.toString(), 0, "PUBMED_RLSP_FILE");
							}
						}
					}
				}
			}
		}
		tx.success();
		tx.finish();
		System.out.println("done creating predicates");
	}

	private static final Logger logger = LoggerFactory.getLogger(PubmedFileRlspWriter.class);

	@Autowired
	private IgnoredCuiReader ignoredCuiReader;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private LabelRepository labelRepository;
	@Autowired
	private Neo4jTemplate template;
	@Autowired
	private ConceptRepository conceptRepository;
	private Map<String, Long> predicateNodeIdMap = new HashMap<String, Long>();

	private static final int txSize = 1000;

}
