package org.biosemantics.disambiguation.script;

public interface AmbiguousLabelScript {

	void setOutputSink(OutputSink outputSink);

	void writeAmbiguousLabels();

}
