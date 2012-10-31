package org.biosemantics.eviped.lodd.exposure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Query;

import org.biosemantics.wsd.utility.sesame.SesameRepositoryClient;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DrugExposureClient {

	private static final String GET_DBID = "select ?x from <file://C:/fakepath/drugbank_dump.nt> where {?x <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/atcCode>";


	private static final String[] predicates = new String[] {
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/indication>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/warning>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/adverseReaction>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/overdosage>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/precaution>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/contraindication>" };

	private static final String[] names = new String[] {
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/name>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/fullName>",
			"<http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/genericMedicine>" };

	private static final String FOLDER = "/Users/bhsingh/code/data/EVIPED/Experiment1";
	private static final String ATC_FILE = "all_Children_drugs.csv";
	private static final String BRAND_FILE = "all_brands.csv";
	private static final String DBID_FILE = "dbid.csv";
	private Map<String, String> atcCodesMap = new HashMap<String, String>();
	private Map<String, List<String>> atcCodeBrandMap = new HashMap<String, List<String>>();
	@Autowired
	private SesameRepositoryClient sesameRepositoryClient;
	private static final Logger logger = LoggerFactory.getLogger(DrugExposureClient.class);

	private static final String DAILYMED_ID_FILE = "dailymed_ids.csv";

	public void getAtcCode() throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(FOLDER, ATC_FILE)));
		List<String[]> lines = csvReader.readAll();
		for (String[] columns : lines) {
			atcCodesMap.put(columns[0].trim(), columns[1].trim());
		}
	}

	public void getDbId() throws IOException, RepositoryException, QueryEvaluationException, MalformedQueryException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(FOLDER, DBID_FILE)));
		for (Entry<String, String> entry : atcCodesMap.entrySet()) {
			Repository repository = sesameRepositoryClient.getRepository();
			RepositoryConnection connection = repository.getConnection();
			try {
				StringBuilder query = new StringBuilder(GET_DBID).append(" ").append("\"").append(entry.getValue())
						.append("\"").append("}");
				logger.info("{}", query.toString());
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query.toString());
				TupleQueryResult result = tupleQuery.evaluate();

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfX = bindingSet.getValue("x");
					String dbId = valueOfX.stringValue();
					List<String> output = new ArrayList<String>();
					output.add(entry.getKey());
					output.add(entry.getValue());
					output.add(dbId);
					csvWriter.writeNext(output.toArray(new String[output.size()]));
				}
				result.close();

			} finally {
				connection.close();
			}
		}
		csvWriter.flush();
		csvWriter.close();
	}

	public void getBrand() throws IOException, RepositoryException, QueryEvaluationException, MalformedQueryException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(FOLDER, DBID_FILE)));
		List<String[]> lines = csvReader.readAll();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(FOLDER, BRAND_FILE)));
		Repository repository = sesameRepositoryClient.getRepository();
		RepositoryConnection connection = repository.getConnection();
		for (String[] columns : lines) {
			try {
				StringBuilder query = new StringBuilder("select ?x from <file://C:/fakepath/drugbank_dump.nt> where {")
						.append(" ").append("<").append(columns[2].trim()).append(">").append(" ")
						.append("<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/brandName> ?x}");
				logger.info("{}", query.toString());
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query.toString());
				TupleQueryResult result = tupleQuery.evaluate();
				List<String> output = new ArrayList<String>();
				output.add(columns[0]);
				output.add(columns[1]);
				output.add(columns[2]);
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfX = bindingSet.getValue("x");
					output.add(valueOfX.stringValue());
				}
				result.close();
				csvWriter.writeNext(output.toArray(new String[output.size()]));
			} finally {
				connection.close();
			}
		}

		csvWriter.flush();
		csvWriter.close();
		csvReader.close();

	}

	public void drugbankToDailymed() throws IOException, RepositoryException, QueryEvaluationException,
			MalformedQueryException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(FOLDER, DBID_FILE)));
		List<String[]> lines = csvReader.readAll();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(FOLDER, DAILYMED_ID_FILE)));
		Repository repository = sesameRepositoryClient.getRepository();
		RepositoryConnection connection = repository.getConnection();
		try {
			for (String[] columns : lines) {
				String drugbankId = columns[2];
				drugbankId = "<" + drugbankId + ">";
				StringBuilder query = new StringBuilder(
						"SELECT distinct ?x FROM <file://C:/fakepath/dailymed_dump.nt> WHERE { ?x ")
						.append(" <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/genericDrug> ")
						.append(drugbankId).append("}");
				logger.info("{}", query.toString());
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query.toString());
				TupleQueryResult result = tupleQuery.evaluate();
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfX = bindingSet.getValue("x");
					List<String> output = new ArrayList<String>();
					output.add(columns[0]);
					output.add(columns[1]);
					output.add(columns[2]);
					output.add(valueOfX.stringValue());
					csvWriter.writeNext(output.toArray(new String[output.size()]));
				}
				result.close();

			}
		} finally {
			csvReader.close();
			csvWriter.flush();
			csvWriter.close();
			connection.close();
		}
	}

	public void getDailymedIds() throws IOException, RepositoryException, QueryEvaluationException,
			MalformedQueryException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(FOLDER, BRAND_FILE)));
		List<String[]> lines = csvReader.readAll();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(FOLDER, DAILYMED_ID_FILE)));
		Repository repository = sesameRepositoryClient.getRepository();
		RepositoryConnection connection = repository.getConnection();
		try {
			for (String[] columns : lines) {

				Set<String> dailymedIds = new HashSet<String>();
				for (int i = 3; i < columns.length; i++) {
					String brandName = columns[i];
					StringBuilder query = new StringBuilder(
							"SELECT distinct ?x FROM <file://C:/fakepath/dailymed_dump.nt> WHERE { ?x <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/fullName> ?z . FILTER regex(?z,")
							.append("\"").append(brandName).append("\", \"i\") }");
					logger.info("{}", query.toString());
					TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query.toString());
					TupleQueryResult result = tupleQuery.evaluate();
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						Value valueOfX = bindingSet.getValue("x");
						dailymedIds.add(valueOfX.stringValue());
					}
					result.close();
				}
				for (String dailymedId : dailymedIds) {
					List<String> output = new ArrayList<String>();
					output.add(columns[0]);
					output.add(columns[1]);
					output.add(columns[2]);
					output.add(dailymedId);
					csvWriter.writeNext(output.toArray(new String[output.size()]));
				}
			}

		} finally {
			csvReader.close();
			csvWriter.flush();
			csvWriter.close();
			connection.close();
		}
	}

}
