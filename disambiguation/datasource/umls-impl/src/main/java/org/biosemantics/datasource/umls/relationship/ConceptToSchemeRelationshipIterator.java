package org.biosemantics.datasource.umls.relationship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptToSchemeRelationshipIterator implements Iterator<UmlsRelationship> {

	private final JdbcTemplate jdbcTemplate;
	private static final String GET_CONCEPT_TO_SCHEME_RELATIONS_SQL = "SELECT CUI, STY FROM MRSTY ";
	private List<String[]> allCuis = new ArrayList<String[]>();
	private Iterator<String[]> iterator;
	private Logger logger = LoggerFactory.getLogger(ConceptToSchemeRelationshipIterator.class);

	public ConceptToSchemeRelationshipIterator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void init() {
		logger.info("init() called getting all concepts-to-concept scheme relationships, making sql query {} ",
				GET_CONCEPT_TO_SCHEME_RELATIONS_SQL);
		jdbcTemplate.query(GET_CONCEPT_TO_SCHEME_RELATIONS_SQL, new ResultSetExtractor<List<String[]>>() {
			@Override
			public List<String[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					allCuis.add(new String[] { rs.getString("CUI"), rs.getString("STY") });
				}
				return allCuis;
			}

		});
		this.iterator = allCuis.iterator();
		logger.info("{} results returned for sql query", allCuis.size());
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public UmlsRelationship next() {
		String[] strings = iterator.next();
		UmlsRelationship umlsRelationship = new UmlsRelationship(strings[0], null, strings[1]);
		return umlsRelationship;
	}

	public void destroy() {
		logger.info("destroy called setting datamembers to null explicitly");
		iterator = null;
		allCuis = null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
