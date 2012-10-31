package org.biosemantics.eviped.tools.service.attribute;

import java.util.List;

import org.biosemantics.eviped.tools.service.Annotation;

public interface AttributeExtractorService {

	List<Annotation> getAnnotations(String text, int sentenceNumber);

}
