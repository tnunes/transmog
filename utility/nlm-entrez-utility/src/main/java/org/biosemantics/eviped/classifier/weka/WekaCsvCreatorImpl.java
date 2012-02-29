package org.biosemantics.eviped.classifier.weka;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class WekaCsvCreatorImpl implements WekaCsvCreator {

	private List<String[]> allLines = new ArrayList<String[]>();

	public WekaCsvCreatorImpl() {
		
	}

	@Override
	public void setHeader(String[] header) {
		allLines.add(0, header);
	}

	@Override
	public void setData(List<String[]> data) {
		allLines.addAll(data);
	}

	@Override
	public void write(String outFile) {
		File out = new File(outFile);
		if (out.exists()) {
			throw new IllegalStateException("output file already exists");
		}
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriter(out));
			csvWriter.writeAll(allLines);
			csvWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (csvWriter != null)
				try {
					csvWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
}
