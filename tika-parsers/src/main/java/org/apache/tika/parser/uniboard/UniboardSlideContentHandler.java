package org.apache.tika.parser.uniboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.SafeContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class UniboardSlideContentHandler extends SafeContentHandler {

	private boolean recording;
	private XHTMLContentHandler xhtml;
	private StringBuilder currentText;

	public UniboardSlideContentHandler(ContentHandler handler, Metadata metadata) {
		super(handler);
		xhtml = new XHTMLContentHandler(handler, metadata);
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		if (localName.equals("action")) {
			{
				if (atts.getIndex("task") >= 0) {
					String ownerCode = atts.getValue("owner");
					String owner = "unknown";
					if (ownerCode != null) {
						owner = ownerCode.equals("1") ? "learner" : "teacher";
					}
					xhtml.startElement("p", "data-owner", owner);
					xhtml.characters(atts.getValue("task"));
					xhtml.endElement("p");
				}

			}
		} else if (localName.equals("foreignObject")) {
			startRecording();
		}

	}

	@Override
	public void startDocument() throws SAXException {
		// ignore
	}

	@Override
	public void endDocument() throws SAXException {
		// ignore
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (localName.equals("foreignObject")) {
			endRecording();
			String text = getCurrentTextContent();
			if (text.isEmpty())
				return;
			xhtml.startElement("p", "data-type", "slide-object");
			try {
				xhtml.characters(text);
			} catch (SAXException e) {
				e.printStackTrace();
			}
			xhtml.endElement("p");
		}

	}

	private String getCurrentTextContent() {
		return removeTags(currentText.toString()).trim();
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
		if (recording)
			currentText.append(ch, start, length);
	}

	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	private static final Pattern REMOVE_STYLES = Pattern
			.compile("<style[^>]+>[^<]*</style>");

	/**
	 * The text boxes in the svg slides are not treated as XML because the html
	 * content is url encoded. We have to remove HTML tags manually
	 * 
	 * @param string
	 *            the HTML encoded String
	 * @return only text content
	 */
	public static String removeTags(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		Matcher ms = REMOVE_STYLES.matcher(string);
		Matcher m = REMOVE_TAGS.matcher(ms.replaceAll(""));
		return m.replaceAll("");
	}

}
