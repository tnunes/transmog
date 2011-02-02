package org.biosemantics.disambiguation.datasource.umls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UmlsConceptReaderDatabaseImpl implements Iterator<Concept> {

	private static final String GET_CONCEPT_DETAILS_SQL = "select CUI, SUI, TS, ISPREF, STT, LAT, STR, SAB, CODE from MRCONSO WHERE CUI = ?";
	private static final String GET_CONCEPT_COUNT_SQL = "select distinct(CUI) from MRCONSO";
	private static final Logger logger = LoggerFactory.getLogger(UmlsConceptReaderDatabaseImpl.class);

	private JdbcTemplate jdbcTemplate;
	private ArrayList<String> distinctCuis = new ArrayList<String>();
	private int size = 0;
	private boolean isInit;
	private int ctr;
	private ConceptResultSetExtractor conceptResultSetExtractor;

	@Required
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setConceptResultSetExtractor(ConceptResultSetExtractor conceptResultSetExtractor) {
		this.conceptResultSetExtractor = conceptResultSetExtractor;
	}

	public void init() {
		size = jdbcTemplate.query(GET_CONCEPT_COUNT_SQL, new ResultSetExtractor<Collection<String>>() {
			@Override
			public Collection<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					distinctCuis.add(rs.getString(1));
				}
				return distinctCuis;
			}
		}).size();
		logger.info("total {} distinct concepts found in MRCONSO table", size);
		isInit = true;
	}

	@Override
	public boolean hasNext() {
		return ctr <= size;
	}

	@Override
	public Concept next() {
		if (isInit) {
			final String cui = distinctCuis.get(ctr);
			ctr++;// readability
			return jdbcTemplate.query(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(GET_CONCEPT_DETAILS_SQL);
					ps.setString(1, cui);
					return ps;
				}
			}, conceptResultSetExtractor);
		}
		throw new IllegalStateException("init not called before read");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove is unsupported");

	}

}
