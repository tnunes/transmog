package gov.nih.nlm.ncbi.eutils;

import gov.nih.nlm.ncbi.eutils.generated.ESearchResult;
import gov.nih.nlm.ncbi.eutils.generated.PubmedArticleSet;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/*
 * http://www.ncbi.nlm.nih.gov/books/NBK25500/
 */
public class PubmedRestClient {
	private Client client;
	private WebResource eSearchResource;
	private WebResource eFetchResource;
	private JAXBContext jc;
	private Unmarshaller unmarshaller;
	private String baseUrl;
	private static final Logger logger = LoggerFactory.getLogger(PubmedRestClient.class);
	private static final String ESEARCH = "esearch.fcgi";
	private static final String EFETCH = "efetch.fcgi";

	public void setBaseUrl(String baseUrl) throws JAXBException {
		this.baseUrl = baseUrl;
		client = Client.create();
		eSearchResource = client.resource(this.baseUrl + ESEARCH);
		eFetchResource = client.resource(this.baseUrl + EFETCH);
		jc = JAXBContext.newInstance("gov.nih.nlm.ncbi.eutils.generated");
		unmarshaller = jc.createUnmarshaller();
	}

	public ESearchResult search(MultivaluedMap<String, String> queryParams) throws JAXBException, IOException {
		logger.debug("making getSearchResult query with params {}", queryParams.toString());
		InputStream is = eSearchResource.queryParams(queryParams).get(InputStream.class);
		ESearchResult searchResult = (ESearchResult) unmarshaller.unmarshal(is);
		is.close();
		logger.debug("results count {}", searchResult.getCount().intValue());
		return searchResult;
	}

	public PubmedArticleSet fetch(MultivaluedMap<String, String> queryParams) throws JAXBException, IOException {
		logger.debug("making getArticleDetails query with params {}", queryParams.toString());
		InputStream is = eFetchResource.queryParams(queryParams).post(InputStream.class);
		PubmedArticleSet pubmedArticleSet = (PubmedArticleSet) unmarshaller.unmarshal(is);
		is.close();
		logger.debug("results count {}", pubmedArticleSet.getPubmedArticle().size());
		return pubmedArticleSet;
	}

	public void destroy() {

	}

	public static void main(String[] args) throws JAXBException, IOException {
		PubmedRestClient pubmedRestClient = new PubmedRestClient();
		pubmedRestClient.setBaseUrl("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/");
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("db", "pubmed");
		params.add("term", "malaria");
		params.add("retmax", "10");
		ESearchResult eSearchResult = pubmedRestClient.search(params);
		List<BigInteger> ids = eSearchResult.getIdList().getId();
		for (BigInteger bigInteger : ids) {
			System.out.println(bigInteger.intValue());
		}
	}

}
