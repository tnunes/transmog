package org.biosemantics.eviped.utils;

public class DosageAnnotation {

	private int start;
	private int end;
	private String text;
	private String pattern;
	private String context;

	public DosageAnnotation(int start, int end, String text, String pattern, String context) {
		super();
		this.start = start;
		this.end = end;
		this.text = text;
		this.pattern = pattern;
		this.context = context;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getText() {
		return text;
	}

	public String getPattern() {
		return pattern;
	}

	public String getContext() {
		return context;
	}

}
