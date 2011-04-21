package org.biosemantics.datasource.umls.concept;

import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import au.com.bytecode.opencsv.CSVReader;

public class PredicateIterator implements Iterator<String> {

	private static final String GET_CONCEPT_SCHEME_PREDICATES_SQL = "select distinct RL as RL from SRSTRE2";
	private JdbcTemplate jdbcTemplate;
	private String predicateTSVFile;
	// might be an overlap between database and text file, we do not want 2 different predicate concepts for the same
	// predicate
	Set<String> allPredicates = new HashSet<String>();
	Iterator<String> iterator;

	@Required
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Required
	public void setPredicateTSVFile(String predicateTSVFile) {
		this.predicateTSVFile = predicateTSVFile;
	}

	public void init() throws IOException {
		jdbcTemplate.query(GET_CONCEPT_SCHEME_PREDICATES_SQL, new ResultSetExtractor<Collection<String>>() {
			@Override
			public Collection<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					allPredicates.add(rs.getString("RL"));
				}
				return allPredicates;
			}

		});
		List<String[]> otherPredicates = readTsvFileContents();
		for (String[] row : otherPredicates) {
			// using the values with "_"
			allPredicates.add(row[0].trim());
		}
		iterator = allPredicates.iterator();
	}

	private List<String[]> readTsvFileContents() throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(predicateTSVFile), '\t');
		return csvReader.readAll();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
