package org.biosemantics.disambiguation.datasource.reader.umls;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.datasource.common.ConceptDetail;
import org.biosemantics.datasource.common.DataSourceRdbmsReader;
import org.springframework.jdbc.core.JdbcTemplate;

public class UmlsDataSourceRdbmsReader implements DataSourceRdbmsReader {

	private JdbcTemplate jdbcTemplate;
	private Concept defaultDomain;

	@Override
	public void init() {
	}

	@Override
	public void destroy() {

	}

	@Override
	public Iterator<ConceptDetail> getConcepts() {
		ConceptDetailIterator conceptDetailIterator = new ConceptDetailIterator();
		conceptDetailIterator.setJdbcTemplate(jdbcTemplate);
		conceptDetailIterator.setDefaultDomain(defaultDomain);
		try {
			conceptDetailIterator.init();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conceptDetailIterator;
	}

	@Override
	public Iterator<Relationship> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void setDefaultDomain(Concept domain) {
		this.defaultDomain = domain;
	}

}
