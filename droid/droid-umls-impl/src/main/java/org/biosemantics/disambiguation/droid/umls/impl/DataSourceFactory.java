package org.biosemantics.disambiguation.droid.umls.impl;

import org.biosemantics.disambiguation.droid.umls.impl.DataSourceFactoryImpl.DataSourceType;

public interface DataSourceFactory {

	DataSource getInstance(DataSourceType dataSourceType);

}