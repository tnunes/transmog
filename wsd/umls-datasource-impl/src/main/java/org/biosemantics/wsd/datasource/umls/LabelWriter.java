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
import org.biosemantics.wsd.repository.ConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

public class LabelWriter {

	@Transactional
	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		Connection innerConnection = dataSource.getConnection();
		PreparedStatement innerStatement = connection.prepareStatement(GET_ALL_CUI);
		stmt.setFetchSize(Integer.MIN_VALUE);
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_SUIS);
			while (rs.next()) {
				String sui = rs.getString("SUI");
				innerStatement.setString(1, sui);
				ResultSet innerRs = innerStatement.executeQuery();
				while (innerRs.next()) {
					Label label = null;
					String text = innerRs.getString("STR");
					String language = rs.getString("LAT");
					String cui = innerRs.getString("CUI");
					String ts = rs.getString("TS");
					String isPref = rs.getString("ISPREF");
					String stt = rs.getString("STT");// TS, ISPREF, STT
					int ctr = 0;
					if (ctr == 0) {
						label = new Label(text, language);
					}
					ctr++;
					Concept concept = conceptRepository.getConceptById(cui);
					concept.hasLabel(neo4jTemplate, label, getLabelType(ts, isPref, stt), LABEL_SOURCE);
				}
				innerRs.close();
			}
			rs.close();
		} finally {
			innerStatement.close();
			innerConnection.close();
			stmt.close();
			connection.close();
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
	private static final Logger logger = LoggerFactory.getLogger(ConceptNodeWriter.class);
	@Autowired
	private DataSource dataSource;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	private static final String LABEL_SOURCE = "UMLS_2011_AA | MRCONSO";
}
