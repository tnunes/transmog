package org.biosemantics.wsd.nlm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class NlmWsdRecordReader {

	private String resultFolder;

	public void setResultFolder(String resultFolder) {
		this.resultFolder = resultFolder;
	}

	public List<NlmWsdRecord> readRecordsForAmbiguousWord(String ambiguousWordText) throws IOException {
		File file = new File(resultFolder + File.separatorChar + ambiguousWordText + File.separatorChar
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
		List<NlmWsdRecord> nlmWsdRecords = nlmWsdRecordReader.readRecordsForAmbiguousWord("japanese");
		for (NlmWsdRecord nlmWsdRecord : nlmWsdRecords) {
			System.err.println(nlmWsdRecord.toString());
		}
	}
}
