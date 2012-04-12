package org.biosemantics.eviped.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class PeregrineErrorAnalysis {

	private JdbcTemplate jdbcTemplate;

	private static final String SQL = "select abstract_text from medline_abstract where pmid = ?";
	private static final String TITLE = "select article_title from medline_citation where pmid = ?";
	private static final String[] CONTEXTS = new String[] { "eviped-utils-context.xml" };
	private static final String[] SYNONYMNS = new String[] { "doxapram", "dopram" };
	private AbstractTextExtractor abstractTextExtractor;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setAbstractTextExtractor(AbstractTextExtractor abstractTextExtractor) {
		this.abstractTextExtractor = abstractTextExtractor;
	}

	public String getAbstract(int pmid) {
		return (String) jdbcTemplate.query(SQL, new Object[] { pmid }, abstractTextExtractor);

	}

	public String getTitle(int pmid) {
		return (String) jdbcTemplate.queryForObject(TITLE, new Object[] { pmid }, String.class);
	}

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		applicationContext.registerShutdownHook();
		PeregrineErrorAnalysis errorAnalysis = applicationContext.getBean(PeregrineErrorAnalysis.class);
		CSVReader csvReader = new CSVReader(new FileReader(new File(
				"/Users/bhsingh/Code/data/EVIPED/doxapram/overlap-peregrine-mesh.csv")));
		List<String[]> lines = csvReader.readAll();
		csvReader.close();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(
				"/Users/bhsingh/Code/data/EVIPED/doxapram/dosage-analysis.txt")));
		for (String[] columns : lines) {
			if (columns[1].contains("NO_MATCH")) {
				Integer pmid = Integer.valueOf(columns[0]);
				String titleTxt = errorAnalysis.getTitle(pmid);
				String abstractTxt = errorAnalysis.getAbstract(pmid);
				String finalText = (titleTxt + " " + abstractTxt).toLowerCase();
				boolean contains = false;
				for (String synonymn : SYNONYMNS) {
					if (finalText.contains(synonymn)) {
						contains = true;
					}
				}
				if (!contains) {
					System.err.println("");
				}
				csvWriter.writeNext(new String[] { columns[0], titleTxt, abstractTxt, "" + contains });
			}
		}
		csvWriter.close();
	}
}
