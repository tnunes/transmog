package org.biosemantics.disambiguation.droid.umls.impl;


public interface DataSource {
	void setBatchSize(int batchSize);
	void initialize() throws Exception;
}
