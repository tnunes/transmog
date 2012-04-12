package org.biosemantics.eviped.lodd.exposure.dailymed;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.biosemantics.wsd.metamap.MetamapClient;
import org.biosemantics.wsd.utility.sesame.SesameRepositoryClient;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import au.com.bytecode.opencsv.CSVReader;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class ExposureDailymedClient {
	private static final String DAILYMED_ID_FILE = "dailymed_ids.csv";
	private static final String FOLDER = "/Users/bhsingh/code/data/EVIPED/Experiment1";
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

	private String outputFile;
	private ObjectContainer db;
	private static final Logger logger = LoggerFactory.getLogger(ExposureDailymedClient.class);

	@Autowired
	private SesameRepositoryClient sesameRepositoryClient;
	@Autowired
	private MetamapClient metamapClient;

	@Required
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void init() {
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), outputFile);
	}

	public void writeAll() throws Exception {
		CSVReader csvReader = new CSVReader(new FileReader(new File(FOLDER, DAILYMED_ID_FILE)));
		List<String[]> lines = csvReader.readAll();
		try {
			for (String[] columns : lines) {
				String drugname = columns[3].trim();
				getIndexingResults(drugname);
			}
		} finally {
			db.commit();
			db.close();
		}

	}

	private void getIndexingResults(String drugname) throws Exception {
		Repository repository = sesameRepositoryClient.getRepository();
		RepositoryConnection connection = repository.getConnection();
		int ctr = 0;
		try {
			drugname = "<" + drugname + ">";
			logger.info("indexing {} drug", drugname);
			List<DailymedName> dailymedNames = new ArrayList<DailymedName>();
			List<DailymedPredicate> dailymedPredicates = new ArrayList<DailymedPredicate>();
			for (String predicate : names) {
				StringBuilder nameQuery = new StringBuilder("select ?x where {").append(drugname).append(" ")
						.append(predicate).append(" ").append(" ?x}");
				logger.trace("{}", nameQuery);
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, nameQuery.toString());
				TupleQueryResult result = tupleQuery.evaluate();
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfX = bindingSet.getValue("x");
					dailymedNames.add(new DailymedName(predicate, valueOfX.stringValue()));
				}
				result.close();
			}
			for (String predicate : predicates) {
				StringBuilder nameQuery = new StringBuilder("select ?x where {").append(drugname).append(" ")
						.append(predicate).append(" ").append(" ?x}");
				logger.trace("{}", nameQuery);
				TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, nameQuery.toString());
				TupleQueryResult result = tupleQuery.evaluate();
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value valueOfX = bindingSet.getValue("x");

					logger.trace("INDEXING TEXT: {}", valueOfX.stringValue());
					List<Result> results = metamapClient.getResults(valueOfX.stringValue());
					List<Ev> evs = new ArrayList<Ev>();
					for (Result metamapResult : results) {
						List<Utterance> utterances = metamapResult.getUtteranceList();
						if (utterances.size() > 0) {
							for (Utterance utterance : metamapResult.getUtteranceList()) {
								List<PCM> pcms = utterance.getPCMList();
								if (pcms.size() > 0) {
									for (PCM pcm : pcms) {
										List<Mapping> maps = pcm.getMappingList();
										if (maps.size() > 0) {
											for (Mapping map : pcm.getMappingList()) {
												for (Ev mapEv : map.getEvList()) {
													evs.add(mapEv);
												}
											}
										}
									}
								}
							}
						}
					}
					logger.info("EVS : {}", evs.size());
					dailymedPredicates.add(new DailymedPredicate(predicate, evs));
				}
				result.close();
			}
			DailymedIndexingResult dailymedIndexingResult = new DailymedIndexingResult(drugname, dailymedNames,
					dailymedPredicates);
			db.store(dailymedIndexingResult);
			logger.info("indexing drug {} finished", ++ctr);

		} finally {

			connection.close();
		}

	}

	public void destroy() {
		db.close();
	}

}
