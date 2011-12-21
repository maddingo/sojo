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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import net.sf.sojo.util.Util;

/**
 * Find all fields of the search classes (go upward in the class hierachy, how to the <code>java.lang.Object</code>).
 * 
 * @author linke
 *
 */
public final class ReflectionFieldHelper {

	private static final ClassPropertiesCache classFieldCache = new ClassPropertiesCache();
	
	public static Field[] getAllFieldsByClass (Class<?> pvClass) {
		Map<Object, Object> lvFieldMap = getAllFieldMapsByClassIntern(pvClass, null);
		return (Field[]) lvFieldMap.values().toArray(new Field [lvFieldMap.size()]);
	}

	public static Map<Object, Object> getAllGetFieldMapsByClass (Class<?> pvClass, String pvFilter[]) {
		Map<Object, Object> lvFieldMap = new TreeMap<Object, Object>(getAllFieldMapsByClassIntern (pvClass, pvFilter));
		lvFieldMap.put(Util.getKeyWordClass(), pvClass.getName());
		return Collections.unmodifiableMap(lvFieldMap);
	}

	public static Map<Object, Object> getAllSetFieldMapsByClass (Class<?> pvClass, String pvFilter[]) {
		Map<?, ?> lvFieldMap = getAllFieldMapsByClassIntern (pvClass, pvFilter);
		return Collections.unmodifiableMap(lvFieldMap);
	}
	
	private static Map<Object, Object> getAllFieldMapsByClassIntern (Class<?> pvClass, String pvFilter[]) {
		Map<Object, Object> lvFieldMap = classFieldCache.getClassPropertiesMapByClass(pvClass);
		if (lvFieldMap == null) {
			lvFieldMap = getAllFieldsByClassIntern(pvClass, new TreeMap<Object, Object>());
		}
		lvFieldMap = Util.filterMapByKeys(lvFieldMap, pvFilter);
		return lvFieldMap;		
	}
	
	public static void addAllFields2MapByClass (Class<?> pvClass, String pvFilter[]) {
		if (classFieldCache.containsClass(pvClass) == false) {
			Map<Object, Object> lvFieldMap = getAllSetFieldMapsByClass(pvClass, pvFilter);
			classFieldCache.addClassPropertiesMap(pvClass, lvFieldMap);
		} 
	}
	
	public static void removePropertiesByClass (Class<?> pvClass) {
		classFieldCache.removePropertiesByClass(pvClass);
	}
	
	public static boolean containsClass(Class<?> pvClass) {
		return classFieldCache.containsClass(pvClass);
	}
	

	/**
	 * Recursive search all methods from the Class in the Class Hierarchy to Object class.
	 * @param pvClass Search class.
	 * @param pvFieldsMap Method map (key=property name, value=method).
	 * @return All fields found.
	 */
	private static Map<Object, Object> getAllFieldsByClassIntern (Class<?> pvClass, Map<Object, Object> pvFieldsMap) {
		putAllFieldsIntern( pvClass.getFields(), pvFieldsMap) ;		
		putAllFieldsIntern( pvClass.getDeclaredFields(), pvFieldsMap);
		
		if (!(pvClass.getSuperclass().equals(Object.class))) {
			getAllFieldsByClassIntern(pvClass.getSuperclass(), pvFieldsMap);
		}
		return pvFieldsMap;
	}
	
	private static void putAllFieldsIntern (Field pvAllFields[], Map<Object, Object> pvFieldsMap) {
		for (Field field : pvAllFields) {
			pvFieldsMap.put(field.getName(), field);
		}
	}
}
