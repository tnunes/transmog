package org.biosemantics.wsd.nlm.app;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class WordSenseFinalScoreApp {
	private static final String OUTPUT_FOLDER = "/ssd/bhsingh/data/hierarchy-new-score";

	public static void main(String[] args) throws IOException {
		File folder = new File(OUTPUT_FOLDER);
		File[] files = folder.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if (pathname.toString().endsWith("_score"))
					return true;
				else
					return false;
			}
		});
		CSVWriter allScore = new CSVWriter(new FileWriter(new File(OUTPUT_FOLDER, "01_all_score")));
		for (File file : files) {
			String ambiguousName = file.getName();
			ambiguousName = ambiguousName.substring(0, (ambiguousName.length() - 6));
			CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUTPUT_FOLDER, ambiguousName + "_final_score")));

			CSVReader csvReader = new CSVReader(new FileReader(file));
			List<String[]> lines = csvReader.readAll();
			int senses = lines.size() / 100;
			int ctr = 0;
			int lineCtr = 0;
			float maxScore = 0;
			int ordinal = 0;
			float matches = 0;
			float totalResults = 0;
			for (String[] columns : lines) {
				ctr++;
				Float score = Float.valueOf(columns[4]);
				if (score > maxScore) {
					maxScore = score;
					ordinal = ctr;
				}
				if (ctr % senses == 0) {
					// write
					String actualResult = lines.get(lineCtr)[3].trim();

					if (!actualResult.equalsIgnoreCase("None")) {
						totalResults++;
						if (actualResult.equals(String.valueOf(ordinal))) {
							matches++;
						}
					}
					csvWriter.writeNext(new String[] { "" + actualResult, "" + maxScore, "" + ordinal,
							"" + (actualResult.equals(String.valueOf(ordinal))) });

					ctr = 0;
					maxScore = 0F;
					ordinal = 0;
				}
				lineCtr++;
			}
			csvWriter
					.writeNext(new String[] { "" + matches, "" + totalResults, "" + ((matches / totalResults) * 100) });
			allScore.writeNext(new String[] { ambiguousName, "" + matches, "" + totalResults,
					"" + ((matches / totalResults) * 100) });
			csvWriter.flush();
			csvWriter.close();
		}
		allScore.flush();
		allScore.close();

	}
}
