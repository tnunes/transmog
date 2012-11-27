/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.biosemantics.conceptstore.dataimport;

/**
 *
 * @author bhsingh
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class IgnoredCuiReader {

	private Map<String, Object> ignoredCuisMap = new HashMap<String, Object>();

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

	public boolean isIgnored(String cui) {
		return ignoredCuisMap.containsKey(cui);
	}

	private static final Logger logger = LoggerFactory.getLogger(IgnoredCuiReader.class);

	public static void main(String[] args) throws IOException {
		IgnoredCuiReader ignoredCuiReader = new IgnoredCuiReader();
		ignoredCuiReader.init();
		System.out.println(ignoredCuiReader.ignoredCuisMap);
	}

}
