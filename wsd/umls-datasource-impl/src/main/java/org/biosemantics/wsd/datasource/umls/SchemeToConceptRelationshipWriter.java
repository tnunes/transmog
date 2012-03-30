package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class SchemeToConceptRelationshipWriter {

	private static final String UMLS_MRSTY = "UMLS_2011_AA | MRSTY";
	@Autowired
	private DataSource dataSource;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	private static final String GET_ALL_RLSP_SQL = "select CUI, TUI from MRSTY";// 2,151,295
	private static final Logger logger = LoggerFactory.getLogger(SchemeToConceptRelationshipWriter.class);

	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		Transaction tx = neo4jTemplate.beginTx();
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_RLSP_SQL);
			int ctr = 0;

			while (rs.next()) {
				String cui = rs.getString("CUI1");
				String tui = rs.getString("CUI2");
				Concept concept = conceptRepository.getConceptById(cui);
				Concept scheme = conceptRepository.getConceptById(tui);
				concept.inScheme(neo4jTemplate, scheme, 100, null, UMLS_MRSTY);
			}
			// 1 million
			if (++ctr % 1000000 == 0) {
				tx.success();
				tx.finish();
				tx = neo4jTemplate.beginTx();
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

}
