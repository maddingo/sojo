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

import java.util.Map;

/**
 * It is a class, to switch transparently between methods (java.lang.reflect.Method) and fields (java.lang.reflect.Field).
 * 
 * @author linke
 *
 */
public class ReflectionPropertyHelper {

	public static Map<?,?> getAllGetterProperties(Class<?> pvClass, String pvFilter[]) {
		Map<?,?> lvProperties = null;
		if (ReflectionFieldHelper.containsClass(pvClass) == true) {
			lvProperties = ReflectionFieldHelper.getAllGetFieldMapsByClass(pvClass, pvFilter);
		} else {		
			lvProperties = ReflectionMethodHelper.getAllGetterMethodWithCache(pvClass, pvFilter);
		}
		return lvProperties;
	}
	
	public static Map<?,?> getAllSetterProperties(Class<?> pvClass, String pvFilter[]) {
		Map<?,?> lvProperties = null;
		if (ReflectionFieldHelper.containsClass(pvClass) == true) {
			lvProperties = ReflectionFieldHelper.getAllSetFieldMapsByClass(pvClass, pvFilter);
		} else {		
			lvProperties = ReflectionMethodHelper.getAllSetterMethodWithCache(pvClass, pvFilter);
		}
		return lvProperties;
	}
}