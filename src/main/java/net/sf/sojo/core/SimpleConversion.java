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

import net.sf.sojo.core.reflect.ReflectionHelper;

/**
 * A simple conversion is the transformation from basis classes to a basis class, how String to Double
 * for example.
 * 
 * @author Linke
 *
 */
public abstract class SimpleConversion extends AbstractConversion {

	protected Class<?> fromType = null;
	protected Class<?> toType = null;
	
	public SimpleConversion(Class<?> pvFromType) {
		this(pvFromType, pvFromType);
	}

	public SimpleConversion (Class<?> pvFromType, Class<?> pvToType) {
		if (ReflectionHelper.isSimpleType(pvFromType) == false || ReflectionHelper.isSimpleType(pvToType) == false) {
			throw new IllegalArgumentException("The classes: " + pvFromType + " and " + pvToType + " must be a simple type.");
		}
		fromType = pvFromType;
		toType = pvToType;
	}
	
	public boolean isFromTypeAndToTypeDifferent(Class<?> pvFromtype, Class<?> pvToType) {
		boolean lvIsFromTypeAndToTypeDifferent = false;
		if (fromType.equals(toType) == false) {
			lvIsFromTypeAndToTypeDifferent = true;
		} 
		return lvIsFromTypeAndToTypeDifferent;
	}
	
	public Class<?> getFromType() { 
	  return fromType; 
	}
	
	public Class<?> getToType() { 
	  return toType; 
	}

	@Override
	public boolean isAssignableFrom(Object pvObject) {
		if (pvObject == null) { 
			return false;
		}
		if (pvObject.getClass().equals(fromType)) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		return ReflectionHelper.isSimpleType(pvToType);
	}
	
	public abstract Object convert(final Object pvObject, final Class<?> pvToType);

}
