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
package net.sf.sojo.optional.filter.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandler;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.core.reflect.ReflectionMethodHelper;
import net.sf.sojo.util.Util;

import org.apache.commons.attributes.Attributes;

public class ClassPropertyFilterHanlderForAttributes implements ClassPropertyFilterHandler {
	
	private Map<Class<?>, ClassPropertyFilter> classCache = new HashMap<Class<?>, ClassPropertyFilter>();

	@Override
	public ClassPropertyFilter getClassPropertyFilterByClass(Class<?> pvClass) {
		ClassPropertyFilter lvFilter = null;
		if (classCache.containsKey(pvClass)) {
			lvFilter = (ClassPropertyFilter) classCache.get(pvClass);
		} else {
			lvFilter = createClassPropertyFilter(pvClass);
		}
		return lvFilter;
	}
	
	
	private ClassPropertyFilter createClassPropertyFilter(Class<?> pvClass) {
		ClassPropertyFilter lvFilter = null;
		ClassAttribute lvClassAttribute = (ClassAttribute) Attributes.getAttribute(pvClass, ClassAttribute.class);
		if (lvClassAttribute != null) {
			lvFilter = new ClassPropertyFilter(pvClass);
			if (lvClassAttribute.getFilterUniqueId() == true) {
				lvFilter.addProperty(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
			}
			lvFilter.setSupport4AddClassProperty(lvClassAttribute.getFilter4ClassProperty());
			
			createPropertyFilterForFieldAnnotation(lvFilter, pvClass);
			createPropertyFilterForPropertyAnnotation(lvFilter, pvClass);
		}
		classCache.put(pvClass, lvFilter);
		return lvFilter;
	}
	
	private void createPropertyFilterForFieldAnnotation(final ClassPropertyFilter pvFilter, Class<?> pvClass) {
		Field lvFields[] = ReflectionFieldHelper.getAllFieldsByClass(pvClass);
		for (int i = 0; i < lvFields.length; i++) {
			Object lvPropertyAttribute = Attributes.getAttribute(lvFields[i], PropertyAttribute.class);
			if (lvPropertyAttribute != null) {
				pvFilter.addProperty(lvFields[i].getName());
			}
		}
	}

	private void createPropertyFilterForPropertyAnnotation(final ClassPropertyFilter pvFilter, Class<?> pvClass) {
		Map<?, ?> lvMethodMap = ReflectionMethodHelper.getAllGetterMethodWithCache(pvClass, null);
		for (Map.Entry<?, ?> lvEntry : lvMethodMap.entrySet()) {
			String lvPropertyName = (String) lvEntry.getKey();
			if (Util.getKeyWordClass().equals(lvPropertyName) == false) {
				Method lvMethod = (Method)  lvEntry.getValue();
				Object lvPropertyAttribute = Attributes.getAttribute(lvMethod, PropertyAttribute.class);
				if (lvPropertyAttribute != null) {
					pvFilter.addProperty(lvPropertyName);
				}					
			}
			else if ( pvFilter.getSupport4AddClassProperty()) {
				pvFilter.addProperty(lvPropertyName);
			}

		}		
	}
}
