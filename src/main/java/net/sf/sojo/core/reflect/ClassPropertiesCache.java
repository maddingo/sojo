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
package net.sf.sojo.core.reflect;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for save information for methods and field and do the access to this information more performant.
 * 
 * @author linke
 *
 */
public class ClassPropertiesCache {

	
	private Map<Object, Object> classPropertiesMap = new HashMap<Object, Object>();
	
	public void clear() {
		classPropertiesMap.clear();
	}
	
	/**
	 * Add to a Class a <code>Map</code>, where the key is e.g. the method- per field-name and the value is the method or the field.
	 * 
	 * @param pvClass The class, to there is save the information.
	 * @param pvClassProperteiesMap Map, with key the property-name and the value contains the method or field object.
	 */
	public void addClassPropertiesMap (Class<?> pvClass, Map<?,?> pvClassProperteiesMap) {
		classPropertiesMap.put(pvClass, pvClassProperteiesMap);
	}

	public void removePropertiesByClass (Class<?> pvClass) {
		classPropertiesMap.remove(pvClass);
	}

	/**
	 * Get the method/field - <code>Map</code> by class.
	 * @param pvClass The class where the information are saved.
	 * @return The Map with reflection informations.
	 */
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getClassPropertiesMapByClass (Class<?> pvClass) {
		return (Map<Object, Object>) classPropertiesMap.get(pvClass);
	}
	
	public boolean containsClass(Class<?> pvClass) {
		return classPropertiesMap.containsKey(pvClass);
	}
	
	/**
	 * 
	 * @return Size of saved class-informations.
	 */
	public int size() {
		return classPropertiesMap.size();
	}
}
