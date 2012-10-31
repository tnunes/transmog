package org.biosemantics.wsd.msh;

public class MeshResult {

	private String term;
	private String pmid;
	private String cui;
	
	public MeshResult(String[] columns) {
		super();
		this.term = columns[0];
		this.pmid = columns[1];
		this.cui = columns[2];
	}
	

	public String getTerm() {
		return term;
	}

	public String getPmid() {
		return pmid;
	}

	public String getCui() {
		return cui;
	}

}
