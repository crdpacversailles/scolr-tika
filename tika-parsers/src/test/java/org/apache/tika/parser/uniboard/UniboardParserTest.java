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

import org.apache.poi.hssf.record.chart.TickRecord;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.DublinCoreEducation;
import org.apache.tika.metadata.LOM;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.uniboard.UniboardDcXMLParser;
import org.apache.tika.parser.uniboard.UniboardParser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

public class UniboardParserTest extends TestCase {

	public void testXMLParser() throws Exception {
		InputStream input = UniboardParserTest.class
				.getResourceAsStream("/test-documents/tikatest.ubz");
		Metadata metadata = new Metadata();
		// TODO for unkhnown reason, with only a BodyContentHnadler, the handler
		// comes back empty
		// so we do the same way as TikaGui...
		StringWriter buffer = new StringWriter();
		ContentHandler handler = new BoilerpipeContentHandler(buffer);
		try {

			new UniboardParser().parse(input, handler, metadata,
					new ParseContext());
			assertEquals("2012-11-11T05:12:18ZZ",
					metadata.get(TikaCoreProperties.CREATED));
			assertEquals(
					"http://uniboard.mnemis.com/document/17ee1b4e-cf99-41b1-9c94-f7fa7140e032",
					metadata.get(DublinCore.IDENTIFIER));
			assertEquals("application/x-uniboard+zip",
					metadata.get(Metadata.CONTENT_TYPE));
			assertEquals("Niveau universitaire",
					metadata.get(DublinCoreEducation.EDUCATION_LEVEL));
			assertEquals("Informatique",
					metadata.get(TikaCoreProperties.DESCRIPTION));
			assertEquals("Tika, Uniboard", metadata.get(TikaCoreProperties.KEYWORDS));
			assertEquals("Joachim DORNBUSCH",
					metadata.get(TikaCoreProperties.CREATOR));
			assertEquals("This is the objective of the lesson",
					metadata.get(LOM.EDUCATIONAL_DESCRIPTION));
			assertEquals("Test file for Tika UBZ Parser",
					metadata.get(TikaCoreProperties.TITLE));
			assertEquals("Autres", metadata.get(TikaCoreProperties.TYPE));
			assertEquals("1280x960", metadata.get(LOM.SIZE));
			assertEquals("2012-11-11T05:20:35ZZ",
					metadata.get(TikaCoreProperties.MODIFIED));
			assertEquals("6", metadata.get(TikaCoreProperties.RIGHTS));
			String content = buffer.toString();
			// TODO enhance this test by lookink at html tagnames and
			// attribues
			assertTrue(content.contains("Voici un texte pour la première page"));
			assertTrue(content
					.contains("This text describes an action to be performed by the teacher"));
			assertTrue(content
					.contains("This text describes an action to be performed by the learner"));
			assertTrue(content
					.contains("Here is a text inside the second slide"));
			assertTrue(content
					.contains("And another text inside the second slide"));

		} finally {
			input.close();
		}

	}

}
