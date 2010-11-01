package org.biosemantics.disambiguation.datasource.reader.umls;

import java.util.Iterator;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.datasource.common.ConceptDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestRun {
	private static final Logger logger = LoggerFactory.getLogger(TestRun.class);
	private static ApplicationContext applicationContext;
	private static final String[] CONTEXT = new String[] { "datasource-reader-umls-context.xml" };

	public static void main(String[] args) {
		applicationContext = new ClassPathXmlApplicationContext(CONTEXT);
		DataSource dataSource = applicationContext.getBean(DataSource.class);
		ConceptImpl defaultDomain = new ConceptImpl();
		LabelImpl labelImpl = new LabelImpl("UMLS", Language.EN);
		defaultDomain.addLabelByType(LabelType.PREFERRED, labelImpl);
		UmlsDataSourceRdbmsReader umlsDataSourceRdbmsReader = new UmlsDataSourceRdbmsReader();
		umlsDataSourceRdbmsReader.setDataSource(dataSource);
		umlsDataSourceRdbmsReader.setDefaultDomain(defaultDomain);
		umlsDataSourceRdbmsReader.init();
		Iterator<ConceptDetail> conceptDetailIterator = umlsDataSourceRdbmsReader.getConcepts();
		//StringBuilder text = new StringBuilder();
		int ctr = 0;
		long start = System.currentTimeMillis();
		while (conceptDetailIterator.hasNext()) {
			ConceptDetail conceptDetail = conceptDetailIterator.next();
			if (ctr % 10000 == 0) {
				long end = System.currentTimeMillis();
				logger.info("{} in {}", new Object[]{ ctr, (end-start)});
				start = end;
			}
			ctr++;
			// Concept concept = conceptDetail.getConcept();
			// for (Label label : concept.getLabels()) {
			// text.append(label.getLanguage().name()).append("-").append(label.getText()).append(" || ");
			// }
			// for (Notation notation : concept.getNotations()) {
			// text.append(notation.getCode()).append(" || ");
			// }
			// logger.info(text.toString());
			// text.setLength(0);
		}
	}

}
