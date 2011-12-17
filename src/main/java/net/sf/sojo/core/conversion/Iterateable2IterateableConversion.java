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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.IConverter;
import net.sf.sojo.core.IConverterExtension;
import net.sf.sojo.core.IterableConversion;
import net.sf.sojo.core.reflect.ReflectionHelper;
import net.sf.sojo.util.ArrayIterator;


/**
 * Convert iterateable object (list, set, array or map) into iterateable object.
 * 
 * @author linke
 *
 */
public class Iterateable2IterateableConversion extends IterableConversion {

	public static final Class<?> DEFAULT_COLLECTION_TYPE = ArrayList.class;

	
	public Iterateable2IterateableConversion() {
		this(null);
	}
	
	
	public Iterateable2IterateableConversion(Class<?> pvNewIteratableType) {
		newIteratableType = pvNewIteratableType;
		if (newIteratableType == null) {
			newIteratableType = DEFAULT_COLLECTION_TYPE;
		}
		validateTargetIteratableType(newIteratableType);
	}
	
	private void validateTargetIteratableType(Class<?> pvIteratableType) {
		if (Collection.class.isAssignableFrom(pvIteratableType) == false) {
			throw new IllegalArgumentException("The class: " + pvIteratableType + " must be implements the java.util.Collection interface.");
		}
		if (pvIteratableType.isInterface() == true) {
			throw new IllegalArgumentException("The class: " + pvIteratableType + " must be a implementation and not an interface.");
		}		
	}

	@Override
	public final boolean isAssignableFrom(Object pvObject) {
		return ReflectionHelper.isIterableType(pvObject);
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		return ReflectionHelper.isIterateableType(pvToType);
	}

	
	@Override
	public Object convert(final Object pvObject, final Class<?> pvToType, final IConverterExtension pvConverter) {
		Iterator<?> it = null; 
		int size = 0;

		if (pvObject.getClass().isArray()) {
			it = new ArrayIterator(pvObject);
			size = ((ArrayIterator) it).getLength();
		} else {
			Collection<?> lvOldList = (Collection<?>) pvObject;
			size = lvOldList.size();
			it = lvOldList.iterator();
		}
		
		// either Array, Set or List
		Object lvNewTargetType = null;
		// Array
		if (pvToType != null && pvToType.isArray()) {
			int lvSize = 0;
			if (pvObject instanceof Collection) {
				Collection<?> lvOldList = (Collection<?>) pvObject;
				lvSize = lvOldList.size();
			} else {
				Object lvObjectArray[] = (Object[]) pvObject;
				lvSize = lvObjectArray.length;
			}
			lvNewTargetType = Array.newInstance(pvToType.getComponentType(), lvSize);
		} else {
			Class<?> lvToType = ( ( pvToType == null || pvToType.isInterface() ) ? newIteratableType : pvToType);
			// Set
			if (pvToType != null && pvToType.isAssignableFrom(Set.class)  && ( ! lvToType.isAssignableFrom(Set.class) ) && pvObject instanceof Collection) {				
				lvNewTargetType = ReflectionHelper.createNewIterableInstance(HashSet.class, size);
			} 
			// List
			else {
				lvNewTargetType = ReflectionHelper.createNewIterableInstance(lvToType, size);
			}
		}
		return super.iterate(pvObject, lvNewTargetType, it, pvConverter);
	}

	

	@Override
	protected Object[] doTransformIteratorObject2KeyValuePair(Object pvIteratorObject) {
		return new Object [] { null, pvIteratorObject};
	}

	@Override
	protected Object[] doConvert(Object pvSourceObject, final Object pvNewTargetObject, Object pvKey, Object pvValue, IConverter pvConverter) {
		Object lvValueAfterConvert = pvConverter.convert(pvValue);
		return new Object [] { null, lvValueAfterConvert };
	}

	@Override
	protected void doAddObject(Object pvSourceObject, Object pvNewTargetObject, Object pvKey, Object pvValue, int pvIteratorPosition) {
		if (pvNewTargetObject.getClass().isArray() == false) {
			@SuppressWarnings("unchecked")
			Collection<Object> lvCollection = (Collection<Object>) pvNewTargetObject;
			lvCollection.add(pvValue);
		} else {
			
			// transform value, is this a good idea?
			// pvValue.getClass != clazz
			try {
				Class<?> clazz = pvNewTargetObject.getClass().getComponentType();
				if (pvValue != null && pvValue.getClass().equals(clazz) == false) {
					pvValue = ReflectionHelper.createNewSimpleObject(clazz, pvValue);
				}
			} catch (InstantiationException e) {
				throw new ConversionException(e.getMessage(), e);
			}
			
			Array.set(pvNewTargetObject, pvIteratorPosition, pvValue);
		}
	}
	
}
