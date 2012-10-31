package org.biosemantics.wsd.nlm.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.wsd.nlm.NlmWsdRecord;
import org.biosemantics.wsd.nlm.NlmWsdRecordReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class WekaInputFileApp {

	private static final String INDEXING_INPUT_FILE = "/ssd/bhsingh/data/metamap-annotations.txt";
	private static final String SENSE_FILE = "/home/bhsingh/Public/metamap/adjustment_sense.csv";
	private static final String OUT_FOLDER = "/ssd/bhsingh/data/metamap";

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"nlm-test-collection-context.xml");
		applicationContext.registerShutdownHook();
		// divideFiles();
		// extractSense(applicationContext);
		createWekaFile();

	}

	private static void createWekaFile() throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(SENSE_FILE)));
		List<String[]> out = new ArrayList<String[]>();
		List<String[]> lines = csvReader.readAll();
		csvReader.close();
		for (String[] line : lines) {
			if (line[3].trim().equals("1")) {
				out.add(line);
			}
		}
		Map<String, Integer> cuisMap = new HashMap<String, Integer>();
		Set<String> cuisSet = new HashSet<String>();
		for (String[] columns : out) {
			String[] cuis = Arrays.copyOfRange(columns, 4, columns.length);
			for (String cui : cuis) {
				if (!StringUtils.isBlank(cui)) {
					cuisSet.add(cui);
				}
			}
		}
		int ctr = 0;
		for (String cui : cuisSet) {
			cuisMap.put(cui, ctr);
			ctr++;
		}

		List<String[]> bodyLines = new ArrayList<String[]>();
		for (String[] columns : out) {
			String[] cuis = Arrays.copyOfRange(columns, 4, columns.length);
			String[] bodyLine = new String[cuisMap.size()];
			Arrays.fill(bodyLine, "0");
			for (String cui : cuis) {
				if (!StringUtils.isEmpty(cui)) {
					int position = cuisMap.get(cui);
					bodyLine[position] = "1";
				}
			}
			bodyLines.add(bodyLine);
		}

		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, "adjustment_sense_1.csv")));
		ValueComparator bvc = new ValueComparator(cuisMap);
		TreeMap<String, Integer> sorted_map = new TreeMap(bvc);
		sorted_map.putAll(cuisMap);
		csvWriter.writeNext(sorted_map.keySet().toArray(new String[sorted_map.size()]));
		for (String[] bodyLine : bodyLines) {
			csvWriter.writeNext(bodyLine);
		}
		csvWriter.flush();
		csvWriter.close();

	}

	private static void extractSense(ClassPathXmlApplicationContext applicationContext) throws IOException {
		NlmWsdRecordReader reader = applicationContext.getBean(NlmWsdRecordReader.class);
		List<NlmWsdRecord> nlmWsdRecords = reader.readRecordsForAmbiguousWord("adjustment");
		for (NlmWsdRecord nlmWsdRecord : nlmWsdRecords) {
			System.err.println(nlmWsdRecord.getAnnotatedSense());
		}
	}

	private static void divideFiles() throws FileNotFoundException, IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(INDEXING_INPUT_FILE)));
		List<String[]> lines = csvReader.readAll();
		csvReader.close();
		for (int i = 0; i < 4900; i += 100) {
			List<String[]> subList = lines.subList(i, i + 100);
			String name = subList.get(0)[0];
			CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, name)));
			csvWriter.writeAll(subList);
			csvWriter.flush();
			csvWriter.close();
		}
	}
}

class ValueComparator implements Comparator {

	Map base;

	public ValueComparator(Map base) {
		this.base = base;
	}

	public int compare(Object a, Object b) {

		if ((Integer) base.get(a) > (Integer) base.get(b)) {
			return 1;
		} else if ((Integer) base.get(a) == (Integer) base.get(b)) {
			return 0;
		} else {
			return -1;
		}
	}
}
