package org.biosemantics.eviped.tools.service;

import java.io.Serializable;

public class Annotation implements Serializable {
	private String annotationType;
	private int startPos;
	private int endPos;
	private int sentenceNo;
	private String text;

	public Annotation(String annotationType, int startPos, int endPos, int sentenceNo, String text) {
		super();
		this.annotationType = annotationType;
		this.startPos = startPos;
		this.endPos = endPos;
		this.sentenceNo = sentenceNo;
		this.text = text;
	}

	public String getAnnotationType() {
		return annotationType;
	}

	public int getStartPos() {
		return startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public int getSentenceNo() {
		return sentenceNo;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "Annotation [annotationType=" + annotationType + ", startPos=" + startPos + ", endPos=" + endPos
				+ ", sentenceNo=" + sentenceNo + ", text=" + text + "]";
	}

}
