package org.biosemantics.datasource.umls.relationship;

public class UmlsRelationship {
	private String subject;
	private String predicate;
	private String object;

	public UmlsRelationship(String subject, String predicate, String object) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public String getObject() {
		return object;
	}

	@Override
	public String toString() {
		return "UmlsRelationship [subject=" + subject + ", predicate=" + predicate + ", object=" + object + "]";
	}
	
	

}
