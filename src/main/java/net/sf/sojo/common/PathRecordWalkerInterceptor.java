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
package net.sf.sojo.common;

import java.util.Map;
import java.util.TreeMap;

import net.sf.sojo.core.Constants;
import net.sf.sojo.core.UniqueIdGenerator;

/**
 * This implementation record all paths of a object graph.
 * 
 * @author linke
 *
 */
public class PathRecordWalkerInterceptor implements WalkerInterceptor {

	private Map<String,Object> paths = new TreeMap<String, Object>();
	private boolean filterUniqueIdProperty = false;
	private boolean onlySimpleProperties = false;
	
	public PathRecordWalkerInterceptor() { }
	
	public boolean getFilterUniqueIdProperty() { return filterUniqueIdProperty; }
	public void setFilterUniqueIdProperty(boolean pvFilterUniqueIdProperty) { filterUniqueIdProperty = pvFilterUniqueIdProperty; }
	
	public void setOnlySimpleProperties(boolean pvOnlySimpleProperties) { onlySimpleProperties = pvOnlySimpleProperties; }
	public boolean getOnlySimpleProperties() { return onlySimpleProperties; }
			
	public boolean addToPaths(int pvType, String pvPath) {
		boolean lvReturn = true;
		
		if (getFilterUniqueIdProperty() && pvPath.indexOf(UniqueIdGenerator.UNIQUE_ID_PROPERTY) >= 0) {
			lvReturn = false;
		} 
		if (getOnlySimpleProperties() && Constants.TYPE_SIMPLE != pvType) {
			lvReturn = false;
		}
		
		return lvReturn;
	}
	
	@Override
	public boolean visitElement(Object pvKey, int pvIndex, Object pvValue, int pvType, String pvPath, int pvNumberOfRecursion) {

		if (addToPaths(pvType, pvPath)) {
			Object o = paths.put(pvPath, pvValue);
			if (o != null) {
				throw new IllegalArgumentException("Path is not unique: " + pvPath + " - " + pvValue);
			}
		}
		
		return false;
	}
	
	
	public Map<String,Object> getAllRecordedPaths() {
		return paths;
	}

	@Override
	public void endWalk() {  }

	@Override
	public void startWalk(Object pvStartObject) {
		paths.clear();
	}

	@Override
	public void visitIterateableElement(Object pvValue, int pvType, String pvPath, int pvBeginEnd) {
	}

}
