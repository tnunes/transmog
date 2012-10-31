package org.biosemantics.disambiguation.umls.wsd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class OutputFile {
	private static final String FOLDER = "/Users/bhsingh/Desktop/out";
	private static final String EXTN = ".csv";
	private File file;
	private CSVWriter csvWriter;
	private static final Logger logger = LoggerFactory.getLogger(OutputFile.class);

	public OutputFile(String fileName) throws IOException {
		super();
		file = new File(FOLDER, fileName + EXTN);
		if (file.exists()) {
			logger.error("{} file exists. output will append to this file", file.getAbsolutePath());
		} else {
			logger.debug("creating output file: {}", file.getAbsolutePath());
			file.createNewFile();
		}
		csvWriter = new CSVWriter(new FileWriter(file));
	}

	public void writeLine(OutputObject outputObject) throws IOException {
		csvWriter.writeNext(outputObject.toStringArray());
		csvWriter.flush();
	}

	public void writeNext(String[] line) throws IOException {
		csvWriter.writeNext(line);
		csvWriter.flush();
	}

	public void close() throws IOException {
		csvWriter.flush();
		csvWriter.close();
	}
}
