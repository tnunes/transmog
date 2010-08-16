package org.biosemantics.disambiguation.droid.umls.impl;

import java.util.HashMap;
import java.util.Map;

public class DataSourceFactoryImpl implements DataSourceFactory {

	public enum DataSourceType {
		MYSQL, WEB_SERVICE
	}

	private Map<DataSourceType, DataSource> dataSourceMap = new HashMap<DataSourceType, DataSource>();

	public void setDataSourceMap(Map<DataSourceType, DataSource> dataSourceMap) {
		this.dataSourceMap = dataSourceMap;
	}

	@Override
	public DataSource getInstance(DataSourceType dataSourceType) {
		DataSource dataSource = dataSourceMap.get(dataSourceType);
		if (dataSource == null) {
			throw new IllegalArgumentException();
		} else {
			return dataSource;
		}
	}

}
