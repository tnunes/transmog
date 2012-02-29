package org.biosemantics.eviped.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class RegexExtractor {

	private static final Pattern pattern = Pattern.compile("mg");
	private static final Logger logger = LoggerFactory.getLogger(RegexExtractor.class);	

	public static void main(String[] args) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(
				"/Users/bhsingh/Code/data/EVIPED/doxapram/dosage-analysis.csv")));
		List<String[]> rows = csvReader.readAll();
		for (String[] columns : rows) {
			logger.info("------{}----", columns[0]);
			Matcher matcher = pattern.matcher(columns[1]);
			while (matcher.find()) {
				logger.info("{}", matcher.group());
			}
			
			matcher = pattern.matcher(columns[2]);
			while (matcher.find()) {
				logger.info("{}", matcher.group());
			}

		}
	}

}
