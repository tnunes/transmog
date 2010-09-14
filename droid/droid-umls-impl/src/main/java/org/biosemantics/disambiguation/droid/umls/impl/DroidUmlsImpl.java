package org.biosemantics.disambiguation.droid.umls.impl;

import org.biosemantics.disambiguation.droid.umls.impl.DataSourceFactoryImpl.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DroidUmlsImpl {

	private static String[] configFiles = new String[] { "droid-umls-impl-context.xml" };
	private static final Logger logger = LoggerFactory.getLogger(DroidUmlsImpl.class);

	public static void main(String[] args) {
		try {
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFiles);
			DataSourceFactory dataSourceFactoryImpl = applicationContext.getBean(DataSourceFactoryImpl.class);
			DataSource dataSource = dataSourceFactoryImpl.getInstance(DataSourceType.MYSQL);
			dataSource.initialize();
		} catch (Exception e) {
			logger.error(" ", e);
		}
	}
}
