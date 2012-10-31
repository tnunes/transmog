package org.biosemantics.eviped.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ExtraFieldUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class UniqueIdExtractor {

	public static final String INPUT = "/Users/bhsingh/Code/data/EVIPED/doxapram/pubmed-doxapram-title-abstract.txt";
	public static final String OUTPUT = "/Users/bhsingh/Code/data/EVIPED/doxapram/pubmed-doxapram-title-abstract-unique-out.txt";

	public static void extractId(String input, String output) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(input)));
		List<String[]> lines = csvReader.readAll();
		csvReader.close();
		Set<String> uniqueIds = new HashSet<String>();
		for (String[] columns : lines) {
			uniqueIds.add(columns[0]);
		}

		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUTPUT)));
		for (String uniqueId : uniqueIds) {
			csvWriter.writeNext(new String[] { uniqueId });
		}
		csvWriter.flush();
		csvWriter.close();
	}

	public static void main(String[] args) throws IOException {
		extractId(INPUT, OUTPUT);
	}
}
