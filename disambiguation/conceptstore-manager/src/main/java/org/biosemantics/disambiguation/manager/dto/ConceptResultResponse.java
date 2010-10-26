package org.biosemantics.disambiguation.manager.dto;

public class ConceptResultResponse extends ResponseAdapter {

	private static final long serialVersionUID = -4997259373274716610L;
	private ConceptResult conceptResult;

	public ConceptResult getConceptResult() {
		return conceptResult;
	}

	public void setConceptResult(ConceptResult conceptResult) {
		this.conceptResult = conceptResult;
	}

	public ConceptResultResponse(ConceptResult conceptResult, long responseTime) {
		super();
		this.conceptResult = conceptResult;
		this.responseTime = responseTime;
	}

	public ConceptResultResponse(ResponseType responseType, String message) {
		this.responseType = responseType;
		this.message = message;
	}

}
