package org.biosemantics.wsd.msh;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FilenameUtils;

import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class ArffToCsv {
	private static final String resultFolder = "/Users/bhsingh/Desktop/MSHCorpus";

	public static void main(String[] args) throws Exception {
		File[] files = new File(resultFolder).listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".arff")) {
					return true;
				} else {
					return false;
				}
			}
		});
		for (File file : files) {
			CSVSaver csvSaver = new CSVSaver();
			csvSaver.setOptions(new String[]{"-F ;"});
			DataSource source = new DataSource(file.getAbsolutePath());
			csvSaver.setInstances(source.getDataSet());
			String outFileName = file.getName().split("\\_")[0];
			File outputFile = new File(resultFolder, outFileName);
			csvSaver.setFile(outputFile);
			csvSaver.writeBatch();
			// CSVSaver.main(csvArgs);
		}
	}

}
