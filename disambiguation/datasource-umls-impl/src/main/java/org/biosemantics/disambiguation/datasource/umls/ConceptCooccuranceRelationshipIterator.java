package org.biosemantics.disambiguation.datasource.umls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptCooccuranceRelationshipIterator implements Iterator<RelationshipDetail> {

	private static final String GET_CO_OCCURANCE_RLSP_SQL = "select CUI1, CUI2, COF from MRCOC";
	private static final Logger logger = LoggerFactory.getLogger(ConceptFactualRelationshipIterator.class);
	private ResultSet resultSet;
	private DataSource dataSource;
	private Statement statement;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void init() throws SQLException {
		// lazy loading to avoid connection timeout
	}

	@Override
	public boolean hasNext() {

		try {
			// lazy loading
			if (statement == null && resultSet == null) {
				statement = dataSource.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				statement.setFetchSize(Integer.MIN_VALUE);
				resultSet = statement.executeQuery(GET_CO_OCCURANCE_RLSP_SQL);
			}
			return (resultSet.next());
		} catch (SQLException e) {
			logger.error("error when checking result set position", e);
			return false;
		}
	}

	@Override
	public RelationshipDetail next() {
		try {
			// min value for COF in database is 1
			return new RelationshipDetail(resultSet.getString("CUI1"), resultSet.getString("CUI2"),
					resultSet.getInt("COF"));
		} catch (SQLException e) {
			logger.error("error when iterating result set", e);
			throw new IllegalStateException();
		}
	}

	public void destroy() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			logger.info("error closing result set / statement", e);
		}

	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}
