package org.biosemantics.eviped.lodd.exposure.dailymed;

import gov.nih.nlm.nls.metamap.Ev;

import java.io.Serializable;
import java.util.List;

public class DailymedPredicate implements Serializable {
	private static final long serialVersionUID = 3642735982623512089L;
	private String predicate;
	private List<Ev> evs;

	public DailymedPredicate(String predicate, List<Ev> evs) {
		super();
		this.predicate = predicate;
		this.evs = evs;
	}

	public DailymedPredicate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPredicate() {
		return predicate;
	}

	public List<Ev> getEvs() {
		return evs;
	}

}
