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
package net.sf.sojo.core.conversion.interceptor;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.sojo.core.ConversionContext;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.ConverterInterceptorRecursive;
import net.sf.sojo.core.reflect.ReflectionHelper;

/**
 * A key from a map, where this key is extending to information of the number from inserting in the map
 * and the datatype from the key.
 * 
 * @author Linke
 *
 */
public class SimpleKeyMapperInterceptor implements ConverterInterceptorRecursive {

	public static final String DELIMITER = "~_-_~";	
	
	private boolean makeSimple = false;
	
	public SimpleKeyMapperInterceptor() {	
	}

	public SimpleKeyMapperInterceptor(boolean pvMakeSimple) {
		setMakeSimple(pvMakeSimple);
	}
	
	public void setMakeSimple(boolean pvMakeSimple) { 
		makeSimple = pvMakeSimple; 
	}
	
	public boolean getMakeSimple() { 
		return makeSimple;  
	}
	
	@Override
	public void beforeConvertRecursion(ConversionContext pvContext) {
		if (getMakeSimple() == true) {
			toSimple(pvContext);
		} 
	}
		
	@Override
	public Object beforeConvert(final Object pvConvertObject, final Class<?> pvToType) {
		Object lvReturn = null;
		if (getMakeSimple() == false) {
			@SuppressWarnings("unchecked")
			Map<Object,Object> lvMap = (Map<Object,Object>) pvConvertObject;
			
			Map<Object, Object> lvHashMap = new LinkedHashMap<Object, Object>(lvMap.size());
			SortedMap<SimpleKeyComparator, Object> lvOrderedMap = map2SortedMap(lvMap);
			for (SimpleKeyComparator skc : lvOrderedMap.keySet()) {
				lvHashMap.put(skc.getKey(), lvOrderedMap.get(skc));
			}
			lvReturn = lvHashMap;
		} else {
			lvReturn = pvConvertObject;
		}
		
		return lvReturn;
	}

	protected void toSimple (ConversionContext pvContext) {
		String lvNewKey = null; 
		if (pvContext.key instanceof Date) {
			lvNewKey = Long.toString(((Date) pvContext.key).getTime());
		} else {
			lvNewKey = pvContext.key.toString();
		}
    	int l = pvContext.key.toString().split(DELIMITER).length;
    	if (l < 2) {
			if (pvContext.key.getClass().equals(String.class)) {
				lvNewKey = pvContext.numberOfRecursion + DELIMITER + lvNewKey;
			} else {
				lvNewKey = pvContext.numberOfRecursion + DELIMITER + lvNewKey + DELIMITER + pvContext.key.getClass().getName();
			}
    	} else {
    		lvNewKey = pvContext.key.toString();
    	}
		pvContext.key = lvNewKey;
	}
	
	protected SimpleKeyComparator toComplex (Object pvKey) {
		if ( ! (pvKey instanceof String) ) {
			throw new IllegalArgumentException("Expected String, but key is: " + pvKey  + " -> class: " + pvKey.getClass().getName());
		}
		String lvKeyStr = (String) pvKey;
		String lvKeyArray[] = lvKeyStr.split(DELIMITER);
		if (lvKeyArray.length < 2) {
			throw new IllegalArgumentException("The key must contains " +
					" the delimiter: " + DELIMITER + " - " + lvKeyStr + " (" + lvKeyArray.length + ")");
		}
		
		String lvPosStr = lvKeyArray[0];
		Integer integer = Integer.valueOf(lvPosStr);
		int lvPos = integer.intValue();

		String lvKeyValue = lvKeyArray[1];
		Object lvKey = null;
		if (lvKeyArray.length == 3) {
			String lvKeyClass = lvKeyArray[2];
			try {
				Class<?> clazz = ReflectionHelper.forName(lvKeyClass);
				lvKey = ReflectionHelper.createNewSimpleObject(clazz, lvKeyValue);				
			} catch (Exception e) {
				throw new ConversionException("Can't create a new instance of class: " + lvKeyClass + " with value: " + lvKeyValue);
			}
		} else {
			lvKey = lvKeyValue;
		}
		return new SimpleKeyComparator(lvPos, lvKey);
	}
	
	protected SortedMap<SimpleKeyComparator, Object> map2SortedMap (Map<?,?> pvMap) {
		SortedMap<SimpleKeyComparator, Object> lvTreeMap = new TreeMap<SimpleKeyComparator, Object>(new SimpleKeyComparator());
		for (Map.Entry<?,?> entry : pvMap.entrySet()) {
			SimpleKeyComparator skc = toComplex(entry.getKey());
			lvTreeMap.put(skc, entry.getValue());
		}
		return lvTreeMap;
	}
	
	@Override
	public void afterConvertRecursion(ConversionContext pvContext) { 
	}
	
	@Override
	public Object afterConvert(final Object pvResult, final Class<?> pvToType) { 
		return pvResult; 
	}
	
	@Override
	public void onError(Exception pvException) {}

	private static class SimpleKeyComparator implements Comparator<SimpleKeyComparator>, Serializable {

		private static final long serialVersionUID = 7181951816535396688L;
		
		private int pos = -1;
		private Object key;
		
		public SimpleKeyComparator() { 
		}
		
		public SimpleKeyComparator(int pvPos, Object pvKey) { 
			pos = pvPos; 
			key = pvKey;
		}

		public int getPos() { 
			return pos; 
		}
		
		public Object getKey() { 
			return key; 
		}
		
		@Override
		public int compare(SimpleKeyComparator pvO1, SimpleKeyComparator pvO2) {
			int res = pvO1.getPos() - pvO2.getPos();
			if (res > 0) {
				return 1;
			} else if (res == 0) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}
