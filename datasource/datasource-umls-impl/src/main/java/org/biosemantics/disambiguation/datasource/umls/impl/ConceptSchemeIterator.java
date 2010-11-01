package org.biosemantics.disambiguation.datasource.umls.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

public class ConceptSchemeIterator implements Iterator<ConceptDetail> {

	private JdbcTemplate jdbcTemplate;
	private Concept defaultDomain;
	private Iterator<ConceptDetail> iterator;
	private static final String GET_ALL_CONCEPT_SCHEME = "select  UI , STY_RL from SRDEF where RT='STY'";

	public ConceptSchemeIterator(JdbcTemplate jdbcTemplate, Concept defaultDomain) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.defaultDomain = defaultDomain;
	}

	public void init() {
		Collection<ConceptDetail> conceptSchemes = jdbcTemplate.query(GET_ALL_CONCEPT_SCHEME,
				new ResultSetExtractor<Collection<ConceptDetail>>() {
					private Collection<ConceptDetail> conceptSchemes = new ArrayList<ConceptDetail>();

					@Override
					public Collection<ConceptDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
						String labelText = rs.getString(2);
						LabelImpl labelImpl = new LabelImpl(labelText, Language.EN);
						NotationImpl notationImpl = new NotationImpl(defaultDomain, rs.getString(1));
						ConceptImpl conceptImpl = new ConceptImpl();
						conceptImpl.addLabelByType(LabelType.PREFERRED, labelImpl);
						conceptImpl.addNotations(notationImpl);
						conceptSchemes.add(new ConceptDetailImpl(ConceptType.CONCEPT_SCHEME, conceptImpl));
						return conceptSchemes;
					}
				});
		iterator = conceptSchemes.iterator();
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
		throw new UnsupportedOperationException("remove is not supported");

	}
}
