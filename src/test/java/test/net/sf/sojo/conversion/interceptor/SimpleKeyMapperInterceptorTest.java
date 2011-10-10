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
package test.net.sf.sojo.conversion.interceptor;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;
import net.sf.sojo.core.ConversionContext;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.IterateableMap2MapConversion;
import net.sf.sojo.core.conversion.interceptor.SimpleKeyMapperInterceptor;

public class SimpleKeyMapperInterceptorTest extends TestCase {

	public void testOnError() throws Exception {
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		lvInterceptor.onError(null);
	}
	
	public void testToSimpleAsString() throws Exception {
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		ConversionContext lvContext = new ConversionContext();
		lvContext.numberOfRecursion = 1;
		lvContext.key = "key";

		String lvStrKey = lvContext.numberOfRecursion + SimpleKeyMapperInterceptor.DELIMITER + lvContext.key;
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals(lvStrKey, lvContext.key);
		
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals(lvStrKey, lvContext.key);
	}
	
	public void testToSimpleAsLong() throws Exception {
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		ConversionContext lvContext = new ConversionContext();
		lvContext.numberOfRecursion = 1;
		lvContext.key = new Long(77);

		String lvStrKey = lvContext.numberOfRecursion + SimpleKeyMapperInterceptor.DELIMITER + lvContext.key  
							+ SimpleKeyMapperInterceptor.DELIMITER + lvContext.key.getClass().getName();
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals(lvStrKey, lvContext.key);
		
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals(lvStrKey, lvContext.key);
	}

	public void testToComplexAsString() throws Exception {
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		ConversionContext lvContext = new ConversionContext();
		lvContext.numberOfRecursion = 1;
		lvContext.key = "key";
		lvInterceptor.beforeConvertRecursion(lvContext);
		
		lvInterceptor = new SimpleKeyMapperInterceptor();
		Hashtable lvHashtable = new Hashtable();
		lvHashtable.put(lvContext.key, "result-key");
		Map lvMap = (Map) lvInterceptor.beforeConvert(lvHashtable, null);
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals(lvMap.get("key"), "result-key");
	}
	
	public void testToComplexAsLong() throws Exception {
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		ConversionContext lvContext = new ConversionContext();
		lvContext.numberOfRecursion = 1;
		lvContext.key = new Long(77);
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals("1~_-_~77~_-_~java.lang.Long", lvContext.key);
		
		lvInterceptor = new SimpleKeyMapperInterceptor();
		Hashtable lvHashtable = new Hashtable();
		lvHashtable.put(lvContext.key, new Long(55));
		Map lvMap = (Map) lvInterceptor.beforeConvert(lvHashtable, null);
		lvInterceptor.beforeConvertRecursion(lvContext);
		assertEquals(lvMap.get(new Long(77)), new Long(55));
	}

	public void testResultOrderedMap() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("key_1", "value_1");
		lvMap.put("key_2", "value_2");
		lvMap.put(new Double(12.89), "value_3");
		
		Object lvKeys[] = new Object[3];
		Converter c = new Converter();
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptor); 
		c.addConversion(lvConversion);

		Map lvResultMap = (Map) c.convert(lvMap);

		lvInterceptor.setMakeSimple(false);
		lvResultMap = (Map) c.convert(lvResultMap);
		Iterator lvIterator = lvResultMap.entrySet().iterator();
		int i = 0;
		while (lvIterator.hasNext()) {
			Map.Entry lvEntry = (Entry) lvIterator.next();
			lvKeys[i] = lvEntry.getKey();
			i++;
		}
		assertEquals("key_1", lvKeys[0]);
		assertEquals("key_2", lvKeys[1]);
		assertEquals(new Double(12.89), lvKeys[2]);
	}

	public void testResultOrderedMap2() throws Exception {
		Date lvDate = new Date();
		Map lvMap = new Hashtable();
		lvMap.put("key_1", "value_1");
		lvMap.put("key_2", "value_2");
		lvMap.put(lvDate, "value_date");
		lvMap.put("key_4", "value_4");
		
		Converter c = new Converter();
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptor); 
		c.addConversion(lvConversion);

		Map lvResultMap = (Map) c.convert(lvMap);

		lvConversion.getConverterInterceptorHandler().clear();
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(new SimpleKeyMapperInterceptor());
		lvResultMap = (Map) c.convert(lvResultMap);
		
		assertEquals("value_1", lvResultMap.get("key_1"));
		assertEquals("value_2", lvResultMap.get("key_2"));
		assertEquals("value_date", lvResultMap.get(lvDate));
		assertEquals("value_4", lvResultMap.get("key_4"));
	}

	public void testResultOrderedMapInvalidKey() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("key_1", "value_1");
		lvMap.put("key_2", "value_2");
		
		Converter c = new Converter();
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptor); 
		c.addConversion(lvConversion);

		Map lvResultMap = (Map) c.convert(lvMap);
		String lvStrKey_1 = "2" + SimpleKeyMapperInterceptor.DELIMITER + "key_1";
		String lvStrKey_2 = "1" + SimpleKeyMapperInterceptor.DELIMITER + "key_2";
		assertEquals("value_1", lvResultMap.get(lvStrKey_1));
		assertEquals("value_2", lvResultMap.get(lvStrKey_2));
		
		lvResultMap.put(new Long(99), new Long(99));
		lvInterceptor.setMakeSimple(false);
		try {
			c.convert(lvResultMap);
			fail("Long ist not a valid key-type");
		} catch (ConversionException e) {
			assertNotNull(e);
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof IllegalArgumentException);
		}
	}

	public void testResultOrderedMapInvalidKey2() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("key_1", "value_1");
		lvMap.put("key_2", "value_2");
		
		Converter c = new Converter();
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptor); 
		c.addConversion(lvConversion);

		Map lvResultMap = (Map) c.convert(lvMap);
		lvResultMap.put("key_faul","faul");
		lvInterceptor.setMakeSimple(false);
		try {
			c.convert(lvResultMap);
			fail("The key must contains the delimiter: " + SimpleKeyMapperInterceptor.DELIMITER );
		} catch (ConversionException e) {
			assertNotNull(e);
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof IllegalArgumentException);
		}
	}
	
	public void testResultOrderedMapInvalidKey3() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("key_1", "value_1");
		lvMap.put(new Integer(1), "value_2");
		
		Converter c = new Converter();
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptor); 
		c.addConversion(lvConversion);

		Map lvResultMap = (Map) c.convert(lvMap);
		lvResultMap.put("2~_-_~1~_-_~not.valid.class.Name","faul");
		lvInterceptor.setMakeSimple(false);
		try {
			c.convert(lvResultMap);
			fail("The key must contains the delimiter: " + SimpleKeyMapperInterceptor.DELIMITER );
		} catch (ConversionException e) {
			assertNotNull(e);
			assertNull(e.getCause());
			assertTrue(e instanceof ConversionException);
		}
	}


}
