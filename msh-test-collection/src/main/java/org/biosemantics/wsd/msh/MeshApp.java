package org.biosemantics.wsd.msh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biosemantics.utility.peregrine.PeregrineRmiClient;
import org.biosemantics.wsd.script.path.ShortestPathImpl;
import org.erasmusmc.data_mining.ontology.api.Language;
import org.erasmusmc.data_mining.peregrine.api.IndexingResult;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVWriter;

public class MeshApp {

	private static final String RESULT_FILE = "/Users/bhsingh/code/data/paper1/msh/MSHCorpus_2/term_pmid_cui";
	private static final String RECORD_FOLDER = "/Users/bhsingh/code/data/paper1/msh/MSHCorpus_3";
	private static final String SENSE_FILE = "/Users/bhsingh/code/data/paper1/msh/MSHCorpus_2/benchmark_mesh.txt";
	private static final String[] CONTEXTS = new String[] { "", "" };
	private static final String RUN_OUTPUT_FOLDER = null;

	public static void main(String[] args) throws IOException {
		MeshResultReaderImpl meshResultReaderImpl = new MeshResultReaderImpl();
		meshResultReaderImpl.setResultFile(RESULT_FILE);
		MeshRecordReaderImpl meshRecordReaderImpl = new MeshRecordReaderImpl();
		meshRecordReaderImpl.setRecordFolder(RECORD_FOLDER);
		meshRecordReaderImpl.setSenseFile(SENSE_FILE);

		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		applicationContext.registerShutdownHook();
		PeregrineRmiClient peregrineRmiClient = (PeregrineRmiClient) applicationContext.getBean("peregrineRmiClient");
		ShortestPathImpl shortestPathImpl = (ShortestPathImpl) applicationContext.getBean("shortestPathImpl");

		List<MeshResult> meshResults = meshResultReaderImpl.readAll();
		for (MeshResult meshResult : meshResults) {
			CSVWriter detailWriter = new CSVWriter(new FileWriter(new File(RUN_OUTPUT_FOLDER, meshResult.getTerm()
					+ "_" + meshResult.getPmid())));
			MeshRecord meshRecord = meshRecordReaderImpl.getMeshRecord(meshResult.getTerm(), meshResult.getPmid());
			List<IndexingResult> indexingResults = peregrineRmiClient.getPeregrine().index(meshRecord.getText(),
					Language.EN);
			Set<String> conceptIds = new HashSet<String>();
			for (IndexingResult indexingResult : indexingResults) {
				String conceptId = (String) indexingResult.getTermId().getConceptId();
				conceptIds.add(conceptId);
			}
			String[] wordSenses = meshRecordReaderImpl.getSenses(meshResult.getTerm());
			for (String conceptId : conceptIds) {

				for (String wordSense : wordSenses) {
					if (!wordSense.equalsIgnoreCase(conceptId)) {
						try {
							int hierarchicalPathLength = shortestPathImpl.findShortestHierarchicalPath(wordSense,
									conceptId).length();
							int relatedPathLength = shortestPathImpl.findShortestRelatedPath(wordSense, conceptId)
									.length();
							detailWriter.writeNext(new String[] { conceptId, wordSense, "" + hierarchicalPathLength,
									"" + relatedPathLength });
						} catch (Exception e) {

						}
					}

				}
			}
			detailWriter.flush();
			detailWriter.close();
		}
	}
}
