package org.biosemantics.eviped.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class AbstractTextExtractor implements ResultSetExtractor {

	public String extractData(ResultSet rs) throws SQLException, DataAccessException {
		StringBuilder stringBuilder = new StringBuilder();
		while(rs.next()){
			stringBuilder.append(rs.getString("abstract_text"));
		}
		return (String) stringBuilder.toString();
	}

}
