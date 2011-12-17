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

import java.sql.Time;
import java.sql.Timestamp;

import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.SimpleConversion;
import net.sf.sojo.core.reflect.ReflectionHelper;

/**
 * Convert simple object (String) into simple object (Long, Double and so on). 
 * 
 * @author linke
 *
 */
public class Simple2SimpleConversion extends SimpleConversion {
	
	public Simple2SimpleConversion(Class<?> pvFromType) {
		super(ReflectionHelper.mapFromSimpeToWrapper(pvFromType));
	}

	public Simple2SimpleConversion(Class<?> pvFromType, Class<?> pvToType) {
		super(ReflectionHelper.mapFromSimpeToWrapper(pvFromType), ReflectionHelper.mapFromSimpeToWrapper(pvToType));
	}

	private Object createNewSimpleObject(final Object pvObject, Class<?> pvType) {
		Object lvParam = pvObject;
		
        if (pvObject.getClass().equals(java.sql.Date.class) ||
        				pvObject.getClass().equals(java.util.Date.class) ||
        				pvObject.getClass().equals(Time.class) ||
        				pvObject.getClass().equals(Timestamp.class)) {
        	lvParam = new Long(((java.util.Date) pvObject).getTime());
        }
        
        Object lvNewObject = null;
        try {
        	lvNewObject = ReflectionHelper.createNewSimpleObject(pvType, lvParam);
		} catch (Exception e) {
			throw new ConversionException(e.getMessage(), e);
		}
		return lvNewObject;
	}

	@Override
	public Object convert(final Object pvObject, final Class<?> pvToType) {
		Class<?> lvToType = (pvToType == null ? toType : pvToType);
		Object lvReturn = pvObject;
		if (isFromTypeAndToTypeDifferent(pvObject.getClass(), lvToType)) {
			lvReturn = createNewSimpleObject(pvObject, lvToType);
		}
		return lvReturn;
	}

}
