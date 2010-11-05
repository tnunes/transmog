package org.biosemantics.disambiguation.datasource;

import javax.sql.DataSource;

public interface RdbmsDataSourceImporter extends DataSourceImporter {
	
	void setDataSource(DataSource dataSource);


}
