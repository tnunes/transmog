package org.biosemantics.eviped.tools.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Joiner;

public class AnnotationReaderImpl {

	private static final Joiner joiner = Joiner.on("|").skipNulls();
	private List<String[]> lines;

	public AnnotationReaderImpl(String fileName) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(fileName)));
		lines = csvReader.readAll();
		csvReader.close();
	}

	public List<String> getAnnotationTextByType(String type) {
		List<String> annotationTexts = new ArrayList<String>();
		for (String[] columns : lines) {
			if (columns[1].equals(type)) {
				annotationTexts.add(columns[4]);
			}
		}
		return annotationTexts;
	}

	public List<Annotation> getAnnotationForFile(String fileName) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (String[] columns : lines) {
			if (columns[5].equals(fileName)) {
				annotations.add(new Annotation(columns[1], Integer.parseInt(columns[2]), Integer.parseInt(columns[3]),
						0, columns[4]));
			}
		}
		return annotations;
	}

	public static void main(String[] args) throws IOException {
		AnnotationReaderImpl annotationReaderImpl = new AnnotationReaderImpl(
				"/Users/bhsingh/Desktop/annotation-all.txt");
		List<String> strings = annotationReaderImpl.getAnnotationTextByType("Frequency");
		Set<String> commonStrings = new HashSet<String>();
		for (String string : strings) {
			commonStrings.add(string);
		}
		System.err.println(joiner.join(commonStrings));
	}
}
