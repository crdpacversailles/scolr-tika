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

import org.apache.tika.metadata.DublinCoreEducation;
import org.apache.tika.metadata.LOM;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.DcXMLParser;
import org.apache.tika.parser.xml.ElementMetadataHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;

/**
 * Unibard Dublin Core metadata parser
 */
public class UniboardDcXMLParser extends DcXMLParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8877397237534753757L;

	private static final String NAMESPACE_URI_UB = "http://uniboard.mnemis.com/document";
	public static final String PREFIX_UB = "ub";

	private static ContentHandler getUniboardDublinCoreHandler(
			Metadata metadata, Property property, String element) {
		return new ElementMetadataHandler(NAMESPACE_URI_UB, element, metadata,
				property);
	}

	protected ContentHandler getContentHandler(ContentHandler handler,
			Metadata metadata, ParseContext context) {
		return new TeeContentHandler(super.getContentHandler(handler, metadata,
				context), getUniboardDublinCoreHandler(metadata, LOM.VERSION,
				"version"), getUniboardDublinCoreHandler(metadata, LOM.SIZE,
				"size"), getUniboardDublinCoreHandler(metadata,
						TikaCoreProperties.MODIFIED, "updated-at"),
				getUniboardDublinCoreHandler(metadata, TikaCoreProperties.TITLE,
						"sessionTitle"), getUniboardDublinCoreHandler(metadata,
								TikaCoreProperties.CREATOR, "sessionAuthors"),
				getUniboardDublinCoreHandler(metadata,
						LOM.EDUCATIONAL_DESCRIPTION, "sessionObjectives"),
				getUniboardDublinCoreHandler(metadata, TikaCoreProperties.KEYWORDS,
						"sessionKeywords"), getUniboardDublinCoreHandler(
						metadata, DublinCoreEducation.EDUCATION_LEVEL,
						"sessionGradeLevel"), getUniboardDublinCoreHandler(
						metadata, TikaCoreProperties.DESCRIPTION,
						"sessionSubjects"), getUniboardDublinCoreHandler(
						metadata, TikaCoreProperties.TYPE, "sessionType"),
				getUniboardDublinCoreHandler(metadata,
						TikaCoreProperties.RIGHTS, "sessionLicence"));
	}

}