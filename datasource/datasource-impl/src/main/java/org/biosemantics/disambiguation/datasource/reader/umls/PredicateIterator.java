package org.biosemantics.disambiguation.datasource.reader.umls;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.common.ConceptDetail;
import org.biosemantics.datasource.common.utils.ConceptDetailImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import au.com.bytecode.opencsv.CSVReader;

public class PredicateIterator implements Iterator<ConceptDetail> {

	private static final String GET_PREDICATES_SQL = "select distinct RL as RL from SRSTRE2";
	private final JdbcTemplate jdbcTemplate;
	private final Concept defaultDomain;
	private String predicateTSVFile = "";
	private Iterator<ConceptDetail> iterator;

	public PredicateIterator(JdbcTemplate jdbcTemplate, Concept domain) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.defaultDomain = domain;
	}

	public void setPredicateTSVFile(String predicateTSVFile) {
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
		FileReader fileReader = new FileReader(new File(predicateTSVFile));
		CSVReader csvReader = new CSVReader(fileReader, '\t');
		List<String[]> allLines = csvReader.readAll();
		if (allLines != null) {
			for (String[] line : allLines) {
				predicates.add(line[0].trim());
			}
		}
		Collection<ConceptDetail> predicateConcepts = new ArrayList<ConceptDetail>(predicates.size());
		for (String predicate : predicates) {
			String labelText = predicate.replace("_", " ");
			LabelImpl labelImpl = new LabelImpl(labelText, Language.EN);
			NotationImpl notationImpl = new NotationImpl(defaultDomain, predicate);
			ConceptImpl conceptImpl = new ConceptImpl();
			conceptImpl.addLabelByType(LabelType.PREFERRED, labelImpl);
			conceptImpl.addNotations(notationImpl);
			predicateConcepts.add(new ConceptDetailImpl(ConceptType.PREDICATE, conceptImpl));
		}
		iterator = predicateConcepts.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public ConceptDetail next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
