package org.biosemantics.wsd.datasource.umls;

import java.io.*;
import java.util.*;

import org.biosemantics.conceptstore.domain.*;
import org.biosemantics.conceptstore.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.task.*;
import org.springframework.data.neo4j.conversion.*;

public class MultithreadedFileImport {

	private File folder;

	public MultithreadedFileImport(TaskExecutor taskExecutor, FileImporter fileImporter) {
		this.taskExecutor = taskExecutor;
		this.fileImporter = fileImporter;
	}

	public void setFolder(File folder) {
		this.folder = folder;
		if (!this.folder.exists()) {
			throw new IllegalArgumentException("does not exist");
		}
		if (!this.folder.isDirectory()) {
			throw new IllegalArgumentException("not a folder");
		}
	}

	public void fire() {
		File[] files = folder.listFiles();
		final MultithreadedFileImport multithreadedFileImport = this;
		for (final File file : files) {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						fileImporter.parseFileAndImport(file, ENCODING, multithreadedFileImport);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void createCache() {
		EndResult<Notation> allNotations = notationRepository.findAllByQuery("code", "C*");
		int ctr = 0;
		long start = System.currentTimeMillis();
		for (Notation notation : allNotations) {
			Iterable<Concept> concepts = notation.getRelatedConcepts();
			for (Concept concept : concepts) {
				cuiNodeIdMap.put(notation.getCode(), concept.getNodeId());
				break;
			}
			if (++ctr % 1000 == 0) {
				System.out.println(ctr + " " + (System.currentTimeMillis() - start));
				start = System.currentTimeMillis();
			}
		}
		System.out.println("CUINODEMAPID size:" + cuiNodeIdMap.size() + " done->" + System.currentTimeMillis());
		EndResult<Concept> allPredicates = conceptRepository.findAllByQuery("type", ConceptType.PREDICATE.toString());
		ctr = 0;
		start = System.currentTimeMillis();
		for (Concept concept : allPredicates) {
			Iterable<Label> labels = concept.getLabels();
			for (Label label : labels) {
				predicateNodeIdMap.put(label.getText(), concept.getNodeId());
			}
			if (++ctr % 1000 == 0) {
				System.out.println(ctr + " " + (System.currentTimeMillis() - start));
				start = System.currentTimeMillis();
			}
		}
		System.out.println("PREDICATENODEIDMAP size:" + predicateNodeIdMap.size() + " done->"
				+ System.currentTimeMillis());

	}

	private Map<String, Long> predicateNodeIdMap = new HashMap<String, Long>();
	private Map<String, Long> cuiNodeIdMap = new HashMap<String, Long>();
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private FileImporter fileImporter;

	private static final String ENCODING = "UTF-8";

	public Long getNodeIdForCui(String cui) {
		return cuiNodeIdMap.get(cui);
	}

	public Long getNodeIdForPredicate(String predicateText) {
		return predicateNodeIdMap.get(predicateText);
	}
}
