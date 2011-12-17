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

import net.sf.sojo.core.SimpleConversion;

/**
 * Handle <code>null</code> objects. <code>Null</code> - objects can replace with a placeholder.
 * 
 * @author linke
 *
 */
public class NullConversion extends SimpleConversion {

	private Object nullReplaceObject = null;
	private Class<?> fromType = null;	
	
	public NullConversion(Object pvNullReplaceObject) {
		super(String.class, String.class);
		fromType = (pvNullReplaceObject == null ? String.class : pvNullReplaceObject.getClass());
		toType = fromType;
		setNullReplaceObject(pvNullReplaceObject);
	}

	public Object getNullReplaceObject() { 
		return nullReplaceObject; 
	}
	
	public final void setNullReplaceObject(Object pvNullReplaceObject) { 
		nullReplaceObject = pvNullReplaceObject; 
	}
	
	@Override
	public boolean isAssignableFrom(Object pvObject) {
		if (pvObject == null) {
			return true;
		} else if (pvObject.equals(getNullReplaceObject())) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		boolean b = fromType.isAssignableFrom(pvToType);
		return b;
	}
	
	@Override
	public Object convert(final Object pvObject, final Class<?> pvToType) {
		if (pvObject == null) {
			return getNullReplaceObject();
		} else {
			// convert back to null-value
			return null;
		}
	}
}
