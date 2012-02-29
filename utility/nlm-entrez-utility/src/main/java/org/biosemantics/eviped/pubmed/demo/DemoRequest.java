package org.biosemantics.eviped.pubmed.demo;

import gov.nih.nlm.ncbi.entrez_utility.generated.pubmed.EFetchPubmedService;
import gov.nih.nlm.ncbi.entrez_utility.generated.pubmed.EFetchRequest;
import gov.nih.nlm.ncbi.entrez_utility.generated.pubmed.EFetchResult;
import gov.nih.nlm.ncbi.entrez_utility.generated.pubmed.EUtilsServiceSoap;

public class DemoRequest {

	public static void main(String[] args) {
		EFetchPubmedService eFetchPubmedService = new EFetchPubmedService();
		EUtilsServiceSoap eUtilsServiceSoap = eFetchPubmedService.getEUtilsServiceSoap();
		EFetchRequest eFetchRequest = new EFetchRequest();
		eFetchRequest.setId("235156");
		EFetchResult eFetchResult = eUtilsServiceSoap.runEFetch(eFetchRequest);
		System.err.println(eFetchResult);
		
	}

}
