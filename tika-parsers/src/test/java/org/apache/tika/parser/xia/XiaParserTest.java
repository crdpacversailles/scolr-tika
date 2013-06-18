/**
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

import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.DublinCoreEducation;
import org.apache.tika.metadata.LOM;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xia.XIAParser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

public class XiaParserTest extends TestCase {

	public void testXMLParser() throws Exception {
		InputStream input = XiaParserTest.class
				.getResourceAsStream("/test-documents/testxiatika.xia");
		Metadata metadata = new Metadata();
		ContentHandler handler = new BodyContentHandler();
		try {

			new XIAParser().parse(input, handler, metadata, new ParseContext());
			assertEquals("Auteur de l'image de test",
					metadata.get(DublinCore.CREATOR));
			assertEquals("Date de l'image de test",
					metadata.get(DublinCore.CREATED));
			assertEquals("Droits sur l'image de test",
					metadata.get(DublinCore.RIGHTS));
			assertEquals("Source de l'image de test",
					metadata.get(DublinCore.SOURCE));
			// assertEquals("Titre de l'image de test",
			// metadata.get(DublinCore.TITLE));

			String content = handler.toString();
			assertTrue(content.contains("Image active de test"));
			assertTrue(content.contains("Titre description générale"));
			assertTrue(content.contains("Description générale"));
			assertTrue(content.contains("Détail 1"));
			assertTrue(content.contains("Description du détail 1"));

		} finally {
			input.close();
		}
	}

}
