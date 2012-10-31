package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Notation;
import org.biosemantics.wsd.repository.NotationRepository;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class SchemeToConceptRelationshipWriter {

	public void setIgnoredCuiReader(IgnoredCuiReader ignoredCuiReader) {
		this.ignoredCuiReader = ignoredCuiReader;
	}

	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		Transaction tx = neo4jTemplate.getGraphDatabaseService().beginTx();
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_RLSP_SQL);
			int ctr = 0;

			while (rs.next()) {
				ctr++;
				String cui = rs.getString("CUI");
				if (ignoredCuiReader.isIgnored(cui)) {
					continue;
				}
				String tui = rs.getString("TUI");
				Notation notation = notationRepository.findByPropertyValue("code", cui);
				Concept concept = notationRepository.getRelatedConcept(notation);
				Notation otherNotation = notationRepository.findByPropertyValue("code",tui);
				Concept scheme = notationRepository.getRelatedConcept(otherNotation);
				concept.inScheme(neo4jTemplate, scheme, 100, null, UMLS_MRSTY);
			}
			// 1 million
			if (ctr % 100000 == 0) {
				tx.success();
				tx.finish();
				tx = neo4jTemplate.getGraphDatabaseService().beginTx();
				logger.info("ctr:{}", ctr);
			}
			rs.close();
		} finally {
			stmt.close();
			connection.close();
			tx.success();
			tx.finish();
		}

	}

	private static final String UMLS_MRSTY = "UMLS_201AA_MRSTY";
	@Autowired
	private DataSource dataSource;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	private IgnoredCuiReader ignoredCuiReader;

	private static final String GET_ALL_RLSP_SQL = "select CUI, TUI from MRSTY";// 2,151,295
	private static final Logger logger = LoggerFactory.getLogger(SchemeToConceptRelationshipWriter.class);

}
