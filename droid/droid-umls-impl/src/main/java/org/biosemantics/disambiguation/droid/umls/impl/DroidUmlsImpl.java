package org.biosemantics.disambiguation.droid.umls.impl;

import org.biosemantics.disambiguation.droid.umls.impl.DataSourceFactoryImpl.DataSourceType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DroidUmlsImpl {

	private static String[] configFiles = new String[] { "droid-umls-impl-context.xml" };

	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFiles);
		DataSourceFactory dataSourceFactoryImpl = applicationContext.getBean(DataSourceFactoryImpl.class);
		DataSource dataSource = dataSourceFactoryImpl.getInstance(DataSourceType.MYSQL);
		dataSource.initialize();
	}
}
