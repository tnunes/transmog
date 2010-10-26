package org.biosemantics.disambiguation.manager.dto;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;

public class QueryResultResponse extends ResponseAdapter {

	private static final long serialVersionUID = -4677441578048189455L;
	private String searchText;
	private Collection<String> previousSearchTexts;
	private List<QueryResult> queryResults;

	public QueryResultResponse(ResponseType responseType, String message) {
		this.responseType = responseType;
		this.message = message;
	}

	public QueryResultResponse(List<QueryResult> queryResults, long responseTime) {
		this.queryResults = Preconditions.checkNotNull(queryResults);
		this.responseType = ResponseType.SUCCESS;
		this.resultSize = queryResults.size();
		this.responseTime = responseTime;
	}

	public List<QueryResult> getQueryResults() {
		return queryResults;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Collection<String> getPreviousSearchTexts() {
		return previousSearchTexts;
	}

	public void setPreviousSearchTexts(Collection<String> previousSearchTexts) {
		this.previousSearchTexts = previousSearchTexts;
	}

	public void setQueryResults(List<QueryResult> queryResults) {
		this.queryResults = queryResults;
	}

}
