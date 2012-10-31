package org.biosemantics.wsd.nlm.app;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.wsd.metamap.MetamapClient;
import org.biosemantics.wsd.metamap.MetamapIndexingResult;
import org.biosemantics.wsd.nlm.AmbiguousWord;
import org.biosemantics.wsd.nlm.AmbiguousWordReader;
import org.biosemantics.wsd.nlm.NlmWsdRecord;
import org.biosemantics.wsd.nlm.NlmWsdRecordReader;
import org.biosemantics.wsd.ssi.SsiImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVWriter;

public class MetamapIndexerApp {

	private static final String OUTPUT_FILE = "/ssd/bhsingh/data/metamap-annotations.txt";

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"nlm-test-collection-context.xml");
		applicationContext.registerShutdownHook();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUTPUT_FILE)));
		try {
			MetamapClient metamapClient = applicationContext.getBean(MetamapClient.class);
			AmbiguousWordReader ambiguousWordReader = applicationContext.getBean(AmbiguousWordReader.class);
			NlmWsdRecordReader nlmWsdRecordReader = applicationContext.getBean(NlmWsdRecordReader.class);
			Collection<AmbiguousWord> ambiguousWords = ambiguousWordReader.getAmbiguousWords();
			for (AmbiguousWord ambiguousWord : ambiguousWords) {
				List<NlmWsdRecord> nlmWsdRecords = nlmWsdRecordReader.readRecordsForAmbiguousWord(ambiguousWord
						.getText());
				for (NlmWsdRecord nlmWsdRecord : nlmWsdRecords) {
					StringBuilder finalText = new StringBuilder(nlmWsdRecord.getTitleText()).append(" ").append(
							nlmWsdRecord.getAbstractText());
					MetamapIndexingResult metamapIndexingResult = metamapClient.getCuis(finalText.toString());
					List<String> output = new ArrayList<String>();
					output.add(ambiguousWord.getText());
					output.add("" + nlmWsdRecord.getRecordNumber());
					output.add("" + nlmWsdRecord.getPmid());
					output.addAll(metamapIndexingResult.getCuis());
					csvWriter.writeNext(output.toArray(new String[output.size()]));
				}
			}
		} finally {
			csvWriter.flush();
			csvWriter.close();
		}
	}
}
