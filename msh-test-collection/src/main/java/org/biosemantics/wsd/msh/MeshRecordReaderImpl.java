package org.biosemantics.wsd.msh;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MeshRecordReaderImpl {
	private String recordFolder;
	private String senseFile;
	private List<String[]> senseLines;

	public void setRecordFolder(String recordFolder) {
		this.recordFolder = recordFolder;
	}

	public void setSenseFile(String senseFile) throws IOException {
		this.senseFile = senseFile;
		CSVReader senseFileReader = new CSVReader(new FileReader(new File(this.senseFile)), '\t');
		senseLines = senseFileReader.readAll();
		senseFileReader.close();
	}

	public String[] getSenses(String term) {
		String[] senses = null;
		for (String[] columns : senseLines) {
			if (columns[0].equals(term)) {
				senses = Arrays.copyOfRange(columns, 1, columns.length);
				break;
			}
		}
		return senses;
	}

	public MeshRecord getMeshRecord(String term, String pmid) throws IOException {
		File file = new File(recordFolder, term);
		CSVReader csvReader = new CSVReader(new FileReader(file));
		List<String[]> lines = csvReader.readAll();
		MeshRecord meshRecord = null;
		for (String[] columns : lines) {
			if (columns[0].equalsIgnoreCase(pmid)) {
				String meaning = columns[columns.length - 1];
				StringBuilder text = new StringBuilder();
				for (int i = 1; i < columns.length - 1; i++) {
					text.append(columns[i]);
				}
				if (text.substring(0, 1).equals("'")) {
					text = text.deleteCharAt(0);
				}
				if (text.substring(text.length() - 1, text.length()).equals("'")) {
					text = text.deleteCharAt(text.length() - 1);
				}
				meshRecord = new MeshRecord(columns[0], text.toString(), meaning, getMeaningCui(term, meaning));
				break;
			}
		}
		csvReader.close();
		return meshRecord;
	}

	private String getMeaningCui(String term, String meaning) {
		int meaningOrdinal = Integer.parseInt(meaning.substring(1, meaning.length()));
		String meaningCui = null;
		for (String[] columns : senseLines) {
			if (columns[0].equalsIgnoreCase(term)) {
				meaningCui = columns[meaningOrdinal];
				break;
			}
		}
		return meaningCui;
	}

}
