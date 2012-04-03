package org.biosemantics.wsd.nlm.app;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.biosemantics.wsd.nlm.AmbiguousWord;
import org.biosemantics.wsd.nlm.AmbiguousWordReader;
import org.biosemantics.wsd.nlm.NlmWsdRecord;
import org.biosemantics.wsd.nlm.NlmWsdRecordReader;
import org.biosemantics.wsd.ssi.Score;
import org.biosemantics.wsd.ssi.SsiImpl;
import org.biosemantics.wsd.ssi.SsiScore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class NlmWsdApp {

	private static final String INDEXED_INPUT = "/ssd/bhsingh/data/metamap-annotations.txt";
	private static final String OUT_FOLDER = "/ssd/bhsingh/data/6-1/";

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"nlm-test-collection-context.xml");
		applicationContext.registerShutdownHook();

		CSVReader csvReader = new CSVReader(new FileReader(new File(INDEXED_INPUT)));
		List<String[]> metamapAnnotationLines = csvReader.readAll();
		csvReader.close();
		AmbiguousWordReader ambiguousWordReader = applicationContext.getBean(AmbiguousWordReader.class);
		NlmWsdRecordReader nlmWsdRecordReader = applicationContext.getBean(NlmWsdRecordReader.class);
		SsiImpl ssiImpl = applicationContext.getBean(SsiImpl.class);
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
				List<SsiScore> ssiScores = ssiImpl.getScore(cuis, ambiguousCuis);
				for (SsiScore ssiScore : ssiScores) {
					String unambigCui = ssiScore.getUnambiguousCui();
					for (Score score : ssiScore.getScores()) {
						detailWriter.writeNext(new String[] { "" + nlmWsdRecord.getRecordNumber(), unambigCui,
								score.getAmbiguousCui(), "" + score.getMinHierarchicalHops(),
								"" + score.getMinRelatedHops() });
					}
				}

			}
			detailWriter.flush();
			detailWriter.close();

		}

		List<String> cuis = new ArrayList<String>();
		cuis.add("C0012147");
		ssiImpl.getSsiScore(cuis, "C0024530");
	}
}
