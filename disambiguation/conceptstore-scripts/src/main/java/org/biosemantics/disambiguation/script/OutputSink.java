package org.biosemantics.disambiguation.script;

public interface OutputSink {
	void init();

	void write(OutputObject object);

	void destroy();

}
