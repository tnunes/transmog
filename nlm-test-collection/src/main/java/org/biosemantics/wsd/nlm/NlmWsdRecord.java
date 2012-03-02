package org.biosemantics.wsd.nlm;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class NlmWsdRecord implements Serializable {
	private static final long serialVersionUID = -3371805472652149331L;
	private String header;
	private String contextSentence;
	private String positionInformation;
	private int pmid;
	private String titleText;
	private String abstractText;
	private String footer;
	private int recordNumber;
	private String annotatedSense;

	public NlmWsdRecord(String header, String contextSentence, String positionInformation, int pmid, String titleText,
			String abstractText, String footer) {
		super();
		this.header = header;
		this.contextSentence = contextSentence;
		this.positionInformation = positionInformation;
		this.pmid = pmid;
		this.titleText = titleText;
		this.abstractText = abstractText;
		this.footer = footer;
		if (!StringUtils.isBlank(this.header)) {
			String[] tokens = header.split("\\|");
			recordNumber = Integer.parseInt(tokens[0]);
			if (tokens[2].startsWith("M")) {
				annotatedSense = tokens[2].replace("M", "");
			} else {
				annotatedSense = tokens[2];
			}

		}
	}

	public String getHeader() {
		return header;
	}

	public String getContextSentence() {
		return contextSentence;
	}

	public String getPositionInformation() {
		return positionInformation;
	}

	public int getPmid() {
		return pmid;
	}

	public String getTitleText() {
		return titleText;
	}

	public String getAbstractText() {
		return abstractText;
	}

	public String getFooter() {
		return footer;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public String getAnnotatedSense() {
		return annotatedSense;
	}

	@Override
	public String toString() {
		return "NlmWsdRecord [header=" + header + ", contextSentence=" + contextSentence + ", positionInformation="
				+ positionInformation + ", pmid=" + pmid + ", titleText=" + titleText + ", abstractText="
				+ abstractText + ", footer=" + footer + "]";
	}

}
