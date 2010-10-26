package org.biosemantics.disambiguation.manager.dto;

public class ResponseAdapter implements Response {

	private static final long serialVersionUID = -6999446940785816965L;
	protected ResponseType responseType;
	protected String message;
	protected int resultSize;
	protected long responseTime;

	public ResponseAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseAdapter(ResponseType responseType, String message, int resultSize, long responseTime) {
		super();
		this.responseType = responseType;
		this.message = message;
		this.resultSize = resultSize;
		this.responseTime = responseTime;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public String getMessage() {
		return message;
	}

	public int getResultSize() {
		return resultSize;
	}

	public long getResponseTime() {
		return responseTime;
	}

}
