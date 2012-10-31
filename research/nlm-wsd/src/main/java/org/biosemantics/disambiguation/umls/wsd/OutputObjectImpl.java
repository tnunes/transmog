package org.biosemantics.disambiguation.umls.wsd;

import java.util.ArrayList;
import java.util.List;

public class OutputObjectImpl implements OutputObject {

	private int recordNumber;
	private String annotatedOutput;
	private List<Integer> matches;

	public OutputObjectImpl(int recordNumber, String annotatedOutput, List<Integer> matches) {
		super();
		this.recordNumber = recordNumber;
		this.annotatedOutput = annotatedOutput;
		this.matches = matches;
	}

	public String[] toStringArray() {
		List<String> columns = new ArrayList<String>();
		columns.add(String.valueOf(recordNumber));
		columns.add(annotatedOutput);

		int max = 0;
		for (int i = 0; i < matches.size(); i++) {
			if (matches.get(i) > max) {
				max = i + 1;
			}
		}
		columns.add(String.valueOf(max));
		columns.add(matches.toString());
		return columns.toArray(new String[columns.size()]);
	}
}
