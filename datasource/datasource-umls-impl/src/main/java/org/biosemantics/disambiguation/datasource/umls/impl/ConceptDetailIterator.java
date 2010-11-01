package org.biosemantics.disambiguation.datasource.umls.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.datasource.common.ConceptDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConceptDetailIterator implements Iterator<ConceptDetail> {

	private JdbcTemplate jdbcTemplate;
	private Concept defaultDomain;

	private PredicateIterator predicateIterator;
	private boolean predicatesAvailable;
	private DomainIterator domainIterator;
	private boolean domainsAvailable;
	private ConceptSchemeIterator conceptSchemeIterator;
	private boolean conceptSchemesAvailable;
	private ConceptIterator conceptIterator;
	private boolean conceptsAvailable;

	private static final Logger logger = LoggerFactory.getLogger(ConceptDetailIterator.class);

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void init() throws IOException, SQLException {
		predicateIterator = new PredicateIterator(jdbcTemplate, defaultDomain);
		predicateIterator.init();
		domainIterator = new DomainIterator();
		domainIterator.init();
		conceptSchemeIterator = new ConceptSchemeIterator(jdbcTemplate, defaultDomain);
		conceptSchemeIterator.init();
		conceptIterator = new ConceptIterator(jdbcTemplate, defaultDomain);
		conceptIterator.init();
	}

	@Override
	public boolean hasNext() {
		predicatesAvailable = predicateIterator.hasNext();
		if (predicatesAvailable) {
			return true;
		} else {
			domainsAvailable = domainIterator.hasNext();
			if (domainsAvailable) {
				return true;
			} else {
				conceptSchemesAvailable = conceptSchemeIterator.hasNext();
				if (conceptSchemesAvailable) {
					return true;
				} else {
					conceptsAvailable = conceptIterator.hasNext();
				}
				if (conceptsAvailable) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	@Override
	public ConceptDetail next() {
		if (predicatesAvailable) {
			return predicateIterator.next();
		} else if (domainsAvailable) {
			return domainIterator.next();
		} else if (conceptSchemesAvailable) {
			return conceptSchemeIterator.next();
		} else if (conceptsAvailable) {
			return conceptIterator.next();
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}
