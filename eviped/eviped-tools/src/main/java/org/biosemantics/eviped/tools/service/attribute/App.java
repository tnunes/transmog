package org.biosemantics.eviped.tools.service.attribute;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.biosemantics.eviped.tools.service.Annotation;

public class App {

	private static final String FOLDER = "/home/bhsingh/public_html/brat-v1.2_The_Larch/data/Annotation-Gina";
	private static final String REGEX_FOLDER = "/home/bhsingh/code/git/transmog/eviped/eviped-tools/src/main/resources/";

	public static void main(String[] args) throws IOException {
		AdministrationRouteImpl adminImpl = new AdministrationRouteImpl(REGEX_FOLDER
				+ "administration_route.properties");
		CountryImpl country = new CountryImpl(false);
		DosageImpl dosageImpl = new DosageImpl(REGEX_FOLDER + "dosage.properties");
		FollowUpImpl followUpImpl = new FollowUpImpl(REGEX_FOLDER + "follow_up.properties");
		FrequencyImpl frequencyImpl = new FrequencyImpl(REGEX_FOLDER + "frequency.properties");
		GenderImpl genderImpl = new GenderImpl(REGEX_FOLDER + "gender.properties");
		StudySizeImpl studySizeImpl = new StudySizeImpl(REGEX_FOLDER + "study_size.properties");
		WeightImpl weightImpl = new WeightImpl(REGEX_FOLDER + "weight.properties");
		// read all txt files
		File folder = new File(FOLDER);
		File[] txtFiles = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		for (File txtFile : txtFiles) {
			// read text
			String text = FileUtils.readFileToString(txtFile);
			List<Annotation> annotations = new ArrayList<Annotation>();
			annotations.addAll(adminImpl.getAnnotations(text, 0));
			annotations.addAll(country.getAnnotations(text, 0));
			annotations.addAll(dosageImpl.getAnnotations(text, 0));
			annotations.addAll(followUpImpl.getAnnotations(text, 0));
			annotations.addAll(frequencyImpl.getAnnotations(text, 0));
			//annotations.addAll(genderImpl.getAnnotations(text, 0));
			annotations.addAll(studySizeImpl.getAnnotations(text, 0));
			annotations.addAll(weightImpl.getAnnotations(text, 0));
			// get annotation File
			String fileNameNoExten = FilenameUtils.removeExtension(txtFile.getName());
			File annotationFile = new File(FOLDER, fileNameNoExten + ".ann");
			if (!annotationFile.exists()) {
				System.err.println("annotation file not found" + annotationFile.getName());
			}
			// write to annotationFile
			int ctr = 100;
			for (Annotation annotation : annotations) {
				StringBuilder str = new StringBuilder("T" + ctr++).append("\t")
						.append("TOOL_" + annotation.getAnnotationType()).append(" ").append(annotation.getStartPos())
						.append(" ").append(annotation.getEndPos()).append("\t").append(annotation.getText()).append("\n");
				FileUtils.write(annotationFile, str.toString(), "UTF-8", true);
			}
		}

	}

}
