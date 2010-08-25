package org.biosemantics.disambiguation.knowledgebase.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.validation.ValidationUtilityService;
import org.springframework.util.CollectionUtils;

public class ValidationUtilityServiceImpl implements ValidationUtilityService {

	@Override
	public boolean isNull(Object object) {
		return object == null;
	}
	
	@Override
	public boolean isBlankString(String string){
		return StringUtils.isBlank(string);
	}
	
	@Override
	public boolean isBlankCollection(Collection<?> collection){
		return CollectionUtils.isEmpty(collection);
	}

}
