package org.biosemantics.disambiguation.conceptstore.web.common;

import javax.servlet.ServletContext;

import org.biosemantics.conceptstore.common.service.ConceptRelationshipStorageService;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.disambiguation.service.local.AlgorithmServiceLocal;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

public class SpringServiceLocator {
	private static final String CONCEPT_STORAGE_SERVICE = "conceptStorageServiceLocal";
	private static final String CONCEPT_RLSP_STORAGE_SERVICE = "conceptRelationshipStorageServiceLocal";
	private static final String ALGORITHM_SERVICE = "algorithmServiceLocal";
	private ApplicationContext context;
	private ConceptStorageService conceptStorageService;
	private ConceptRelationshipStorageService conceptRelationshipStorageService;
	private AlgorithmServiceLocal algorithmServiceLocal;

	public SpringServiceLocator(Application application) {
		ServletContext servletContext = ((WebApplicationContext) application.getContext()).getHttpSession()
				.getServletContext();
		context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		conceptStorageService = (ConceptStorageService) context.getBean(CONCEPT_STORAGE_SERVICE);
		conceptRelationshipStorageService = (ConceptRelationshipStorageService) context
				.getBean(CONCEPT_RLSP_STORAGE_SERVICE);
		algorithmServiceLocal = (AlgorithmServiceLocal) context.getBean(ALGORITHM_SERVICE);
	}

	public Object getBean(final String beanRef) {
		return context.getBean(beanRef);
	}

	public ConceptStorageService getConceptStorageService() {
		return conceptStorageService;
	}

	public ConceptRelationshipStorageService getConceptRelationshipStorageService() {
		return conceptRelationshipStorageService;
	}

	public AlgorithmServiceLocal getAlgorithmServiceLocal() {
		return algorithmServiceLocal;
	}

}
