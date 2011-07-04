package org.biosemantics.disambiguation.umls.wsd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UmlsWsdImpl {

	private static final String[] CONTEXTS = new String[] { "nlm-wsd-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(UmlsWsdImpl.class);
	private static final String OUT_FOLDER = "/Users/bhsingh/Desktop/out";
	private static final Object SEPERATOR = " | ";
	private ClassPathXmlApplicationContext classPathXmlApplicationContext;
	private RelatedCuiReader relatedCuiReader;
	private AmbiguousWordReader ambiguousWordReader;
	NlmWsdRecordReader nlmWsdRecordReader;
	PeregrineManager peregrineManager;

	private void init() throws Exception {
		classPathXmlApplicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		classPathXmlApplicationContext.registerShutdownHook();
		logger.debug("application context loaded.");
		relatedCuiReader = new RelatedCuiReader(classPathXmlApplicationContext);
		ambiguousWordReader = new AmbiguousWordReader();
		nlmWsdRecordReader = new NlmWsdRecordReader();
		peregrineManager = new PeregrineManager();
		logger.debug("init completed.");
	}

	private void run() throws IOException {
		Collection<AmbiguousWord> ambiguousWords = ambiguousWordReader.getAmbiguousWords();
		logger.debug("read {} ambiguous words", ambiguousWords.size());
		// only for cold
		AmbiguousWord ambiguousWord = null;
		for (AmbiguousWord word : ambiguousWords) {
			if (word.getText().equalsIgnoreCase("cold")) {
				ambiguousWord = word;
				break;
			}
		}
		// ends
		// for (AmbiguousWord ambiguousWord : ambiguousWords) {

		logger.debug("starting for ambiguous word: {}", ambiguousWord.getText());
		OutputFile outputFile = new OutputFile(ambiguousWord.getText());
		List<RelatedConcept> relatedConcepts = new ArrayList<RelatedConcept>();
		for (String cui : ambiguousWord.getCuis()) {
			logger.debug("getting RelatedConcept for ambiguousCui {}", cui);
			relatedConcepts.add(relatedCuiReader.getRelatedConcept(cui));
		}
		logger.debug("all concept relationships retrieved");
		logger.debug("Starting to read all records for ambiguous word: {}", ambiguousWord);
		List<NlmWsdRecord> records = nlmWsdRecordReader.read(ambiguousWord.getText());
		logger.debug("{} records read for ambiguous word {}", new Object[] { records.size(), ambiguousWord });
		for (NlmWsdRecord nlmWsdRecord : records) {
			logger.info("record number------------------------------------- {}", nlmWsdRecord.getRecordNumber());
			Collection<String> peregrineCuis = peregrineManager.getConcepts(nlmWsdRecord.getTitleText() + " "
					+ nlmWsdRecord.getAbstractText());
			// remove all ambiguous concepts from fingerprint.
			logger.debug("{} non ambiguous cuis found in fingerprint", peregrineCuis.size());
			// remove all ambiguousWord cuis from peregrine output
			for (RelatedConcept relatedConcept : relatedConcepts) {
				peregrineCuis.remove(relatedConcept.getCui());
			}
			logger.debug("{} non ambiguous cuis in fingerprint after removing ambiguousWord.getCuis() ",
					peregrineCuis.size());
			logger.debug("calculating overlap");
			List<Integer> matches = calculateOverlap(relatedConcepts, peregrineCuis);
			OutputObjectImpl outputObjectImpl = new OutputObjectImpl(nlmWsdRecord.getRecordNumber(),
					nlmWsdRecord.getAnnotatedSense(), matches);
			outputFile.writeLine(outputObjectImpl);

			// }// records
			outputFile.close();
			logger.debug("completed for ambiguousWord: {}", ambiguousWord.getText());
		}// am words
	}

	public static void main(String[] args) throws Exception {
		UmlsWsdImpl umlsWsdImpl = new UmlsWsdImpl();
		umlsWsdImpl.init();
		umlsWsdImpl.run();
		umlsWsdImpl.destroy();
	}

	private void destroy() {
		classPathXmlApplicationContext.destroy();
		classPathXmlApplicationContext.close();
	}

	private static List<Integer> calculateOverlap(List<RelatedConcept> relatedConcepts, Collection<String> peregrineCuis) {
		List<Integer> matches = new ArrayList<Integer>();
		for (RelatedConcept relatedConcept : relatedConcepts) {
			int ctr = 0;
			for (String peregrineCui : peregrineCuis) {
				if (peregrineCui.equals(relatedConcept.getCui())) {
					logger.debug("peregrine cui same as relatedConcept.getCui() ={}. Hence no comparison done.",
							relatedConcept.getCui());
					continue;
				} else {
					if (relatedConcept.getCuiConceptRelationshipMap().containsKey(peregrineCui)) {
						logger.info("match found for peregrine cui = {}", peregrineCui);
						ctr++;
					} else {
						logger.debug("no match found for peregrine cui = {}", peregrineCui);
					}
				}
			}
			logger.info("cui:{}\tmatch:{}", new Object[] { relatedConcept.getCui(), ctr });
			matches.add(ctr);
		}
		logger.info("{}" + matches);
		return matches;
	}

}
