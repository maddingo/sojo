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

import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.SimpleConversion;

/**
 * Thrown for all register classes (all not supported classes) a <code>ConversionException</code>.
 * 
 * @author linke
 *
 */
public class NotSupportedClassConversion extends SimpleConversion {

	protected Class<?> notSupportedClassArray[] = null;
		
	public NotSupportedClassConversion() {
		this(null);
	}
	
	public NotSupportedClassConversion(Class<?> pvNotSupportedClassArray[]) {
		super(String.class);
		if (pvNotSupportedClassArray != null) {
			notSupportedClassArray = pvNotSupportedClassArray;
		} else {
			notSupportedClassArray = new Class[0];
		}
	}
	
	@Override
	public boolean isAssignableFrom(final Object pvObject) {
		if (pvObject == null) {
			return isAssignableTo(null);
		} else {
			return isAssignableTo(pvObject.getClass());
		}
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		for (int i=0; i<notSupportedClassArray.length; i++) {
			if (notSupportedClassArray[i] == null && pvToType == null) {
				return true;
			} else if (pvToType != null && pvToType.equals(notSupportedClassArray[i])) {
				return true;
			}
		}
		return false;		
	}

	
	@Override
	public Object convert(Object pvObject, Class<?> pvToType) {
		throw new ConversionException("Not supported Conversion for Object: " + pvObject + " (" + pvToType + ").");
	}

}
