package org.biosemantics.datasource.common;

import javax.sql.DataSource;

public interface DataSourceRdbmsReader extends DataSourceReader {
	
	void setDataSource(DataSource dataSource);

}
