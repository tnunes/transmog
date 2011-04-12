package org.biosemantics.datasource.umls.concept;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CuiCountResultSetExtractor implements ResultSetExtractor<Collection<String>> {

	private Collection<String> allCuis = new ArrayList<String>();

	@Override
	public Collection<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
		while (rs.next()) {
			String cui = rs.getString(1);
			allCuis.add(cui);
		}
		return allCuis;
	}

}
