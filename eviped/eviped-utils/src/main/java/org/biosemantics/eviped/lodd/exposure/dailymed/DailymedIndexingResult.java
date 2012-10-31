package org.biosemantics.eviped.lodd.exposure.dailymed;

import java.io.Serializable;
import java.util.List;

public class DailymedIndexingResult implements Serializable {
	private static final long serialVersionUID = -7219511997862489177L;
	private String id;
	private List<DailymedName> dailymedNames;
	private List<DailymedPredicate> predicates;

	public String getId() {
		return id;
	}

	public List<DailymedName> getDrugbankNames() {
		return dailymedNames;
	}

	public List<DailymedPredicate> getPredicates() {
		return predicates;
	}

	public DailymedIndexingResult(String id, List<DailymedName> dailymedNames, List<DailymedPredicate> predicates) {
		super();
		this.id = id;
		this.dailymedNames = dailymedNames;
		this.predicates = predicates;
	}

	public DailymedIndexingResult() {
		super();
		// TODO Auto-generated constructor stub
	}

}
