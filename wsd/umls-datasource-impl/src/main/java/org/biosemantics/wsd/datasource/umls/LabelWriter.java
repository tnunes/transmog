package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Label;
import org.biosemantics.wsd.domain.LabelType;
import org.biosemantics.wsd.domain.Notation;
import org.biosemantics.wsd.repository.LabelRepository;
import org.biosemantics.wsd.repository.NotationRepository;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class LabelWriter {

	public void setIgnoredCuiReader(IgnoredCuiReader ignoredCuiReader) {
		this.ignoredCuiReader = ignoredCuiReader;
	}

	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		Connection innerConnection = dataSource.getConnection();
		PreparedStatement innerStatement = innerConnection.prepareStatement(GET_ALL_CUI);
		stmt.setFetchSize(Integer.MIN_VALUE);
		int counter = 0;
		Transaction tx = neo4jTemplate.getGraphDatabaseService().beginTx();
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_SUIS);
			while (rs.next()) {
				String sui = rs.getString("SUI");
				innerStatement.setString(1, sui);
				ResultSet innerRs = innerStatement.executeQuery();
				int innerCtr = 0;
				Label label = null;
				while (innerRs.next()) {
					String text = innerRs.getString("STR");
					String language = innerRs.getString("LAT");
					String cui = innerRs.getString("CUI");
					String ts = innerRs.getString("TS");
					String isPref = innerRs.getString("ISPREF");
					String stt = innerRs.getString("STT");// TS, ISPREF, STT
					if (ignoredCuiReader.isIgnored(cui)) {
						// ignored
						continue;
					}
					if (innerCtr == 0) {
						label = new Label(text, language);
						labelRepository.save(label);
					}
					innerCtr++;
					Notation notation = notationRepository.findByPropertyValue("code", cui);
					Concept concept = notationRepository.getRelatedConcept(notation);
					concept.hasLabel(neo4jTemplate, label, getLabelType(ts, isPref, stt), LABEL_SOURCE);
				}
				innerRs.close();
				// hundred thousand
				if (++counter % 100000 == 0) {
					tx.success();
					tx.finish();
					tx = neo4jTemplate.getGraphDatabaseService().beginTx();
					logger.info("ctr:{}", counter);
				}
			}
			rs.close();
		} finally {
			innerStatement.close();
			innerConnection.close();
			stmt.close();
			connection.close();
			if (tx != null) {
				tx.success();
				tx.finish();
				logger.info("committed labels");
			}
		}
	}

	private static LabelType getLabelType(String ts, String isPref, String stt) {
		if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
			return LabelType.PREFERRED;
		} else {
			return LabelType.ALTERNATE;
		}
	}

	private static final String GET_ALL_SUIS = "select distinct(SUI) from MRCONSO";
	private static final String GET_ALL_CUI = "select * from MRCONSO where SUI = ?";
	private static final Logger logger = LoggerFactory.getLogger(LabelWriter.class);
	@Autowired
	private DataSource dataSource;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private LabelRepository labelRepository;
	private IgnoredCuiReader ignoredCuiReader;
	private static final String LABEL_SOURCE = "UMLS_2011AA_MRCONSO";
}
