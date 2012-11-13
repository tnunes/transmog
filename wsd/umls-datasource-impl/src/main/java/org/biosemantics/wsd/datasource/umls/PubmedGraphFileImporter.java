package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.ConceptType;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.biosemantics.conceptstore.domain.NotationSourceConstant;
import org.biosemantics.conceptstore.repository.LabelRepository;
import org.biosemantics.conceptstore.repository.NotationRepository;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class PubmedGraphFileImporter implements GraphFileImporter {

	@Override
	public void parseFileAndImport(File file, String encoding) throws IOException {
		LineIterator iterator = FileUtils.lineIterator(file, encoding);
		System.out.println(System.currentTimeMillis() + " iterating file");
		int ctr = 0;
		try {
			while (iterator.hasNext()) {
				Transaction tx = template.getGraphDatabaseService().beginTx();
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
						} else {
							System.err.println("src:" + srcCui + ":" + srcConcept + " pred:" + predicateText + ":"
									+ predConceptNodeId + " tgt:" + tgtCui + ":" + tgtConcept);
						}
					}
					if (++ctr % 1000 == 0) {
						System.out.println("file:" + file.getName() + " ctr:" + ctr + " millis:"
								+ System.currentTimeMillis());
					}
				} else {
					System.err.println("no 4 columns for line =" + line);
				}
				tx.success();
				tx.finish();
			}
			// 78 seconds with no graph activity
			System.out.println(System.currentTimeMillis() + " THREAD COMPLETED FOR FILE: " + file.getName());
		} finally {
			LineIterator.closeQuietly(iterator);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(PubmedGraphFileImporter.class);
	@Autowired
	private IgnoredCuiReader ignoredCuiReader;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private LabelRepository labelRepository;
	@Autowired
	private Neo4jTemplate template;
	private Map<String, Long> predicateNodeIdMap = new HashMap<String, Long>();

}
