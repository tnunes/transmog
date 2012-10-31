package org.biosemantics.wsd.nlm.app;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ScoreApp {

	public static final String RESULT_FOLDER = "/ssd/bhsingh/data/6-1-sum";
	public static final String OUT_FOLDER = "/ssd/bhsingh/data/";

	public static void main(String[] args) throws IOException {
		File folder = new File(RESULT_FOLDER);
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, "03_summary.txt")));
		File[] files = folder.listFiles();
		for (File file : files) {
			CSVReader csvReader = new CSVReader(new FileReader(file));
			List<String[]> lines = csvReader.readAll();
			double accuracy = 0;
			for (String[] columns : lines) {
				String value = columns[columns.length - 1];
				if (Boolean.valueOf(value)) {
					accuracy++;
				}
			}
			double accuracyPercentage = (accuracy / lines.size())*100;
			csvWriter.writeNext(new String[] { file.getName(), "" + accuracy, "" + lines.size(),
					"" + accuracyPercentage });
			csvReader.close();

		}
		csvWriter.flush();
		csvWriter.close();
	}

}
