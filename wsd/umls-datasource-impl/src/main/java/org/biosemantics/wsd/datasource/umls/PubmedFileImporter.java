package org.biosemantics.wsd.datasource.umls;

import java.io.*;

import org.apache.commons.io.*;
import org.biosemantics.conceptstore.repository.*;
import org.neo4j.graphdb.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.neo4j.support.*;

public class PubmedFileImporter implements FileImporter {

	@Override
	public void parseFileAndImport(File file, String encoding, MultithreadedFileImport multithreadedFileImport)
			throws IOException {
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
						Long srcNodeId = multithreadedFileImport.getNodeIdForCui(srcCui);
						Long tgtNodeId = multithreadedFileImport.getNodeIdForCui(tgtCui);
						Long predNodeId = multithreadedFileImport.getNodeIdForPredicate(predicateText);
						if (srcNodeId != null && tgtNodeId != null && predNodeId != null) {
							(conceptRepository.findOne(srcNodeId)).addRelationshipIfNoBidirectionalRlspExists(template,
									(conceptRepository.findOne(tgtNodeId)), String.valueOf(predNodeId), 0, "PMID:"
											+ pmid);
						} else {
							System.err.println(srcCui + ":" + srcNodeId + " " + tgtCui + ":" + tgtNodeId + " "
									+ predicateText + ":" + predNodeId);
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

	private static final Logger logger = LoggerFactory.getLogger(PubmedFileImporter.class);
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

}
