package org.biosemantics.eviped.classifier.weka;

import java.util.List;

public interface TxtSourceReader {

	void read(String folder);

	String[] getHeader();

	List<String[]> getData();
}
