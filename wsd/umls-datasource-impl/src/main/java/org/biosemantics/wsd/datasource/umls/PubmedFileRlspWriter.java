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
import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.LabelType;
import org.biosemantics.conceptstore.domain.Notation;
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

public class PubmedFileRlspWriter {

	private File inputFile;

	public void writeRlspsBetweenConceptsFromCsvFile(String file) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(file));
		String[] nextLine;
		Transaction tx = template.getGraphDatabaseService().beginTx();
		int ctr = 0;
		int totalLines = 0;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine.length == 4) {
				if (++totalLines % txSize == 0) {
					System.out.println("millis:" + System.currentTimeMillis() + " lines:" + totalLines);
				}
				String rela = nextLine[3].trim();
				String rel = nextLine[2].trim();
				boolean useRel = false;
				if (StringUtils.isBlank(rela) || rela.equalsIgnoreCase("N")) {
					useRel = true;
				}
				// rlsps in mrrel are CUI2 to CUI1
				String sourceCui = nextLine[1].trim();
				String targetCui = nextLine[0].trim();
				if (ignoredCuiReader.isIgnored(sourceCui) || ignoredCuiReader.isIgnored(targetCui)) {
					continue;
				}
				Long predNodeId = null;
				if (useRel) {
					if (predicateNodeIdMap.containsKey(rel)) {
						predNodeId = predicateNodeIdMap.get(rel);
					} else {
						Notation predNotation = notationRepository.findByPropertyValue("code", rel);
						if (predNotation != null) {
							for (Concept concept : predNotation.getRelatedConcepts()) {
								if (concept.getType() == ConceptType.PREDICATE) {
									predNodeId = concept.getNodeId();
									predicateNodeIdMap.put(rel, predNodeId);
									break;
								}
							}
						}
					}
				} else {
					if (predicateNodeIdMap.containsKey(rela)) {
						predNodeId = predicateNodeIdMap.get(rela);
					} else {
						Label predLabel = labelRepository.findByPropertyValue("text", rela);
						if (predLabel != null) {
							for (Concept concept : predLabel.getRelatedConcepts()) {
								if (concept.getType() == ConceptType.PREDICATE) {
									predNodeId = concept.getNodeId();
									predicateNodeIdMap.put(rela, predNodeId);
									break;
								}
							}
						}
					}
				}
				Long sourceNodeId = null;
				if (cuiNodeIdMap.containsKey(sourceCui)) {
					sourceNodeId = cuiNodeIdMap.get(sourceCui);
				} else {
					Notation srcNotation = notationRepository.findByPropertyValue("code", sourceCui);
					if (srcNotation != null) {
						for (Concept concept : srcNotation.getRelatedConcepts()) {
							if (concept.getType() == ConceptType.CONCEPT) {
								sourceNodeId = concept.getNodeId();
								cuiNodeIdMap.put(sourceCui, sourceNodeId);
								break;
							}
						}
					}
				}
				Long targetNodeId = null;
				if (cuiNodeIdMap.containsKey(targetCui)) {
					targetNodeId = cuiNodeIdMap.get(targetCui);
				} else {
					Notation targetNotation = notationRepository.findByPropertyValue("code", targetCui);
					if (targetNotation != null) {
						for (Concept concept : targetNotation.getRelatedConcepts()) {
							if (concept.getType() == ConceptType.CONCEPT) {
								targetNodeId = concept.getNodeId();
								cuiNodeIdMap.put(targetCui, targetNodeId);
							}
						}
					}
				}
				if (sourceNodeId != null && targetNodeId != null && predNodeId != null) {
					Concept srcConcept = conceptRepository.findOne(sourceNodeId);
					Concept targetConcept = conceptRepository.findOne(targetNodeId);
					srcConcept.addRelationshipIfNoBidirectionalRlspExists(template, targetConcept,
							predNodeId.toString(), 0, MRREL);
				} else {
//					System.err.println("sourceCui:" + sourceCui + " targetCui:" + targetCui + " rel" + rel + " rela:"
//							+ rela);
				}
				if (++ctr % txSize == 0) {
					tx.success();
					tx.finish();
					tx = template.getGraphDatabaseService().beginTx();
					System.out.println("millis:" + System.currentTimeMillis() + " ctr:" + ctr);
				}
			}
		}
		tx.success();
		tx.finish();
	}

	public void addPubmedRelationships(File inputFile) throws IOException {
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
						Long srcConceptNodeId = null;
						Long tgtConceptNodeId = null;
						Long predConceptNodeId = null;
						if (cuiNodeIdMap.containsKey(srcCui)) {
							srcConceptNodeId = cuiNodeIdMap.get(srcCui);
						} else {
							Notation srcNotation = notationRepository.findByPropertyValue("code", srcCui);
							if (srcNotation != null) {
								for (Concept concept : srcNotation.getRelatedConcepts()) {
									srcConceptNodeId = concept.getNodeId();
									cuiNodeIdMap.put(srcCui, srcConceptNodeId);
								}
							}
						}
						if (cuiNodeIdMap.containsKey(tgtCui)) {
							tgtConceptNodeId = cuiNodeIdMap.get(tgtCui);
						} else {
							Notation tgtNotation = notationRepository.findByPropertyValue("code", tgtCui);
							if (tgtNotation != null) {
								for (Concept concept : tgtNotation.getRelatedConcepts()) {
									tgtConceptNodeId = concept.getNodeId();
									cuiNodeIdMap.put(tgtCui, tgtConceptNodeId);
								}
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
						if (srcConceptNodeId != null && tgtConceptNodeId != null && predConceptNodeId != null) {
							(conceptRepository.findOne(srcConceptNodeId)).addRelationshipIfNoneExists(template,
									(conceptRepository.findOne(tgtConceptNodeId)), String.valueOf(predConceptNodeId),
									0, "PMID:" + pmid);
							if (++ctr % txSize == 0) {
								tx.success();
								tx.finish();
								tx = template.getGraphDatabaseService().beginTx();
								System.out.println("millis:" + System.currentTimeMillis() + " ctr:" + ctr);
							}
						} else {
//							System.err.println("src:" + srcCui + ":" + srcConceptNodeId + " pred:" + predicateText
//									+ ":" + predConceptNodeId + " tgt:" + tgtCui + ":" + tgtConceptNodeId);
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
	private Map<String, Long> cuiNodeIdMap = new HashMap<String, Long>();

	private static final int txSize = 1000;
	private static final String UMLS_VERSION = "UMLS2012AA";
	private static final String MRREL = UMLS_VERSION + "|MRREL";

}
