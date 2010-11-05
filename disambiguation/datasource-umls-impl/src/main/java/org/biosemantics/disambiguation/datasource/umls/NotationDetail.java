package org.biosemantics.disambiguation.datasource.umls;

public class NotationDetail {
	private String code;
	private String domain;

	public NotationDetail(String code, String domain) {
		super();
		this.code = code;
		this.domain = domain;
	}

	public NotationDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
