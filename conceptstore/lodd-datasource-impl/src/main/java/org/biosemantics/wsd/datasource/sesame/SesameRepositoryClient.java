package org.biosemantics.wsd.datasource.sesame;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SesameRepositoryClient {

	public SesameRepositoryClient(String server, String repoId) {
		this.sesameServer = server;
		this.repositoryId = repoId;
	}

	public void init() throws RepositoryException {
		repository = new HTTPRepository(sesameServer, repositoryId);
		repository.initialize();
		logger.info("connected to sesame repository url:{} id:{}", new Object[] { sesameServer, repositoryId });
	}

	public void destroy() throws RepositoryException {
		repository.shutDown();
	}

	public Repository getRepository() {
		return repository;
	}

	private String sesameServer;
	private String repositoryId;
	private Repository repository;
	private static final Logger logger = LoggerFactory.getLogger(SesameRepositoryClient.class);

}
