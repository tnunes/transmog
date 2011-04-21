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

public class ConceptSchemeRelationshipIterator implements Iterator<UmlsRelationship> {

	private final JdbcTemplate jdbcTemplate;
	private static final String GET_CONCEPT_SCHEME_RELATIONS_SQL = "select STY1, RL, STY2 from SRSTRE2  where STY1 != STY2 order by STY1";
	private List<UmlsRelationship> relationships = new ArrayList<UmlsRelationship>();
	private Iterator<UmlsRelationship> iterator;
	private Logger logger = LoggerFactory.getLogger(ConceptSchemeRelationshipIterator.class);

	public ConceptSchemeRelationshipIterator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void init() {
		logger.info("init() called getting all concept scheme relationships, SQL {} ",
				GET_CONCEPT_SCHEME_RELATIONS_SQL);
		jdbcTemplate.query(GET_CONCEPT_SCHEME_RELATIONS_SQL, new ResultSetExtractor<List<UmlsRelationship>>() {
			@Override
			public List<UmlsRelationship> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					relationships.add(new UmlsRelationship(rs.getString("STY1"), rs.getString("RL"), rs
							.getString("STY2")));
				}
				return relationships;
			}

		});
		logger.info("got results for query");
		this.iterator = relationships.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public UmlsRelationship next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	public void destroy() {
		logger.info("destroy called setting datamembers to null explicitly");
		iterator = null;
		relationships = null;
	}

}
