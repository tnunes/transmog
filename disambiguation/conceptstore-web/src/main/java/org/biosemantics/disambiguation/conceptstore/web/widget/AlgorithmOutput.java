package org.biosemantics.disambiguation.conceptstore.web.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AlgorithmOutput extends VerticalLayout {

	public AlgorithmOutput(Collection<String> conceptLabelTexts) {
		StringBuilder googleChartApiUrl = new StringBuilder(
				"http://chart.googleapis.com/chart?cht=gv&chs=620x480&chl=digraph{");
		StringBuilder txt = new StringBuilder();
		int ctr = 0;
		for (String string : conceptLabelTexts) {
			txt.append(string);
			try {
				googleChartApiUrl.append("\"").append(URLEncoder.encode(string, "UTF-8")).append("\"");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (++ctr < conceptLabelTexts.size()) {
				txt.append("->");
				googleChartApiUrl.append("->");
			}
		}
		this.addComponent(new Label("<h3>Textual Representation:</h3>", Label.CONTENT_XHTML));
		this.addComponent(new Label(txt.toString(), Label.CONTENT_TEXT));
		ExternalResource externalResource = new ExternalResource(googleChartApiUrl.toString());
		Embedded embeddedImage = new Embedded("", externalResource);
		embeddedImage.setType(Embedded.TYPE_IMAGE);
		this.addComponent(new Label("<h3>Visual Representation:</h3>", Label.CONTENT_XHTML));
		this.addComponent(embeddedImage);

	}

}
