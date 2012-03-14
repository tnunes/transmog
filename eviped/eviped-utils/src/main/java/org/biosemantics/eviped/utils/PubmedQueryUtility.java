package org.biosemantics.eviped.utils;

import gov.nih.nlm.ncbi.eutils.PubmedRestClient;
import gov.nih.nlm.ncbi.eutils.generated.ESearchResult;
import gov.nih.nlm.ncbi.eutils.generated.Journal;
import gov.nih.nlm.ncbi.eutils.generated.PubmedArticle;
import gov.nih.nlm.ncbi.eutils.generated.PubmedArticleSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Joiner;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class PubmedQueryUtility {

	protected static final String GET_JOURNAL_SQL = "select pmid, journal_title from medline_citation where pmid = ?";
	private static final Joiner joiner = Joiner.on(",").skipNulls();
	public void setPubmedRestClient(PubmedRestClient pubmedRestClient) {
		this.pubmedRestClient = pubmedRestClient;
	}

	public List<BigInteger> queryForDosage() throws JAXBException, IOException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("db", "pubmed");
		params.add("term", "administration & dosage");
		params.add("retmax", "10000000");
		ESearchResult searchResult = pubmedRestClient.search(params);
		return searchResult.getIdList().getId();
	}

	private PubmedRestClient pubmedRestClient;
	private static final Logger logger = LoggerFactory.getLogger(PubmedQueryUtility.class);
	private static final String[] CONTEXT = new String[] { "eviped-utils-context.xml" };
	private static final String OUT_FILE = "/Users/bhsingh/Desktop/out.txt";

	public static void main(String[] args) throws IOException, JAXBException {
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(CONTEXT);
		classPathXmlApplicationContext.registerShutdownHook();
		PubmedQueryUtility pubmedQueryUtility = classPathXmlApplicationContext.getBean(PubmedQueryUtility.class);
		PubmedRestClient pubmedRestClient = classPathXmlApplicationContext.getBean(PubmedRestClient.class);

		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FILE)));
		List<BigInteger> pmids = pubmedQueryUtility.queryForDosage();
		String pmidList = joiner.join(pmids);
//		for (BigInteger bigInteger : pmids) {
//			int pmid = bigInteger.intValue();
			MultivaluedMap<String, String> params = new MultivaluedMapImpl();
			params.add("db", "pubmed");
			params.add("id", "" + pmidList);
			params.add("retmode", "xml");
//			try{
			PubmedArticleSet pubmedArticleSet = pubmedRestClient.fetch(params);
			for (PubmedArticle pubmedArticle : pubmedArticleSet.getPubmedArticle()) {
				Journal journal = pubmedArticle.getMedlineCitation().getArticle().getJournal();
				csvWriter.writeNext(new String[] { "" + pubmedArticle.getMedlineCitation().getPMID().getValue().intValue(), journal.getISOAbbreviation(), journal.getTitle() });
			}
//			}catch (Exception e) {
//				logger.error("ERROR PARSING {}", pmid);
//			}
//		}
		csvWriter.flush();
		csvWriter.close();
	}

}
