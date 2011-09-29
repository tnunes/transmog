package org.biosemantics.disambiguation.umls.wsd;


public interface SenseScoreService {
	
	double getSenseScore(String cui, String contextCui);
	
	
}
