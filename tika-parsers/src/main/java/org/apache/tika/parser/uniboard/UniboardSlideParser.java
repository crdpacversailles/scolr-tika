/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.parser.uniboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

/**
 * Parser for UBZ <code>pagexxx.svg</code> files.
 * Each file represents a slide
 * Extracts text from text boxes and "teacher guide" 
 */
public class UniboardSlideParser extends AbstractParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -170572598607381319L;

	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return Collections.emptySet(); // not a top-level parser
	}

	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			try {
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			} catch (SAXNotRecognizedException e) {
				e.printStackTrace();
			}
			SAXParser parser = factory.newSAXParser();
			parser.parse(new CloseShieldInputStream(stream),
					new EmbeddedContentHandler(handler));
		} catch (ParserConfigurationException e) {
			throw new TikaException("XML parser error", e);
		}

	}

}
