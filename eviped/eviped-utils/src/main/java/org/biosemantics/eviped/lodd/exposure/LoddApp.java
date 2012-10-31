package org.biosemantics.eviped.lodd.exposure;

import org.biosemantics.eviped.lodd.exposure.dailymed.ExposureDailymedClient;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoddApp {
	
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"lodd-import-context.xml"});
		applicationContext.registerShutdownHook();
//		DrugExposureClient drugExposureClient = applicationContext.getBean(DrugExposureClient.class);
//		drugExposureClient.getAtcCode();
//		drugExposureClient.getDbId();
//		drugExposureClient.drugbankToDailymed();
//		drugExposureClient.getBrand();
//		drugExposureClient.getDailymedIds();
		
		ExposureDailymedClient exposureDailymedClient = applicationContext.getBean(ExposureDailymedClient.class);
		exposureDailymedClient.writeAll();
		
	}

}
