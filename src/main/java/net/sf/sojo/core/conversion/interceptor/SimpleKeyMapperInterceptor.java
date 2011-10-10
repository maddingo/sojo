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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

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
	private Map keyMapper = new TreeMap(new SimpleKeyComparator());
	
	public SimpleKeyMapperInterceptor() {	}

	public SimpleKeyMapperInterceptor(boolean pvMakeSimple) {
		setMakeSimple(pvMakeSimple);
	}
	
	public void setMakeSimple(boolean pvMakeSimple) { makeSimple = pvMakeSimple; }
	public boolean getMakeSimple() { return makeSimple;  }

	
	public void beforeConvertRecursion(ConversionContext pvContext) {
		if (getMakeSimple() == true) {
			toSimple(pvContext);
		} 
	}

		
	public Object beforeConvert(final Object pvConvertObject, final Class pvToType) {
		Object lvReturn = null;
		if (getMakeSimple() == false) {
			Map lvMap = (Map) pvConvertObject;
			
			Map lvOrderedMap = map2SortedMap(lvMap);
            Iterator it = lvOrderedMap.entrySet().iterator();
            Hashtable lvHashtable = new Hashtable(lvMap.size());
            while (it.hasNext()) {
            	Map.Entry lvMapEntry = (Entry) it.next();
            	SimpleKeyComparator lvKeyComparator = (SimpleKeyComparator) lvMapEntry.getKey();
            	Object lvKey = lvKeyComparator.getKey();
            	Object lvValue = lvMapEntry.getValue();
            	lvHashtable.put(lvKey, lvValue);
            }
			lvReturn = lvHashtable;
		} else {
			lvReturn = pvConvertObject;
		}
		
		keyMapper.clear();
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
				Class clazz = ReflectionHelper.forName(lvKeyClass);
				lvKey = ReflectionHelper.createNewSimpleObject(clazz, lvKeyValue);				
			} catch (Exception e) {
				throw new ConversionException("Can't create a new instance of class: " + lvKeyClass + " with value: " + lvKeyValue);
			}
		} else {
			lvKey = lvKeyValue;
		}
		return new SimpleKeyComparator(lvPos, lvKey);
	}
	
	protected Map map2SortedMap (Map pvMap) {
		Iterator it = pvMap.entrySet().iterator();
		TreeMap lvTreeMap = new TreeMap(new SimpleKeyComparator());
		while (it.hasNext()) {
			Map.Entry lvEntry = (Entry) it.next();
			Object lvKey = lvEntry.getKey();
			Object lvValue = lvEntry.getValue();
			SimpleKeyComparator skm = toComplex(lvKey);
			lvTreeMap.put(skm, lvValue);
		}
		return lvTreeMap;
	}
	

	
	public void afterConvertRecursion(ConversionContext pvContext) { }
	public Object afterConvert(final Object pvResult, final Class pvToType) { return pvResult; }
	public void onError(Exception pvException) { }

	
	
	
	private static class SimpleKeyComparator implements Comparator, Serializable {

		private static final long serialVersionUID = 2873440526210140656L;
		
		private int pos = -1;
		private Object key = null;
		
		public SimpleKeyComparator() { 
			
		}
		public SimpleKeyComparator(int pvPos, Object pvKey) { 
			pos = pvPos; 
			key = pvKey;
		}

		public int getPos() { return pos; }
		public Object getKey() { return key; }
		
		public int compare(Object pvO1, Object pvO2) {
			SimpleKeyComparator skm1 = (SimpleKeyComparator) pvO1;
			SimpleKeyComparator skm2 = (SimpleKeyComparator) pvO2;
			if (skm1.getPos() > skm2.getPos()) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}
}
