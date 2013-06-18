package org.apache.tika.parser.uniboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * UBZ parser (Sankore interactive white board free software) Looks for metadata
 * in the RDF file and for text and educational informations in svg slides
 * 
 */
public class UniboardParser extends AbstractParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5351293778784711753L;

	private static final Pattern SLIDE_NAME_PATTERN = Pattern
			.compile("page\\d\\d\\d\\.svg");

	private static final Set<MediaType> SUPPORTED_TYPES = Collections
			.singleton(MediaType.application("x-uniboard+zip"));

	private Parser meta = new UniboardDcXMLParser();

	private Parser content = new UniboardSlideParser();

	public Parser getMetaParser() {
		return meta;
	}

	public void setMetaParser(Parser meta) {
		this.meta = meta;
	}

	public Parser getContentParser() {
		return content;
	}

	public void setContentParser(Parser content) {
		this.content = content;
	}

	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
		UniboardSlideContentHandler usch = new UniboardSlideContentHandler(
				xhtml, metadata);
		xhtml.startDocument();
		ZipInputStream zip = new ZipInputStream(stream);
		ZipEntry entry = zip.getNextEntry();
		DefaultHandler mdHandler = new DefaultHandler();
		metadata.set(Metadata.CONTENT_TYPE, "application/x-uniboard+zip");
		while (entry != null) {

			if (entry.getName().equals("metadata.rdf")) {
				meta.parse(zip, mdHandler, metadata, context);
			} else if (SLIDE_NAME_PATTERN.matcher(entry.getName()).matches()) {
				content.parse(zip, usch, metadata, context);
			}

			entry = zip.getNextEntry();
		}
		/*
		 * Copying metadata extract to html output was a bad idea. Typically, in
		 * a search engine like Solr, the user will do it on himself with
		 * copyfield instructions.
		 */
		/*
		 * xhtml.startElement("div", "id", "metadata-digest"); String[] names =
		 * metadata.names(); final Set<String> printableFields = new
		 * HashSet<String>( Arrays.asList(new String[] { LOM.TITLE.getName(),
		 * DublinCoreEducation.CREATOR.getName(), LOM.KEYWORDS.getName(),
		 * LOM.EDUCATIONAL_DESCRIPTION.getName(),
		 * DublinCoreEducation.SUBJECT.getName(),
		 * DublinCoreEducation.EDUCATION_LEVEL.getName() })); for (String name :
		 * names) { if (!printableFields.contains(name)) continue;
		 * xhtml.startElement("p"); xhtml.characters(metadata.get(name));
		 * xhtml.endElement("p"); } xhtml.endElement("div");
		 */

		xhtml.endDocument();

	}
}
