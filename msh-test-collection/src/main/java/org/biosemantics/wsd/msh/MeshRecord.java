package org.biosemantics.wsd.msh;

public class MeshRecord {

	private String pmid;
	private String text;
	private String meaning;
	private String meaningCui;

	public MeshRecord(String pmid, String text, String meaning, String meaningCui) {
		super();
		this.pmid = pmid;
		this.text = text;
		this.meaning = meaning;
		this.meaningCui = meaningCui;
	}

	public String getPmid() {
		return pmid;
	}

	public String getText() {
		return text;
	}

	public String getMeaning() {
		return meaning;
	}

	public String getMeaningCui() {
		return meaningCui;
	}
	
	

}
