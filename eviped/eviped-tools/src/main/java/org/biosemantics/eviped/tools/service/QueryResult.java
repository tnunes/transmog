package org.biosemantics.eviped.tools.service;

import java.io.Serializable;

public class QueryResult implements Serializable {
	private static final long serialVersionUID = -4943020911396587973L;
	private int pmid;
	private int weight;

	public int getPmid() {
		return pmid;
	}

	public int getWeight() {
		return weight;
	}

	public QueryResult(int pmid, int weight) {
		super();
		this.pmid = pmid;
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "SearchQueryResult [pmid=" + pmid + ", weight=" + weight + "]";
	}

}
