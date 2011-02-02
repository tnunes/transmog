package org.biosemantics.disambiguation.datasource.umls;

import java.util.Collection;
import java.util.Iterator;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class UmlsDomainIterator implements Iterator<Concept> {

	private static final String GET_ALL_SAB = "select * from MRSAB";
	private static final Logger logger = LoggerFactory.getLogger(UmlsDomainIterator.class);
	private JdbcTemplate jdbcTemplate;
	private DomainResultSetExtractor domainResultSetExtractor;
	private Iterator<Concept> iterator;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void init() {
		Collection<Concept> domains = jdbcTemplate.query(GET_ALL_SAB, domainResultSetExtractor);
		logger.info("{} domains found", domains.size());
		iterator = domains.iterator();

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
		throw new UnsupportedOperationException();

	}

}
