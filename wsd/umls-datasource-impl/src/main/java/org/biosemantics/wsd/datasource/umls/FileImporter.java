package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;

public interface FileImporter {
	void parseFileAndImport(File file, String encoding, MultithreadedFileImport multithreadedFileImport) throws IOException;

}
