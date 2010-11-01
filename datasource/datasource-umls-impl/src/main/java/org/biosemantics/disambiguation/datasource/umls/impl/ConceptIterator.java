package org.biosemantics.disambiguation.datasource.umls.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.datasource.common.ConceptDetail;
import org.biosemantics.datasource.common.utils.ConceptDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptIterator implements Iterator<ConceptDetail> {

	private JdbcTemplate jdbcTemplate;
	private Concept defaultDomain;
	private static final String GET_CONCEPT_DETAILS_SQL = "select CUI, SUI, TS, ISPREF, STT, LAT, STR, SAB, CODE from MRCONSO WHERE CUI = ?";
	private static final String GET_CONCEPT_COUNT_SQL = "select distinct(CUI) from MRCONSO";

	private static final Logger logger = LoggerFactory.getLogger(ConceptIterator.class);
	private ArrayList<String> distinctCuis = new ArrayList<String>();
	private int ctr = 0;
	// so we don't have to call it for all hasnext() calls
	private int size = 0;

	public ConceptIterator(JdbcTemplate jdbcTemplate, Concept defaultDomain) {
		this.jdbcTemplate = jdbcTemplate;
		this.defaultDomain = defaultDomain;
	}

	public void init() throws SQLException {
		size = jdbcTemplate.query(GET_CONCEPT_COUNT_SQL, new ResultSetExtractor<Collection<String>>() {
			@Override
			public Collection<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					distinctCuis.add(rs.getString(1));
				}
				return distinctCuis;
			}
		}).size();
	}

	@Override
	public boolean hasNext() {
		return ctr < size;
	}

	@Override
	public ConceptDetail next() {
		final String cui = distinctCuis.get(ctr++);
		Concept concept = jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(GET_CONCEPT_DETAILS_SQL);
				ps.setString(1, cui);
				return ps;
			}
		}, new ConceptResultSetExtractor(defaultDomain));
		return new ConceptDetailImpl(ConceptType.CONCEPT, concept);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
