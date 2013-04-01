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

import java.util.Iterator;

import net.sf.sojo.core.filter.ClassPropertyFilterHandler;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import net.sf.sojo.util.CycleDetector;
import net.sf.sojo.util.Util;

/**
 * Help to harmonise the iteration from Collection, Array and Map. All container ought to the same kind of doing.
 * 
 * @author Linke
 *
 */
public abstract class ConversionIterator extends AbstractConversion {
	
	private boolean ignoreNullValues = false;
	private boolean withCycleDetection = false;
	protected CycleDetector cycleDetector = new CycleDetector();
	protected ClassPropertyFilterHandler classPropertyFilterHandler = null;

	public boolean getIgnoreNullValues() { return ignoreNullValues; }
	public void setIgnoreNullValues(boolean pvIgnoreNullValues) { ignoreNullValues = pvIgnoreNullValues; }
	
	public boolean getWithCycleDetection() { return withCycleDetection; }
	public void setWithCycleDetection(boolean pvWithCycleDetection) { withCycleDetection = pvWithCycleDetection; }

	/**
	 * Handle filter for class properties.
	 * 
	 * @param pvClassPropertyFilterHandler
	 */
	public void setClassPropertyFilterHandler(ClassPropertyFilterHandler pvClassPropertyFilterHandler) {
		classPropertyFilterHandler = pvClassPropertyFilterHandler;
	}

	/**
	 * This method is calling by every iteration. They seperated from the iterating object, the key and the value.
	 * 
	 * @param pvIteratorObject The current object from the iterator.
	 * @return By Map is the first position the key and the second the value. By Collection is the key null and the value is the value.
	 */
	protected abstract Object[] doTransformIteratorObject2KeyValuePair(Object pvIteratorObject);
	
	/**
	 * This is the really converting method.
	 * 
	 * @param pvSourceObject The original Collection, Map and so on.
	 * @param pvNewTargetObject To the converting object (e.g. the bean). 
	 * @param pvKey Extract from the doTransformIteratorObject2KeyValuePair-method.
	 * @param pvValue Extract from the doTransformIteratorObject2KeyValuePair-method.
	 * @param pvConverter The Converter, from the convert method. With the Converter can do the recursion.
	 * @return Object-Array from the length 2, where pos one is the key and pos second is the value
	 */
	protected abstract Object[] doConvert(Object pvSourceObject, final Object pvNewTargetObject, Object pvKey, Object pvValue, IConverter pvConverter);
	
	/**
	 * After the converting, cann add the result to the map or collection.
	 * 
	 * @param pvSourceObject The original Collection, Map and so on.
	 * @param pvNewTargetObject To the converting object (e.g. the bean).
	 * @param pvKey Extract from the doTransformIteratorObject2KeyValuePair-method.
	 * @param pvValue Extract from the doTransformIteratorObject2KeyValuePair-method.
	 */
	protected abstract void doAddObject (Object pvSourceObject, Object pvNewTargetObject, Object pvKey, Object pvValue, int pvIteratorPosition);
	

	/**
	 * This is the main iterate method. This method has a counter of recursion and handle the event before and after conversion.
	 * This method is calling from the convert-method.
	 * The order of calling methods is:
	 * <ol>
	 *  <li>doTransformIteratorObject2KeyValuePair
	 *  <li>doConvert
	 *  <li>doAddObject
	 *  </ol>
	 * 
	 * @param pvSourceObject The old Collection, Array or Map, where object (from iterator) ought to read.
	 * @param pvNewTargetObject The new Collection, Array, Map or Bean, where object (from iterator) ought to added.
	 * @param pvIterator The iterator him self.
	 * @param pvConverter Instance of Converter, for recursive execution.
	 * @return The new Collection, Array or Map, where all object (from iterator) has added.
	 */
	protected final Object iterate(final Object pvSourceObject, final Object pvNewTargetObject, final Iterator<?> pvIterator, final IConverterExtension pvConverter) {
		int pos = 0;
		
		// cycle detection for Collections
		if (getWithCycleDetection() && cycleDetector.cycleDetection(pvSourceObject)) {
			throw new ConversionException("Detected cycle in Collection.");
		}

		Iterator<?> iter = pvIterator;
		while (iter.hasNext()) {
			Object lvIteratorObject = iter.next();
			Object lvKeyValue[] = doTransformIteratorObject2KeyValuePair(lvIteratorObject);
			
			if (ClassPropertyFilterHelper.isPropertyToFiltering(classPropertyFilterHandler, pvSourceObject.getClass(), lvKeyValue[0])  == false) {
				ConversionContext lvContext = fireBeforeConvertRecursion( (pos + 1), lvKeyValue[0], lvKeyValue[1]);
				if (!lvContext.cancelConvert) {
					lvKeyValue = doConvert(pvSourceObject, pvNewTargetObject, lvContext.key, lvContext.value, pvConverter);
					Object lvValue = lvKeyValue[1];
//					if (lvValue != null && lvValue.toString() != null && lvValue.toString().startsWith(UniqueIdGenerator.UNIQUE_ID_PROPERTY)) {
//						String lvKey = lvValue.toString().substring(UniqueIdGenerator.UNIQUE_ID_PROPERTY.length());
//						Object o = pvConverter.getObjectByUniqueId(lvKey);
//						if (o != null) {
//							lvKeyValue[1] = o;						
//						}
//					}
				}
				
				lvContext = fireAfterConvertRecursion(lvContext, lvKeyValue[0], lvKeyValue[1]);
				if ((!lvContext.cancelConvert) &&
					 ((lvKeyValue[1] != null || getIgnoreNullValues() == false) || (lvKeyValue[0] != null && lvKeyValue[0].equals(Util.getKeyWordClass())))) {
						this.doAddObject(pvSourceObject, pvNewTargetObject, lvKeyValue[0], lvKeyValue[1], pos);
				}
			}
			pos++;
		}
		return pvNewTargetObject;
	}
		
	/**
	 * Create a new ConversionContext and fire "before convert recursion" event.
	 *  
	 * @param pvNumberOfIteration Counter for the number of recursion.
	 * @param pvKey The key can be the key by the <code>Map</code> or the property name by a JavaBean.
	 * @param pvValue The value is the map-value or the value in a list or the property-value from a JavaBean.
	 * @return New ConversionContext.
	 */
	private ConversionContext fireBeforeConvertRecursion(int pvNumberOfIteration, Object pvKey, Object pvValue) {
		ConversionContext lvContext = new ConversionContext(pvNumberOfIteration, pvKey, pvValue);
		getConverterInterceptorHandler().fireBeforeConvertRecursion(lvContext);			
		return lvContext;
	}
	
	/**
	 * Get a ConversionContext and fire "after convert recursion" event.
	 * 
	 * @param pvContext The ConversionContext.
	 * @param pvKey The key can be the key by the <code>Map</code> or the property name by a JavaBean.
	 * @param pvValue The value is the map-value or the value in a list or the property-value from a JavaBean.
	 * @return The ConversionContext with new or old keys and values.
	 */
	private ConversionContext fireAfterConvertRecursion(final ConversionContext pvContext, Object pvKey, Object pvValue) {
		pvContext.key = pvKey;
		pvContext.value = pvValue;
		getConverterInterceptorHandler().fireAfterConvertRecursion(pvContext);
		return pvContext;
	}

}
