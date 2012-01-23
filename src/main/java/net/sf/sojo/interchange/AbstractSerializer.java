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
package net.sf.sojo.interchange;

import net.sf.sojo.common.ObjectGraphWalker;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.core.filter.ClassPropertyFilterHandler;

/**
 * The abstracte implementation of the <code>Serializer</code> interface.
 * 
 * @author linke
 *
 */
public abstract class AbstractSerializer implements Serializer {

	protected ObjectGraphWalker walker = new ObjectGraphWalker();
	
	@Override
	public abstract Object serialize(Object pvRootObject);
	
	@Override
	public Object serialize(Object pvRootObject, String[] pvExcludedProperties) {
		Object lvReturn = null;
		try {
			walker.setExcludedProperties(pvExcludedProperties);
			// serialise with temp filter
			lvReturn = serialize(pvRootObject);
		} finally {
			walker.setExcludedProperties(null);
		}
		return lvReturn;
	}
	
	@Override
	public abstract Object deserialize(Object pvSourceObject, Class<?> pvRootClass);
	
	@Override
	public Object deserialize(Object pvSourceObject) {
		return deserialize(pvSourceObject, null);
	}

	
	public ObjectUtil getObjectUtil() { return walker.getObjectUtil(); }
	
	
	public void setWithSimpleKeyMapper(boolean pvWithSimpleKeyMapper) {
		getObjectUtil().setWithSimpleKeyMapper(pvWithSimpleKeyMapper);
	}
	
	public boolean getWithSimpleKeyMapper() { 
		return getObjectUtil().getWithSimpleKeyMapper(); 
	}
	
	@Override
	public void setClassPropertyFilterHandler(ClassPropertyFilterHandler pvClassPropertyFilterHandler) { 
		getObjectUtil().setClassPropertyFilterHandler(pvClassPropertyFilterHandler);
	}
	
	@Override
	public ClassPropertyFilterHandler getClassPropertyFilterHandler() { 
		return getObjectUtil().getClassPropertyFilterHandler(); 
	}


}
