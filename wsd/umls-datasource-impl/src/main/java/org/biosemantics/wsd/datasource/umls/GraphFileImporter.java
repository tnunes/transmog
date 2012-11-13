package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;

public interface GraphFileImporter {

	void parseFileAndImport(File file, String encoding) throws IOException;

}
