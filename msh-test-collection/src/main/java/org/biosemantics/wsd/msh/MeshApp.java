package org.biosemantics.wsd.msh;

import java.io.IOException;
import java.util.List;

public class MeshApp {

	private static final String RESULT_FILE = "/Users/bhsingh/code/data/paper1/msh/MSHCorpus_2/term_pmid_cui";
	private static final String RECORD_FOLDER = "/Users/bhsingh/code/data/paper1/msh/MSHCorpus_3";
	private static final String SENSE_FILE = "/Users/bhsingh/code/data/paper1/msh/MSHCorpus_2/benchmark_mesh.txt";

	public static void main(String[] args) throws IOException {
		MeshResultReaderImpl meshResultReaderImpl = new MeshResultReaderImpl();
		meshResultReaderImpl.setResultFile(RESULT_FILE);
		MeshRecordReaderImpl meshRecordReaderImpl = new MeshRecordReaderImpl();
		meshRecordReaderImpl.setRecordFolder(RECORD_FOLDER);
		meshRecordReaderImpl.setSenseFile(SENSE_FILE);

		List<MeshResult> meshResults = meshResultReaderImpl.readAll();
		for (MeshResult meshResult : meshResults) {
			MeshRecord meshRecord = meshRecordReaderImpl.getMeshRecord(meshResult.getTerm(), meshResult.getPmid());
			System.out.println(meshRecord.getText());
			System.out.println(meshRecordReaderImpl.getSenses(meshResult.getTerm()).length);
		}
	}

}
