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
import java.util.Iterator;
import java.util.Map;

import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.IConverter;
import net.sf.sojo.core.IConverterExtension;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.reflect.Property;
import net.sf.sojo.core.reflect.ReflectionHelper;
import net.sf.sojo.core.reflect.ReflectionPropertyHelper;

/**
 * Convert map into bean (JavaBean) object.
 * 
 * @author linke
 *
 */
public class IterateableMap2BeanConversion extends IterateableMap2MapConversion {

	private Class<?> toType = null;
	
	public IterateableMap2BeanConversion() { }

	@Override
	public boolean isAssignableFrom(Object pvObject) {
		boolean lvReturn = false;
		if (pvObject != null && toType != null) {
			lvReturn = ReflectionHelper.isComplexType(toType);
		}
		if (lvReturn == false) {
			lvReturn = ReflectionHelper.isComplexMapType(pvObject);
		}
		// remove toType (reset)
		toType = null;
		return lvReturn;
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		toType = pvToType;
		return ReflectionHelper.isComplexMapType(pvToType);
	}
	
	@Override
	public Object convert(final Object pvObject, final Class<?> pvToType, IConverterExtension pvConverter) {
		final Map<?,?> lvOldMap = (Map<?,?>) pvObject;
		Object beanObject = null;
		try {
			beanObject = ReflectionHelper.createBeanFromMap(lvOldMap, pvToType);
		} catch (Exception e) {
			throw new ConversionException(e.getMessage(), e);
		}
		String lvUniqueId = (String) lvOldMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		if (lvUniqueId != null) {
			pvConverter.addObject(lvUniqueId, beanObject);
		}
		
		String lvFilter[] = (String[]) lvOldMap.keySet().toArray(new String[0]);
		
		
		Map<?,?> lvSetterMap = ReflectionPropertyHelper.getAllSetterProperties(beanObject.getClass(), lvFilter);
		Iterator<?> iter = lvSetterMap.entrySet().iterator();
		Object lvReturn = super.iterate(pvObject, beanObject, iter, pvConverter);
		return lvReturn;
	}

	@Override
	protected Object[] doConvert(Object pvSourceObject, Object pvNewTargetObject, Object pvKey, Object pvValue, IConverter pvConverter) {
		
		Object lvReturnValue = null;

		AccessibleObject lvAccessibleObject = (AccessibleObject) pvValue;
		Property lvProperty = new Property(lvAccessibleObject);
		Class<?> lvParamType = lvProperty.getParameterType();

		Map<?,?> lvMap = (Map<?,?>) pvSourceObject;
		Object lvValue = lvMap.get(pvKey);
		if (lvValue != null && lvValue.toString().startsWith(UniqueIdGenerator.UNIQUE_ID_PROPERTY)) {
			String lvKey = lvValue.toString().substring(UniqueIdGenerator.UNIQUE_ID_PROPERTY.length());
			lvReturnValue = ((IConverterExtension) pvConverter).getObjectByUniqueId(lvKey);
		} else {
			lvReturnValue = pvConverter.convert(lvValue, lvParamType);
		}

		try {
			if (lvReturnValue != null)  {
				Class<?> lvClass = ReflectionHelper.mapFromSimpeToWrapper(lvReturnValue.getClass());
				Class<?> lvParamClass = ReflectionHelper.mapFromSimpeToWrapper(lvParamType);
				
				// method-param-type and param-value are incompatible, try to recover the situation
				if ( ! lvParamClass.isAssignableFrom(lvClass) ) {
					// convert value to the desired method-param-type
					lvReturnValue = ReflectionHelper.createNewSimpleObject(lvParamClass, lvReturnValue);
					// second try
					if ( ! lvParamClass.isAssignableFrom(lvReturnValue.getClass()) ) {
						throw new ConversionException("Can't execute property: " + lvAccessibleObject + ". Parameter are not assignable: " + lvParamClass + " <--> " + lvClass +
								" and value: " + lvReturnValue);
					}
				 }
			}
			lvProperty.executeSetValue(pvNewTargetObject, lvReturnValue);
		} catch (Exception e) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			} else {
				throw new ConversionException("Can't invoke set property: " + lvAccessibleObject + " with arg: " + lvReturnValue, e);
			}
		} 
		
		return new Object[] { pvKey, lvReturnValue };
	}

	@Override
	protected void doAddObject (Object pvSourceObject, Object pvNewTargetObject, Object pvKey, Object pvValue, int pvIteratorPosition) {
		// do nothing, overwrite the super method
	}
}
