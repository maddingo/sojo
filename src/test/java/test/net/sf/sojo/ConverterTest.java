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
package test.net.sf.sojo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.core.Conversion;
import net.sf.sojo.core.ConversionContext;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.ConversionHandler;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.ConverterInterceptorRecursive;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.core.conversion.Iterateable2IterateableConversion;
import net.sf.sojo.core.conversion.IterateableMap2BeanConversion;
import net.sf.sojo.core.conversion.IterateableMap2MapConversion;
import net.sf.sojo.core.conversion.NullConversion;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.conversion.interceptor.SimpleKeyMapperInterceptor;
import net.sf.sojo.core.conversion.interceptor.ThrowableConverterInterceptor;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import org.junit.Test;
import test.net.sf.sojo.conversion.DummyTestConversion;
import test.net.sf.sojo.conversion.DummyTestSimpleConversion;
import test.net.sf.sojo.model.Address;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.Primitive;

import static org.junit.Assert.*;

public class ConverterTest {

	@Test
	public void testOrderOfFindConversion() throws Exception {
		Converter c = new Converter();
		c.addConversion(new DummyTestConversion());
		c.addConversion(new Simple2SimpleConversion(String.class, Long.class));
		
		Object lvResult = c.convert("57");
		assertNotNull(lvResult);
		assertEquals("57", lvResult);
	}

	@Test
	public void testNotValidConversionWithException() throws Exception {
		Converter c = new Converter();
		c.addConversion(new DummyTestConversion());
		c.addConversion(new Simple2SimpleConversion(String.class, Long.class));
		c.setThrowExceptionIfNoConversionFind(true);
		
		try {
			c.convert("57");
			fail("Not valid Conversion-Type: " + DummyTestConversion.class.getName());
		} catch (Exception e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testConversionHandler() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		Converter c = new Converter();
		assertNotNull(c.getConversionHandler());
		c.setConversionHandler(lvConversionHandler);
		
		assertNotNull(c.getConversionHandler());
		assertEquals(lvConversionHandler, c.getConversionHandler());
	}

	@Test
	public void testConverterInterceptor () throws Exception {
		Converter c = new Converter();
		
		assertEquals(0,	c.getConverterInterceptorSize());
		
		TestConverterInterceptor lvInterceptor = new TestConverterInterceptor();
		c.addConverterInterceptor(lvInterceptor);
		assertEquals(1,	c.getConverterInterceptorSize());
		assertEquals(lvInterceptor,	c.getConverterInterceptorByPosition(0));
		
		c.clearConverterInterceptorSize();
		assertEquals(0,	c.getConverterInterceptorSize());
	}

	@Test
	public void testNumberOfRecursion() throws Exception {
		Converter c = new Converter();
		assertEquals(-1 , c.getNumberOfRecursion());
		
		Object lvResult = c.convert("1", int.class);
		assertNotNull(lvResult);
		assertEquals("1", lvResult);
		assertEquals(-1 , c.getNumberOfRecursion());
		
		c.addConversion(new Simple2SimpleConversion(String.class, int.class));
		lvResult = c.convert("1", int.class);
		assertNotNull(lvResult);
		assertEquals(new Integer("1"), lvResult);
		assertEquals(-1 , c.getNumberOfRecursion());
	}

	@Test
	public void testNullValueForObjectAndType() throws Exception {
		Converter c = new Converter();
		Object lvResult = c.convert(null);
		assertNull(lvResult);
		
		lvResult = c.convert(null, null);
		assertNull(lvResult);
	}

	@Test
	public void testConversionThrownException() throws Exception {
		Converter c = new Converter();
		c.addConversion(new DummyTestSimpleConversion(String.class));
		
		try {
			c.convert("buh");
			fail("Thrown Exception from DummyTestSimpleConversion.");
		} catch (ConversionException e) {
			assertNotNull(e);
			assertTrue(e.getCause() instanceof IllegalStateException);
		}		
		
	}

	@Test
	public void testConverterInterceptorRecursiveSimple() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(String.class, Integer.class));
		TestConverterInterceptorRecursive lvInterceptorRecursive = new TestConverterInterceptorRecursive();
		c.addConverterInterceptor(lvInterceptorRecursive);
		
		Object lvResult = c.convert("47.11", Double.class);
		assertNotNull(lvResult);
		assertEquals(new Double("47.11"), lvResult);
		
		assertEquals(0, lvInterceptorRecursive.conversionContext.numberOfRecursion);
		assertEquals(1, lvInterceptorRecursive.beforeConvert);
		assertEquals(1, lvInterceptorRecursive.beforeConvertRecursive);
		assertEquals(1, lvInterceptorRecursive.afterConvert);
		assertEquals(1, lvInterceptorRecursive.afterConvertRecursive);
		assertEquals(0, lvInterceptorRecursive.onError);
	}

	@Test
	public void testConverterInterceptorRecursiveListWithCancel() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(String.class, Integer.class));
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		
		List<String> lvList = new ArrayList<String>(2);
		lvList.add("12");
		lvList.add("34");
		Object lvResult = c.convert(lvList);
		assertNotNull(lvResult);
		List<?> lvListAfter = (List<?>) lvResult;
		assertEquals(lvList.size(), lvListAfter.size());
		assertTrue(lvListAfter instanceof ArrayList);
		assertEquals(Integer.valueOf("12"), lvListAfter.get(0));
		assertEquals(Integer.valueOf("34"), lvListAfter.get(1));
		
		
		
		c.addConverterInterceptor(new ConverterInterceptorRecursive() {

			@Override
			public void afterConvertRecursion(ConversionContext pvContext) { }

			@Override
			public void beforeConvertRecursion(ConversionContext pvContext) {
				pvContext.cancelConvert = true;
			}

			@Override
			public Object afterConvert(Object pvResult, Class<?> pvToType) { return pvResult; }
			
			@Override
			public Object beforeConvert(Object pvConvertObject, Class<?> pvToType) { return pvConvertObject; }
			
			@Override
			public void onError(Exception pvException) { }
			
		});

		lvList = new ArrayList<String>(2);
		lvList.add("12");
		lvList.add("34");
		lvResult = c.convert(lvList);
		assertNotNull(lvResult);
		lvListAfter = (List<?>) lvResult;
		assertEquals(lvList.size(), lvListAfter.size());
		assertTrue(lvListAfter instanceof ArrayList);
		assertEquals("12", lvListAfter.get(0));
		assertEquals("34", lvListAfter.get(1));
	}

	@Test
	public void testConverterInterceptorRecursiveListWithCancel2() throws Exception {
		Converter c = new Converter();
		Iterateable2IterateableConversion lvCollectionConversion = new Iterateable2IterateableConversion(ArrayList.class);
		lvCollectionConversion.getConverterInterceptorHandler().addConverterInterceptor(new ConverterInterceptorRecursive() {

			@Override
			public void afterConvertRecursion(ConversionContext pvContext) { }

			@Override
			public void beforeConvertRecursion(ConversionContext pvContext) {
				pvContext.cancelConvert = true;
			}

			@Override
			public Object afterConvert(Object pvResult, Class<?> pvToType) { return pvResult; }
			
			@Override
			public Object beforeConvert(Object pvConvertObject, Class<?> pvToType) { return pvConvertObject; }
			
			@Override
			public void onError(Exception pvException) { }
		}

		);
		c.addConversion(lvCollectionConversion);
		
		List<String> lvList = new ArrayList<String>(2);
		lvList.add("12");
		lvList.add("34");
		Object lvResult = c.convert(lvList);
		assertNotNull(lvResult);
		List<?> lvListAfter = (List<?>) lvResult;
		assertEquals(0, lvListAfter.size());
		assertTrue(lvListAfter instanceof ArrayList);
	}

	@Test
	public void testConverterInterceptorRecursiveComplexObject() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		
		IterateableMap2BeanConversion lvBeanConversion = new IterateableMap2BeanConversion();
		TestConverterInterceptorRecursive lvInterceptorRecursive = new TestConverterInterceptorRecursive();
		lvBeanConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptorRecursive);
		c.addConversion(lvBeanConversion);
		
		Object lvResult = c.convert(new Node("TestNode_2"));
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals("TestNode_2", ((Node)lvResult).getName());
		
		assertEquals(4, lvInterceptorRecursive.conversionContext.numberOfRecursion);
		assertEquals(1, lvInterceptorRecursive.beforeConvert);
		assertEquals(4, lvInterceptorRecursive.beforeConvertRecursive);
		assertEquals(1, lvInterceptorRecursive.afterConvert);
		assertEquals(4, lvInterceptorRecursive.afterConvertRecursive);
		assertEquals(0, lvInterceptorRecursive.onError);
	}

	@Test
	public void testConvertBeanAndConvertBack() throws Exception {
		Converter c = new Converter();
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());

		
		Node n0 = new Node("TestNode_Parent_0");
		Node n1 = new Node("TestNode_1");
		n1.setParent(n0);
		
		Object lvMapObj = c.convert(n1);
		Object lvNodeAfter = c.convert(lvMapObj);
		assertNotNull(lvNodeAfter);
		Node nAfter = (Node) lvNodeAfter;
		assertEquals("TestNode_1", nAfter.getName());
		assertEquals("TestNode_Parent_0", nAfter.getParent().getName());
	}

	@Test
	public void testConvertBeanAndConvertBackWithRelationToSameBean() throws Exception {
		Converter c = new Converter();
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		
		Node n = new Node("TestNode");
		n.setParent(n);
		
		Object lvMapObj = c.convert(n);
		Object lvNodeAfter = c.convert(lvMapObj);
		assertNotNull(lvNodeAfter);
		Node nAfter = (Node) lvNodeAfter;
		assertEquals("TestNode", nAfter.getName());
		assertEquals("TestNode", nAfter.getParent().getName());
		assertEquals(nAfter, nAfter.getParent());
		assertSame(nAfter, nAfter.getParent());
	}

	@Test
	public void testConvertBeanAndConvertBackWithChildList() throws Exception {
		Converter c = new Converter();
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion( new Iterateable2IterateableConversion());
		c.addConversion(new ComplexBean2MapConversion());
		
		Node n = new Node("TestNode");
		n.getChildren().add(new Node("ChildNode_1"));
		n.getChildren().add(new Node("ChildNode_2"));
		n.getChildren().add(n);
		
		Object lvMapObj = c.convert(n);
		
		Object lvNodeAfter = c.convert(lvMapObj);
		assertNotNull(lvNodeAfter);
		Node nAfter = (Node) lvNodeAfter;
		assertEquals("TestNode", nAfter.getName());
		assertEquals(3, nAfter.getChildren().size());
		assertEquals(0, nAfter.getNamedChildren().size());
		assertEquals(nAfter, nAfter.getChildren().get(2));
		assertEquals("ChildNode_1", ((Node)nAfter.getChildren().get(0)).getName());
		assertEquals("ChildNode_2", ((Node)nAfter.getChildren().get(1)).getName());
	}

	@Test
	public void testConvertBeanAndConvertBackWithChildMap() throws Exception {
		Converter c = new Converter();
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion( new IterateableMap2MapConversion());
		c.addConversion(new ComplexBean2MapConversion());
		
		Node n = new Node("TestNode");
		n.getNamedChildren().put("Node_1", new Node("ChildNode_1"));
		n.getNamedChildren().put("Node_2", new Node("ChildNode_2"));
		n.getNamedChildren().put("TestNode", n);
		
		Object lvMapObj = c.convert(n);
		
		Object lvNodeAfter = c.convert(lvMapObj);
		assertNotNull(lvNodeAfter);
		Node nAfter = (Node) lvNodeAfter;
		assertEquals("TestNode", nAfter.getName());
		assertEquals(3, nAfter.getNamedChildren().size());
		assertEquals(0, nAfter.getChildren().size());
		assertEquals(nAfter, nAfter.getNamedChildren().get("TestNode"));
		assertEquals("ChildNode_1", ((Node)nAfter.getNamedChildren().get("Node_1")).getName());
		assertEquals("ChildNode_2", ((Node)nAfter.getNamedChildren().get("Node_2")).getName());
	}

	@Test
	public void testPrimitiveInt2StringAndBack() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(int.class, String.class));
		c.addConversion(new Simple2SimpleConversion(String.class, int.class));
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		
		Primitive lvPrimitive = new Primitive();
		lvPrimitive.setIntValue(7);
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals("7", lvMap.get("intValue"));
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		Primitive lvPrimitiveAfter = (Primitive) lvResult;
		assertEquals(7, lvPrimitiveAfter.getIntValue());
	}

	@Test
	public void testPrimitiveInteger2StringAndBack() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Integer.class, String.class));
		c.addConversion(new Simple2SimpleConversion(String.class, Integer.class));
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		
		Primitive lvPrimitive = new Primitive();
		lvPrimitive.setIntValue(7);
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals("7", lvMap.get("intValue"));
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		Primitive lvPrimitiveAfter = (Primitive) lvResult;
		assertEquals(7, lvPrimitiveAfter.getIntValue());
	}

	@Test
	public void testPrimitive2MapAndBack() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals(Primitive.createPrimitiveExample(), lvResult);
	}

	@Test
	public void testPrimitive2MapAndBackWithConversions() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new Simple2SimpleConversion(float.class, double.class));
		c.addConversion(new Simple2SimpleConversion(double.class, float.class));
		
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals(Double.valueOf(3.4), lvMap.get("floatValue"));
		assertEquals(Float.valueOf(2.3f), lvMap.get("doubleValue"));
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals(Primitive.createPrimitiveExample(), lvResult);
	}

	@Test
	public void testPrimitive2MapAndBackWithConversions2() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new Simple2SimpleConversion(byte.class, short.class));
		c.addConversion(new Simple2SimpleConversion(short.class, byte.class));
		
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals(Byte.valueOf((byte)7), lvMap.get("shortValue"));
		assertEquals(Short.valueOf((short)2), lvMap.get("byteValue"));
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals(Primitive.createPrimitiveExample(), lvResult);
	}

	@Test
	public void testPrimitive2MapAndBackWithConversions3() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new Simple2SimpleConversion(char.class, String.class));
		c.addConversion(new Simple2SimpleConversion(String.class, char.class));
		
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals("a", lvMap.get("charValue"));
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals(Primitive.createPrimitiveExample(), lvResult);
	}

	@Test
	public void testPrimitive2MapAndBackWithConversions4() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new Simple2SimpleConversion(long.class, int.class));
		c.addConversion(new Simple2SimpleConversion(int.class, long.class));
		
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals(Long.valueOf(3L), lvMap.get("intValue"));
		assertEquals(Integer.valueOf(5), lvMap.get("longValue"));

		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals(Primitive.createPrimitiveExample(), lvResult);
	}

	@Test
	public void testPrimitive2MapAndBackWithConversions5() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion( new IterateableMap2BeanConversion());
		c.addConversion(new Simple2SimpleConversion(boolean.class, String.class));
		c.addConversion(new Simple2SimpleConversion(String.class, boolean.class));
		
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = c.convert(lvPrimitive);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?,?> lvMap = (Map<?,?>) lvResult;
		assertEquals("true", lvMap.get("booleanValue"));
		
		lvResult = c.convert(lvResult);
		assertNotNull(lvResult);
		assertEquals(Primitive.createPrimitiveExample(), lvResult);
	}

	@Test
	public void testMakeSimpleTimeConvert() throws Exception {
		Time lvTime = new Time(new Date().getTime());
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Time.class, Time.class));
		c.addConversion(new Simple2SimpleConversion(Timestamp.class, Timestamp.class));
		
		Object o = c.convert(lvTime, Time.class);
		assertEquals(lvTime, o);
		Timestamp lvTimestamp = new Timestamp(new Date().getTime());
		o = c.convert(lvTimestamp, Timestamp.class);
		assertEquals(lvTimestamp, o);
	}

	@Test
	public void testDate2Time() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Date.class, Time.class));
		
		Date d = new Date();
		Time time = (Time) c.convert(d, Time.class);
		assertNotNull(time);
		assertEquals(d, time);

		c.addConversion(new Simple2SimpleConversion(Timestamp.class, Time.class));
		Timestamp ts = new Timestamp(d.getTime());
		Time time2 = (Time) c.convert(ts, Time.class);
		assertNotNull(time2);
		assertEquals(d, time2);
		
		c.addConversion(new Simple2SimpleConversion(Long.class, Time.class));
		Long l = new Long(d.getTime());
		Time time3 = (Time) c.convert(l, Time.class);
		assertNotNull(time3);
		assertEquals(d, time3);
		
		try {
			c.addConversion(new Simple2SimpleConversion(String.class, Time.class));
			c.convert("blabla", Time.class);
			fail("String: blabla is not a Time");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testMakeSimpleClassConvert() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Class.class, String.class));

		Class<Time> lvTimeClass = Time.class;
		Object o = c.convert(lvTimeClass);
		
		c.addConversion(new Simple2SimpleConversion(String.class, Class.class));
		o = c.convert(o);
		assertEquals(lvTimeClass, o);
	}

	@Test
	public void testMakeSimpleBigConvert() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(BigInteger.class, String.class));
		c.addConversion(new Simple2SimpleConversion(String.class, BigInteger.class));

		BigInteger lvBigInteger = BigInteger.valueOf(12345L); 
		Object o = c.convert(lvBigInteger);
		o = c.convert(o);
		assertEquals(lvBigInteger, o);

		c.getConversionHandler().clear();
		c.addConversion(new Simple2SimpleConversion(BigDecimal.class, String.class));
		c.addConversion(new Simple2SimpleConversion(String.class, BigDecimal.class));

		BigDecimal lvBigDecimal = new BigDecimal("12.34");
		o = c.convert(lvBigDecimal);
		o = c.convert(o);
		assertEquals(lvBigDecimal, o);
	}

	@Test
	public void testMakeSimpleMapWithKeyConverterLong2Integer() throws Exception {
		Map<Long, Long> lvMap = new HashMap<Long,Long>();
		lvMap.put(Long.valueOf(1L), Long.valueOf(2L));
		
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Long.class, Integer.class));
		c.addConversion(new IterateableMap2MapConversion());

		Map<?,?> lvResultMap = (Map<?,?>) c.convert(lvMap);
		Object lvKey = lvResultMap.keySet().iterator().next();
		Object lvValue = lvResultMap.values().iterator().next();
		assertEquals(lvKey.getClass(), Integer.class);
		assertEquals(lvValue.getClass(), Integer.class);
		assertEquals(lvKey, Integer.valueOf(1));
		assertEquals(lvValue, Integer.valueOf(2));
	}

	@Test
	public void testMakeSimpleAndComplexMapWithKeyConverterLong2Integer_2() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(Long.class, Integer.class));
		c.addConversion(new IterateableMap2MapConversion());
		
		Map<Long, Long> lvMap = new HashMap<Long, Long>();
		lvMap.put(Long.valueOf(3L), Long.valueOf(4L));
		lvMap.put(Long.valueOf(1L), Long.valueOf(2L));
		lvMap.put(Long.valueOf(-1L), Long.valueOf(-2L));
		Object lvSimple = c.convert(lvMap);
		Object lvComplex = c.convert(lvSimple);
		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof Map);
		@SuppressWarnings("unchecked")
    Map<Integer,Integer> lvMapAfter = (Map<Integer,Integer>) lvComplex;
		assertEquals(lvMapAfter.size(), 3);
		for (Integer lvKey : lvMapAfter.keySet()) {
			Integer lvValue = lvMapAfter.get(lvKey);
			Long lvValueBefore = lvMap.get(Long.valueOf(lvKey.toString()));
			assertEquals(lvValueBefore.intValue(), lvValue.intValue());
		}
	}

	@Test
	public void testMakeSimpleArrayList() throws Exception {
		Node n1 = new Node("Node");
		Node n2 = new Node("Node");
		List<Node> lvListNodes = new ArrayList<Node>(2);
		lvListNodes.add(n1);
		lvListNodes.add(n2);
		
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		c.addConversion(new ComplexBean2MapConversion());
		ArrayList<?> lvVecNodes = (ArrayList<?>) c.convert(lvListNodes);
		
		Map<?,?> lvNode1Map = (Map<?,?>) lvVecNodes.get(0);
		assertEquals(n1.getName(), lvNode1Map.get("name"));
		assertEquals(n1.getChildren(), lvNode1Map.get("children"));

		Map<?,?> lvNode2Map = (Map<?,?>) lvVecNodes.get(0);
		assertEquals(n2.getName(), lvNode2Map.get("name"));
		assertEquals(n2.getChildren(), lvNode2Map.get("children"));
	}

	@Test
	@SuppressWarnings("unchecked")
  public void testBiDirectionalNodeRelation() throws Exception {
		Node n1 = new Node("Node1");
		Node n2 = new Node("Node2");
		Node n3 = new Node("Node3");
		
		n1.getNamedChildren().put(n2.getName(), n2);
		n1.getNamedChildren().put(n3.getName(), n3);
		n2.setParent(n1);
		n3.setParent(n1);
		ArrayList<Node> v = new ArrayList<Node>();
		v.add(n2);
		v.add(n3);

		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new IterateableMap2MapConversion());

		Object o = c.convert(v);
		o = c.convert(o);
		
		assertTrue(o instanceof ArrayList);
		v = (ArrayList<Node>) o;
		assertEquals(v.size(), 2);
		Node n22 = v.get(0);
		Node n33 = v.get(1);
		assertEquals(n22.getName(), n2.getName());
		assertEquals(n33.getName(), n3.getName());
		Node n11 = n22.getParent();
		assertEquals(n11.getName(), n33.getParent().getName());
		assertEquals(n22.getParent(), n33.getParent());
		assertEquals(n22.getParent(), n11);
		assertEquals(n33.getParent(), n11);
		assertEquals(n11.getNamedChildren().get(n22.getName()), n22);
		assertEquals(n11.getNamedChildren().get(n33.getName()), n33);
	}

	@Test
	public void testMakeComplexList() throws Exception {
		List<Object> lvList = new ArrayList<Object>();
		lvList.add(Integer.valueOf(5));
		lvList.add("abc");

		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		
		List<?> lvComplexList = (List<?>) c.convert(lvList);
		assertTrue(lvComplexList instanceof ArrayList);
		assertTrue(lvComplexList.size() == 2);
		assertEquals(lvComplexList.get(0), Integer.valueOf(5));
		assertEquals(lvComplexList.get(1), "abc");
	}

	@Test
	public void testConvertString2Value() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Simple2SimpleConversion(String.class, boolean.class));
		Object o = c.convert("true");
		assertEquals(Boolean.TRUE, o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, short.class));
		o = c.convert("3");
		assertEquals(Short.valueOf((short)3), o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, byte.class));
		o = c.convert("5");
		assertEquals(Byte.valueOf((byte)5), o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, int.class));
		o = c.convert("7");
		assertEquals(Integer.valueOf(7), o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, long.class));
		o = c.convert("9");
		assertEquals(Long.valueOf(9), o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, double.class));
		o = c.convert("3.5");
		assertEquals(Double.valueOf(3.5), o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, float.class));
		o = c.convert("5.7");
		assertEquals(Float.valueOf(5.7F), o);
		c.getConversionHandler().clear();
		
		c.addConversion(new Simple2SimpleConversion(String.class, char.class));
		o = c.convert("5");
		assertEquals(Character.valueOf('5'), o);
		c.getConversionHandler().clear();

		c.addConversion(new Simple2SimpleConversion(String.class, String.class));
		o = c.convert("sojo");
		assertEquals("sojo", o);
		c.getConversionHandler().clear();

		if (Locale.getDefault().equals(Locale.GERMANY)) {
			c.addConversion(new Simple2SimpleConversion(String.class, Date.class));
			o = c.convert("28.12.2005");
			assertEquals(new Date(1135724400000L), o);
			c.getConversionHandler().clear();
		}

	}

	@Test
	public void testArrayWithNodes() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		c.addConversion(new ComplexBean2MapConversion());
		
		Node lvNodes[] = new Node[3];
		Node lvNode1 = new Node ("Node_1");
		lvNodes[0] = lvNode1;
		lvNodes[1] = lvNode1;
		lvNodes[2] = new Node("Node_2");
		
		Object lvResult = c.convert(lvNodes);
		assertNotNull(lvResult);
		ArrayList<?> v = (ArrayList<?>) lvResult;
		assertEquals(3, v.size());	
		assertEquals("Node_1", ((Map<?,?>) v.get(0)).get("name"));
		assertEquals(UniqueIdGenerator.UNIQUE_ID_PROPERTY + "0", v.get(1));
		assertEquals("Node_2", ((Map<?,?>) v.get(2)).get("name"));
	}

	@Test
	public void testArrayWithNodesAndBackToArray() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		
		Node lvNodes[] = new Node[3];
		Node lvNode1 = new Node ("Node_1");
		lvNodes[0] = lvNode1;
		lvNodes[1] = lvNode1;
		lvNodes[2] = new Node("Node_2");
		
		Object lvResult = c.convert(lvNodes);
		// convert back
		lvResult = c.convert(lvResult, new Node[3].getClass());
		assertNotNull(lvResult);
		Node lvNodesAfter[] = (Node[]) lvResult;
		assertEquals(3, lvNodesAfter.length);	
		assertEquals("Node_1", lvNodesAfter[0].getName());
		assertEquals("Node_1", lvNodesAfter[1].getName());
		assertEquals("Node_2", lvNodesAfter[2].getName());
		assertEquals(lvNodesAfter[0], lvNodesAfter[1]);
	}

	@Test
	@SuppressWarnings("unchecked")
  public void testArrayWithNodesWithNullValue() throws Exception {
		Converter c = new Converter();
		Iterateable2IterateableConversion lvConversion = new Iterateable2IterateableConversion(ArrayList.class);
		lvConversion.setIgnoreNullValues(true);
		c.addConversion(lvConversion);
		
		Node lvNodes[] = new Node[3];
		Node lvNode1 = new Node ("Node_1");
		lvNodes[0] = lvNode1;
		lvNodes[1] = null;
		lvNodes[2] = new Node("Node_2");
		
		Object lvResult = c.convert(lvNodes);
		assertNotNull(lvResult);
		
		ArrayList<Node> v = (ArrayList<Node>) lvResult;
		assertEquals(2, v.size());
		assertEquals("Node_1", v.get(0).getName());
		assertEquals("Node_2", v.get(1).getName());
		
		c.addConversion(new NullConversion("~Null~Value~"));
		lvResult = c.convert(lvNodes);
		assertNotNull(lvResult);
		v = (ArrayList<Node>) lvResult;
		assertEquals(3, v.size());
		assertEquals("Node_1", v.get(0).getName());
		assertEquals("~Null~Value~", v.get(1));
		assertEquals("Node_2", v.get(2).getName());
	}

	@Test
	public void testConvertArrayAndBackToArray() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));

		Object lvArray = new Object[] { new Integer(7), "JUnit-Test-String", new Date(123456789L) };
		
		Object lvResult = c.convert(lvArray);
		// convert back
		lvResult = c.convert(lvResult, Object[].class);
		assertNotNull(lvResult);
		Object lvArrayAfter[] = (Object[]) lvResult;
		assertEquals(3, lvArrayAfter.length);	
		assertEquals(new Integer(7), lvArrayAfter[0]);
		assertEquals("JUnit-Test-String", lvArrayAfter[1]);
		assertEquals(new Date(123456789), lvArrayAfter[2]);
	}

	@Test
	public void testConvertArrayAndBackToArrayWithNull() throws Exception {
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));

		Object lvArray = new Object[] { new Integer(7), "JUnit-Test-String", new Date(123456789), null };
		
		Object lvResult = c.convert(lvArray);
		ArrayList<?> v = (ArrayList<?>) lvResult;
		assertEquals(4, v.size());

		// convert back
		c.getConversionHandler().clear();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		lvResult = c.convert(lvResult, Object[].class);
		assertNotNull(lvResult);
		Object lvArrayAfter[] = (Object[]) lvResult;
		
		// should actually be 3 but null-values are always included
		assertEquals(4, lvArrayAfter.length);	
		assertEquals(new Integer(7), lvArrayAfter[0]);
		assertEquals("JUnit-Test-String", lvArrayAfter[1]);
		assertEquals(new Date(123456789), lvArrayAfter[2]);
	}

	@Test
	public void testConvertArrayAndBackToArrayWithNullValue() throws Exception {
		Converter c = new Converter();
		Iterateable2IterateableConversion lvConversion = new Iterateable2IterateableConversion(ArrayList.class);
		lvConversion.setIgnoreNullValues(true);
		c.addConversion(lvConversion);

		Object lvArray = new Object[] { new Integer(7), "JUnit-Test-String", null, new Date(123456789) };
		
		Object lvResult = c.convert(lvArray);
		// convert back
		lvResult = c.convert(lvResult, Object[].class);
		assertNotNull(lvResult);
		Object lvArrayAfter[] = (Object[]) lvResult;
		assertEquals(3, lvArrayAfter.length);	
		assertEquals(new Integer(7), lvArrayAfter[0]);
		assertEquals("JUnit-Test-String", lvArrayAfter[1]);
		assertEquals(new Date(123456789), lvArrayAfter[2]);
		
		
		c.getConversionHandler().clear();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));
		
		lvResult = c.convert(lvArray);
		// convert back
		lvResult = c.convert(lvResult, Object[].class);
		assertNotNull(lvResult);
		lvArrayAfter = (Object[]) lvResult;
		assertEquals(4, lvArrayAfter.length);	
		assertEquals(new Integer(7), lvArrayAfter[0]);
		assertEquals("JUnit-Test-String", lvArrayAfter[1]);
		assertNull(lvArrayAfter[2]);
		assertEquals(new Date(123456789), lvArrayAfter[3]);
	}

	@Test
	public void testConvertMapWithNullElement() throws Exception {
		Map<String, String> lvMap = new HashMap<String,String>(2);
		lvMap.put("String", "String");
		lvMap.put("null", null);
		
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(HashMap.class, true));

		Object result = c.convert(lvMap);
		assertTrue(result instanceof Map);
		assertTrue(result instanceof HashMap);
		Map<?,?> lvMapAfter = (Map<?,?>) result;
		assertEquals(lvMapAfter.size(), 1);
		assertNotSame(lvMap, result);
		assertTrue( ! lvMap.equals(result) );
	}

	@Test
	public void testConvertByteArray() throws Exception {
		byte lvByteArray[] = new byte[] {5, 6};
		assertFalse(lvByteArray[0] == 0);
		assertFalse(lvByteArray[1] == 0);
		
		Converter c = new Converter();
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));

		ArrayList<?> v = (ArrayList<?>) c.convert(lvByteArray);
		assertEquals(lvByteArray[0], ((Byte) v.get(0)).byteValue());
		assertEquals(lvByteArray[1], ((Byte) v.get(1)).byteValue());
		
		byte lvByteArrayAfter[] = (byte[]) c.convert(v, byte[].class);
		assertEquals(lvByteArray[0], lvByteArrayAfter[0]);
		assertEquals(lvByteArray[1], lvByteArrayAfter[1]);
	}

	@Test
	public void testSimpleStringMap() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(HashMap.class));
		
		Map<String,String> lvMap = new HashMap<String,String>();
		lvMap.put("1", "1");
		lvMap.put("5", "5");
		lvMap.put("3", "3");
		lvMap.put("A", "A");
		lvMap.put("1", "1");
		
		Object lvSimple = c.convert(lvMap);
		assertTrue(lvSimple instanceof Map);
		assertEquals(4, ((Map<?,?>) lvSimple).size());
		
		Object lvComplex = c.convert(lvSimple);
		assertTrue(lvComplex instanceof Map);
		Map<?,?> lvMapAfter = (Map<?,?>) lvComplex;
		assertEquals(4, ((Map<?,?>) lvComplex).size());
		assertEquals("1", lvMapAfter.get("1"));
		assertEquals("5", lvMapAfter.get("5"));
		assertEquals("A", lvMapAfter.get("A"));
		assertEquals("3", lvMapAfter.get("3"));
	}

	@Test
	public void testConvertJavaUtilProperties() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		
		Properties lvProperties = new Properties();
		Object lvResult = c.convert(lvProperties);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof HashMap);
	}

	@Test
	public void testConvertJavaUtilPropertiesAsClassProperty() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		
		Properties lvProperties = new Properties();
		Car lvCar = new Car();
		lvCar.setName("BMW");
		lvCar.setProperties(lvProperties);
		
		Object lvSimple = c.convert(lvCar);
		Object lvComplex = c.convert(lvSimple);
		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof Car);
		Car lvCarAfter = (Car) lvComplex;
		assertEquals(new Properties(), lvCarAfter.getProperties());
	}

	@Test
	public void testConvertJavaUtilPropertiesAsClassProperty_2() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		
		Properties lvProperties = new Properties();
		lvProperties.put("aa", "bb");
		Car lvCar = new Car();
		lvCar.setName("BMW");
		lvCar.setProperties(lvProperties);
		
		Object lvSimple = c.convert(lvCar);
		Object lvComplex = c.convert(lvSimple);
		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof Car);
		Car lvCarAfter = (Car) lvComplex;
		assertNotNull(lvCarAfter.getProperties());
		assertEquals(1, lvCarAfter.getProperties().size());
		assertEquals("bb", lvCarAfter.getProperties().getProperty("aa"));
	}

	@Test
	public void testMapWithOutUiqueId() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2BeanConversion());

		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("class", Car.class.getName());
		lvMap.put("name", "BMW");			
		
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		Car lvCar = (Car) lvResult;
		assertEquals("BMW", lvCar.getName());
		assertNull(lvCar.getDescription());
	}

	@Test
	public void testMapWithOutUiqueIdAndNullValue() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new NullConversion("~Null~Value~"));
		c.addConversion(new NullConversion(new Date(-1)));

		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("class", Car.class.getName());
		lvMap.put("name", "BMW");			
		lvMap.put("description", null);
		lvMap.put("build", null);
		
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		Car lvCar = (Car) lvResult;
		assertEquals("BMW", lvCar.getName());
		assertEquals("~Null~Value~", lvCar.getDescription());
		assertEquals(new Date(-1), lvCar.getBuild());
	}

	@Test
	@SuppressWarnings("unchecked")
  public void testComplexObjectGraph() throws Exception {
		Node n11 = new Node("n11");
		Node n12 = new Node("n12");
		Node n21 = new Node("n21");
		Node n22 = new Node("n22");
		n11.getChildren().add(n21);
		n11.getChildren().add(n22);
		
		n12.getChildren().add(n21);
		n12.getChildren().add(n22);
		
		List<Node> list = new ArrayList<Node>(4);
		list.add(n11);
		list.add(n12);
		list.add(n21);
		list.add(n22);

		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());

		Object o = c.convert(list);
		list = (List<Node>) c.convert(o);
		
		assertNotNull(list);
		assertEquals(list.size(), 4);
		
		Node n1After = list.get(0);
		assertEquals( n1After.getName(), "n11");
		assertEquals( ((Node)n1After.getChildren().get(0)).getName(), "n21");
		assertEquals( ((Node)n1After.getChildren().get(1)).getName(), "n22");

		Node n2After = list.get(1);
		assertEquals( n2After.getName(), "n12");
		assertEquals( ((Node)n2After.getChildren().get(0)).getName(), "n21");
		assertEquals( ((Node)n2After.getChildren().get(1)).getName(), "n22");
		
		assertEquals( list.get(2).getName(), "n21");
		assertEquals( list.get(3).getName(), "n22");
	}

	@Test
	@SuppressWarnings("unchecked")
  public void testComplexObjectGraph_2() throws Exception {
		Node n11 = new Node("n11");
		Node n12 = new Node("n12");
		Node n21 = new Node("n21");
		Node n22 = new Node("n22");
		n11.getChildren().add(n21);
		n11.getChildren().add(n22);
		
		n12.getChildren().add(n21);
		n12.getChildren().add(n22);
		
		List<Node> list = new ArrayList<Node>(4);
		list.add(n11);
		list.add(n12);
		list.add(n21);
		list.add(n22);

		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());

		Object o = c.convert(list);
		list = (List<Node>) c.convert(o);
		
		assertNotNull(list);
		assertEquals(list.size(), 4);
		
		Node n1After = list.get(0);
		assertEquals( n1After.getName(), "n11");
		assertEquals( ((Node)n1After.getChildren().get(0)).getName(), "n21");
		assertEquals( ((Node)n1After.getChildren().get(1)).getName(), "n22");

		Node n2After = list.get(1);
		assertEquals( n2After.getName(), "n12");
		assertEquals( ((Node)n2After.getChildren().get(0)).getName(), "n21");
		assertEquals( ((Node)n2After.getChildren().get(1)).getName(), "n22");
		
		assertEquals( list.get(2).getName(), "n21");
		assertEquals( list.get(3).getName(), "n22");
	}

	@Test
	public void testConvertMakeCompleyWithInValidMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("class", "i.am.a.not.valid.Clazz");

		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());

		try {
			c.convert(map);
			fail("Exception by converter.makeComplex is an Error");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testListWithDoubleInstanceAndNullValue() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		Iterateable2IterateableConversion lvConversion = new Iterateable2IterateableConversion(ArrayList.class);
		lvConversion.setIgnoreNullValues(true);
		c.addConversion(lvConversion);


		List<Object> l = new ArrayList<Object>();
		Customer lvCustomer = new Customer("Junit-Test-Kunde");
		Object lvSimple = c.convert(lvCustomer);
		
		l.add(lvSimple);
		l.add(null);
		l.add(lvSimple);
		
		Object lvComplex = c.convert(l);
		
		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof List);
		// without null value
		assertEquals(2, ((List<?>) lvComplex).size());
	}

	@Test
	public void testMapInArrayByMakeComplex() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion());
		c.addConversion(new IterateableMap2BeanConversion());

		Map<?,?> lvPrimitiveMap = (Map<?,?>) c.convert(Primitive.createPrimitiveExample());
		assertNotNull(lvPrimitiveMap);
		Integer lvIntValue = (Integer) lvPrimitiveMap.get("intValue");
		assertEquals(3, lvIntValue.intValue());
		
		Object lvPrimitiveArray[] = (Object[]) c.convert(new Object [] { lvPrimitiveMap }, Object[].class);
		assertEquals(1, lvPrimitiveArray.length);
		assertEquals(Primitive.createPrimitiveExample(), lvPrimitiveArray[0]);
	}

	@Test
	public void testObjectsInMap() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion());

		Customer lvCustomer = new Customer("Test-Kunde");
		Address a = new Address();
		a.setCity("Hier");
		lvCustomer.getAddresses().add(a);
		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("ADDRESS_KEY", a);
		lvMap.put("CUSTOMER_KEY", lvCustomer);

		Object lvSimple = c.convert(lvMap);
		Map<?,?> lvMapAfter = (Map<?,?>) c.convert(lvSimple);
		assertNotNull(lvMapAfter);
		assertEquals(2, lvMapAfter.size());

		Address lvAddressAfter = (Address) lvMap.get("ADDRESS_KEY");
		Customer lvCustomerAfter = (Customer) lvMap.get("CUSTOMER_KEY");
		assertSame(lvCustomerAfter.getAddresses().iterator().next(), lvAddressAfter);
		assertEquals("Test-Kunde", lvCustomerAfter.getLastName());
		assertEquals(a.getCity(), lvAddressAfter.getCity());
	}

	@Test
	public void testObjectComplexInCollection() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion());

		Customer lvCustomer = new Customer("Test-Kunde");
		Address a = new Address();
		a.setCity("Hier");
		lvCustomer.getAddresses().add(a);
		Map<String, Object> lvMap = new HashMap<String, Object>();
		Collection<Object> lvColl = new ArrayList<Object>(1);
		lvColl.add(a);
		
		lvMap.put("ADDRESS_KEY", lvColl);
		lvMap.put("CUSTOMER_KEY", lvCustomer);


		Object lvSimple = c.convert(lvMap);
		Map<?,?> lvMapAfter = (Map<?,?>)c.convert(lvSimple);
		assertNotNull(lvMapAfter);
		assertEquals(2, lvMapAfter.size());

		Collection<?> lvCollAfter = (Collection<?>) lvMap.get("ADDRESS_KEY");
		Customer lvCustomerAfter = (Customer) lvMap.get("CUSTOMER_KEY");
		
		assertEquals(lvColl.size(), lvCollAfter.size());
		assertEquals(lvCustomerAfter.getAddresses().iterator().next(), lvCollAfter.iterator().next());
		assertSame(lvCustomerAfter.getAddresses().iterator().next(), lvCollAfter.iterator().next());
	}

	@Test
	public void testNoObjectFoundForHashCodeByMakeComplexInArrayWithException() throws Exception {
		Address a = new Address();
		a.setCity("Hier");
		Address ar[] = new Address[2];
		ar[0] = a;
		ar[1] = a;
		
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion(ArrayList.class));

		Object lvSimple = c.convert(ar);
		
		List<Object> l = new ArrayList<Object>();
		// swap the order of the array
		l.add(((List<?>) lvSimple).get(1));
		l.add(((List<?>) lvSimple).get(0));
		
		try {
			c.convert(l, ar.getClass());
			fail("Invalid order of the Array.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testNoObjectFoundForHashCodeByMakeComplex() throws Exception {
		Converter c = new Converter();
		SimpleKeyMapperInterceptor lvInterceptor = new SimpleKeyMapperInterceptor(true);
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		lvConversion.getConverterInterceptorHandler().addConverterInterceptor(lvInterceptor);
		c.addConversion(lvConversion);
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion());

		// prepare test objects
		Customer lvCustomer = new Customer("Test-Kunde");
		Address a = new Address();
		a.setCity("Hier");
		lvCustomer.getAddresses().add(a);
		
		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("KEY_1", a);
		lvMap.put("KEY_2", lvCustomer);
		
		// convert 
		Object lvSimple = c.convert(lvMap);
		assertThat(lvSimple, CoreMatchers.instanceOf(Map.class));
		Map<?,?> lvSimpleMap = (Map<?, ?>)lvSimple;
		assertThat(lvSimpleMap.get("1~_-_~KEY_1"), CoreMatchers.instanceOf(LinkedHashMap.class));

		lvInterceptor.setMakeSimple(false);
		Object lvComplex = c.convert(lvSimple);
		
		assertThat(lvComplex, CoreMatchers.is(CoreMatchers.notNullValue()));
		assertThat(lvComplex, CoreMatchers.instanceOf(Map.class));
		Map<?,?> lvMapAfter = (Map<?,?>) lvComplex;
		assertThat(lvMapAfter.size(), CoreMatchers.is(2));
		assertThat(lvMapAfter.get("KEY_1"), CoreMatchers.instanceOf(Address.class));
		Address aAfter = (Address) lvMapAfter.get("KEY_1");
		assertThat(lvMapAfter.get("KEY_2"), CoreMatchers.instanceOf(Customer.class));
		Customer cAfter = (Customer)lvMapAfter.get("KEY_2");
		assertThat(aAfter.getCity(), CoreMatchers.is("Hier"));
		assertThat(cAfter.getLastName(), CoreMatchers.is("Test-Kunde"));
		assertThat(cAfter.getAddresses().size(), CoreMatchers.is(1));
		assertThat(cAfter.getAddresses().iterator().next(), CoreMatchers.is((Object)aAfter));
	}

	@Test
	public void testObjectFoundForHashCodeByMakeComplex_2() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion());

		Address a = new Address();
		a.setCity("Hier");
		Customer k = new Customer("Test-Kunde");
		k.getAddresses().add(a);

		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("ADRESSE_KEY_3", a);
		lvMap.put("ADRESSE_KEY_2", a);
		lvMap.put("ADRESSE_KEY_1", a);
		lvMap.put("ADRESSE_KEY_0", k);

		Object lvSimple = c.convert(lvMap);
		Object lvComplex = c.convert(lvSimple);
		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof Map);
		Map<?,?> lvMapAfter = (Map<?,?>) lvComplex;
		assertEquals(4, lvMapAfter.size());

		Object lvKundeAfter = lvMapAfter.get("ADRESSE_KEY_0");
		assertTrue(lvKundeAfter instanceof Customer);
		assertEquals("Test-Kunde", ((Customer) lvKundeAfter).getLastName());

		Object lvAdrAfter1 = lvMapAfter.get("ADRESSE_KEY_1");
		assertTrue(lvAdrAfter1 instanceof Address);
		assertEquals("Hier", ((Address) lvAdrAfter1).getCity());
		
		Object lvAdrAfter2 = lvMapAfter.get("ADRESSE_KEY_2");
		assertTrue(lvAdrAfter2 instanceof Address);
		assertEquals("Hier", ((Address) lvAdrAfter2).getCity());

		Object lvAdrAfter3 = lvMapAfter.get("ADRESSE_KEY_3");
		assertTrue(lvAdrAfter3 instanceof Address);
		assertEquals("Hier", ((Address) lvAdrAfter3).getCity());
	}

	@Test
	public void testMakeSimpleAndComplexArrayWithSameObjects() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new Iterateable2IterateableConversion());

		
		Address a = new Address();
		a.setCity("Hier");
		Object lvArray = new Object[] {a, a, a};
		Object lvSimple = c.convert(lvArray);
		Object lvComplex = c.convert(lvSimple, Address[].class);
		assertTrue(lvComplex.getClass().isArray());
		Object lvArrayAfter[] = (Object[]) lvComplex;
		assertEquals(3, lvArrayAfter.length);
		assertEquals(a.getCity(), ((Address) lvArrayAfter[0]).getCity());
		assertEquals(a.getCity(), ((Address) lvArrayAfter[1]).getCity());
		assertEquals(a.getCity(), ((Address) lvArrayAfter[2]).getCity());
	}

	@Test
	public void testSimpleBooleanArray() throws Exception {
		Object o = new ObjectUtil().makeSimple(new boolean[] { true, false, true });
		assertNotNull(o);
		List<?> al = (List<?>) o;
		assertEquals(3, al.size());
		assertEquals(Boolean.TRUE, al.get(0));
	}

	@Test
	public void testReplaceAllConversion() throws Exception {
		Converter lvConverter = new Converter();
		assertEquals(0, lvConverter.getConversionHandler().size());
		
		Simple2SimpleConversion lvSimple2SimpleConversion = new Simple2SimpleConversion(String.class);
		lvConverter.getConversionHandler().addConversion(lvSimple2SimpleConversion);
		assertEquals(1, lvConverter.getConversionHandler().size());
		assertSame(lvSimple2SimpleConversion, lvConverter.getConversionHandler().getConversionByPosition(0));
		
		Simple2SimpleConversion lvSimple2SimpleConversion2 = new Simple2SimpleConversion(String.class);
		lvConverter.replaceAllConversion(lvSimple2SimpleConversion2);
		assertSame(lvSimple2SimpleConversion2, lvConverter.getConversionHandler().getConversionByPosition(0));
		assertNotSame(lvSimple2SimpleConversion, lvConverter.getConversionHandler().getConversionByPosition(0));
	}

	@Test
	public void testSameObjectInAList() throws Exception {
		ObjectUtil lvUtil = new ObjectUtil();
		
		Customer lvCustomer = new Customer("Junit-Test-Kunde");
		List<Customer> l = new ArrayList<Customer>();
		l.add(lvCustomer);
		l.add(null);
		l.add(lvCustomer);

		Object lvSimple = lvUtil.makeSimple(l);
		
		Object lvComplex = lvUtil.makeComplex(lvSimple);
		
		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof List);
		List<?> lAfter = (List<?>) lvComplex;
		assertEquals(3, lAfter.size());
		
		Customer lvCustomerAfter = (Customer) lAfter.get(0);
		assertNull(lAfter.get(1));
		assertEquals(lvCustomerAfter, lAfter.get(2));
		assertSame(lvCustomerAfter, lAfter.get(2));
	}

	@Test
	public void testRemoveConversion() throws Exception {
		ConversionHandler lvHandler = new ConversionHandler();
		assertEquals(0, lvHandler.size());
		
		NullConversion lvNullConversion = new NullConversion(null);
		lvHandler.addConversion(lvNullConversion);
		Simple2SimpleConversion lvSimple2SimpleConversion = new Simple2SimpleConversion(String.class);
		lvHandler.addConversion(lvSimple2SimpleConversion);
		assertEquals(2, lvHandler.size());
		
		Conversion lvConversion = lvHandler.removeConversion(lvNullConversion);
		assertEquals(1, lvHandler.size());
		assertEquals(lvNullConversion, lvConversion);
		
		lvConversion = lvHandler.removeConversion(lvSimple2SimpleConversion);
		assertEquals(0, lvHandler.size());
		assertEquals(lvSimple2SimpleConversion, lvConversion);

		lvConversion = lvHandler.removeConversion(lvSimple2SimpleConversion);
		assertEquals(0, lvHandler.size());
		assertNull(lvConversion);
	}

	@Test
	public void testInvalidParameterType() throws Exception {
		IterateableMap2BeanConversion lvBeanConversion = new IterateableMap2BeanConversion();
		Converter lvConverter = new Converter();
		lvConverter.addConversion(lvBeanConversion);
		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("name", "MyName");
		lvMap.put("build", new Car());
		try {
			lvConverter.convert(lvMap, Car.class);
			fail("Car is not assignable to date.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testInvalidParameterType2() throws Exception {
		IterateableMap2BeanConversion lvBeanConversion = new IterateableMap2BeanConversion();
		Converter lvConverter = new Converter();
		lvConverter.addConversion(lvBeanConversion);
		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("name", "MyName");
		lvMap.put("properties", "not a property");
		try {
			lvConverter.convert(lvMap, Car.class);
			fail("String is not assignable to Properties.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testRemoveConverterInterceptor() throws Exception {
		Converter lvConverter = new Converter();
		assertEquals(0, lvConverter.getConverterInterceptorSize());
		
		ThrowableConverterInterceptor lvThrowableConverterInterceptor = new ThrowableConverterInterceptor();
		lvConverter.addConverterInterceptor(lvThrowableConverterInterceptor);
		assertEquals(1, lvConverter.getConverterInterceptorSize());
		
		lvConverter.removeConverterInterceptor(lvThrowableConverterInterceptor);
		assertEquals(0, lvConverter.getConverterInterceptorSize());
	}

	@Test
	public void testConvertDefaultMutableTreeNodeWithFieldAttribute() throws Exception {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);

		try {
			String lvFilter[] = new String [] {"class", "parent", "children", "userObject", "allowsChildren"};
			ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, lvFilter);
			
			Converter c = new Converter();
			c.addConversion(new ComplexBean2MapConversion());
			c.addConversion(new IterateableMap2BeanConversion());

			Object o = c.convert(root);
			DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode)  c.convert(o);
			assertEquals("ROOT", rootAfter.getUserObject());
			assertEquals(1, rootAfter.getChildCount());
			assertEquals("Child", ((DefaultMutableTreeNode) rootAfter.getChildAt(0)).getUserObject());
		} finally {
			ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
		}
	}

	@Test
	public void testConvertDefaultMutableTreeNodeWithMethodAttribute() throws Exception {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);

		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());

		Object o = c.convert(root);
		DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode)  c.convert(o);
		assertEquals("ROOT", rootAfter.getUserObject());
		// losing childs, because it exist no setter for childs
		assertEquals(0, rootAfter.getChildCount());
	}
	
}
