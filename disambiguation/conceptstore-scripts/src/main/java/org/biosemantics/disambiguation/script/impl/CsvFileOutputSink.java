package org.biosemantics.disambiguation.script.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.biosemantics.disambiguation.script.OutputSink;
import org.biosemantics.disambiguation.script.WritableObject;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvFileOutputSink implements OutputSink {

	private File outputFile;
	private CSVWriter csvWriter;

	public CsvFileOutputSink(String outputFilePath) throws IOException {
		super();
		this.outputFile = new File(outputFilePath);
		if (outputFile.exists()) {
			throw new IllegalArgumentException("output file already exists");
		} else {
			outputFile.createNewFile();
		}
	}

	@Override
	public void init() {
		try {
			csvWriter = new CSVWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void write(WritableObject object) {
		csvWriter.writeNext(object.toStringArray());
		try {
			csvWriter.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			csvWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
