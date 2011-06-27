package org.biosemantics.datasource.umls;

import java.io.IOException;
import java.sql.SQLException;

import org.biosemantics.datasource.umls.concept.ConceptSchemeWriter;
import org.biosemantics.datasource.umls.concept.ConceptWriter;
import org.biosemantics.datasource.umls.concept.DomainWriter;
import org.biosemantics.datasource.umls.concept.PredicateWriter;
import org.biosemantics.datasource.umls.relationship.ConceptCooccuranceRlspWriter;
import org.biosemantics.datasource.umls.relationship.ConceptFactualRlspWriter;
import org.biosemantics.datasource.umls.relationship.ConceptToSchemeRlspWriter;
import org.biosemantics.datasource.umls.relationship.SchemeRlspWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportClient {
	private static final String[] CONTEXTS = new String[] { "import-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(ImportClient.class);
	private final ClassPathXmlApplicationContext applicationContext;

	private DomainWriter domainWriter;
	private PredicateWriter predicateWriter;
	private ConceptSchemeWriter conceptSchemeWriter;
	private ConceptWriter conceptWriter;
	private SchemeRlspWriter schemeRlspWriter;
	private ConceptToSchemeRlspWriter conceptToSchemeRlspWriter;
	private ConceptFactualRlspWriter conceptFactualRlspWriter;
	private ConceptCooccuranceRlspWriter conceptCooccuranceRlspWriter;

	public static void main(String[] args) throws SQLException, IOException {
		ImportClient importClient = new ImportClient();
		importClient.init();
		importClient.destroy();
	}

	public ImportClient() {
		applicationContext = new ClassPathXmlApplicationContext(CONTEXTS);
		applicationContext.registerShutdownHook();
		domainWriter = applicationContext.getBean(DomainWriter.class);
		predicateWriter = applicationContext.getBean(PredicateWriter.class);
		conceptSchemeWriter = applicationContext.getBean(ConceptSchemeWriter.class);
		conceptWriter = applicationContext.getBean(ConceptWriter.class);
		schemeRlspWriter = applicationContext.getBean(SchemeRlspWriter.class);
		conceptToSchemeRlspWriter = applicationContext.getBean(ConceptToSchemeRlspWriter.class);
		conceptFactualRlspWriter = applicationContext.getBean(ConceptFactualRlspWriter.class);
		conceptCooccuranceRlspWriter = applicationContext.getBean(ConceptCooccuranceRlspWriter.class);
	}

	public void init() throws SQLException, IOException {
		domainWriter.writeAll();
		domainWriter.destroy();
		predicateWriter.writeAll();
		predicateWriter.destroy();
		conceptSchemeWriter.writeAll();
		conceptSchemeWriter.destroy();
		conceptWriter.writeAll();
		conceptWriter.destroy();
		schemeRlspWriter.writeAll();
		schemeRlspWriter.destroy();
		conceptToSchemeRlspWriter.writeAll();
		conceptToSchemeRlspWriter.destroy();
		conceptFactualRlspWriter.writeAll();
		conceptFactualRlspWriter.destroy();
		conceptCooccuranceRlspWriter.writeAll();
		conceptCooccuranceRlspWriter.destroy();
	}

	public void destroy() {
		applicationContext.destroy();
		applicationContext.close();
		logger.info("destroy() complete.");
	}
}
