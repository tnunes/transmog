package org.biosemantics.eviped.tools.service.attribute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationReaderImpl;
import org.biosemantics.eviped.tools.service.AnnotationType;

public class WeightImpl implements AttributeExtractorService {

	private List<Pattern> patterns;

	public WeightImpl(String regexFile) throws FileNotFoundException, IOException {
		patterns = PatternReaderUtility.readPatternsFromFile(regexFile, false);
	}

	@Override
	public List<Annotation> getAnnotations(String text, int sentenceNumber) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				Annotation annotation = new Annotation(AnnotationType.STUDY_SIZE, matcher.start(), matcher.end(), 0,
						matcher.group());
				annotations.add(annotation);
			}
		}
		return annotations;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		AnnotationReaderImpl annotationReaderImpl = new AnnotationReaderImpl("/Users/bhsingh/Desktop/annotation-all.txt");
		WeightImpl sizeImpl = new WeightImpl("/Users/bhsingh/code/git/eviped/eviped-tools/src/main/resources/weight.properties");
		List<String> strings = annotationReaderImpl.getAnnotationTextByType("Subject_Weight");
		for (String string : strings) {
			//System.err.println(string);
			List<Annotation> annotations = sizeImpl.getAnnotations(string, 0);
			if (annotations != null && !annotations.isEmpty()) {
				for (Annotation annotation : annotations) {
					System.out.println(string + "\t" + annotation);
				}
			} else {
				System.err.println(string);
			}
		}
		
	}
}
