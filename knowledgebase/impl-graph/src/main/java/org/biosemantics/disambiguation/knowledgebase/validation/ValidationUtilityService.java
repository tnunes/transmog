package org.biosemantics.disambiguation.knowledgebase.validation;

import java.util.Collection;

public interface ValidationUtilityService {

	boolean isNull(Object object);

	boolean isBlankString(String string);

	boolean isBlankCollection(Collection<?> collection);

}