/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */	
package net.sf.sojo.interchange.json;


import java.io.ByteArrayInputStream;

import net.sf.sojo.interchange.json.generate.JsonParserGenerate;
import net.sf.sojo.interchange.json.generate.ParseException;

public class JsonParser {
	
	private JsonParserGenerate jsonParserGenerate = null;
	
	public Object parse(final String pvJsonString) throws JsonParserException {
		Object lvReturn = null;
		if (pvJsonString != null) {
			try {				
				ByteArrayInputStream lvInputStream = new ByteArrayInputStream(pvJsonString.getBytes());
				if (jsonParserGenerate == null) {
					jsonParserGenerate = new JsonParserGenerate(lvInputStream);
				} else {
					jsonParserGenerate.ReInit(lvInputStream);
				}
				lvReturn = jsonParserGenerate.parse();
			} catch (ParseException e) {
				throw new JsonParserException("Exception in String: '" + pvJsonString + "' --> " + e.getMessage());
			}
		}
		return lvReturn;
	}

}
