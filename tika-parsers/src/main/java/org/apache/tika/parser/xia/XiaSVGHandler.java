package org.apache.tika.parser.xia;

import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XiaSVGHandler extends DefaultHandler {

	private boolean recording;
	private XHTMLContentHandler xhtml;
	private StringBuilder currentText;
	private boolean inDetail;
	private String detailTag;
	private boolean inTitle;
	private boolean inDesc;
	private int depth;
	private static final Pattern DETAIL_ID = Pattern
			.compile("detail\\d+|background");

	public XiaSVGHandler(ContentHandler handler, Metadata metadata) {
		xhtml = new XHTMLContentHandler(handler, metadata);
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		depth++;
		if (atts.getIndex("id") >= 0
				&& DETAIL_ID.matcher(atts.getValue("id")).matches()) {
			inDetail = true;
			detailTag = name;
			xhtml.startElement("div", "id", atts.getValue("id"));
		} else if ((inDetail || depth == 2) && name.equals("title")) {
			inTitle = true;
			startRecording();
		} else if (inDetail && name.equals("desc")) {
			inDesc = true;
			startRecording();
		}

	}

	@Override
	public void startDocument() throws SAXException {
		xhtml.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		xhtml.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		depth--;
		if (inDetail && name.equals(detailTag)) {
			xhtml.endElement("div");
			inDetail = false;
			inTitle = false;
			inDesc = false;
		} else if (inTitle && name.equals("title")) {
			endRecording();
			String text = currentText.toString();
			if (text.isEmpty())
				return;
			xhtml.startElement("h1");
			try {
				xhtml.characters(text);
			} catch (SAXException e) {
				e.printStackTrace();
			}
			xhtml.endElement("h1");
			inTitle = false;
		} else if (inDetail && inDesc && name.equals("desc")) {
			endRecording();
			String text = currentText.toString();
			if (text.isEmpty())
				return;
			xhtml.startElement("p");
			try {
				xhtml.characters(text);
			} catch (SAXException e) {
				e.printStackTrace();
			}
			xhtml.endElement("p");
			inDesc = false;
		}

	}

	private void startRecording() {
		currentText = new StringBuilder();
		recording = true;

	}

	private void endRecording() {
		recording = false;
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (recording) {
			currentText.append(ch, start, length);
		}
	}

}
