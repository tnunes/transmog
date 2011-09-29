package org.biosemantics.disambiguation.umls.wsd;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVWriter;

public class UmlsWsdApplication {

	private static ClassPathXmlApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(UmlsWsdApplication.class);
	private static final String[] CONTEXTS = new String[] { "nlm-wsd-context.xml" };

	public static void init(String outputFile) throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		applicationContext.registerShutdownHook();
		logger.debug("application context loaded.");
		AmbiguousWordReader ambiguousWordReader = applicationContext.getBean(AmbiguousWordReader.class);
		NlmWsdRecordReader nlmWsdRecordReader = applicationContext.getBean(NlmWsdRecordReader.class);
		IndexService indexService = applicationContext.getBean(IndexService.class);
		SenseScoreService senseScoreService = applicationContext.getBean(SenseScoreService.class);
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(outputFile)));
		Collection<AmbiguousWord> ambiguousWords = ambiguousWordReader.getAmbiguousWords();
		for (AmbiguousWord ambiguousWord : ambiguousWords) {
			logger.info("ambiguousWord: {}", ambiguousWord.getText());
			List<NlmWsdRecord> nlmWsdRecords = nlmWsdRecordReader.read(ambiguousWord.getText());
			for (NlmWsdRecord nlmWsdRecord : nlmWsdRecords) {
				logger.info("nlm record pmid: {}", nlmWsdRecord.getPmid());
				List<String> outputLineColumns = new ArrayList<String>();
				outputLineColumns.add(ambiguousWord.getText());
				outputLineColumns.add("" + nlmWsdRecord.getRecordNumber());
				outputLineColumns.add(nlmWsdRecord.getAnnotatedSense());
				String text = nlmWsdRecord.getTitleText() + ". " + nlmWsdRecord.getAbstractText();
				Collection<String> contextCuis = indexService.index(text);
				for (String ambiguousCui : ambiguousWord.getCuis()) {
					double senseScore = 0;
					for (String contextCui : contextCuis) {
						if (!ambiguousCui.equalsIgnoreCase(contextCui)) {
							double score = senseScoreService.getSenseScore(ambiguousCui, contextCui);
							logger.info("score {} ambiguousCui {} contextCui {} ", new Object[] { score, ambiguousCui,
									contextCui });
							senseScore += score;
						}
					}
					outputLineColumns.add(ambiguousCui);
					outputLineColumns.add(String.valueOf(senseScore));
				}
				csvWriter.writeNext(outputLineColumns.toArray(new String[outputLineColumns.size()]));
				csvWriter.flush();
			}
		}
		csvWriter.close();
	}

	public static void destroy() {
		applicationContext.close();
	}

	public static void main(String[] args) throws Exception {
		init("");
		destroy();
	}

}
