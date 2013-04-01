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
package net.sf.sojo.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * If the converter find a repeated (known) object (by cycles in a object graph), than exchange
 * this object through a unique id.
 * 
 * 
 * @author Linke
 *
 */
public class UniqueIdGenerator {

	public static final String UNIQUE_ID_PROPERTY = "~unique-id~";
	public static final int MINIMAL_UNIQUE_ID = 0;
	
	private int currentUniqueID = 0;
	private int minimalUniqueID = MINIMAL_UNIQUE_ID;
	private boolean withHashCodeInUniqueId = false;
	private Map<Object, Object> uniqueIdObjectMap = new HashMap<Object,Object>();
	
	public UniqueIdGenerator() {
	}
	
	public void setMinimalUniqueID(int pvMinimalUniqueID) { minimalUniqueID = pvMinimalUniqueID; }
	public int getMinimalUniqueID() { return minimalUniqueID; }
	
	public void setWithHashCodeInUniqueId(boolean pvWithHashCodeInUniqueId) { withHashCodeInUniqueId = pvWithHashCodeInUniqueId; }
	public boolean getWithHashCodeInUniqueId() { return withHashCodeInUniqueId; }
	
	public int getCurrentUniqueID() { return currentUniqueID; }
	
	
	protected String getStringRepresentationFromId (int pvUniqueId, Object pvObject) {
		String lvUniqueId = Integer.toString(pvUniqueId);
		if (getWithHashCodeInUniqueId() == true) {
			lvUniqueId = lvUniqueId + "-" + pvObject.hashCode();
		}
		return lvUniqueId;
	}
	
	
	public final String getUniqueId(Object pvObject) {
		if (pvObject == null) {return null; }

		String lvUniqueId = null;
		
		if ( ! (pvObject instanceof Collection) && ! (pvObject instanceof Map)) {
			lvUniqueId = (String) uniqueIdObjectMap.get(pvObject); 
			if (lvUniqueId == null){
				lvUniqueId = getStringRepresentationFromId(currentUniqueID, pvObject);
				currentUniqueID++;
				uniqueIdObjectMap.put(pvObject, lvUniqueId);
			}
		}
		
		return lvUniqueId; 
	}
	
	public final static String getUniqueIdStringByNumber(String pvNumberString) {
		return UniqueIdGenerator.UNIQUE_ID_PROPERTY + pvNumberString;
	}
	
	public final boolean isKnownObject(Object pvObject) {
		if (pvObject instanceof Collection || pvObject instanceof Map) {
			return false;
		} else {
			return uniqueIdObjectMap.containsKey(pvObject);
		}
	}

	public Object getObjectByUniqueId (String pvUniqueId) {
		for (Entry<Object, Object> entry : uniqueIdObjectMap.entrySet()) {
			if (entry.getValue().equals(pvUniqueId)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public final Object addObject(String pvUniqueId, Object pvObject) {
		return uniqueIdObjectMap.put(pvObject, pvUniqueId);
	}

	public void clear() {
		uniqueIdObjectMap.clear();
	}
}
