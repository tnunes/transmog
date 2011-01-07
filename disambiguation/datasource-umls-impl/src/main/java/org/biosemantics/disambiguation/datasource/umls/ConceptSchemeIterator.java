package org.biosemantics.disambiguation.datasource.umls;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.disambiguation.datasource.umls.DomainIterator.UmlsDomain;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptSchemeIterator implements Iterator<ConceptDetail> {

	private int counter = 0;

	private final JdbcTemplate jdbcTemplate;
	private static String GET_ALL_CONCEPT_SCHEME_SQL = "select STY_RL, UI from SRDEF where RT='STY'";

	private List<String[]> conceptSchemes = new ArrayList<String[]>();

	public ConceptSchemeIterator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = checkNotNull(jdbcTemplate);
	}

	public void init() {
		jdbcTemplate.query(GET_ALL_CONCEPT_SCHEME_SQL, new ResultSetExtractor<List<String[]>>() {
			@Override
			public List<String[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					String[] results = new String[] { rs.getString("STY_RL"), rs.getString("UI") };
					conceptSchemes.add(results);
				}
				return conceptSchemes;
			}
		});
	}

	@Override
	public boolean hasNext() {
		return counter < conceptSchemes.size();
	}

	@Override
	public ConceptDetail next() {
		String[] conceptScheme = conceptSchemes.get(counter);
		counter++;// readability
		ConceptDetail conceptDetail = new ConceptDetail();
		conceptDetail.addLabel(new LabelDetail(conceptScheme[0], LanguageImpl.EN, LabelType.PREFERRED));
		conceptDetail.addNotation(new NotationDetail(conceptScheme[1], UmlsDomain.getDefaultDomain().name()));
		return conceptDetail;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}
