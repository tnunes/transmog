package org.biosemantics.disambiguation.datasource.umls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptSchemeRelationshipIterator implements Iterator<RelationshipDetail> {

	private int counter = 0;
	private final JdbcTemplate jdbcTemplate;
	private static final String GET_CONCEPT_SCHEME_RELATIONS_SQL = "select STY1, RL, STY2 from SRSTRE2 order by STY1";
	private List<String[]> relationships = new ArrayList<String[]>();

	public ConceptSchemeRelationshipIterator(JdbcTemplate jdbcTemplate, Map<String, Long> predicateMap) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void init() {
		jdbcTemplate.query(GET_CONCEPT_SCHEME_RELATIONS_SQL, new ResultSetExtractor<List<String[]>>() {
			@Override
			public List<String[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					relationships.add(new String[] { rs.getString("STY1"), rs.getString("STY2"), rs.getString("RL") });
				}
				return relationships;
			}

		});
	}

	@Override
	public boolean hasNext() {
		return counter < relationships.size();
	}

	@Override
	public RelationshipDetail next() {
		String[] relationship = relationships.get(counter);
		counter++;
		RelationshipDetail relationshipDetail = new RelationshipDetail(relationship[0], relationship[1],
				relationship[2]);
		return relationshipDetail;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
