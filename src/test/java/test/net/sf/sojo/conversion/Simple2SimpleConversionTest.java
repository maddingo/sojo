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
package test.net.sf.sojo.conversion;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import test.net.sf.sojo.model.Node;

import net.sf.sojo.core.ConversionContext;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.ConverterInterceptorRecursive;
import net.sf.sojo.core.conversion.Iterateable2IterateableConversion;
import net.sf.sojo.core.conversion.NullConversion;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;

import junit.framework.TestCase;

public class Simple2SimpleConversionTest extends TestCase {

	public void testSimpleNull() throws Exception {
		Converter c = new Converter();
		Object o = null;
		Object lvResult = c.convert(o);
		assertNull(lvResult);
	}

	public void testSimpleConversionNull() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Integer.class, Integer.class));
		Object o = null;
		Object lvResult = c.convert(o);
		assertNull(lvResult);
		assertEquals(o, lvResult);		
	}

	public void testSimpleInteger() throws Exception {
		Converter c = new Converter();
		Object o = Integer.valueOf("57");
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Integer.class, lvResult.getClass());
		assertEquals(o, lvResult);
	}

	public void testSimpleInteger2() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Integer.class));
		Object o = Integer.valueOf("57");
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Integer.class, lvResult.getClass());
		assertEquals(o, lvResult);
	}

	public void testSimpleInteger3() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NullConversion("---Null-Value-String---"));
		c.addConversion(new Simple2SimpleConversion(Integer.class));
		Object o = Integer.valueOf("57");
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Integer.class, lvResult.getClass());
		assertEquals(o, lvResult);
	}

	public void testSimpleMultiple() throws Exception {
		Converter c = new Converter();
		Object o = Integer.valueOf("57");
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Integer.class, lvResult.getClass());
		assertEquals(o, lvResult);
		
		o = new Double("57.4");
		lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Double.class, lvResult.getClass());
		assertEquals(o, lvResult);

	}

	
	public void testSimpleConversionInterger2Integer() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Integer.class, Integer.class));
		Object o = Integer.valueOf("57");
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Integer.class, lvResult.getClass());
		assertEquals(o, lvResult);		
	}

	public void testSimpleConversionLong2Integer() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, Integer.class));
		Object o = new Long(57);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Integer.class, lvResult.getClass());
		assertEquals(Integer.valueOf("57"), lvResult);		
	}
	
	public void testSimpleConversionLong2IntegerCancelConvert() throws Exception {
		Converter c = new Converter();
		c.addConverterInterceptor(new ConverterInterceptorRecursive () {

			@Override
			public Object afterConvert(Object pvResult, final Class<?> pvToType) { return pvResult; }
			
			@Override
			public Object beforeConvert(Object pvConvertObject, final Class<?> pvToType) { return pvConvertObject; }
			
			@Override
			public void onError(Exception pvException) {}
			
			@Override
			public void afterConvertRecursion(ConversionContext pvContext) { }

			@Override
			public void beforeConvertRecursion(ConversionContext pvContext) {
				pvContext.cancelConvert = true;
			}

			
		});
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, Integer.class));
		Object o = new Long(57);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Long.class, lvResult.getClass());
		assertEquals(new Long(57), lvResult);		
	}


	public void testSimpleConversionLong2String() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, String.class));
		Object o = new Long(57);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(String.class, lvResult.getClass());
		assertEquals("57", lvResult);		
	}

	public void testSimpleConversionLong2StringWithException() throws Exception {
		Converter c = new Converter();
		c.setThrowExceptionIfNoConversionFind(true);
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, String.class));
		Object o = Integer.valueOf("57");
		try {
			c.convert(o);	
			fail("Must throw an ConvertException, for Integer is no conversion register.");
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}

	public void testDoubleSimpleConversionLong2String() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, String.class));
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, String.class));
		Object o = new Long(59);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(String.class, lvResult.getClass());
		assertEquals("59", lvResult);		
	}

	public void testSimpleConversionCharacter2String() throws Exception {
		Converter converter = new Converter();
		converter.addConversion(new Simple2SimpleConversion(Character.class, String.class));
		Character c = new Character('A');
		Object lvResult = converter.convert(c);
		assertNotNull(lvResult);
		assertEquals(String.class, lvResult.getClass());
		assertEquals("A", lvResult);		
	}

	
	public void testSimpleConversionString2Double() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(String.class, Double.class));
		Object o = "123.456";
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Double.class, lvResult.getClass());
		assertEquals(new Double("123.456"), lvResult);		
	}

	public void testSimpleConversionLong2Date() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Long.class, Date.class));
		long lvDate = new Date().getTime();
		Object o = new Long(lvDate);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Date.class, lvResult.getClass());
		assertEquals(new Date(lvDate), lvResult);		
	}

	public void testSimpleConversionDate2Long() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Date.class, Long.class));
		long lvDate = new Date().getTime();
		Object o = new Date(lvDate);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Long.class, lvResult.getClass());
		assertEquals(new Long(lvDate), lvResult);		
	}

	public void testSimpleConversionTime2Long() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Time.class, Long.class));
		long lvDate = new Date().getTime();
		Object o = new Time(lvDate);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(Long.class, lvResult.getClass());
		assertEquals(new Long(lvDate), lvResult);		
	}

	public void testSimpleConversionTime2String() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Time.class, String.class));
		long lvDate = new Date().getTime();
		Object o = new Time(lvDate);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(String.class, lvResult.getClass());
		assertEquals("" + lvDate, lvResult);		
	}
	
	public void testSimpleConversionMultiple() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(Time.class, String.class));
		c.getConversionHandler().addConversion(new Simple2SimpleConversion(BigDecimal.class, String.class));
		long lvDate = new Date().getTime();
		Object o = new Time(lvDate);
		Object lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(String.class, lvResult.getClass());
		assertEquals("" + lvDate, lvResult);
		
		o = new BigDecimal("543.67");
		lvResult = c.convert(o);
		assertNotNull(lvResult);
		assertEquals(String.class, lvResult.getClass());
		assertEquals("543.67", lvResult);		

	}

	public void testString2LongDirect() throws Exception {
		Converter c = new Converter();	
		c.addConversion(new Simple2SimpleConversion(String.class, Long.class));
		Object lvResult = c.convert("56", Long.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Long);
		assertEquals(lvResult, new Long("56"));
	}
	
	public void testString2DoubleDirect() throws Exception {
		Converter c = new Converter();		
		c.addConversion(new Simple2SimpleConversion(String.class, Double.class));
		Object lvResult = c.convert("56", Double.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Double);
		assertEquals(lvResult, new Double("56"));
	}

	
	public void testString2DateDirect() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(String.class, Date.class));
		long lvLongDate = new Date().getTime();
		Object lvResult = c.convert(Long.toString(lvLongDate), Date.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Date);
		assertEquals(lvResult, new Date(lvLongDate));
		
		try {
			String lvString = "NoDate";
			c.convert(lvString, Date.class);
			fail(lvString + " is not a valid Date");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	public void testString2NodeDirect() throws Exception {
		Converter c = new Converter();		
		Object lvResult = c.convert("Node-Name", Node.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof String);
		assertEquals(lvResult, "Node-Name");
		
		try {
			c.setThrowExceptionIfNoConversionFind(true);
			c.convert("Node-Name", Node.class);
			fail("Must thrown Exception for convert String to Node");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	public void testArrayList2ListDirect() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion());
		ArrayList<String> lvList = new ArrayList<String>();
		lvList.add("111");
		lvList.add("222");
		Object lvResult = c.convert(lvList, ArrayList.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof List);
		List<?> newList = (List<?>) lvResult;
		assertEquals(lvList.size(), newList.size());
		assertEquals(lvList.get(0), newList.get(0));
		assertEquals(lvList.get(1), newList.get(1));
	}

	public void testNotString2ValidCharacter() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(String.class, Character.class));
		Object lvResult = c.convert("abc");
		assertEquals(new Character('a'), (Character) lvResult);
		
		lvResult = c.convert("");
		assertEquals(new Character('0'), (Character) lvResult);
	}
	
	public void testFromAndToType() throws Exception {
		Simple2SimpleConversion lvConversion = new Simple2SimpleConversion(String.class, Character.class);
		assertEquals(String.class, lvConversion.getFromType());
		assertEquals(Character.class, lvConversion.getToType());
	}
	
	public void testIllegalArgumentException() throws Exception {
		try {
			new Simple2SimpleConversion(Node.class, Node.class);
			fail("Node is not a simple type!");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	public void testConverterInterceptorHandler() throws Exception {
		Simple2SimpleConversion lvConversion = new Simple2SimpleConversion(String.class);
		assertNotNull(lvConversion.getConverterInterceptorHandler());
		lvConversion.setConverterInterceptorHandler(null);
		assertNull(lvConversion.getConverterInterceptorHandler());
	}
}
