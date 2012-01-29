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
package test.net.sf.sojo.interchange.json;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.interchange.AbstractSerializer;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.SerializerException;
import net.sf.sojo.interchange.json.JsonSerializer;
import net.sf.sojo.util.Util;
import test.net.sf.sojo.model.ABean;
import test.net.sf.sojo.model.BBean;
import test.net.sf.sojo.model.Bean;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.Primitive;
import test.net.sf.sojo.model.SpecialTypeBean;

public class JsonSerializerTest extends TestCase {

	private JsonSerializer jsonSerializer = new JsonSerializer();
	
	public void testSimpleStringAndBack() throws Exception {
		String s = "MyTestString";
		Object lvResult = jsonSerializer.serialize(s);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(s, lvResult);
	}

	public void testSimpleStringAndBack2() throws Exception {
		String s = "\"MyTestString\"";
		Object lvResult = jsonSerializer.serialize(s);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals("\"MyTestString\"", lvResult);
	}

	public void testSimpleStringAndBackWithSpecialCharacter() throws Exception {
		String s = "My\nTest\tString";
		Object lvResult = jsonSerializer.serialize(s);
		assertEquals("\"My\\nTest\\tString\"", lvResult);
		
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals("My\nTest\tString", lvResult);
	}
	
	public void testSimpleBooleanAndBack() throws Exception {
		Object lvResult = jsonSerializer.serialize(Boolean.TRUE);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(Boolean.TRUE, lvResult);
		
		lvResult = jsonSerializer.serialize(Boolean.FALSE);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(Boolean.FALSE, lvResult);
	}

	public void testSimpleDoubleAndBack() throws Exception {
		Double d = new Double("19.0000000001");
		Object lvResult = jsonSerializer.serialize(d);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(d, lvResult);
	}

	public void testSimpleBigDecimalAndBack() throws Exception {
		BigDecimal bd = new BigDecimal("19.0000000001");
		Object lvResult = jsonSerializer.serialize(bd);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(new Double("19.0000000001"), lvResult);
	}
	
	public void testSimpleByteAndBack() throws Exception {
		Byte b = new Byte("20");
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(new Long("20"), lvResult);
	}

	public void testSimpleByteArrayAndBack2LongList() throws Exception {
		byte b[] = new byte[] { 2, 4, 9 };
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult);
		List<Long> l = new ArrayList<Long>();
		l.add(new Long("2"));
		l.add(new Long("4"));
		l.add(new Long("9"));
		assertEquals(l, lvResult);
	}

	public void testSimpleByteArrayAndBack() throws Exception {
		byte b[] = new byte[] { 2, 4, 9 };
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult, byte[].class);
		
		byte bAfter[] = (byte[]) lvResult;
		assertEquals(b.length, bAfter.length);
		assertEquals(b[0], bAfter[0]);
		assertEquals(b[1], bAfter[1]);
		assertEquals(b[2], bAfter[2]);
	}

	public void testArrayWithIntegerAndDoubleValue() throws Exception {
		Object o[] = new Object[] { "2", "4.5", "9" };
		Object lvResult = jsonSerializer.serialize(o);
		try {
			jsonSerializer.deserialize(lvResult, byte[].class);
			fail("4.5 is not from type byte");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	public void testByteArrayAndBack() throws Exception {
		Byte b[] = new Byte[] { new Byte("2"), new Byte("4"), new Byte("9") };
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult, Byte[].class);
		
		Byte bAfter[] = (Byte[]) lvResult;
		assertEquals(b.length, bAfter.length);
		assertEquals(b[0], bAfter[0]);
		assertEquals(b[1], bAfter[1]);
		assertEquals(b[2], bAfter[2]);
	}

	public void testByteWithNullValueArrayAndBack() throws Exception {
		Byte b[] = new Byte[] { new Byte("2"), null, new Byte("9") };
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult, Byte[].class);
		
		Byte bAfter[] = (Byte[]) lvResult;
		assertEquals(b.length, bAfter.length);
		assertEquals(b[0], bAfter[0]);
		assertNull("Value is not null: " + bAfter[1], bAfter[1]);
		assertEquals(b[2], bAfter[2]);
	}

	public void testByteArray2SimpeBack() throws Exception {
		Byte b[] = new Byte[] { new Byte("2"), new Byte("4"), new Byte("9") };
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult, byte[].class);
		
		byte bAfter[] = (byte[]) lvResult;
		assertEquals(b.length, bAfter.length);
		assertEquals(b[0].byteValue(), bAfter[0]);
		assertEquals(b[1].byteValue(), bAfter[1]);
		assertEquals(b[2].byteValue(), bAfter[2]);
	}

	public void testMixedArrayAndBack() throws Exception {
		Object b[] = new Object[] { new Byte("2"), new Integer(4), new Long(9) };
		Object lvResult = jsonSerializer.serialize(b);
		lvResult = jsonSerializer.deserialize(lvResult, Byte[].class);
		
		Byte bAfter[] = (Byte[]) lvResult;
		assertEquals(b.length, bAfter.length);
		assertEquals(b[0].toString(), bAfter[0].toString());
		assertEquals(b[1].toString(), bAfter[1].toString());
		assertEquals(b[2].toString(), bAfter[2].toString());
	}

	public void testSimpleIntArray2SimpleInt() throws Exception {
		int i[] = new int[] { 2, 4, 9 };
		Object lvResult = jsonSerializer.serialize(i);
		lvResult = jsonSerializer.deserialize(lvResult, int[].class);
		
		int iAfter[] = (int[]) lvResult;
		assertEquals(i.length, iAfter.length);
		assertEquals(i[0], iAfter[0]);
		assertEquals(i[1], iAfter[1]);
		assertEquals(i[2], iAfter[2]);
	}

	public void testSimpleIntArray2Integer() throws Exception {
		int i[] = new int[] { 2, 4, 9 };
		Object lvResult = jsonSerializer.serialize(i);
		lvResult = jsonSerializer.deserialize(lvResult, Integer[].class);
		
		Integer iAfter[] = (Integer[]) lvResult;
		assertEquals(i.length, iAfter.length);
		assertEquals(i[0], iAfter[0].intValue());
		assertEquals(i[1], iAfter[1].intValue());
		assertEquals(i[2], iAfter[2].intValue());
	}
	
	public void testByteArrayAsProperty() throws Exception {
		Customer lvCustomer = new Customer("customer");
		byte b[] = new byte[] { 2, 4, 9 };
		lvCustomer.setBytes(b);
		
		Object lvResult = jsonSerializer.serialize(lvCustomer);
		lvResult = jsonSerializer.deserialize(lvResult);

		assertTrue("Is not a Customer: " + lvResult.getClass(), lvResult instanceof Customer);
		Customer lvCustomerAfter = (Customer) lvResult;
		assertEquals("customer", lvCustomerAfter.getLastName());
		assertEquals(b.length, lvCustomerAfter.getBytes().length);
		assertEquals(b[0], lvCustomerAfter.getBytes()[0]);
		assertEquals(b[1], lvCustomerAfter.getBytes()[1]);
		assertEquals(b[2], lvCustomerAfter.getBytes()[2]);
	}

	public void testMapWithByteArray() throws Exception {
		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("a", "a");
		lvMap.put("array", new byte[] { 2, 4, 9 });
		
		Object lvResult = jsonSerializer.serialize(lvMap);
		lvResult = jsonSerializer.deserialize(lvResult);
		
		assertTrue("Is not a Map: " + lvResult.getClass(), lvResult instanceof Map);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvResult;
		assertEquals("a", lvMapAfter.get("a"));
		List<?> l = (List<?>) lvMapAfter.get("array");
		assertEquals(new Long(2), l.get(0));
		assertEquals(new Long(4), l.get(1));
		assertEquals(new Long(9), l.get(2));
	}
	
	public void testEmptyListAndBack() throws Exception {
		Collection<?> c = new ArrayList<Object>();
		Object lvResult = jsonSerializer.serialize(c);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(c, lvResult);
	}

	public void testListAndBack() throws Exception {
		Collection<Object> c = new ArrayList<Object>();
		c.add("text");
		c.add(new Integer(4711));
		c.add(new Car("MyCar"));
		Map<String, String> m = new HashMap<String, String>();
		m.put("k1", "v1");
		List<Object> lvList2 = new Vector<Object>();
		lvList2.add(c);
		lvList2.add(m);
		Object lvResult = jsonSerializer.serialize(lvList2);
		lvResult = jsonSerializer.deserialize(lvResult);
		List<?> lvListAfter = (List<?>) lvResult;
		assertEquals(2, lvListAfter.size());
		Collection<?> cAfter = (Collection<?>) lvListAfter.get(0);
		Iterator<?> it = cAfter.iterator();
		assertEquals("text", it.next());
		assertEquals(new Long(4711), it.next());
		Car lvCarAfter = (Car) it.next();
		assertEquals("MyCar", lvCarAfter.getName());
		assertEquals(m, lvListAfter.get(1));
	}

	public void testEmptySetAndBack() throws Exception {
		Set<?> s = new HashSet<Object>();
		Object lvResult = jsonSerializer.serialize(s);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(new Vector<Object>(), lvResult);
	}

	public void testEmptyMapAndBack() throws Exception {
		Map<?, ?> m = new HashMap<Object, Object>();
		Object lvResult = jsonSerializer.serialize(m);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(m, lvResult);
	}
	
	public void testMapContainsEmptyListAndBack() throws Exception {
		Map<String, Vector<?>> m = new HashMap<String, Vector<?>>();
		m.put("empty-list", new Vector<Object>());
		Object lvResult = jsonSerializer.serialize(m);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(m, lvResult);
		assertEquals(m.get("empty-list"), new ArrayList<Object>());
	}

	public void testListContainsEmptyMapAndBack() throws Exception {
		Collection<HashMap<?, ?>> c = new ArrayList<HashMap<?, ?>>();
		c.add(new HashMap<Object, Object>());
		Object lvResult = jsonSerializer.serialize(c);
		lvResult = jsonSerializer.deserialize(lvResult);
		assertEquals(c, lvResult);
		assertEquals(c.iterator().next(), new Hashtable<Object, Object>());
	}
	
	public void testNestedMapInList() throws Exception {
		List<Object> l = new ArrayList<Object>();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("k1", "v1");
		Set<String> s = new HashSet<String>();
		s.add("SetValue");
		m.put("set", s);
		l.add(s);
		l.add(m);
		
		Object lvResult = jsonSerializer.serialize(l);
		lvResult = jsonSerializer.deserialize(lvResult);
		List<?> lAfter = (List<?>) lvResult;
		Set<?> sAfter = (Set<?>) l.get(0);
		Map<?, ?> mAfter = (Map<?, ?>) l.get(1);
		
		assertEquals(l.size(), lAfter.size());
		assertEquals(s.size(), sAfter.size());
		assertEquals(m.size(), mAfter.size());
		assertEquals(s.iterator().next(), sAfter.iterator().next());
		assertEquals(m.get("k1"), mAfter.get("k1"));
		assertEquals(m.get("set"), mAfter.get("set"));
		assertEquals(((Set<?>) m.get("set")).iterator().next(), ((Set<?>) mAfter.get("set")).iterator().next());
	}

	public void testDeserializeNullValue() throws Exception {
		Object lvResult = jsonSerializer.deserialize(null);
		assertNull(lvResult);
	}

	public void testSimpleBean() throws Exception {
		Car lvCar = new Car("MyCar");
		Object lvResult = jsonSerializer.serialize(lvCar);
		lvResult = jsonSerializer.deserialize(lvResult);
		Car lvCarAfter = (Car) lvResult;
		assertEquals("MyCar", lvCarAfter.getName());
	}

	public void testBeanWithProperties() throws Exception {
		Car lvCar = new Car("TheCar");
		Date d = new Date(980859295000l);
		lvCar.setBuild(d);
		lvCar.setProperties(new Properties());
		lvCar.getProperties().put("k1", "v1");
		lvCar.getProperties().put("k2", "v2");
		
		Object lvResult = jsonSerializer.serialize(lvCar);
		lvResult = jsonSerializer.deserialize(lvResult);
		Car lvCarAfter = (Car) lvResult;
		assertEquals("TheCar", lvCarAfter.getName());
		assertEquals(d, lvCarAfter.getBuild());
		assertEquals(2, lvCarAfter.getProperties().size());
		assertEquals("v1", lvCarAfter.getProperties().get("k1"));
		assertEquals("v2", lvCarAfter.getProperties().get("k2"));
	}
	
	public void testBeanWithChilds() throws Exception {
		Node n = new Node("N");
		Node n1 = new Node("N1");
		Node n2 = new Node("N2");
		n.getChildren().add(n1);
		n.getChildren().add(n2);
		
		Object lvResult = jsonSerializer.serialize(n);
		lvResult = jsonSerializer.deserialize(lvResult);
		Node nAfter = (Node) lvResult;
		assertEquals("N", nAfter.getName());
		assertNull(nAfter.getParent());
		assertEquals(new HashMap<Object, Object>(), nAfter.getNamedChildren());
		
		assertEquals(2, nAfter.getChildren().size());
		assertEquals("N1", ((Node) nAfter.getChildren().get(0)).getName());
		assertEquals("N2", ((Node) nAfter.getChildren().get(1)).getName());
	}

	public void testBeanWithNamedChilds() throws Exception {
		Node n = new Node("N");
		Node n1 = new Node("N1");
		Node n2 = new Node("N2");
		n.getNamedChildren().put("N1", n1);
		n.getNamedChildren().put("N2", n2);
		
		Object lvResult = jsonSerializer.serialize(n);
		lvResult = jsonSerializer.deserialize(lvResult);
		Node nAfter = (Node) lvResult;
		assertEquals("N", nAfter.getName());
		assertNull(nAfter.getParent());
		assertEquals(new ArrayList<Object>(), nAfter.getChildren());
		
		assertEquals(2, nAfter.getNamedChildren().size());
		assertEquals("N1", ((Node) nAfter.getNamedChildren().get("N1")).getName());
		assertEquals("N2", ((Node) nAfter.getNamedChildren().get("N2")).getName());
	}

	public void testBeanWithChildsWithCycles() throws Exception {
		Node n = new Node("N");
		Node n1 = new Node("N1");
		Node n2 = new Node("N2");
		n.getChildren().add(n1);
		n.getChildren().add(n2);
		n2.setParent(n);
		
		Object lvResult = jsonSerializer.serialize(n);
		lvResult = jsonSerializer.deserialize(lvResult);
		Node nAfter = (Node) lvResult;
		assertEquals("N", nAfter.getName());
		assertNull(nAfter.getParent());
		assertEquals(new HashMap<Object, Object>(), nAfter.getNamedChildren());
		
		assertEquals(2, nAfter.getChildren().size());
		assertEquals("N1", ((Node) nAfter.getChildren().get(0)).getName());
		Node n2After = (Node) nAfter.getChildren().get(1);
		assertEquals("N2", n2After.getName());
		assertEquals("N", n2After.getParent().getName());
		assertEquals(nAfter, n2After.getParent());
	}

	public void testBeanWithNamedChildsWithCycles() throws Exception {
		Node n = new Node("N");
		Node n1 = new Node("N1");
		Node n2 = new Node("N2");
		n.getNamedChildren().put("N1", n1);
		n.getNamedChildren().put("N2", n2);
		n1.setParent(n);
		
		Object lvResult = jsonSerializer.serialize(n);
		lvResult = jsonSerializer.deserialize(lvResult);
		Node nAfter = (Node) lvResult;
		assertEquals("N", nAfter.getName());
		assertNull(nAfter.getParent());
		assertEquals(new ArrayList<Object>(), nAfter.getChildren());
		
		assertEquals(2, nAfter.getNamedChildren().size());
		Node n1After = (Node) nAfter.getNamedChildren().get("N1");
		assertEquals("N1", n1After.getName());
		assertEquals("N2", ((Node) nAfter.getNamedChildren().get("N2")).getName());
		
		assertEquals("N", n1After.getParent().getName());
		assertEquals(nAfter, n1After.getParent());
	}
	
	public void testPrimitive() throws Exception {
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		Object lvResult = jsonSerializer.serialize(lvPrimitive);
		lvResult = jsonSerializer.deserialize(lvResult);
		Primitive lvPrimitiveAfter = (Primitive) lvResult;
		
		assertEquals(lvPrimitive.getBooleanValue(), lvPrimitiveAfter.getBooleanValue());
		assertEquals(lvPrimitive.getDoubleValue(), lvPrimitiveAfter.getDoubleValue(), 0);
		assertEquals(lvPrimitive.getLongValue(), lvPrimitiveAfter.getLongValue());
		assertEquals(lvPrimitive.getByteValue(), lvPrimitiveAfter.getByteValue());
		assertEquals(lvPrimitive.getCharValue(), lvPrimitiveAfter.getCharValue());
		assertEquals(lvPrimitive.getFloatValue(), lvPrimitiveAfter.getFloatValue(), 0);
		assertEquals(lvPrimitive.getIntValue(), lvPrimitiveAfter.getIntValue());
		assertEquals(lvPrimitive.getShortValue(), lvPrimitiveAfter.getShortValue());
	}

	public void testWithSimpleKeyMapper() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("k1", "v1");
		jsonSerializer.serialize(lvMap);
		
		jsonSerializer.setWithSimpleKeyMapper(true);
		assertTrue(jsonSerializer.getWithSimpleKeyMapper());
		try {
			Object o = jsonSerializer.serialize(lvMap);
			o = jsonSerializer.deserialize(o);
			fail("Invalid key in map.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testSerializeWithClassPathFilter() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		assertNull(lvSerializer.getClassPropertyFilterHandler());
		
		ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).removeProperties(new String[] { "description", "build"});
		lvSerializer.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(lvFilter));
		
		Car lvCar = new Car("MyCar");
		lvCar.setBuild(new Date(123456789));
		lvCar.setDescription("This is my car");
		
		Object o = lvSerializer.serialize(lvCar);
		Car lvCarAfter = (Car) lvSerializer.deserialize(o);
		
		assertNull(lvCarAfter.getName());
		assertEquals( (long) new Date(123456789).getTime() / 1000, lvCarAfter.getBuild().getTime() / 1000);
		assertEquals("This is my car", lvCarAfter.getDescription());
		assertNotNull(lvSerializer.getClassPropertyFilterHandler());
	}

	public void testMapWithKeyThatAreNotStrings() throws Exception {
		Map<Comparable<?>, Comparable<?>> lvMap = new Hashtable<Comparable<?>, Comparable<?>>();
		lvMap.put("foo", "foo");
		lvMap.put(new Integer(4711), new Integer(4712));
		
		Serializer lvSerializer = new JsonSerializer();
		try {
			lvSerializer.serialize(lvMap);
			fail("Map-Keys with type Integer are not supported.");
		} catch (SerializerException e) {
			assertNotNull(e);
		}
	}
	
	public void testMapWithKeyThatAreStrings() throws Exception {
		Map<String, String> lvMap = new Hashtable<String, String>();
		lvMap.put("foo", "foo");
		lvMap.put("bar", "bar");
		
		Serializer lvSerializer = new JsonSerializer();
		Object lvJsonStr = lvSerializer.serialize(lvMap);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvSerializer.deserialize(lvJsonStr);
		
		assertEquals("foo", lvMapAfter.get("foo"));
		assertEquals("bar", lvMapAfter.get("bar"));
		assertEquals(HashMap.class, lvMapAfter.getClass());
	}

	public void testMapWithKeyThatAreStringsWithNullValue() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("foo", "foo");
		lvMap.put("null", null);
		
		JsonSerializer lvSerializer = new JsonSerializer();
		
		Object lvJsonStr = lvSerializer.serialize(lvMap);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvSerializer.deserialize(lvJsonStr);
		
		assertEquals("foo", lvMapAfter.get("foo"));
		assertTrue(lvMapAfter.containsKey("null"));
		assertNull(lvMapAfter.get("null"));
		assertEquals(HashMap.class, lvMapAfter.getClass());
	}

	public void testMapWithKeyThatAreStringsWithNoNullValue() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("foo", "foo");
		lvMap.put("null", null);
		
		JsonSerializer lvSerializer = new JsonSerializer();
		
		assertTrue(lvSerializer.getWithNullValuesInMap());
		lvSerializer.setWithNullValuesInMap(false);
		assertFalse(lvSerializer.getWithNullValuesInMap());
		
		Object lvJsonStr = lvSerializer.serialize(lvMap);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvSerializer.deserialize(lvJsonStr);
		
		assertEquals("foo", lvMapAfter.get("foo"));
		assertFalse(lvMapAfter.containsKey("null"));
		assertNull(lvMapAfter.get("null"));
		assertEquals(HashMap.class, lvMapAfter.getClass());
	}
		
	public void testQuotationMarkEscapes() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		Object o = lvSerializer.serialize("this is an \"quoted string\"");
		assertEquals("\"this is an \\\"quoted string\\\"\"", o);
		
		o = lvSerializer.serialize("this \" is an \"quoted string\"");
		assertEquals("\"this \\\" is an \\\"quoted string\\\"\"", o);
		
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("ke\"y", "val\"ue");
		o = lvSerializer.serialize(lvMap);
		assertEquals("{\"ke\\\"y\":\"val\\\"ue\"}", o);

	}

	public void testDoNotIgnoreOneBackSlash() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		Object o = lvSerializer.serialize("this is one\\");
		assertEquals("\"this is one\\\\\"", o);
	}

	public void testSlashMarkEscapesAndIgnoreOneBackSlash() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		Object o = lvSerializer.serialize("this is an /slash string/");
		assertEquals("\"this is an \\/slash string\\/\"", o);
		
		o = lvSerializer.serialize("this / is an /slash string/");
		assertEquals("\"this \\/ is an \\/slash string\\/\"", o);

		o = lvSerializer.serialize("this \\/ is an \\/slash string\\/");
		assertEquals("\"this \\\\\\/ is an \\\\\\/slash string\\\\\\/\"", o);

		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("ke/y", "val/ue");
		o = lvSerializer.serialize(lvMap);
		assertEquals("{\"ke\\/y\":\"val\\/ue\"}", o);
	}

	public void testBackSlashMarkEscapes() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		Object o = lvSerializer.serialize("this is an \\slash string\\");
		assertEquals("\"this is an \\\\slash string\\\\\"", o);
		
		o = lvSerializer.serialize("this \\ is an \\/slash string\\");
		assertEquals("\"this \\\\ is an \\\\\\/slash string\\\\\"", o);
		
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("ke\\\\y", "va\\\\lue");
		o = lvSerializer.serialize(lvMap);
		assertEquals("{\"ke\\\\\\\\y\":\"va\\\\\\\\lue\"}", o);
	}

	public void testEscapeQuotaInBean() throws Exception {
		Car lvCar = new Car("BMW");
		String lvDescription = "This BMW \" is my Car";
		lvCar.setDescription(lvDescription);
		
		Serializer lvSerializer = new JsonSerializer();
		Object lvResult = lvSerializer.serialize(lvCar);
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvResult);
		assertEquals(lvDescription, lvCarAfter.getDescription());
		
		lvResult = lvSerializer.serialize(lvCar);
		lvCarAfter = (Car) lvSerializer.deserialize(lvResult);
		assertEquals(lvDescription, lvCarAfter.getDescription());
	}

	public void testEscapeSimpleQuotaInBean() throws Exception {
		Car lvCar = new Car("BMW");
		String lvDescription = "This BMW ' is my Car";
		lvCar.setDescription(lvDescription);
		
		Serializer lvSerializer = new JsonSerializer();
		Object lvResult = lvSerializer.serialize(lvCar);
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvResult);
		assertEquals(lvDescription, lvCarAfter.getDescription());
		
		lvResult = lvSerializer.serialize(lvCar);
		lvCarAfter = (Car) lvSerializer.deserialize(lvResult);
		assertEquals(lvDescription, lvCarAfter.getDescription());
	}

	public void testDeSerializeWithOutRootClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setDescription("This BMW is my Car");
		
		Serializer lvSerializer = new JsonSerializer();
		String lvJsonStr = "{\"description\":\"This BMW is my Car\",\"name\":\"BMW\"}";
		Object o = lvSerializer.deserialize(lvJsonStr);
		Map<?, ?> lvMap = (Map<?, ?>) o;
		assertEquals("BMW", lvMap.get("name"));
		assertEquals("This BMW is my Car", lvMap.get("description"));
		assertFalse("Map don't contains class attribute", lvMap.containsKey("class"));
	}

	public void testDeSerializeWithRootClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setDescription("This BMW is my Car");
		
		Serializer lvSerializer = new JsonSerializer();
		String lvJsonStr = "{\"description\":\"This BMW is my Car\",\"name\":\"BMW\"}";
		Object o = lvSerializer.deserialize(lvJsonStr, Car.class);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals("This BMW is my Car", lvCarAfter.getDescription());
	}

	public void testDeSerializeException() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		Exception lvException = new ConversionException("JUnit-Test-Exception");
		Object lvJsonStr = lvSerializer.serialize(lvException);
		ConversionException e = (ConversionException) lvSerializer.deserialize(lvJsonStr);
		assertEquals("JUnit-Test-Exception", e.getMessage());
		assertNull(e.getCause());
		assertTrue(5 < e.getStackTrace().length);
	}
	
	public void testDeSerializeNestedException() throws Exception {
		Serializer lvSerializer = new JsonSerializer();
		Exception lvException = new ConversionException("JUnit-Test-Exception", new NullPointerException("Nested"));
		Object lvJsonStr = lvSerializer.serialize(lvException);
		ConversionException e = (ConversionException) lvSerializer.deserialize(lvJsonStr);
		assertEquals("JUnit-Test-Exception", e.getMessage());
		assertTrue(5 < e.getStackTrace().length);
		assertNotNull(e.getCause());
		Throwable lvNestedExc = e.getCause();
		assertEquals("Nested", lvNestedExc.getMessage());
		assertTrue(5 < lvNestedExc.getStackTrace().length);
	}

	public void __testWithDateFormat() throws Exception {
		Car lvCar = new Car("Ferrari");
		Date lvDate = new Date(82800000);
		lvCar.setBuild(lvDate);
		lvCar.setDescription("This is my car.");

		AbstractSerializer lvSerializer = new JsonSerializer();
		lvSerializer.getObjectUtil().addFormatterForType(new SimpleDateFormat("dd-MM-yyyy"), Date.class);
		Object o = lvSerializer.serialize(lvCar);
		assertEquals("{\"~unique-id~\":\"0\",\"class\":\"test.net.sf.sojo.model.Car\",\"build\":\"02-01-1970\",\"description\":\"This is my car.\",\"name\":\"Ferrari\"}", o);
		Car lvCarAfter = (Car) lvSerializer.deserialize(o);
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertEquals(lvCar.getBuild(), lvCarAfter.getBuild());
	}
	
	public void testBeanTimestamp() throws Exception {
		JsonSerializer serializer = new JsonSerializer();
		Bean bean = new Bean();
		long lvTime = new Date().getTime();
		Timestamp lvTimestamp = new Timestamp(lvTime);
		bean.setTimestamp(lvTimestamp);
		String lvJsonStr = (String) serializer.serialize(bean);
		bean = (Bean) serializer.deserialize(lvJsonStr);
		assertEquals(lvTimestamp, bean.getTimestamp());
	}

	public void testTimestamp() throws Exception {
		JsonSerializer serializer = new JsonSerializer();
		serializer.getObjectUtil().getConverter().addConversion(new Simple2SimpleConversion(String.class, Timestamp.class));
		long lvTime = new Date().getTime();
		Object o = serializer.serialize(new Timestamp(lvTime));
		o = serializer.deserialize(o, Timestamp.class);
		assertEquals(lvTime, ((Timestamp) o).getTime());
	}

	public void testBeanSqlDate() throws Exception {
		JsonSerializer serializer = new JsonSerializer();
		Bean bean = new Bean();
		long lvTime = 980809200000l;
                
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lvTime);
                
		bean.setDate(new java.sql.Date(lvTime));
		bean = (Bean) serializer.deserialize(serializer.serialize(bean));
                
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(bean.getDate().getTime());
                
		assertEquals(cal.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
                assertEquals(cal.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
                assertEquals(cal.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
	}

	public void testSqlDate() throws Exception {
		JsonSerializer serializer = new JsonSerializer();
		serializer.getObjectUtil().getConverter().addConversion(new Simple2SimpleConversion(String.class, java.sql.Date.class));
		long lvTime = 980809200000l;
                
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lvTime);
                
		Object o = serializer.serialize(new java.sql.Date(lvTime));
		o = serializer.deserialize(o, java.sql.Date.class);
                
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(((java.sql.Date) o).getTime());
                
                assertEquals(cal.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
                assertEquals(cal.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
                assertEquals(cal.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
	}

	public void testRepeatedEscapesWithSlash() throws Exception {
		final String str = "{\"key\":\"\\/path\"}";
		JsonSerializer lvSerializer = new JsonSerializer();
		Object lvResult = lvSerializer.deserialize(str, HashMap.class);
		assertTrue("Result is no Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertTrue(lvMap.containsKey("key"));
		String lvValue = (String) lvMap.get("key");
		assertEquals("/path", lvValue);

		lvResult = lvSerializer.serialize(lvResult);
		assertEquals(str, lvResult);
		
		lvResult = lvSerializer.deserialize(lvResult, HashMap.class);
		lvResult = lvSerializer.serialize(lvResult);
		assertEquals(str, lvResult);
	}
	
	public void testRepeatedEscapesWithNewLine() throws Exception {
		final String str = "{\"key\":\"\n path\"}";
		JsonSerializer lvSerializer = new JsonSerializer();
		Object lvResult = lvSerializer.deserialize(str, HashMap.class);
		assertTrue("Result is no Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertTrue(lvMap.containsKey("key"));
		String lvValue = (String) lvMap.get("key");
		assertEquals("\n path", lvValue);

		lvResult = lvSerializer.serialize(lvResult);
		assertEquals("{\"key\":\"\\n path\"}", lvResult);
		
		lvResult = lvSerializer.deserialize(lvResult, HashMap.class);
		lvMap = (Map<?, ?>) lvResult;
		assertTrue(lvMap.containsKey("key"));
		lvValue = (String) lvMap.get("key");
		assertEquals("\n path", lvValue);

		lvResult = lvSerializer.serialize(lvResult);
		assertEquals("{\"key\":\"\\n path\"}", lvResult);
	}

	public void testRepeatedEscapesWithSolidus() throws Exception {
		final String str = "{\"key\":\"\\/ path\"}";
		JsonSerializer lvSerializer = new JsonSerializer();
		Object lvResult = lvSerializer.deserialize(str, HashMap.class);
		assertTrue("Result is no Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertTrue(lvMap.containsKey("key"));
		String lvValue = (String) lvMap.get("key");
		assertEquals("/ path", lvValue);

		lvResult = lvSerializer.serialize(lvResult);
		assertEquals("{\"key\":\"\\/ path\"}", lvResult);
		
		lvResult = lvSerializer.deserialize(lvResult, HashMap.class);
		lvMap = (Map<?, ?>) lvResult;
		assertTrue(lvMap.containsKey("key"));
		lvValue = (String) lvMap.get("key");
		assertEquals("/ path", lvValue);

		lvResult = lvSerializer.serialize(lvResult);
		assertEquals("{\"key\":\"\\/ path\"}", lvResult);
	}

	public void testSerializeWithPropertyFilter() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		JsonSerializer lvSerializer = new JsonSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build" });
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp);
		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}
	
	public void testSerializeWithPropertyFilterAndFilteringClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		JsonSerializer lvSerializer = new JsonSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build", "class" });
		Object lvResult = lvSerializer.deserialize(lvTemp);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey("class"));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp, Car.class); 		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	public void testSerializeWithPropertyFilterAndFilteringUniqueId() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		JsonSerializer lvSerializer = new JsonSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build", UniqueIdGenerator.UNIQUE_ID_PROPERTY});
		Object lvResult = lvSerializer.deserialize(lvTemp, HashMap.class);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		assertTrue(lvMap.containsKey("class"));
		
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp, Car.class); 		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	public void testPrivateProperty() throws Exception {
		Bean lvBean = new Bean("Test Property");
		Object lvResult = jsonSerializer.serialize(lvBean);
		
		Bean lvBeanAfter = (Bean) jsonSerializer.deserialize(lvResult);
		assertEquals(lvBean.getMyProp(), lvBeanAfter.getMyProp());
	}
	
	public void testEmptyString() throws Exception {
		Map<String, String> map = new HashMap<String, String>();  
		map.put("name", "" );

		Object result =new JsonSerializer().serialize(map);
		assertEquals("{\"name\":\"\"}", result);
		
		Map<?, ?> mapAfter = (Map<?, ?>) new JsonSerializer().deserialize(result);
		assertEquals(map, mapAfter);
	}
	
	/** Solve Bug 1755893 */
	public void testSerializeMapWithBeanValue() throws Exception {
		JsonSerializer lvSerializer = new JsonSerializer();
		Bean lvBean = new Bean();
                long lvTime = 980809200000l;
                        
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lvTime);
                
		java.sql.Date lvDate = new java.sql.Date(lvTime);
		lvBean.setDate(lvDate);
		
		Map<String, Object> map = new HashMap<String, Object>();  
		map.put("bean", lvBean );
		map.put("key", "value" );
		
		Object lvResult = lvSerializer.serialize(map);
		lvResult = lvSerializer.deserialize(lvResult);
		assertNotNull(lvResult);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvResult;
                assertEquals("value", lvMapAfter.get("key"));
                
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(((Bean)lvMapAfter.get("bean")).getDate().getTime());
                
		assertEquals(cal.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
                assertEquals(cal.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
                assertEquals(cal.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
	}
	
	/** Solve Bug 1958512 empty string serialized to JSON */
	public void testEscapingEmptyStringByMap2Json() {
		Map<String, String> object = new HashMap<String, String>();
		object.put("emptyString", "");

		Serializer serializer = new JsonSerializer();
		String serializedValue = ((String)serializer.serialize(object));

		assertEquals("Correctly escaped", "{\"emptyString\":\"\"}", serializedValue);
	}
	
	/** Solve Bug 1958512 empty string serialized to JSON */
	public void testEscapingEmptyStringByJson2Map() {
		String jsonStr = "{\"emptyString\":\"\"}";
		Serializer serializer = new JsonSerializer();
		Map<?, ?> lvMap = (Map<?, ?>) serializer.deserialize(jsonStr);

		Object lvExpectedValue = lvMap.get("emptyString");
		assertEquals("Expected empty String and NOT:" + lvExpectedValue, lvExpectedValue , "");
	}

	public void __testSerializeURLproperty() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		String lvUrlStr = "http://myurl.net";
		lvBean.setUrl(new URL(lvUrlStr));

		JsonSerializer lvSerializer = new JsonSerializer();
		String lvJsonStr = (String) lvSerializer.serialize(lvBean);
		assertEquals("{\"~unique-id~\":\"0\",\"class\":\"test.net.sf.sojo.model.SpecialTypeBean\",\"url\":\"http:\\/\\/myurl.net\"}", lvJsonStr);
		
		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) lvSerializer.deserialize(lvJsonStr);
		assertEquals(lvBean.getUrl(), lvBeanAfter.getUrl());
		assertEquals(lvUrlStr, lvBeanAfter.getUrl().toString());
		assertNull(lvBeanAfter.getObject());
	}
	
	public void __testSerializeObject2DoubeProperty() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		Double lvValue = new Double(47.11);
		lvBean.setObject(lvValue);

		JsonSerializer lvSerializer = new JsonSerializer();
		String lvJsonStr = (String) lvSerializer.serialize(lvBean);
		assertEquals("{\"~unique-id~\":\"0\",\"class\":\"test.net.sf.sojo.model.SpecialTypeBean\",\"object\":47.11}", lvJsonStr);
		
		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) lvSerializer.deserialize(lvJsonStr);
		assertEquals(lvBean.getObject(), lvBeanAfter.getObject());
		assertEquals(lvValue, lvBeanAfter.getObject());
		assertNull(lvBeanAfter.getUrl());
	}

	public void __testRenameKeyWordClass() throws Exception {
		Util.setKeyWordClass("clazzz");
		assertEquals("clazzz", Util.getKeyWordClass());
		
		JsonSerializer lvSerializer = new JsonSerializer();
		String lvJsonStr = (String) lvSerializer.serialize(new Bean());
		assertEquals(36, lvJsonStr.indexOf("clazzz"));
		
		// !!! reset key word to class !!!
		Util.resetKeyWordClass();
	}
	
	public void testMultipleReferences() throws Exception {
	    BBean b = new BBean();
	    ABean a = new ABean();
	    a.setFirstRef(b);
	    a.setSecondRef(b);
	    a.setThirdRef(b);
	    a.setFourthRef(b);
	    
	    JsonSerializer jsonserializer = new JsonSerializer();

	    String aJsonStr = jsonserializer.serialize(a).toString();
	    ABean ades = (ABean) jsonserializer.deserialize(aJsonStr);
	    BBean bBeanAfter = ades.getFirstRef();
	    assertTrue (bBeanAfter != null); 
	    assertTrue (ades.getSecondRef() != null); 
	    assertTrue (ades.getSecondRef() == bBeanAfter);
	    assertTrue (ades.getThirdRef() != null); 
	    assertTrue (ades.getThirdRef() == bBeanAfter);
	    assertTrue (ades.getFourthRef() != null);
	    assertTrue (ades.getFourthRef() == bBeanAfter);
	} 

	public void testTransformDefaultMutableTreeNode() throws Exception {
		String lvFilter[] = new String [] {"class", "parent", "children", "userObject", "allowsChildren"};
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, lvFilter);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);
		
		Serializer s = new JsonSerializer();
		Object object = s.serialize(root);
		DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) s.deserialize(object);

		assertEquals("ROOT", rootAfter.getUserObject());
		assertEquals(1, rootAfter.getChildCount());
		assertEquals("Child", ((DefaultMutableTreeNode) rootAfter.getChildAt(0)).getUserObject());

		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}
}
