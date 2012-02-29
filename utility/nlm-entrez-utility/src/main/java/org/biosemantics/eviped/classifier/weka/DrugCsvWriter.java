package org.biosemantics.eviped.classifier.weka;

public class DrugCsvWriter {
	private static final String INPUT_FOLDER = "/Users/bhsingh/Desktop/eviped/Quetiapine/all_input_mod";
	private static final String OUT_FILE = "/Users/bhsingh/Desktop/eviped/Quetiapine/Quetiapine_less_features.csv";

	public static void main(String[] args) {
		TxtSourceReaderImpl txtSourceReaderImpl = new TxtSourceReaderImpl();
		WekaCsvCreatorImpl wekaCsvCreatorImpl = new WekaCsvCreatorImpl();
		txtSourceReaderImpl.read(INPUT_FOLDER);
		wekaCsvCreatorImpl.setHeader(txtSourceReaderImpl.getHeader());
		wekaCsvCreatorImpl.setData(txtSourceReaderImpl.getData());
		wekaCsvCreatorImpl.write(OUT_FILE);
	}

}
