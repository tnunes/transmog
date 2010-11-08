package org.biosemantics.disambiguation.datasource.umls;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.datasource.umls.DomainIterator.UmlsDomain;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import au.com.bytecode.opencsv.CSVReader;

public class PredicateIterator implements Iterator<ConceptDetail> {

	private static final String GET_PREDICATES_SQL = "select distinct RL as RL from SRSTRE2";
	private final JdbcTemplate jdbcTemplate;
	private final String predicateTSVFile;
	// might be an overlap between database and text file, we do not want 2 different predicate concepts for the same
	// predicate
	Set<String> allPredicates = new HashSet<String>();
	Iterator<String> predicateIterator;
	int counter = 0;

	public PredicateIterator(JdbcTemplate jdbcTemplate, String predicateTSVFile) {
		this.jdbcTemplate = jdbcTemplate;
		this.predicateTSVFile = predicateTSVFile;
	}

	public void init() throws IOException {
		Collection<String> predicates = jdbcTemplate.query(GET_PREDICATES_SQL,
				new ResultSetExtractor<Collection<String>>() {
					private List<String> predicates = new ArrayList<String>();

					@Override
					public Collection<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
						while (rs.next()) {
							predicates.add(rs.getString(1).trim());
						}
						return predicates;
					}
				});
		for (String predicate : predicates) {
			allPredicates.add(predicate.trim());
		}
		FileReader fileReader = new FileReader(new File(predicateTSVFile));
		CSVReader csvReader = new CSVReader(fileReader, '\t');
		List<String[]> predicateList = csvReader.readAll();
		// preprocess any blank spaces: generally a side effect of reading from files
		for (String[] strings : predicateList) {
			allPredicates.add(strings[0].trim());
		}
		predicateIterator = allPredicates.iterator();
	}

	@Override
	public boolean hasNext() {
		return predicateIterator.hasNext();
	}

	@Override
	public ConceptDetail next() {
		String code = predicateIterator.next();
		String text = code.replace("_", " ");
		ConceptDetail conceptDetail = new ConceptDetail();
		conceptDetail.addLabel(new LabelDetail(text, Language.EN, LabelType.PREFERRED));
		conceptDetail.addNotation(new NotationDetail(code, UmlsDomain.getDefaultDomain().name()));
		return conceptDetail;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove is unsupported for this iterator");

	}

}
