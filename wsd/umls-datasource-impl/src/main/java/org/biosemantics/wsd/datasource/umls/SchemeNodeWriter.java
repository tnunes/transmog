package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.ConceptType;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class SchemeNodeWriter {
	private static final String GET_ALL_UI = "select UI from SRDEF where RT=\"STY\"";
	private static final Logger logger = LoggerFactory.getLogger(SchemeNodeWriter.class);
	@Autowired
	private DataSource dataSource;
	@Autowired
	private ConceptRepository conceptRepository;

	@Transactional
	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);

		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_UI);
			int ctr = 0;
			while (rs.next()) {
				String ui = rs.getString("UI");
				conceptRepository.save(new Concept(ui, ConceptType.CONCEPT_SCHEME));
				logger.info("{}", ++ctr);
			}
			rs.close();
		} finally {
			stmt.close();
			connection.close();
		}

	}

}
