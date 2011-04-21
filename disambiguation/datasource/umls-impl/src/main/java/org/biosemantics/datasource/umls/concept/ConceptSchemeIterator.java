package org.biosemantics.datasource.umls.concept;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Note.NoteType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NoteImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptSchemeIterator implements Iterator<Concept> {

	private final JdbcTemplate jdbcTemplate;
	private static String GET_ALL_CONCEPT_SCHEME_SQL = "select STY_RL, UI, DEF from SRDEF where RT='STY'";
	private static final Logger logger = LoggerFactory.getLogger(ConceptSchemeIterator.class);

	private List<String[]> conceptSchemes = new ArrayList<String[]>();
	private Iterator<String[]> iterator;

	public ConceptSchemeIterator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void init() {
		logger.info("init called, geting all concept schemes SQL: {}", GET_ALL_CONCEPT_SCHEME_SQL);
		jdbcTemplate.query(GET_ALL_CONCEPT_SCHEME_SQL, new ResultSetExtractor<List<String[]>>() {
			@Override
			public List<String[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					String[] results = new String[] { rs.getString("STY_RL"), rs.getString("UI"), rs.getString("DEF") };
					conceptSchemes.add(results);
				}
				return conceptSchemes;
			}
		});
		iterator = conceptSchemes.iterator();
		logger.info("{} concept schemes retrieved", conceptSchemes.size());
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Concept next() {
		String[] strings = iterator.next();
		ConceptLabelImpl conceptLabelImpl = new ConceptLabelImpl(LanguageImpl.EN, strings[0], LabelType.PREFERRED);
		NotationImpl notationImpl = new NotationImpl(UmlsUtils.DEFAULT_SAB, strings[1]);
		NoteImpl noteImpl = new NoteImpl(NoteType.DEFINITION, LanguageImpl.EN, strings[2]);
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptImpl.addConceptLabels(conceptLabelImpl);
		conceptImpl.addNotations(notationImpl);
		conceptImpl.addNotes(noteImpl);
		return conceptImpl;
	}

	public void destroy() {
		logger.info("destroy called, resetting members to null explicitly");
		iterator = null;
		conceptSchemes = null;

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
