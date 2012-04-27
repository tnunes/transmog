package org.biosemantics.wsd.msh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class MeshResultReaderImpl {

	private List<String[]> resultLines;

	public void setResultFile(String resultFile) throws IOException {
		CSVReader resultFileReader = new CSVReader(new FileReader(new File(resultFile)), '|');
		resultLines = resultFileReader.readAll();
		resultFileReader.close();
	}

	public List<MeshResult> readAll() throws FileNotFoundException {
		List<MeshResult> meshResults = new ArrayList<MeshResult>();
		for (String[] columns : resultLines) {
			meshResults.add(new MeshResult(columns));
		}
		return meshResults;
	}

}
