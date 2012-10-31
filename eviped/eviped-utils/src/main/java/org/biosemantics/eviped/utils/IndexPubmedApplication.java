package org.biosemantics.eviped.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.biosemantics.common.utility.DatabaseUtility;
import org.erasmusmc.data_mining.ontology.api.Language;
import org.erasmusmc.data_mining.peregrine.api.IndexingResult;
import org.erasmusmc.data_mining.peregrine.api.Peregrine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

import au.com.bytecode.opencsv.CSVWriter;

public class IndexPubmedApplication {

	private static final String[] CONTEXTS = new String[] { "eviped-utils-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(IndexPubmedApplication.class);
	private ApplicationContext applicationContext;
	private Peregrine peregrine;

	public IndexPubmedApplication() {
		applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		peregrine = applicationContext.getBean(Peregrine.class);
	}

	public List<IndexingResult> index(String text) {
		return peregrine.index(text, Language.EN);
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		IndexPubmedApplication obj = new IndexPubmedApplication();
		DatabaseUtility baseDatabaseConnector = new DatabaseUtility();
		Connection connection = baseDatabaseConnector.getConnection();
		Statement statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE); // Inter.MIN_VALUE <- and ONLY this value, 1,5 or 100 won't fix your
													// problem.
		ResultSet rs = statement.executeQuery("SELECT pmid, abstract_text from medline_abstract");
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File("/Users/bhsingh/desktop/out.txt")));
		int ctr = 0;
		while (rs.next()) {
			int pmid = rs.getInt("pmid");
			String text = rs.getString("abstract_text");
			List<IndexingResult> indexingResults = obj.index(text);
			if (!CollectionUtils.isEmpty(indexingResults)) {
				csvWriter.writeNext(new String[] { "" + pmid });
				csvWriter.flush();
			}
			if (++ctr % 1000 == 0) {
				logger.info("parsed abstracts: {}", ctr);
			}
		}
		csvWriter.close();
		rs.close();
		statement.close();
		connection.close();
	}
}
