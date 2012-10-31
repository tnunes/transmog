package org.biosemantics.eviped.utils;

import gov.nih.nlm.ncbi.eutils.PubmedRestClient;
import gov.nih.nlm.ncbi.eutils.generated.esearch.ESearchResult;
import gov.nih.nlm.ncbi.eutils.generated.esummary.DocSum;
import gov.nih.nlm.ncbi.eutils.generated.esummary.ESummaryResult;
import gov.nih.nlm.ncbi.eutils.generated.esummary.Item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
	private PubmedRestClient pubmedRestClient;
	private static final Logger logger = LoggerFactory.getLogger(PubmedQueryUtility.class);
	private static final String[] CONTEXT = new String[] { "eviped-utils-context.xml" };
	private static final String OUT_FILE = "/Users/bhsingh/Desktop/out.txt";
	
	public void setPubmedRestClient(PubmedRestClient pubmedRestClient) {
		this.pubmedRestClient = pubmedRestClient;
	}
//
//	public List<BigInteger> queryForDosage() throws JAXBException, IOException {
//		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
//		params.add("db", "pubmed");
//		params.add("term", "administration & dosage");
//		params.add("retmax", "10000000");
//		ESearchResult searchResult = pubmedRestClient.search(params);
//		return searchResult.getIdList().getId();
//	}
//

//
//	public static void main(String[] args) throws IOException, JAXBException {
//		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(CONTEXT);
//		classPathXmlApplicationContext.registerShutdownHook();
//		PubmedQueryUtility pubmedQueryUtility = classPathXmlApplicationContext.getBean(PubmedQueryUtility.class);
//		PubmedRestClient pubmedRestClient = classPathXmlApplicationContext.getBean(PubmedRestClient.class);
//		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FILE)));
//		List<BigInteger> pmids = pubmedQueryUtility.queryForDosage();
//		int totalPmids = pmids.size();
//		int batchSize = 1000;
//		int iterations = totalPmids / batchSize;
//		int remainder = totalPmids % batchSize;
//		for (int i = 0; i < iterations; i++) {
//			int start = i * batchSize;
//			int end = start + batchSize;
//			List<BigInteger> subList = pmids.subList(start, end);
//			String ids = joiner.join(subList);
//			List<String> journalNames = pubmedQueryUtility.getJournals(ids);
//			
//		}
//
//		csvWriter.flush();
//		csvWriter.close();
//	}
//
//	public List<String> getJournals(String ids) throws JAXBException, IOException {
//		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
//		params.add("db", "pubmed");
//		params.add("id", ids);
//		ESummaryResult summaryResult = pubmedRestClient.summary(params);
//		List<String> journalNames = new ArrayList<String>();
//		for (DocSum docSum : summaryResult.getDocSum()) {
//			for (Item item : docSum.getItem()) {
//				if (item.getName().equals("FullJournalName")) {
//					journalNames.add(((String) item.getContent().get(0)));
//					break;
//				}
//			}
//		}
//		return journalNames;
//	}

}
