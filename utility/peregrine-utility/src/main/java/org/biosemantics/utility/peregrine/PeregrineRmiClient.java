package org.biosemantics.utility.peregrine;

import org.erasmusmc.data_mining.ontology.api.Language;
import org.erasmusmc.data_mining.ontology.api.Ontology;
import org.erasmusmc.data_mining.peregrine.api.Peregrine;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PeregrineRmiClient {

	private Peregrine peregrine;
	private Ontology ontology;

	public Peregrine getPeregrine() {
		return peregrine;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setPeregrine(Peregrine peregrine) {
		this.peregrine = peregrine;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/org/biosemantics/utility/peregrine/peregrine-utility-context.xml" });
		PeregrineRmiClient peregrineRmiClient = (PeregrineRmiClient) appContext.getBean("peregrineRmiClient");
		peregrineRmiClient.getPeregrine().index("Malaria", Language.DEFAULT);
		
	}

}
