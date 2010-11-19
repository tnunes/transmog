package org.biosemantics.disambiguation.datasource.drugbank;

import org.biosemantics.disambiguation.datasource.drugbank.DomainIterator.DrugbankDomain;

import ca.drugbank.generated.Drugs;

public interface DataSourceWriter {
	void writeDomain(DrugbankDomain drugbankDomain);

	void writeConcept(Drugs drugs);
}
