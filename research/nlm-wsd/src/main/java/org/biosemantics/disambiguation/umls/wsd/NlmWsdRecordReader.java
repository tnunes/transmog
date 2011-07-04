package org.biosemantics.disambiguation.umls.wsd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class NlmWsdRecordReader {

	private static final String REVIEWED_RESULTS_FOLDER_PATH = "/Users/bhsingh/Code/workspace/transmog/umls-wsd/src/main/resources/Basic_Reviewed_Results";

	public List<NlmWsdRecord> read(String ambiguousWordText) throws IOException {
		File file = new File(REVIEWED_RESULTS_FOLDER_PATH + File.separatorChar + ambiguousWordText + File.separatorChar
				+ ambiguousWordText + "_set");
		List<String> lines = FileUtils.readLines(file);
		List<NlmWsdRecord> nlmWsdRecords = new ArrayList<NlmWsdRecord>();
		List<String> record = new ArrayList<String>(8);
		for (String line : lines) {
			if (line.trim().isEmpty()) {
				NlmWsdRecord nlmWsdRecord = createRecord(record);
				nlmWsdRecords.add(nlmWsdRecord);
				record.clear();
			} else {
				record.add(line);
			}
		}
		return nlmWsdRecords;

	}

	private NlmWsdRecord createRecord(List<String> record) {
		String strPmid = null;
		String titleText = null;
		String abstractText = null;
		for (String line : record) {
			if (line.startsWith("PMID-")) {
				strPmid = line.replace("PMID-", "").trim();
			} else if (line.startsWith("TI  - ")) {
				titleText = line.replace("TI  - ", "").trim();
			} else if (line.startsWith("AB  - ")) {
				abstractText = line.replace("AB  - ", "").trim();
			}
		}
		NlmWsdRecord nlmWsdRecord = new NlmWsdRecord(record.get(0), record.get(1), record.get(2),
				Integer.parseInt(strPmid), titleText, abstractText, record.get(record.size() - 1));
		return nlmWsdRecord;

	}

	public static void main(String[] args) throws IOException {
		NlmWsdRecordReader nlmWsdRecordReader = new NlmWsdRecordReader();
		List<NlmWsdRecord> nlmWsdRecords = nlmWsdRecordReader.read("japanese");
		for (NlmWsdRecord nlmWsdRecord : nlmWsdRecords) {
			System.err.println(nlmWsdRecord.toString());
		}
	}
}
