package org.biosemantics.eviped.tools.service;

import gov.nih.nlm.ncbi.eutils.PubmedRestClient;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticle;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticleSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVWriter;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class CorpusStatistics {

	private static final String FOLDER = "/Users/bhsingh/Annotation-Geert";
	private static final String DB4OFILENAME = "/Users/bhsingh/corpus.yup";

	public static void main(String[] args) throws JAXBException, IOException {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB4OFILENAME);
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"/org/biosemantics/eviped/tools/eviped-tools-context.xml");
		applicationContext.registerShutdownHook();
		PubmedRestClient pubmedRestClient = applicationContext.getBean(PubmedRestClient.class);
		File folder = new File(FOLDER);
		File[] files = folder.listFiles();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File("/Users/bhsingh/out.txt")));
		for (File file : files) {
			Integer pmid = Integer.parseInt(stripExtension(file.getName()));
			MultivaluedMapImpl params = new MultivaluedMapImpl();
			params.add("db", "pubmed");
			params.add("id", pmid);
			params.add("retmode", "xml");
			PubmedArticleSet pubmedArticleSet = pubmedRestClient.fetch(params);
			db.store(pubmedArticleSet);
			String country = null;
			String year = null;
			String journalName = null;
			String journalAbbreviation = null;
			for (PubmedArticle pubmedArticle : pubmedArticleSet.getPubmedArticle()) {
				try {
					journalAbbreviation = pubmedArticle.getMedlineCitation().getArticle().getJournal().getTitle();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					journalName = pubmedArticle.getMedlineCitation().getArticle().getJournal().getISOAbbreviation();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					year = pubmedArticle.getMedlineCitation().getArticle().getJournal().getJournalIssue().getPubDate()
							.getYear().toString();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					country = pubmedArticle.getMedlineCitation().getMedlineJournalInfo().getCountry();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			csvWriter.writeNext(new String[] { "" + pmid, country, year, journalName, journalAbbreviation });
			csvWriter.flush();
			System.out.println(pmid);
		}
		
		csvWriter.close();
		db.close();
	}

	private static String stripExtension(String str) {
		// Handle null case specially.

		if (str == null)
			return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.

		if (pos == -1)
			return str;

		// Otherwise return the string, up to the dot.

		return str.substring(0, pos);
	}

}
