package org.biosemantics.datasource.umls.concept;

import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

public class DomainIterator implements Iterator<Concept> {

	private static final String GET_ALL_DOMAIN_SQL = "SELECT RSAB, RCUI, VCUI, SON from MRSAB";
	private Iterator<Concept> iterator;
	private JdbcTemplate jdbcTemplate;
	private DomainResultSetExtractor domainResultSetExtractor;

	@Required
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Required
	public void setDomainResultSetExtractor(DomainResultSetExtractor domainResultSetExtractor) {
		this.domainResultSetExtractor = domainResultSetExtractor;
	}

	public void init() {
		iterator = jdbcTemplate.query(GET_ALL_DOMAIN_SQL, domainResultSetExtractor).iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Concept next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
