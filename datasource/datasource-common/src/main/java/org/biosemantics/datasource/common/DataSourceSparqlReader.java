package org.biosemantics.datasource.common;

import java.net.URI;

public interface DataSourceSparqlReader extends DataSourceReader {
	void setSparqlURI(URI uri);
}
