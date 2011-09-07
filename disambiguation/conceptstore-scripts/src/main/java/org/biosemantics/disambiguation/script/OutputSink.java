package org.biosemantics.disambiguation.script;

public interface OutputSink {
	void init();

	void write(WritableObject object);

	void destroy();

}
