package org.biosemantics.eviped.tools.service;

import gov.nih.nlm.ncbi.eutils.PubmedRestClient;
import gov.nih.nlm.ncbi.eutils.generated.efetch.AbstractText;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticle;
import gov.nih.nlm.ncbi.eutils.generated.efetch.PubmedArticleSet;
import gov.nih.nlm.ncbi.eutils.generated.esearch.ESearchResult;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Joiner;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.*;

@Service
public class MeshQueryBuilderServiceImpl implements QueryBuilder {

	private static final String OUT_FOLDER = "/Users/bhsingh/code/data/EVIPED/annotation/Raw";
	private static final String[] PREDICATES = new String[]{"administration & dosage", "adverse effects",
		"pharmacokinetics"};
	// private static final String FILTER =
	// "(\"Humans\"[Filter]) AND (\"all infant\"[Filter] OR \"Newborn\"[Filter] OR \"Preschool Child\"[Filter])";
	private static final String FILTER = "AND \"has abstract\"[Filter] AND (\"Child\"[Filter] OR \"Infant\"[Filter] OR \"Newborn\"[Filter] OR \"adolescent\"[Filter] ) NOT \"Middle Aged\"[Filter] NOT \"Adult\"[FILTER] NOT \"Pregnancy\"[MeSH Terms] NOT \"Pregnancy Outcome\"[MeSH Terms]  NOT \"Breast Feeding\"[MeSH Terms]  NOT \"Review\"[Publication Type] )";
	private static final String ANNOTATION_FOLDER = "/Users/bhsingh/code/data/EVIPED/annotation/Annotation";
	private static final Logger logger = LoggerFactory.getLogger(MeshQueryBuilderServiceImpl.class);
	private String drugFile;
	private static final Joiner joiner = Joiner.on(",").skipNulls();
	@Autowired
	private PubmedRestClient pubmedRestClient;

	@Override
	public Collection<QueryResult> searchMedline(String drugName) throws JAXBException {
		Collection<String> queries = createQueriesForDrug(drugName);
		Map<Integer, Integer> queryResultWeightMap = new HashMap<Integer, Integer>();
		for (String query : queries) {
			ESearchResult eSearchResult = searchPubmed(query);
			List<BigInteger> ids = eSearchResult.getIdList().getId();
			for (BigInteger id : ids) {
				Integer pmid = id.intValue();
				int weight = 1;
				if (queryResultWeightMap.containsKey(pmid)) {
					weight = queryResultWeightMap.get(pmid) + 1;
					logger.info("weight = {}", weight);
				}
				queryResultWeightMap.put(pmid, weight);
			}
		}
		List<QueryResult> meshQueryResults = new ArrayList<QueryResult>(queryResultWeightMap.size());
		for (Entry<Integer, Integer> entry : entriesSortedByValues(queryResultWeightMap)) {
			meshQueryResults.add(new QueryResult(entry.getKey(), entry.getValue()));
		}
		return meshQueryResults;
	}

	@Override
	public Collection<Article> getArticles(String drugName) throws JAXBException {
		Collection<String> queries = createQueriesForDrug(drugName);
		Map<Integer, Integer> queryResultWeightMap = new HashMap<Integer, Integer>();
		for (String query : queries) {
			ESearchResult eSearchResult = searchPubmed(query);
			List<BigInteger> ids = eSearchResult.getIdList().getId();
			for (BigInteger id : ids) {
				Integer pmid = id.intValue();
				int weight = 1;
				if (queryResultWeightMap.containsKey(pmid)) {
					weight = queryResultWeightMap.get(pmid) + 1;
				}
				queryResultWeightMap.put(pmid, weight);
			}
		}
		Set<Integer> pmids = queryResultWeightMap.keySet();
		PubmedArticleSet pubmedArticleSet = fetchPmidDetails(pmids);
		Collection<Article> articles = new ArrayList<Article>();
		for (PubmedArticle pubmedArticle : pubmedArticleSet.getPubmedArticle()) {
			int pmid = pubmedArticle.getMedlineCitation().getPMID().getValue().intValue();
			articles.add(new Article(queryResultWeightMap.get(pmid), pubmedArticle));
		}
		return articles;
	}

	private Collection<String> createQueriesForDrug(String drugMeshName) {
		Collection<String> queries = new ArrayList<String>();
		for (String predicate : PREDICATES) {
			StringBuilder stringBuilder = new StringBuilder("(").append(drugMeshName).append("/").append(predicate).append(")").append(" ").append(FILTER);
			queries.add(stringBuilder.toString());
		}
		return queries;
	}

	private Collection<String> getDrugs() throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(drugFile)));
		List<String[]> lines = csvReader.readAll();
		List<String> meshNames = new ArrayList<String>();
		for (String[] columns : lines) {
			meshNames.add(columns[1].trim());
		}
		csvReader.close();
		return meshNames;
	}

	private ESearchResult searchPubmed(String searchQuery) throws JAXBException {
		MultivaluedMapImpl params = new MultivaluedMapImpl();
		params.add("db", "pubmed");
		params.add("retMax", "100000");
		params.add("term", searchQuery);
		ESearchResult esearchResult = pubmedRestClient.search(params);
		return esearchResult;
	}

	private PubmedArticleSet queryAbstract(String pmid) throws JAXBException, IOException {
		MultivaluedMapImpl params = new MultivaluedMapImpl();
		params.add("db", "pubmed");
		params.add("id", pmid);
		params.add("retmode", "xml");
		PubmedArticleSet pubmedArticleSet = pubmedRestClient.fetch(params);
		return pubmedArticleSet;
	}

	private PubmedArticleSet fetchPmidDetails(Collection<Integer> pmids) throws JAXBException {
		String strPmids = joiner.join(pmids);
		MultivaluedMapImpl params = new MultivaluedMapImpl();
		params.add("db", "pubmed");
		params.add("id", strPmids);
		params.add("retmode", "xml");
		PubmedArticleSet pubmedArticleSet = pubmedRestClient.fetch(params);
		return pubmedArticleSet;
	}

	public void writeAll() throws IOException, JAXBException {
		Collection<String> drugs = getDrugs();
		for (String drug : drugs) {
			Collection<String> queries = createQueriesForDrug(drug);
			int ctr = 0;
			for (String query : queries) {
				ESearchResult eSearchResult = searchPubmed(query);
				List<BigInteger> ids = eSearchResult.getIdList().getId();
				CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, drug + "_" + ctr + ".txt")));
				ctr++;
				for (BigInteger bigInteger : ids) {
					csvWriter.writeNext(new String[]{"" + bigInteger.intValue()});
				}
				csvWriter.flush();
				csvWriter.close();
			}
		}
	}

	public void sortOutput() throws IOException, JAXBException {
		Collection<String> drugs = getDrugs();
		for (String drug : drugs) {
			Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
			for (int i = 0; i < PREDICATES.length; i++) {
				CSVReader csvReader = new CSVReader(new FileReader(new File(OUT_FOLDER, drug + "_" + i + ".txt")));
				List<String[]> lines = csvReader.readAll();
				for (String[] columns : lines) {
					int key = Integer.valueOf(columns[0].trim());
					int value = 1;
					if (countMap.containsKey(key)) {
						value = (countMap.get(key) + 1);
					}
					countMap.put(key, value);
				}
				csvReader.close();
			}
			CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(OUT_FOLDER, drug + "_FINAL.txt")));
			int ctr = 0;
			for (Entry<Integer, Integer> entry : countMap.entrySet()) {
				ctr++;
				csvWriter.writeNext(new String[]{"" + entry.getKey(), "" + entry.getValue()});
			}
			csvWriter.flush();
			csvWriter.close();
			System.out.println(drug + " " + ctr);
		}
	}

	public void createAnnotation() throws IOException, JAXBException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(
				"/Users/bhsingh/code/data/EVIPED/annotation/records_number.csv")));
		Map<String, Integer> maxRecordsMap = new HashMap<String, Integer>();
		List<String[]> maxRecordLines = csvReader.readAll();
		for (String[] columns : maxRecordLines) {
			maxRecordsMap.put(columns[0].trim(), Integer.parseInt(columns[2]));
		}
		csvReader.close();
		Collection<String> drugs = getDrugs();
		for (String drug : drugs) {
			drug = drug.trim();
			int maxRows = maxRecordsMap.get(drug);
			logger.info("{} {}", new Object[]{drug, maxRows});
			CSVReader finalReader = new CSVReader(new FileReader(new File(OUT_FOLDER, drug + "_FINAL" + ".txt")));
			Map<String, Integer> map = new TreeMap<String, Integer>();

			List<String[]> lines = finalReader.readAll();
			if (lines.isEmpty()) {
				logger.error("{} IS EMPTY", new Object[]{drug});
			}
			for (String[] columns : lines) {
				map.put(columns[0], Integer.parseInt(columns[1]));
			}
			finalReader.close();
			// sort map
			int ctr = 0;
			for (Entry<String, Integer> entry : entriesSortedByValues(map)) {
				if (ctr < maxRows) {
					StringBuilder text = new StringBuilder();
					PubmedArticleSet pubmedArticleSet = queryAbstract(entry.getKey());
					try {
						PubmedArticle article = pubmedArticleSet.getPubmedArticle().get(0);
						List<AbstractText> texts = article.getMedlineCitation().getArticle().getAbstract().getAbstractText();
						for (AbstractText abstractText : texts) {
							text.append(abstractText.getContent());
						}
						String abstractText = text.toString().trim();
						if (!abstractText.isEmpty()) {
							File file = new File(ANNOTATION_FOLDER, entry.getKey() + ".txt");
							if (file.exists()) {
								logger.info("duplicate {}", entry.getKey());
								continue;
							}
							FileWriter fileWriter = new FileWriter(file);
							fileWriter.write(abstractText);
							fileWriter.flush();
							fileWriter.close();
							File annFile = new File(ANNOTATION_FOLDER, entry.getKey() + ".ann");
							annFile.createNewFile();
							ctr++;
						}
					} catch (Exception e) {
						// e.printStackTrace();
					}

				} else {
					// logger.info("breaking {}", ctr);
					break;
				}
			}
			logger.info("done-->{} {}", new Object[]{drug, ctr});
		}
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {

			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1; // Special fix to preserve items with
				// equal values
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public PubmedArticleSet retrieveArticles(Collection<Integer> pmids) throws JAXBException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	

	
		
}
