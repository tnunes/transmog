package org.biosemantics.conceptstore.dataimport;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.drugbank.generated.DrugType;
import ca.drugbank.generated.Drugs;
import ca.drugbank.generated.ExternalLink;

public class DrugbankDataImport implements DataImport {

	public DrugbankDataImport(String drugbankXmlFile) throws JAXBException {
		inputFile = new File(drugbankXmlFile);
		jaxbContext = JAXBContext.newInstance("ca.drugbank.generated");
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	@Override
	public void importData() throws Exception {
		parseXml();

	}

	private void parseXml() throws JAXBException {
		Drugs drugs = (Drugs) unmarshaller.unmarshal(inputFile);
		logger.info("(6711) drugs found: {}", drugs.getDrug().size());
		int ctr = 0;
		for (DrugType drugType : drugs.getDrug()) {
			boolean hasConnection = false;
			for (ExternalLink externalLink : drugType.getExternalLinks().getExternalLink()) {
				if (externalLink.getResource().equalsIgnoreCase("Wikipedia")) {
					hasConnection = true;
					break;
				}
			}
			if (hasConnection) {
				ctr++;
			}

		}
		logger.info("ctr = {}", ctr);

	}

	private File inputFile;
	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	private static final Logger logger = LoggerFactory.getLogger(DrugbankDataImport.class);

	public static void main(String[] args) throws Exception {
		DrugbankDataImport drugbankDataImport = new DrugbankDataImport("/Users/bhsingh/code/data/drugbank.xml");
		drugbankDataImport.importData();

	}

}
