package org.biosemantics.disambiguation.datasource;

public interface DataSourceImporter {
	void setOutputDir(String dirPath);

	void setCleanImport(boolean cleanImport);

	void init();
	
	void destroy();
}
