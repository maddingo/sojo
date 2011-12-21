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
package net.sf.sojo.core.filter;

import java.util.Map;

import net.sf.sojo.core.reflect.ReflectionMethodHelper;

/**
 * Helper class for the <code>ClassPropertyFilter</code> class. This class help to create, validate or check the property filter.
 * @author linke
 *
 */
public class ClassPropertyFilterHelper {
	
	/**
	 * Check a property from a class by a handler.
	 * 
	 * @param pvClassPropertyFilterHandler The handler implementation.
	 * @param pvClassForFindFilter The class for the filter.
	 * @param pvKey The to filtering property.
	 * @return <code>true</code>, if the property from the class is known by the handler, else <code>false</code>.
	 */
	public static boolean isPropertyToFiltering (ClassPropertyFilterHandler pvClassPropertyFilterHandler, Class<?> pvClassForFindFilter, Object pvKey) {
		boolean lvAddProperty = false;
		if (pvClassPropertyFilterHandler != null) {
			ClassPropertyFilter lvClassPropertyFilter = pvClassPropertyFilterHandler.getClassPropertyFilterByClass(pvClassForFindFilter);
			String lvKey = (pvKey == null ? null : pvKey.toString());
			if (lvClassPropertyFilter != null && lvClassPropertyFilter.isKnownProperty(lvKey) == true) {
				lvAddProperty = true;
			}
		}
		return lvAddProperty;
	}

	/**
	 * Create a <code>ClassPropertyFilder</code> by analyze (per reflection) the parameter class.
	 * All properties are filtered (transient).
	 * @param pvClass To analyze class.
	 * @return The <code>ClassPropertyFilter</code>.
	 */
	public static ClassPropertyFilter createClassPropertyFilterByClass(Class<?> pvClass) {
		ClassPropertyFilter lvClassPropertyFilter = new ClassPropertyFilter(pvClass);
		Map<Object, Object> lvProperties = ReflectionMethodHelper.getAllGetterMethodWithCache(pvClass,null);
		
		for (Object key : lvProperties.keySet()) {
			lvClassPropertyFilter.addProperty((String) key);
		}
		return lvClassPropertyFilter;
	}

	/**
	 * Test the <code>ClassPropertyFilter</code>. It means, that test the class-name (Class.forName) and
	 * test all properties with the properties of the class.
	 * 
	 * @param pvClassPropertyFilter The source for the tests.
	 * @return <code>Null</code>, than is the <code>ClassPropertyFilter</code> ok, else the message with the fault.
	 */
	public static String validateClassPropertyFilter(ClassPropertyFilter pvClassPropertyFilter) {
		String lvReturn = null;
		if (pvClassPropertyFilter != null) {
			try {
				Class<?> lvClass = pvClassPropertyFilter.getFilterClass();
				Map<Object, Object> lvProperties = ReflectionMethodHelper.getAllGetterMethodWithCache(lvClass, null);
				Object lvAllProperties[] =  pvClassPropertyFilter.getAllProperties();
				for (int i = 0; i < lvAllProperties.length; i++) {
					if (lvProperties.containsKey(lvAllProperties[i]) == false) {
						return lvAllProperties[i] + " is not a valid property name";
					}
				}
			} catch (Exception e) {
				lvReturn = pvClassPropertyFilter.getFilterClass() + " is not a valid class name";
			}
		}
		return lvReturn;
	}


}
