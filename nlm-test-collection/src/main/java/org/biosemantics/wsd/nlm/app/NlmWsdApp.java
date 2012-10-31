package org.biosemantics.wsd.nlm.app;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biosemantics.wsd.nlm.AmbiguousWord;
import org.biosemantics.wsd.nlm.AmbiguousWordReader;
import org.biosemantics.wsd.nlm.NlmWsdRecord;
import org.biosemantics.wsd.nlm.NlmWsdRecordReader;
import org.biosemantics.wsd.similarity.PathScore;
import org.biosemantics.wsd.similarity.SimilarityImpl;
import org.biosemantics.wsd.ssi.Score;
import org.biosemantics.wsd.ssi.SsiImpl;
import org.biosemantics.wsd.ssi.SsiScore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class NlmWsdApp {

	private static final String INDEXED_INPUT = "/Users/bhsingh/code/data/paper1/metamap-annotations.txt";
	private static final String OUT_FOLDER = "/Users/bhsingh/code/data/paper1/6-1/";

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"nlm-test-collection-context.xml");
		applicationContext.registerShutdownHook();

		CSVReader csvReader = new CSVReader(new FileReader(new File(INDEXED_INPUT)));
		List<String[]> metamapAnnotationLines = csvReader.readAll();
		csvReader.close();
		AmbiguousWordReader ambiguousWordReader = applicationContext.getBean(AmbiguousWordReader.class);
		NlmWsdRecordReader nlmWsdRecordReader = applicationContext.getBean(NlmWsdRecordReader.class);
		SimilarityImpl ssiImpl = applicationContext.getBean(SimilarityImpl.class);
		Collection<AmbiguousWord> ambiguousWords = ambiguousWordReader.getAmbiguousWords();

		for (AmbiguousWord ambiguousWord : ambiguousWords) {

			String ambiguousText = ambiguousWord.getText();
			// CSVWriter countWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, ambiguousText +
			// "_count.txt")));
			CSVWriter detailWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, ambiguousText + "_detail.txt")));
			List<NlmWsdRecord> nlmRecords = nlmWsdRecordReader.readRecordsForAmbiguousWord(ambiguousText);
			for (NlmWsdRecord nlmWsdRecord : nlmRecords) {
				int pmid = nlmWsdRecord.getPmid();
				Set<String> cuis = new HashSet<String>();
				for (String[] columns : metamapAnnotationLines) {
					if (columns[0].equalsIgnoreCase(ambiguousText) && Integer.parseInt(columns[2]) == pmid) {
						String[] cuiStrings = Arrays.copyOfRange(columns, 4, columns.length);
						for (String cui : cuiStrings) {
							cuis.add(cui.trim());
						}
						break;
					}
				}
				List<String> ambiguousCuis = Arrays.asList(ambiguousWord.getCuis());
				for (String ambiguousCui : ambiguousCuis) {
					cuis.remove(ambiguousCui.trim());
				}
				Collection<PathScore> ssiScores = ssiImpl.pathSimilarity(cuis, ambiguousCuis);
				for (PathScore ssiScore : ssiScores) {
					detailWriter.writeNext(ssiScore.asStringArray());
				}
			}
			detailWriter.flush();
			detailWriter.close();

		}
	}
}
