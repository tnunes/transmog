package org.biosemantics.eviped.classifier.weka;

import java.util.List;

public interface WekaCsvCreator {

	void setHeader(String[] header);

	void setData(List<String[]> data);
	
	void write(String outFile);

}
