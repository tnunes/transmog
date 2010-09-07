package org.biosemantics.disambiguation.droid.umls.impl;

import java.io.Serializable;

public class CuiConceptId implements Serializable {

	private static final long serialVersionUID = -1908814620370219649L;
	private String cui;
	private String conceptId;
	public String getCui() {
		return cui;
	}
	public void setCui(String cui) {
		this.cui = cui;
	}
	public String getConceptId() {
		return conceptId;
	}
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}
	public CuiConceptId(String cui, String conceptId) {
		super();
		this.cui = cui;
		this.conceptId = conceptId;
	}
	public CuiConceptId() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
