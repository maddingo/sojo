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
package net.sf.sojo.core.conversion;

import java.lang.reflect.AccessibleObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.sojo.core.ComplexConversion;
import net.sf.sojo.core.IConverter;
import net.sf.sojo.core.IConverterExtension;
import net.sf.sojo.core.NonCriticalExceptionHandler;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import net.sf.sojo.core.reflect.Property;
import net.sf.sojo.core.reflect.ReflectionHelper;
import net.sf.sojo.core.reflect.ReflectionPropertyHelper;
import net.sf.sojo.util.Util;

/**
 * Convert complex bean (this mean a JavaBean) to a <code>java.util.Map</code>.
 * 
 * @author linke
 *
 */
public class ComplexBean2MapConversion extends ComplexConversion {

	public static final Class<?> DEFAULT_MAP_TYPE = LinkedHashMap.class;
	
	private Class<?> newBeanConversionType = null;
	
	public ComplexBean2MapConversion() { this(null);}
	
	public ComplexBean2MapConversion(Class<?> pvBeanType) {
		newBeanConversionType = pvBeanType;
		if (newBeanConversionType == null) {
			newBeanConversionType = DEFAULT_MAP_TYPE;
		}
		if (Map.class.isAssignableFrom(newBeanConversionType) == false) {
			throw new IllegalArgumentException("The class: " + newBeanConversionType + " must be implements the java.util.Map interface.");
		}
		if (newBeanConversionType.isInterface() == true) {
			throw new IllegalArgumentException("The class: " + newBeanConversionType + " mus be a implementation and not a interface.");
		}
	}

	@Override
	public final boolean isAssignableFrom(Object pvObject) {
		return ReflectionHelper.isComplexType(pvObject);
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		return ReflectionHelper.isMapType(pvToType);
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public Object convert(final Object pvObject, final Class<?> pvToType, final IConverterExtension pvConverter) {
		Map<Object,Object> lvBeanMap = null;
		try {
			Class<?> lvToType = ( ( pvToType == null || pvToType.isInterface() ) ? newBeanConversionType : pvToType);
			
			Map<?,?> lvGetterMap = ReflectionPropertyHelper.getAllGetterProperties(pvObject.getClass(), null);
			lvBeanMap = (Map<Object, Object>) ReflectionHelper.createNewIterableInstance(lvToType, lvGetterMap.size()); 

			// filter for synthetic key unique id, this is a specific case
			if (ClassPropertyFilterHelper.isPropertyToFiltering(classPropertyFilterHandler, pvObject.getClass(), UniqueIdGenerator.UNIQUE_ID_PROPERTY)  == false) {
				String lvUniqueId = pvConverter.getUniqueId(pvObject);
				lvBeanMap.put(UniqueIdGenerator.UNIQUE_ID_PROPERTY, lvUniqueId);				
			}
			

			Iterator<?> it = lvGetterMap.entrySet().iterator();
			lvBeanMap = (Map<Object,Object>) super.iterate(pvObject, lvBeanMap, it, pvConverter);
		} catch (Exception e) {
			if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
				NonCriticalExceptionHandler.handleException(ComplexBean2MapConversion.class, e, "Problem by conver bean to map: " + e);
			}
		} 
		return lvBeanMap;
	}

	@Override
	protected Object[] doTransformIteratorObject2KeyValuePair(Object pvIteratorObject) {
		@SuppressWarnings("unchecked")
    Map.Entry<Object,Object> lvMapEntry = (Map.Entry<Object, Object>) pvIteratorObject;
		Object lvKey = lvMapEntry.getKey();
		Object lvValue = lvMapEntry.getValue();
		return new Object[] { lvKey, lvValue };
	}

	@Override
	protected Object[] doConvert(Object pvSourceObject, final Object pvNewTargetObject, Object pvKey, Object pvValue, IConverter pvConverter) {
		String propName = (String) pvKey;
		Object lvNewValue = null;
		if (Util.getKeyWordClass().equals(propName) == false) {
			Object lvValue = null;
			AccessibleObject lvAccessibleObject = null;
     			try {
     				lvAccessibleObject = (AccessibleObject) pvValue;
     				lvValue = new Property(lvAccessibleObject).executeGetValue(pvSourceObject);
     				lvNewValue = pvConverter.convert(lvValue);

     			} catch (Exception e) {
     				if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
     					NonCriticalExceptionHandler.handleException(ComplexBean2MapConversion.class, e, "Problem by invoke get from property: " + lvAccessibleObject);
     				}
     			}
     		
		} 
		return new Object [] { pvKey, lvNewValue };

	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doAddObject(Object pvSourceObject, Object pvNewTargetObject, Object pvKey, Object pvValue, int pvIteratorPosition) {
		Map lvBeanMap = (Map) pvNewTargetObject;
		String propName = (String) pvKey;
		
		if (!Util.getKeyWordClass().equals(propName))  {
			try {
			lvBeanMap.put(propName, pvValue);
			} catch (NullPointerException e) {
				if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
					NonCriticalExceptionHandler.handleException(ComplexBean2MapConversion.class, e, "Try to add a null-value to Map: " + lvBeanMap.getClass().getName());
				}
			}
    } else  {
      //for unmarshalling
      lvBeanMap.put(Util.getKeyWordClass(), pvSourceObject.getClass().getName()); 
    }
	}
}
