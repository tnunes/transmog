package org.biosemantics.eviped.classifier.weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class TxtSourceReaderImpl implements TxtSourceReader {
	private File[] listOfFiles;
	private List<String> header = new ArrayList<String>();
	private Map<String, List<String>> allWords = new HashMap<String, List<String>>();
	private List<String[]> data = new ArrayList<String[]>();
	private static final String CLASS = "Quetiapine_Class";
	private static final String CLASS_VALUE_YES = "yes";
	private static final String CLASS_VALUE_NO = "no";
	private static final Logger logger = LoggerFactory.getLogger(TxtSourceReaderImpl.class);

	@Override
	public void read(String folderPath) {
		File folder = new File(folderPath);
		listOfFiles = folder.listFiles();
		try {
			for (File file : listOfFiles) {
				CSVReader csvReader = new CSVReader(new FileReader(file), '\t');
				List<String[]> lines = csvReader.readAll();
				List<String> words = new ArrayList<String>();
				for (String[] line : lines) {
					if (line == null || line.length != 2) {
						logger.warn("illegal line");
					} else {
						String word = line[0].trim();
						int frequency = Integer.valueOf(line[1].trim());
						if (!header.contains(word)) {
							if (frequency >3) {
								header.add(word);
								words.add(word);
							}
						} else {
							logger.debug("{} already exists in header.", word);
						}
						
					}
				}
				csvReader.close();
				allWords.put(file.getName(), words);
			}
			header.add(CLASS);
			logger.debug("header size = {}", header.size());

			for (Entry<String, List<String>> entrySet : allWords.entrySet()) {
				String[] dataLine = new String[header.size()];
				int ctr = 0;
				for (String headerWord : header) {
					if (entrySet.getValue().contains(headerWord)) {
						dataLine[ctr] = "1";
					} else {
						dataLine[ctr] = "0";
					}
					ctr++;
				}
				if (entrySet.getKey().startsWith("0")) {
					dataLine[dataLine.length - 1] = CLASS_VALUE_NO;
				} else {
					dataLine[dataLine.length - 1] = CLASS_VALUE_YES;
				}
				data.add(dataLine);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String[] getHeader() {
		return header.toArray(new String[header.size()]);
	}

	@Override
	public List<String[]> getData() {
		return data;
	}

}
