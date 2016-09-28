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
package test.net.sf.sojo.interchange.csv;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.SimpleFormatConversion;
import net.sf.sojo.interchange.AbstractSerializer;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.csv.CsvParser;
import net.sf.sojo.interchange.csv.CsvParserException;
import net.sf.sojo.interchange.csv.CsvSerializer;
import net.sf.sojo.interchange.csv.CsvWalkerInterceptor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.net.sf.sojo.model.Address;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.SpecialTypeBean;

import static org.junit.Assert.*;

public class CsvSerializerTest {
	
	private static CsvSerializer csvSerializer;

	@Before
	public void setUp() throws Exception {
		csvSerializer = new CsvSerializer();
		csvSerializer.setWithPropertyNamesInFirstLine(false);
		csvSerializer.setIgnoreNullValues(true);
	}

	@Test
	public void testSerializeNull() throws Exception {
		Object o = csvSerializer.serialize(null);
		assertNull(o);
	}

	@Test
	public void testDeSerializeEmpytStringl() throws Exception {
		Object o = csvSerializer.serialize("");
		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals(csvSerializer.getNullValue(), o);
		
		csvSerializer.setNullValue("~Null~Value~");
		o = csvSerializer.serialize("");
		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals(csvSerializer.getNullValue(), o);
		assertEquals("~Null~Value~", o);
	}

	@Test
	public void testSerializeString() throws Exception {
		Object o = csvSerializer.serialize("MyString");
		assertEquals("MyString", o);
	}

	@Test
	public void testSerializeStringWithComma() throws Exception {
		Object o = csvSerializer.serialize("My, String");
		assertEquals("\"My, String\"", o);
	}

	@Test
	public void testSerializeStringWithCrLf() throws Exception {
		Object o = csvSerializer.serialize("My" + CsvParser.CRLF + " String");
		assertEquals("\"My" + CsvParser.CRLF + " String\"", o);
	}

	@Test
	public void testSerializeStringWithDoubleQuote() throws Exception {
		Object o = csvSerializer.serialize("My \" String");
		assertEquals("\"My \"\" String\"", o);
	}

	@Test
	public void testSerializeStringWithCrLfAndDoubleQuote() throws Exception {
		Object o = csvSerializer.serialize("My" + CsvParser.CRLF + " Stri \" ng");
		assertEquals("\"My" + CsvParser.CRLF + " Stri \"\" ng\"", o);
	}

	@Test
	public void testSerializeStringWithCommandDoubleQuote() throws Exception {
		Object o = csvSerializer.serialize("My, Stri \" ng");
		assertEquals("\"My, Stri \"\" ng\"", o);
	}

	@Test
	public void testSerializeStringWithCrLfAndCommaAndDoubleQuote() throws Exception {
		Object o = csvSerializer.serialize("My, " + CsvParser.CRLF + " Stri \" ng");
		assertEquals("\"My, " + CsvParser.CRLF + " Stri \"\" ng\"", o);
	}

	@Test
	public void testSerializetWithPropertyNamesInFirstLineString() throws Exception {
		CsvSerializer lvSerializer = new CsvSerializer();
		lvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = lvSerializer.serialize("MyString");

		assertEquals("MyString", o);
	}

	@Test
	public void testDeSerializeString() throws Exception {
		Object o = csvSerializer.serialize("MyString");
		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals("MyString", o);
	}

	@Test
	public void testDeSerializeStringToNullValue() throws Exception {
		Object o = csvSerializer.serialize("MyString");
		o = csvSerializer.deserialize(o, null);
		assertNotNull(o);
		assertEquals("MyString", o);		
	}

	@Test
	public void testSerializeLong() throws Exception {
		Object o = csvSerializer.serialize(Long.valueOf("4711"));
		assertEquals("4711", o);
	}

	@Test
	public void testDeSerializeLong() throws Exception {
		Object o = csvSerializer.serialize(Long.valueOf("4711"));
		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals("4711", o);
	}

	@Test
	public void testDeSerializeLongWithToType() throws Exception {
		Object o = csvSerializer.serialize(Long.valueOf("4711"));
		o = csvSerializer.deserialize(o, Long.class);
		assertNotNull(o);
		assertEquals(Long.valueOf("4711"), o);
	}

	@Test
	public void testDeSerializeLongFromInvalidValue() throws Exception {
		Object o = csvSerializer.serialize("Not a Long Value");
		try {
			csvSerializer.deserialize(o, Long.class);
			fail("The String is an invalid Long value");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testSerializeStringArray() throws Exception {
		String s[] = new String[] { "a", "b", "c" };
		Object o = csvSerializer.serialize(s);
		assertEquals("a,b,c", o);
	}

	@Test
	public void testDeSerializeStringArray() throws Exception {
		String s[] = new String[] { "a", "b", "c" };
		Object o = csvSerializer.serialize(s);
		s = (String[]) csvSerializer.deserialize(o, s.getClass());
		assertEquals(3, s.length);
		assertEquals("a", s[0]);
		assertEquals("b", s[1]);
		assertEquals("c", s[2]);
		
		List<?> l = (List<?>) csvSerializer.deserialize(o, ArrayList.class);
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	@Test
	public void testDeSerializeCarArrayWithoutNamesInTheFirstRow() throws Exception {
		Date d = new Date();
		Car c1 = new Car("BMW");
		c1.setBuild(d);
		Car arr[] = new Car[] { c1 };
		Object o = csvSerializer.serialize(arr);
		try {
			csvSerializer.deserialize(o, arr.getClass());
			fail("No names was set in the first row (setWithPropertyNamesInFirstLine(true))");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testDeSerializeCarListWithOneElement() throws Exception {
		Date d = new Date();
		Car c1 = new Car("BMW");
		c1.setBuild(d);
		Car arr[] = new Car[] { c1 };
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(arr);
		List<?> lvList = (List<?>) csvSerializer.deserialize(o);
		assertEquals(1, lvList.size());
		assertTrue("Class is not a Car: " + lvList.get(0), lvList.get(0) instanceof Car);
		Car c = (Car) lvList.get(0);
		assertEquals("BMW", c.getName());
		// round (without ms)
		long l = d.getTime() / 1000;
		assertEquals(l * 1000, c.getBuild().getTime());
	}

	@Test
	public void testDeSerializeCarListWithManyElement() throws Exception {
		Date d = new Date();
		Car c1 = new Car("BMW");
		c1.setBuild(d);
		Car c2 = new Car("Audi");
		c2.setDescription("This is my car");
		Car c3 = new Car("Opel");
		c3.setDescription("This is your car");
		Car arr[] = new Car[] { c1, c2, c3 };

		csvSerializer.setWithPropertyNamesInFirstLine(true);
		assertTrue(csvSerializer.getIgnoreNullValues());
		csvSerializer.setIgnoreNullValues(false);
		assertFalse(csvSerializer.getIgnoreNullValues());
		csvSerializer.setNullValue("");

		Object o = csvSerializer.serialize(arr);
		List<?> lvList = (List<?>) csvSerializer.deserialize(o);
		assertEquals(3, lvList.size());
		assertTrue("Class is not a Car: " + lvList.get(0), lvList.get(0) instanceof Car);
		Car c = (Car) lvList.get(0);
		assertEquals("BMW", c.getName());
		// round (without ms)
		long l = d.getTime() / 1000;
		assertEquals(l * 1000, c.getBuild().getTime());
		
		Car c22 = (Car) lvList.get(1);
		assertEquals("Audi", c22.getName());
		assertEquals("This is my car", c22.getDescription());

		Car c33 = (Car) lvList.get(2);
		assertEquals("Opel", c33.getName());
		assertEquals("This is your car", c33.getDescription());
	}

	@Test
	public void testDeSerializeCarArray() throws Exception {
		Date d = new Date();
		Car c1 = new Car("BMW");
		c1.setBuild(d);
		Car c2 = new Car("Audi");
		c2.setDescription("The second car.");
		Car arr[] = new Car[] { c1, c2 };
		
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		csvSerializer.setIgnoreNullValues(false);
		
		Object o = csvSerializer.serialize(arr);
		arr = (Car[]) csvSerializer.deserialize(o, arr.getClass());
		
		assertEquals(2, arr.length);
		assertTrue("Class is not a Car: " + arr[0], arr[0] instanceof Car);
		Car c = (Car) arr[0];
		assertEquals("BMW", c.getName());
		// round (without ms)
		long l = d.getTime() / 1000;
		assertEquals(l * 1000, c.getBuild().getTime());
		assertNull(c.getDescription());
		assertNull(c.getProperties());
		
		c = (Car) arr[1];
		assertEquals("Audi", c.getName());
		assertEquals("The second car.", c.getDescription());
		assertNull(c.getBuild());
		assertNull(c.getProperties());
	}

	@Test
	public void testSerializeNestedStringArray() throws Exception {
		String row1[] = new String[] { "a", "b", "c" };
		String row2[] = new String[] { "1", "2", "3" };
		Object s[] = new Object[] { row1, row2 };
		
		Object o = csvSerializer.serialize(s);
		assertEquals("a,b,c" + CsvParser.CRLF + "1,2,3", o);
	}

	@Test
	public void testDeSerializeNestedStringArray() throws Exception {
		String row1[] = new String[] { "a", "b", "c" };
		String row2[] = new String[] { "1", "2", "3" };
		Object rows[] = new Object[] { row1, row2 };
		
		Object o = csvSerializer.serialize(rows);
		List<?> l = (List<?>) csvSerializer.deserialize(o);
		assertEquals(2, l.size());
		
		List<?> l2 = (List<?>) l.get(0);
		assertEquals("a", l2.get(0));
		assertEquals("b", l2.get(1));
		assertEquals("c", l2.get(2));
		
		l2 = (List<?>) l.get(1);
		assertEquals("1", l2.get(0));
		assertEquals("2", l2.get(1));
		assertEquals("3", l2.get(2));
		
		
		String row3[] = new String[] { ".", ";", "?" };
		rows = new Object[] { row1, row2, row3 };
		
		o = csvSerializer.serialize(rows);
		l = (List<?>) csvSerializer.deserialize(o);
		assertEquals(3, l.size());
		
		l2 = (List<?>) l.get(0);
		assertEquals("a", l2.get(0));
		assertEquals("b", l2.get(1));
		assertEquals("c", l2.get(2));
		
		l2 = (List<?>) l.get(1);
		assertEquals("1", l2.get(0));
		assertEquals("2", l2.get(1));
		assertEquals("3", l2.get(2));
		
		l2 = (List<?>) l.get(2);
		assertEquals(".", l2.get(0));
		assertEquals(";", l2.get(1));
		assertEquals("?", l2.get(2));

	}

	@Test
	public void testSerializeNestedStringArrayWithNamesInFirstRow() throws Exception {
		String row1[] = new String[] { "a", "b", "c" };
		String row2[] = new String[] { "1", "2", "3" };
		Object s[] = new Object[] { row1, row2 };
		
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(s);
		assertEquals("[0].[0],[0].[1],[0].[2]" + CsvParser.CRLF + "a,b,c" + CsvParser.CRLF + "1,2,3", o);
	}

	@Test
	public void testSerializeStringMap() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("k1", "v1");
		lvMap.put("k2", "v2");
		lvMap.put("k3", "v3");
		Object o = csvSerializer.serialize(lvMap);
		assertEquals("v1,v2,v3", o);
	}

	@Test
	public void testDeSerializeStringMap() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("k1", "v1");
		lvMap.put("k2", "v2");
		lvMap.put("k3", "v3");
		Object o = csvSerializer.serialize(lvMap);
		o = csvSerializer.deserialize(o);
		List<?> l = (List<?>) o;
		assertEquals(3, l.size());
		assertEquals("v1", l.get(0));
		assertEquals("v2", l.get(1));
		assertEquals("v3", l.get(2));
	}

	@Test
	public void testSerializeStringMapWithNamesInFirstRow() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("k1", "v1");
		lvMap.put("k2", "v2");
		lvMap.put("k3", "v3");
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(lvMap);
		assertEquals("k1,k2,k3" + CsvParser.CRLF + "v1,v2,v3", o);
	}

	@Test
	public void testToDeepNestedPropertiesWithArrays() throws Exception {
		String row1[] = new String[] { "a", "b", "c" };
		Object row2[] = new Object[] { "1", "2", new String[] { "3", "4" } };
		Object s[] = new Object[] { row1, row2 };
		try {
			csvSerializer.serialize(s);
			fail("Properties are to deep nested.");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testToDeepNestedPropertiesWithObjectGraph() throws Exception {
		Customer c = new Customer("Linke");
		c.setBirthDate(new Date());
		Address a1 = new Address();
		a1.setCity("Nuernberg");
		c.getAddresses().add(a1);
		try {
			csvSerializer.serialize(c);
			fail("One to many relation is not supported.");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testDeSerializerWithNullValue() throws Exception {
		Object o = csvSerializer.deserialize(null);
		assertNull(o);
	}

	@Test
	public void testDeSerializerSimpleString() throws Exception {
		Object o = csvSerializer.deserialize("MyString");
		assertNotNull(o);
		assertEquals("MyString", o);
	}

	@Test
	public void testDeSerializeListWithNullValues() throws Exception {
		String s ="a,b,c" + CsvParser.CRLF  +"1,2,3";
		
		List<?> l = (List<?>) csvSerializer.deserialize(s);
		assertEquals(2, l.size());

		List<?> l0 = (List<?>) l.get(0);
		assertEquals("a", l0.get(0));
		assertEquals("b", l0.get(1));
		assertEquals("c", l0.get(2));
		
		List<?> l1 = (List<?>) l.get(1);
		assertEquals("1", l1.get(0));
		assertEquals("2", l1.get(1));
		assertEquals("3", l1.get(2));

	}

	@Test
	public void testDeSerializeListWithNullValuesAndWithOutCrLf() throws Exception {
		List<String> l = new ArrayList<String>();
		l.add("a,b,c");

		try {
			csvSerializer.deserialize(l);
			fail("Cann only String deserialize");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testCsvWalkerInterceptorGetTable() throws Exception {
		CsvWalkerInterceptor lvCsvWalkerInterceptor = new CsvWalkerInterceptor();
		assertNotNull(lvCsvWalkerInterceptor.getTable());
	}

	@Test
	public void testWithPropertyNamesInFirstLine() throws Exception {
		assertFalse(csvSerializer.getWithPropertyNamesInFirstLine());
		
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		assertTrue(csvSerializer.getWithPropertyNamesInFirstLine());
	}

	@Test
	public void testDelimiter() throws Exception {
		assertEquals(",", csvSerializer.getDelimiter());

		csvSerializer.setDelimiter("|");
		assertEquals("|", csvSerializer.getDelimiter());
	}

	@Test
	public void testToManyColumns() throws Exception {
		String lvCsvString = 	"description,build,properties,name,~unique-id~,class" + CsvParser.CRLF +
										",Sat Feb 03 15:55:12 CET 2007,,BMW,0,test.net.sf.sojo.model.Car,ADD FAULT COLUMN";

		try {
			csvSerializer.setWithPropertyNamesInFirstLine(true);
			csvSerializer.deserialize(lvCsvString);
			fail("To many columns in row 1");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testToFewColumns() throws Exception {
		String lvCsvString = 	"description,build,properties,name,~unique-id~,class" + CsvParser.CRLF +
										",Sat Feb 03 15:55:12 CET 2007,BMW,0,test.net.sf.sojo.model.Car";

		try {
			csvSerializer.setWithPropertyNamesInFirstLine(true);
			csvSerializer.deserialize(lvCsvString);
			fail("To few columns in row 1");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testToDeepPath() throws Exception {
		Node n1 = new Node("Node 1");
		Node n2 = new Node("Node 2");
		n1.setParent(n2);

		csvSerializer.setWithPropertyNamesInFirstLine(true);
		csvSerializer.setIgnoreNullValues(false);
		try {
			csvSerializer.serialize(n1);			
			fail("Nested path is to deep.");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testEmptyMap() throws Exception {
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(new HashMap<Object, Object>());
		assertNotNull(o);
		assertEquals("", o);

		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals("", o);
	}

	@Test
	public void testEmptyList() throws Exception {
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(new ArrayList<Object>());
		assertNotNull(o);
		assertEquals("", o);

		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals("", o);
	}

	@Test
	public void testEmptyArray() throws Exception {
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(new Object[0]);
		assertNotNull(o);
		assertEquals("", o);

		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals("", o);
	}

	@Test
	public void testEmptySet() throws Exception {
		csvSerializer.setWithPropertyNamesInFirstLine(true);
		Object o = csvSerializer.serialize(new HashSet<Object>());
		assertNotNull(o);
		assertEquals("", o);

		o = csvSerializer.deserialize(o);
		assertNotNull(o);
		assertEquals("", o);
	}

	@Test
	public void testBeanWithCrLfInProperty() throws Exception {
		Car lvCar = new Car("BMW");
		String s = "This is my car." + CsvParser.CRLF + "I love this car.";
		lvCar.setDescription(s);
		CsvSerializer lvCsvSerializer = new CsvSerializer();
		Object o = lvCsvSerializer.serialize(lvCar);
		List<?> l = (List<?>) lvCsvSerializer.deserialize(o);
		o = l.get(0);
		assertTrue("Is not a Car: " + o.getClass(), o instanceof Car);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals(s, lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
		assertNull(lvCarAfter.getProperties());
	}

	@Test
	public void testBeanWithCommaInProperty() throws Exception {
		Car lvCar = new Car("BMW");
		String s = "This is my car." + CsvParser.COMMA + "I love this car.";
		lvCar.setDescription(s);
		CsvSerializer lvCsvSerializer = new CsvSerializer();
		Object o = lvCsvSerializer.serialize(lvCar);
		List<?> l = (List<?>) lvCsvSerializer.deserialize(o);
		o = l.get(0);
		assertTrue("Is not a Car: " + o.getClass(), o instanceof Car);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals(s, lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
		assertNull(lvCarAfter.getProperties());
	}

	@Test
	public void testBeanWithDoubleQuoteInProperty() throws Exception {
		Car lvCar = new Car("BMW");
		String s = "This is my car. \"I love this car.";
		lvCar.setDescription(s);
		CsvSerializer lvCsvSerializer = new CsvSerializer();
		Object o = lvCsvSerializer.serialize(lvCar);
		List<?> l = (List<?>) lvCsvSerializer.deserialize(o);
		o = l.get(0);
		assertTrue("Is not a Car: " + o.getClass(), o instanceof Car);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals(s, lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
		assertNull(lvCarAfter.getProperties());
	}

	@Test
	public void testDeSerializeWithOutRootClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setDescription("This BMW is my Car");
		
		Serializer lvSerializer = new CsvSerializer();
		String lvCsvStr = "description,name" + CsvParser.CRLF + "This BMW is my Car,BMW";
		Object o = lvSerializer.deserialize(lvCsvStr);
		List<?> lvList =  (List<?>) o;
		Map<?, ?> lvMap = (Map<?, ?>) lvList.get(0);
		assertEquals("BMW", lvMap.get("name"));
		assertEquals("This BMW is my Car", lvMap.get("description"));
		assertFalse("Map don't contains class attribute", lvMap.containsKey("class"));		
	}

	@Test
	public void testDeSerializeWithRootClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setDescription("This BMW is my Car");
		
		Serializer lvSerializer = new CsvSerializer();
		String lvCsvStr = "description,name" + CsvParser.CRLF + "This BMW is my Car,BMW";
		Object o = lvSerializer.deserialize(lvCsvStr, Car.class);
		List<?> lvList = (List<?>) o;
		Car lvCarAfter = (Car) lvList.get(0);
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals("This BMW is my Car", lvCarAfter.getDescription());
	}

	@Test
	public void testSerializeWithPropertyFilter() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		AbstractSerializer lvSerializer = new CsvSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build" });
		List<?> l = (List<?>) lvSerializer.deserialize(lvTemp);
		Car lvCarAfter = (Car) l.get(0);
		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	@Test
	public void testSerializeWithPropertyFilterAndFilteringClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		AbstractSerializer lvSerializer = new CsvSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build", "class" });
		Object lvResult = ((List<?>) lvSerializer.deserialize(lvTemp)).get(0);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey("class"));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		
		List<?> l = (List<?>) lvSerializer.deserialize(lvTemp, Car.class);
		Car lvCarAfter = (Car) l.get(0);
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	@Test
	public void testSerializeWithPropertyFilterAndFilteringUniqueId() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		AbstractSerializer lvSerializer = new CsvSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build", UniqueIdGenerator.UNIQUE_ID_PROPERTY});
		List<?> l = (List<?>) lvSerializer.deserialize(lvTemp, HashMap.class);
		Object lvResult = l.get(0);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		assertTrue(lvMap.containsKey("class"));
		
		l = (List<?>) lvSerializer.deserialize(lvTemp, Car.class);
		Car lvCarAfter = (Car) l.get(0);
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	@Test
	public void testSerializeURLpropety() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		String lvUrlStr = "http://myurl.net";
		lvBean.setUrl(new URL(lvUrlStr));
		
		Serializer lvSerializer = new CsvSerializer();
		String lvXmlStr = (String) lvSerializer.serialize(lvBean);
		assertTrue(lvXmlStr.indexOf(lvUrlStr) > 0);
		
		List<?> lvList = (List<?>) lvSerializer.deserialize(lvXmlStr);
		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) lvList.get(0);
		assertEquals(lvBean.getUrl(), lvBeanAfter.getUrl());
		assertEquals(lvUrlStr, lvBeanAfter.getUrl().toString());
		assertNull(lvBeanAfter.getObject());
	}

	@Test
	public void testSerializeObject2BigDecimalProperty() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		BigDecimal lvValue = new BigDecimal("47.11");
		lvBean.setObject(lvValue);
		
		Serializer lvSerializer = new CsvSerializer();
		String lvXmlStr = (String) lvSerializer.serialize(lvBean);
		assertTrue(lvXmlStr.indexOf(lvValue.toString()) > 0);
		
		List<?> lvList = (List<?>) lvSerializer.deserialize(lvXmlStr);
		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) lvList.get(0);
		assertEquals(lvBean.getObject().toString(), lvBeanAfter.getObject());
		assertEquals(lvValue, new BigDecimal (lvBeanAfter.getObject().toString()));
		assertNull(lvBeanAfter.getUrl());
	}

}
