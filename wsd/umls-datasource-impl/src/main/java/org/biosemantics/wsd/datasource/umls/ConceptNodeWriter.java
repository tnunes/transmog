package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.ConceptType;
import org.biosemantics.wsd.domain.Notation;
import org.biosemantics.wsd.domain.NotationSourceConstant;
import org.biosemantics.wsd.repository.ConceptRepository;
import org.biosemantics.wsd.repository.NotationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

public class ConceptNodeWriter {

	public void setIgnoredCuiReader(IgnoredCuiReader ignoredCuiReader) {
		this.ignoredCuiReader = ignoredCuiReader;
	}

	@Transactional
	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);

		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_CUIS);
			int ctr = 0;
			while (rs.next()) {
				ctr++;
				String cui = rs.getString("CUI");
				if (ignoredCuiReader.isIgnored(cui)) {
					// ignoring
					continue;
				}
				Concept concept = conceptRepository
						.save(new Concept(UUID.randomUUID().toString(), ConceptType.CONCEPT));
				Notation notation = notationRepository.save(new Notation(NotationSourceConstant.UMLS, cui));
				concept.hasNotation(neo4jTemplate, notation, NOTATION_SOURCE);
				if (ctr % 100000 == 0) {
					logger.info("{}", ctr);
				}
			}
			rs.close();
		} finally {
			stmt.close();
			connection.close();
		}

	}

	private static final String GET_ALL_CUIS = "select distinct(CUI) from MRCONSO";
	private static final Logger logger = LoggerFactory.getLogger(ConceptNodeWriter.class);
	@Autowired
	private DataSource dataSource;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	private IgnoredCuiReader ignoredCuiReader;
	private static final String NOTATION_SOURCE = "UMLS_MRCONSO";
}
