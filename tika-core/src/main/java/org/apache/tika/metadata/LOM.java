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
package org.apache.tika.metadata;

/**
 * A collection of Dublin Core metadata names.
 * 
 * @see <a href="http://dublincore.org">dublincore.org</a>
 */
public interface LOM {

	public static final String NAMESPACE_LOM = "http://purl.org/dc/elements/1.1/";
	public static final String PREFIX_LOM = "lom";

	Property KEYWORDS = Property.internalText(PREFIX_LOM
			+ Metadata.NAMESPACE_PREFIX_DELIMITER + "keywords");
	Property SIZE = Property.internalText(PREFIX_LOM
			+ Metadata.NAMESPACE_PREFIX_DELIMITER + "size");
	Property VERSION = Property.internalText(PREFIX_LOM
			+ Metadata.NAMESPACE_PREFIX_DELIMITER + "version");
	Property TITLE = Property.internalText(PREFIX_LOM
			+ Metadata.NAMESPACE_PREFIX_DELIMITER + "title");
	Property EDUCATIONAL_DESCRIPTION = Property.internalText(PREFIX_LOM
			+ Metadata.NAMESPACE_PREFIX_DELIMITER + "educational.description");

}
