package org.biosemantics.eviped.lodd.exposure.dailymed;

import java.io.Serializable;

public class DailymedName implements Serializable {
	private static final long serialVersionUID = -404276007494573476L;
	private String predicate;
	private String value;

	public String getPredicate() {
		return predicate;
	}

	public String getValue() {
		return value;
	}

	public DailymedName(String predicate, String value) {
		super();
		this.predicate = predicate;
		this.value = value;
	}

	public DailymedName() {
		super();
		// TODO Auto-generated constructor stub
	}

}
