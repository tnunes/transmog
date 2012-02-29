package gov.nih.nlm.ncbi.eutils;

import gov.nih.nlm.ncbi.eutils.generated.ESearchResult;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
/*
 * http://www.ncbi.nlm.nih.gov/books/NBK25500/
 */
public class PubmedRestClient {

	private static final String EUTILS_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";
	private Client client;
	private WebResource webResource;
	private JAXBContext jc;
	private Unmarshaller unmarshaller;

	public PubmedRestClient() throws JAXBException {
		client = Client.create();
		webResource = client.resource(EUTILS_URL);
		jc = JAXBContext.newInstance( "gov.nih.nlm.ncbi.eutils.generated" );
		unmarshaller = jc.createUnmarshaller();
	}

	/**
	 * 
	 * @param queryParams ?db=pubmed&term=malaria&RetMax=100
	 * @return
	 */
	public String getPmidsAsXmlString(MultivaluedMap<String, String> queryParams) {
		String s = webResource.queryParams(queryParams).get(String.class);
		return s;
	}
	
	
	public ESearchResult getSearchResult(MultivaluedMap<String, String> queryParams) throws JAXBException {
		String s = webResource.queryParams(queryParams).get(String.class);
		ESearchResult searchResult = (ESearchResult)unmarshaller.unmarshal(new ByteArrayInputStream(s.getBytes()));
		return searchResult;
	}
	    
	        

	public void destroy() {
		
	}
	
	
	public static void main(String[] args) throws JAXBException {
		PubmedRestClient pubmedRestClient = new PubmedRestClient();
		MultivaluedMap<String, String> params=  new MultivaluedMapImpl();
		params.add("db", "pubmed");
		params.add("term", "malaria");
		params.add("retmax", "1000");
		ESearchResult eSearchResult = pubmedRestClient.getSearchResult(params);
		List<BigInteger> ids = eSearchResult.getIdList().getId();
		for (BigInteger bigInteger : ids) {
			System.err.println(bigInteger.intValue());
		}
	}

}
