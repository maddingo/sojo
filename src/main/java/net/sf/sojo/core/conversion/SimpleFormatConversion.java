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

import java.text.Format;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.SimpleConversion;

/**
 * In cases, where want a text rerpesentation of a type, for example date or number,
 * than can use this conversion, to get formated value.
 * <br>
 * Example for a <code>Date</code>-Type and the formatter:
 * <code>addFormatter(Date.class, new SimpleDateFormat("dd-MM-yyyy"));</code> 
 * 
 * @author linke
 *
 */
public class SimpleFormatConversion extends SimpleConversion {

	private Map<Class<?>,Format> formatter = new HashMap<Class<?>,Format>();
	
	public SimpleFormatConversion() {
		super(String.class);
	}

	public void addFormatter(Class<?> pvType, Format pvFormat) {
		formatter.put(pvType, pvFormat);
	}
	
	public int getFormatterSize() {
		return formatter.size();
	}
	
	public void removeFormatterByType(Class<?> pvType) {
		formatter.remove(pvType);
	}

	@Override
	public boolean isAssignableFrom(Object pvObject) {
		if (pvObject == null) { 
			return false;
		}
		else if (pvObject.getClass().equals(String.class)) {
			return true;
		}
		return formatter.containsKey(pvObject.getClass());
	}
	
	@Override
	public boolean isAssignableTo(final Class<?> pvToType) {
		return formatter.containsKey(pvToType);
	}

	@Override
	public Object convert(Object pvObject, Class<?> pvToType) {
		Object lvReturn = pvObject;
		Class<?> lvClass = lvReturn.getClass();
		Format lvFormat = null;
		if (lvClass.equals(String.class) && pvToType != null) {
			try {
				lvFormat = formatter.get(pvToType);
				if (lvFormat != null) {
					lvReturn = lvFormat.parseObject(lvReturn.toString());
				} 
			} catch (ParseException e) {
				throw new ConversionException("Can't convert value: " + lvReturn + " to: " + pvToType.getName());
			}
		} else {
			lvFormat = formatter.get(lvClass);
			if (lvFormat != null) {
				lvReturn = lvFormat.format(lvReturn);
			}
		}
		return lvReturn;
	}

}
