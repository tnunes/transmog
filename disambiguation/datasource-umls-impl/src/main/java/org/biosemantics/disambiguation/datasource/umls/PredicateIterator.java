package org.biosemantics.disambiguation.datasource.umls;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	List<String[]> allPredicates;
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
			String[] predicateArray = new String[2];
			predicateArray[0] = predicate;
			predicateArray[1] = predicate.replace("_", " ");
			allPredicates.add(predicateArray);
		}
		FileReader fileReader = new FileReader(new File(predicateTSVFile));
		CSVReader csvReader = new CSVReader(fileReader, '\t');
		allPredicates = csvReader.readAll();
	}

	@Override
	public boolean hasNext() {
		return counter < allPredicates.size();
	}

	@Override
	public ConceptDetail next() {
		String[] predicate = allPredicates.get(counter);
		counter++;
		ConceptDetail conceptDetail = new ConceptDetail();
		conceptDetail.addLabel(new LabelDetail(predicate[1], Language.EN, LabelType.PREFERRED));
		conceptDetail.addNotation(new NotationDetail(predicate[0], UmlsDomain.getDefaultDomain().name()));
		return conceptDetail;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
