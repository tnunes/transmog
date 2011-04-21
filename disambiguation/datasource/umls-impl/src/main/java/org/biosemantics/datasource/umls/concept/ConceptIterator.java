package org.biosemantics.datasource.umls.concept;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class ConceptIterator implements Iterator<Concept> {

	private JdbcTemplate jdbcTemplate;
	private Iterator<String> iterator;
	private CuiDetailsResultSetExtractor cuiDetailsResultSetExtractor;
	private Collection<String> allCuis;
	private static final String GET_ALL_CUI_SQL = "select DISTINCT(CUI) from MRCONSO";
	private static final String GET_CUI_DETAILS_SQL = "select CUI, SUI, TS, ISPREF, STT, LAT, STR, SAB, CODE from MRCONSO WHERE CUI = ?";
	private static final Logger logger = LoggerFactory.getLogger(ConceptIterator.class);

	@Required
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Required
	public void setCuiDetailsResultSetExtractor(CuiDetailsResultSetExtractor cuiDetailsResultSetExtractor) {
		this.cuiDetailsResultSetExtractor = cuiDetailsResultSetExtractor;
	}

	public void init() {
		logger.info("init() called getting all cuis, SQL: {}", GET_ALL_CUI_SQL);
		allCuis = jdbcTemplate.query(GET_ALL_CUI_SQL, new CuiCountResultSetExtractor());
		iterator = allCuis.iterator();
		logger.info("all cuis size is {}", allCuis.size());
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Concept next() {
		Concept concept = jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(GET_CUI_DETAILS_SQL);
				ps.setString(1, iterator.next());
				return ps;
			}
		}, cuiDetailsResultSetExtractor);
		return concept;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	public void destroy() {
		logger.info("destroy called setting datamembers to null explicitly");
		iterator = null;
		allCuis = null;
	}

}
