package org.biosemantics.wsd.datasource.umls;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class PubmedRlspWriter {

	public void init() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("ignore.txt");
		if (is == null) {
			logger.info("no ignore.txt file found");
		} else {
			CSVReader csvReader = new CSVReader(new InputStreamReader(is));
			List<String[]> lines = csvReader.readAll();
			for (String[] columns : lines) {
				ignoredCuisMap.put(columns[0].trim(), null);
			}
			logger.info("{} cuis ignored.", ignoredCuisMap.size());
			csvReader.close();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(PubmedRlspWriter.class);

}
