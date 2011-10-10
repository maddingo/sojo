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

import net.sf.sojo.interchange.AbstractSerializer;

public class JsonSerializer extends AbstractSerializer {

	private JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();

	
	public JsonSerializer() {
		setWithSimpleKeyMapper(false);
		setWithNullValuesInMap(true);
		walker.addInterceptor(jsonInterceptor);
	}

	public boolean getWithNullValuesInMap() { return jsonInterceptor.getWithNullValuesInMap(); }
	public void setWithNullValuesInMap(boolean pvWithNullValuesInMap) { jsonInterceptor.setWithNullValuesInMap(pvWithNullValuesInMap); }

	public Object serialize(Object pvRootObject) {
		walker.walk(pvRootObject);
		return jsonInterceptor.getJsonString();
	}

	
	public Object deserialize(Object pvSourceObject, Class pvRootClass) {
		String lvParseString = (pvSourceObject == null ? null : pvSourceObject.toString());
		Object lvResult = new JsonParser().parse(lvParseString);
		lvResult = getObjectUtil().makeComplex(lvResult, pvRootClass);
		return lvResult;
	}

}
