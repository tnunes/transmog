package org.biosemantics.disambiguation.umls.wsd;

import java.util.Collection;

public interface IndexService {
	
	public Collection<String> index(String text) throws Exception;

}
