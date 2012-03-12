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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.sojo.core.IConverter;
import net.sf.sojo.core.IConverterExtension;
import net.sf.sojo.core.IterableConversion;
import net.sf.sojo.core.reflect.ReflectionHelper;

/**
 * Convert map object into map object.
 * 
 * @author linke
 *
 */
public class IterateableMap2MapConversion extends IterableConversion {


	public static final Class<?> DEFAULT_MAP_TYPE = LinkedHashMap.class;
	
	public IterateableMap2MapConversion () {
		this(null, false);
	}
	public IterateableMap2MapConversion (Class<?> pvNewIteratableType) {
		this(pvNewIteratableType, false);
	}
	public IterateableMap2MapConversion (Class<?> pvNewIteratableType, boolean pvIgnoreNullValues) {
		newIteratableType = pvNewIteratableType;
		if (newIteratableType == null) {
			newIteratableType = DEFAULT_MAP_TYPE;
		}
		validateTargetIteratableType(newIteratableType);
		setIgnoreNullValues(pvIgnoreNullValues);
	}
	
	private void validateTargetIteratableType(Class<?> pvIteratableType) {
		if (Map.class.isAssignableFrom(pvIteratableType) == false) {
			throw new IllegalArgumentException("The class: " + pvIteratableType + " must be implements the java.util.Map interface.");
		}
		if (pvIteratableType.isInterface() == true) {
			throw new IllegalArgumentException("The class: " + pvIteratableType + " must be a implementation and not an interface.");
		}		
	}

	@Override
	public boolean isAssignableFrom(Object pvObject) {
		boolean lvReturn = false;
		if (ReflectionHelper.isMapType(pvObject) == true && ReflectionHelper.isComplexMapType(pvObject) == false) {
			lvReturn = true;
		}
		return lvReturn;
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		return ReflectionHelper.isMapType(pvToType);
	}

	@Override
	public Object convert(final Object pvObject, final Class<?> pvToType, IConverterExtension pvConverter) {
		Class<?> lvToType = ( ( pvToType == null || pvToType.isInterface() ) ? newIteratableType : pvToType);
		final Map<?,?> lvOldMap = (Map<?,?>) pvObject;
		final Map<?,?> lvNewMap = (Map<?,?>) ReflectionHelper.createNewIterableInstance(lvToType, lvOldMap.size());
		Iterator<?> iter = lvOldMap.entrySet().iterator();
		return super.iterate(pvObject, lvNewMap, iter, pvConverter);
	}

	@Override
	protected Object[] doTransformIteratorObject2KeyValuePair(Object pvIteratorObject) {
		Map.Entry<?,?> lvMapEntry = (Map.Entry<?, ?>) pvIteratorObject;
		Object lvKey = lvMapEntry.getKey();
		Object lvValue = lvMapEntry.getValue();
		return new Object[] { lvKey, lvValue };
	}

	@Override
	protected Object[] doConvert(Object pvSourceObject, final Object pvNewTargetObject, Object pvKey, Object pvValue, IConverter pvConverter) {
		Object lvKey = pvConverter.convert(pvKey);
		Object lvValue = pvConverter.convert(pvValue);
		return new Object[] { lvKey, lvValue };
	}

	@Override
	protected void doAddObject (Object pvSourceObject, Object pvNewTargetObject, Object pvKey, Object pvValue, int pvIteratorPosition) {
		@SuppressWarnings("unchecked")
		Map<Object,Object> lvNewMap = (Map<Object,Object>) pvNewTargetObject;
		lvNewMap.put(pvKey, pvValue);
	}

}
