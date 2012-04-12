package org.biosemantics.eviped.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class OverlapCalculator {

	private static final String FILE2 = "/Users/bhsingh/code/data/EVIPED/doxapram/mesh+text-query-output-570.txt";
	private static final String FILE1 = "/Users/bhsingh/code/data/EVIPED/doxapram/peregrine-output-456.txt";
	private static final String OUTPUT = "/Users/bhsingh/Code/data/EVIPED/doxapram/mest+text-peregrine-coverage.csv";

	public static void calculateOverlap(String file1, String file2, String outFile) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(file1)));
		List<String[]> file1Lines = csvReader.readAll();
		csvReader.close();
		csvReader = new CSVReader(new FileReader(new File(file2)));
		List<String[]> file2Lines = csvReader.readAll();
		Map<String, Object> cache = new HashMap<String, Object>();
		csvReader.close();
		for (String[] columns : file2Lines) {
			cache.put(columns[0], null);
		}
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(outFile)));
		for (String[] columns : file1Lines) {
			if (cache.containsKey(columns[0])) {
				csvWriter.writeNext(new String[] { columns[0], "MATCH" });
			} else {
				csvWriter.writeNext(new String[] { columns[0], "NO_MATCH" });
			}
		}
		csvWriter.flush();
		csvWriter.close();
	}

	public static void main(String[] args) throws IOException {
		calculateOverlap(FILE1, FILE2, OUTPUT);
	}

}
