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
package test.net.sf.sojo.interchange.xmlrpc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.xmlrpc.XmlRpcException;
import net.sf.sojo.interchange.xmlrpc.XmlRpcParser;
import net.sf.sojo.interchange.xmlrpc.XmlRpcSerializer;
import net.sf.sojo.util.Util;
import test.net.sf.sojo.model.ABean;
import test.net.sf.sojo.model.Address;
import test.net.sf.sojo.model.BBean;
import test.net.sf.sojo.model.Bean;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.Primitive;
import test.net.sf.sojo.model.SpecialTypeBean;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class XmlRpcSerializerTest extends TestCase {

	private XmlRpcSerializer xmlRpcSerializer = new XmlRpcSerializer();
	
	
	public void testXmlRpcParserWithNullValue() throws Exception {
		assertNull(new XmlRpcParser().parse(null));
	}
	
	public void testXmlRpcParserWithNoXmlString() throws Exception {
		try {
			new XmlRpcParser().parse("No valid XML-String!");
			fail("The String is not valid XML-String!");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}
	
	public void testDeserializeWithNullValue() throws Exception {
		assertNull(xmlRpcSerializer.deserialize(null));
	}
	
	public void testSimpleStringValueDeserialize() throws Exception {
		String s = "<params><param><value>Simple String</value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals("Simple String", lvResult);
		assertNull(xmlRpcSerializer.getMethodName());
		
		s = "<params><param><value> Simple String </value></param></params>";
		lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(" Simple String ", lvResult);
		
//		s = "<params><param><value><string>Simple &gt; AAA</string></value></param></params>";
//		lvResult = xmlRpcSerializer.deserialize(s);
//		System.out.println(((List) lvResult).get(0));
//		assertEquals("Simple String", ((List) lvResult).get(0));
	}
	
	public void testSimpleIntValueDeserialize() throws Exception {
		String s = "<params><param><value><i4>4711</i4></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(new Integer(4711), lvResult);
		
		s = "<params><param><value><int>4712</int></value></param></params>";
		lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(new Integer(4712), lvResult);
	}

	public void testSimpleBooleanValueDeserialize() throws Exception {
		String s = "<params><param><value><boolean>1</boolean></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(Boolean.TRUE, lvResult);
		
		s = "<params><param><value><boolean>0</boolean></value></param></params>";
		lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(Boolean.FALSE, lvResult);

		try {
			s = "<params><param><value><boolean>100</boolean></value></param></params>";
			xmlRpcSerializer.deserialize(s);
			fail("100 is not a valid boolean value!");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}

	public void testSimpleDoubleDeserialize() throws Exception {
		String s = "<params><param><value><double>47.11</double></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(new Double("47.11"), lvResult);
		
		s = "<params><param><value><double>0.0004711</double></value></param></params>";
		lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals(new Double("0.0004711"), lvResult);

		try {
			s = "<params><param><value><double>0,0004711</double></value></param></params>";
			xmlRpcSerializer.deserialize(s);
			fail("The value is not valid Double!");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}

	public void testSimpleDateDeserialize() throws Exception {
		String s = "<params><param><value><dateTime.iso8601>20010330T13:54:55</dateTime.iso8601></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
                Calendar calendar = Calendar.getInstance();
                calendar.set(2001, 2, 30, 13, 54, 55);
                calendar.set(Calendar.MILLISECOND, 0);
		assertEquals(calendar.getTime(), lvResult);
		
		try {
			s = "<params><param><value><dateTime.iso8601>20010330X13:54:55</dateTime.iso8601></value></param></params>";
			xmlRpcSerializer.deserialize(s);
			fail("The value is not valid Date!");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}
		
	public void testExtensionsCalendar() throws Exception {
		Calendar lvCalendar = Calendar.getInstance();
		Date d = lvCalendar.getTime();
		Object lvResult = xmlRpcSerializer.serialize(lvCalendar);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS");
		String lvDateStr = sdf.format(d);
		String s = "<params><param><value><ex:dateTime>" + lvDateStr + "</ex:dateTime></value></param></params>";
		assertEquals(s, lvResult);
		
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Calendar lvCalendarAfter = (Calendar) lvResult;
		assertEquals(d, lvCalendarAfter.getTime());
	}
	
	public void testExtensionsCalendarInValid() throws Exception {
		String s = "<params><param><value><ex:dateTime>20010330 25:61:55.123</ex:dateTime></value></param></params>";
		try {
			xmlRpcSerializer.deserialize(s);
			fail("Not valid XML-RPC date");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}


	public void testExtensionsBigDecimal() throws Exception {
		BigDecimal bd = new BigDecimal("47.0011");
		Object lvResult = xmlRpcSerializer.serialize(bd);
		
		String s = "<params><param><value><ex:bigdecimal>" + bd + "</ex:bigdecimal></value></param></params>";
		assertEquals(s, lvResult);
		
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		BigDecimal bdAfter = (BigDecimal) lvResult;
		assertEquals(bd, bdAfter);
	}

	public void testExtensionsBigInteger() throws Exception {
		BigInteger bi = new BigInteger("470011");
		Object lvResult = xmlRpcSerializer.serialize(bi);
		
		String s = "<params><param><value><ex:biginteger>" + bi + "</ex:biginteger></value></param></params>";
		assertEquals(s, lvResult);
		
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		BigInteger biAfter = (BigInteger) lvResult;
		assertEquals(bi, biAfter);
	}
	
	public void testExtensionsNullValue() throws Exception {
		Object lvResult = xmlRpcSerializer.serialize(null);
		
		String s = "<params><param><value><ex:nil>" + null + "</ex:nil></value></param></params>";
		assertEquals(s, lvResult);
		
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		assertNull(lvResult);
	}
	
	public void testDifferentValueDeserialize() throws Exception {
		String s = 	"<params>" +
						"<param><value><int>123</int></value></param>" +
						"<param><value><boolean>1</boolean></value></param>" +
						"<param><value>MyString 1</value></param>" +
						"<param><value><string>MyString 222</string></value></param>" +
					"</params>";

		Object lvResult = xmlRpcSerializer.deserialize(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(new Integer(123), lvList.get(0));
		assertEquals(Boolean.TRUE, lvList.get(1));
		assertEquals("MyString 1", lvList.get(2));
		assertEquals("MyString 222", lvList.get(3));
	}

	public void testStringArray() throws Exception {
		String s = "<params><param><value><array><data>" +
						"<value><string>aaA</string></value>" +
						"<value><string>bBb</string></value>" +
					"</data></array></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals("aaA", lvList.get(0));
		assertEquals("bBb", lvList.get(1));
	}
	
	public void testMixedTypeArray() throws Exception {
		String s = "<params><param><value><array><data>" +
						"<value>aaA</value>" +
						"<value><int>123</int></value>" +
						"<value><boolean>1</boolean></value>" +
						"<value><double>.123</double></value>" +
					"</data></array></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals("aaA", lvList.get(0));
		assertEquals(new Integer(123), lvList.get(1));
		assertEquals(Boolean.TRUE, lvList.get(2));
		assertEquals(new Double("0.123"), lvList.get(3));
	}

	public void testMixedTypeNestedArray() throws Exception {
		String s = "<params><param><value><array><data>" +
						"<value>text</value>" +
						"<value><int>123</int></value>" +
						"<value><array><data>" +
							"<value><boolean>1</boolean></value>" +
							"<value><double>.123</double></value>" +
						"</data></array></value>" +
					"</data></array></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		List<?> lvParams = (List<?>) lvResult;
		assertEquals("text", lvParams.get(0));
		assertEquals(new Integer(123), lvParams.get(1));
		List<?> lvList2 = (List<?>) lvParams.get(2);
		assertEquals(Boolean.TRUE, lvList2.get(0));
		assertEquals(new Double("0.123"), lvList2.get(1));
	}

	public void testStringStructure() throws Exception {
		String s = "<params><param><value><struct>" +
						"<member><name>key 1</name><value><string>value 1</string></value></member>" +
						"<member><name>key 2</name><value>value 2</value></member>" +
					"</struct></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(2, lvMap.size());
		assertEquals("value 1", lvMap.get("key 1"));
		assertEquals("value 2", lvMap.get("key 2"));
	}
	
	public void testMixedTypeStructure() throws Exception {
		String s = "<params><param><value><struct>" +
						"<member><name>symbol</name><value><string>RHAT</string></value></member>" +
						"<member><name>limit</name><value><double>2.25</double></value></member>" +
						"<member><name>int</name><value><int>225</int></value></member>" +
						"<member><name>boolean</name><value><boolean>0</boolean></value></member>" +
					"</struct></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(4, lvMap.size());
		assertEquals("RHAT", lvMap.get("symbol"));
		assertEquals(new Double("2.25"), lvMap.get("limit"));
		assertEquals(new Integer("225"), lvMap.get("int"));
		assertEquals(Boolean.FALSE, lvMap.get("boolean"));
	}

	public void testNestedStructure() throws Exception {
		String s = "<params><param><value><struct>" +
				"<member><name>symbol</name><value><string>RHAT</string></value></member>" +
				"<member><name>limit</name><value><double>2.25</double></value></member>" +
				"<member><name>map</name><value><struct>" +
					"<member><name>k1</name><value><string>v1</string></value></member>" +
					"<member><name>k2</name><value><string>v2</string></value></member>" +
				"</struct></value></member>" +
				"<member><name>boolean</name><value><boolean>1</boolean></value></member>" +
			"</struct></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(4, lvMap.size());
		assertEquals("RHAT", lvMap.get("symbol"));
		assertEquals(new Double("2.25"), lvMap.get("limit"));
		Map<?, ?> lvMap2 = (Map<?, ?>) lvMap.get("map");
		assertEquals("v1", lvMap2.get("k1"));
		assertEquals("v2", lvMap2.get("k2"));
		assertEquals(Boolean.TRUE, lvMap.get("boolean"));
	}
	
	public void testNestedStructureInArray() throws Exception {
		String s = "<params><param><value><array><data>" +
						"<value>text</value>" +
						"<value><int>123</int></value>" +
						"<value><struct>" +
							"<member><name>k1</name><value><string>v1</string></value></member>" +
							"<member><name>k2</name><value><string>v2</string></value></member>" +
						"</struct></value>" +						
					"</data></array></value></param></params>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals("text", lvList.get(0));
		assertEquals(new Integer(123), lvList.get(1));
		Map<?, ?> lvMap = (Map<?, ?>) lvList.get(2);
		assertEquals("v1", lvMap.get("k1"));
		assertEquals("v2", lvMap.get("k2"));
	}
	
	public void testSerializeSimpleBean() throws Exception {
		Car lvCar = new Car("MyCar");
		
		xmlRpcSerializer.setWithSimpleKeyMapper(true);
		Object lvResult = xmlRpcSerializer.serialize(lvCar);
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Car lvCarAfter = (Car) lvResult;
		assertEquals(lvCar.getName(), lvCarAfter.getName());
	}
	
	public void testSerializeSimpleBean2() throws Exception {
		Car lvCar = new Car("MyCar");
		lvCar.setDescription("this is my car");
		lvCar.setBuild(new Date(980859295000l));
		
		xmlRpcSerializer.setWithSimpleKeyMapper(true);
		Object lvResult = xmlRpcSerializer.serialize(lvCar);
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Car lvCarAfter = (Car) lvResult;
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertEquals(lvCar.getBuild(), lvCarAfter.getBuild());
	}
	
	public void testSerializeSimpleBeanWithProperties() throws Exception {
		Car lvCar = new Car("MyCar");
		lvCar.setProperties(new Properties());
		lvCar.getProperties().put("k1", "v1");
		lvCar.getProperties().put("k2", "v2");
		
		xmlRpcSerializer.setWithSimpleKeyMapper(false);
		Object lvResult = xmlRpcSerializer.serialize(lvCar);
		
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Car lvCarAfter = (Car) lvResult;
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		Properties lvProperties = lvCarAfter.getProperties();
		assertEquals(lvCar.getProperties().get("k1"), lvProperties.get("k1"));
		assertEquals(lvCar.getProperties().get("k2"), lvProperties.get("k2"));
		assertEquals("v1", lvProperties.get("k1"));
		assertEquals("v2", lvProperties.get("k2"));
	}
	
	public void testSerializeComplexBean() throws Exception {
		Customer lvCustomer = new Customer();
		lvCustomer.setLastName("LastName");
		Date d = new Date(980859295000l);
		lvCustomer.setBirthDate(d);
		Address a1 = new Address();
		a1.setCity("MyCity 1");
		a1.setPostcode("12345");
		Address a2 = new Address();
		a2.setCity("MyCity 2");
		a2.setPostcode("54321");

		lvCustomer.getAddresses().add(a1);
		lvCustomer.getAddresses().add(a2);
		
		Object lvResult = xmlRpcSerializer.serialize(lvCustomer);
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Customer lvCustomerAfter = (Customer) lvResult;
		assertEquals("LastName", lvCustomerAfter.getLastName());
		assertEquals(d, lvCustomerAfter.getBirthDate());
		assertEquals(2, lvCustomerAfter.getAddresses().size());
		Iterator<?> it = lvCustomer.getAddresses().iterator();
		Address a1After = (Address) it.next();
		Address a2After = (Address) it.next();
		if (a1After.getCity().equals("MyCity 1")) {
			assertEquals("MyCity 1", a1After.getCity());
			assertEquals("12345", a1After.getPostcode());
			assertEquals("MyCity 2", a2After.getCity());
			assertEquals("54321", a2After.getPostcode());
		}
		else {
			assertEquals("MyCity 2", a1After.getCity());
			assertEquals("54321", a1After.getPostcode());
			assertEquals("MyCity 1", a2After.getCity());
			assertEquals("12345", a2After.getPostcode());
		}

	}

	public void testSerializeComplexBeanWithArray() throws Exception {
		Customer lvCustomer = new Customer();
		lvCustomer.setLastName("LastName");
		Date d = new Date(980859295000l);
		lvCustomer.setBirthDate(d);
		Address a1 = new Address();
		a1.setCity("MyCity 1");
		a1.setPostcode("12345");
		Address a2 = new Address();
		a2.setCity("MyCity 2");
		a2.setPostcode("54321");

		lvCustomer.setPartner(new Address[] { a1, a2 });
		Object lvResult = xmlRpcSerializer.serialize(lvCustomer);
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Customer lvCustomerAfter = (Customer) lvResult;
		assertEquals("LastName", lvCustomerAfter.getLastName());
		assertEquals(d, lvCustomerAfter.getBirthDate());
		
		Address aArray[] = (Address[]) lvCustomer.getPartner();
		assertEquals(2, aArray.length);
		assertEquals("MyCity 1", aArray[0].getCity());
		assertEquals("12345", aArray[0].getPostcode());
		assertEquals("MyCity 2", aArray[1].getCity());
		assertEquals("54321", aArray[1].getPostcode());	
	}
	
	public void testPrimitive() throws Exception {
		Primitive p = Primitive.createPrimitiveExample();

		Object lvResultSer = xmlRpcSerializer.serialize(p);
		Object lvResultDeser = xmlRpcSerializer.deserialize(lvResultSer);
		assertThat(lvResultDeser, is(instanceOf(Primitive.class)));

		assertEquals(p, lvResultDeser);
	}

	public void testCharPrimitive() {
	    CharPrimitive p = new CharPrimitive();
	    p.setCharVal('a');
	    Object lvResultSer = xmlRpcSerializer.serialize(p);

	    assertThat((String)lvResultSer, containsString("<name>charVal</name><value><string>a</string></value>"));
        Object lvResultDeser = xmlRpcSerializer.deserialize(lvResultSer);
        assertThat(lvResultDeser, is(instanceOf(CharPrimitive.class)));

        assertThat((CharPrimitive)lvResultDeser, is(equalTo(p)));
    }

    public void testSimpleChar() {
	    Object lvResultSer = xmlRpcSerializer.serialize('A');
	    Object lvResultDeser = xmlRpcSerializer.deserialize(lvResultSer);
	    assertThat(lvResultDeser, instanceOf(String.class));
	    assertThat(lvResultDeser.toString(), equalTo("A"));
    }

	public void testBeanWithCycle() throws Exception {
		Node n = new Node("ROOT");
		Node n1 = new Node("N1");
		Node n2 = new Node("N2");
		n.getNamedChildren().put("n1", n1);
		n.getNamedChildren().put("n2", n2);
		n2.setParent(n);
		
		Object lvResult = xmlRpcSerializer.serialize(n);
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		Node nAfter = (Node) lvResult;
		assertEquals("ROOT", nAfter.getName());
		assertEquals(2, nAfter.getNamedChildren().size());
		Node n2After = (Node) nAfter.getNamedChildren().get("n2");
		assertEquals(nAfter, n2After.getParent());
		
		Node n1After = (Node) nAfter.getNamedChildren().get("n1");
		assertEquals("N1", n1After.getName());
	}

	public void testFault() throws Exception {

		String s = "<fault><value><struct>" +
		        		"<member><name>faultCode</name><value><int>23</int></value></member>" +
		        		"<member><name>faultString</name><value><string>Unknown stock symbol ABCD</string></value></member>" +
		        	"</struct></value></fault>";
		
		xmlRpcSerializer.setReturnValueAsList(false);
		Object lvResult = xmlRpcSerializer.deserialize(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(new Integer("23"), lvMap.get("faultCode"));
		assertEquals("Unknown stock symbol ABCD", lvMap.get("faultString"));
		xmlRpcSerializer.setReturnValueAsList(true);
	}
	
	public void testFaultAndThrow() throws Exception {

		String s = "<fault><value><struct>" +
		        		"<member><name>faultCode</name><value><int>23</int></value></member>" +
		        		"<member><name>faultString</name><value><string>Unknown stock symbol ABCD</string></value></member>" +
		        	"</struct></value></fault>";
		
		xmlRpcSerializer.setReturnValueAsList(false);
		xmlRpcSerializer.setConvertResult2XmlRpcExceptionAndThrow(true);
		try {
			xmlRpcSerializer.deserialize(s);	
			fail("Must throw a XmlRpcException.");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
		

		xmlRpcSerializer.setReturnValueAsList(true);
		xmlRpcSerializer.setConvertResult2XmlRpcExceptionAndThrow(false);
	}


	public void testMethodWithSimpleStringValueDeserialize() throws Exception {
		String s = "<methodCall><methodName>myMethod</methodName>" +
						"<params><param><value>Simple String</value></param></params>" +
					"</methodCall>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals("Simple String", lvResult);
		assertEquals("myMethod", xmlRpcSerializer.getMethodName());				
	}
	
	public void testRespons() throws Exception {
		String s = "<methodResponse>" +
						"<params><param><value>Simple String</value></param></params>" +
					"</methodResponse>";
		Object lvResult = xmlRpcSerializer.deserialize(s);
		assertEquals("Simple String", lvResult);
		assertNull(xmlRpcSerializer.getMethodName());				
	}

	public void testCompleteXmlRpcRequest() throws Exception {
		String lvResult = xmlRpcSerializer.serializeXmlRpcRequest("echo", "my echo string");
		String s = "<?xml version='1.0' encoding='UTF-8'?>" +
						"<methodCall><methodName>echo</methodName>" +
						"<params><param><value><string>my echo string</string></value></param></params>" +
					"</methodCall>";
		assertEquals(s, lvResult);
	}

	public void testCompleteXmlRpcRequestWithOutmethodName() throws Exception {
		try {
			xmlRpcSerializer.serializeXmlRpcRequest("", "my echo string");
			fail("Must thrown XmlRpcException - empty method-name");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
		try {
			xmlRpcSerializer.serializeXmlRpcRequest(null, "my echo string");
			fail("Must thrown XmlRpcException - missing method-name");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}

	public void testCompleteXmlRpcResponse() throws Exception {
		String lvResult = xmlRpcSerializer.serializeXmlRpcResponse("my echo string");
		String s = "<?xml version='1.0' encoding='UTF-8'?><methodResponse>" +
						"<params><param><value><string>my echo string</string></value></param></params>" +
					"</methodResponse>";
		assertEquals(s, lvResult);
	}

	public void testEmptyString() throws Exception {
		Object lvResult = xmlRpcSerializer.serialize("");
		String s = "<params><param><value><string></string></value></param></params>";
		assertEquals(s, lvResult);
		
		lvResult = xmlRpcSerializer.serialize(" ");
		s = "<params><param><value><string> </string></value></param></params>";
		assertEquals(s, lvResult);
	}
	
	public void testThrownXmlRpcException() throws Exception {
		Object lvResult = xmlRpcSerializer.serialize("aa");
		String s = "<params><param><value><string>aa</string></value></param></params>";
		assertEquals(s, lvResult);
		xmlRpcSerializer.setConvertResult2XmlRpcExceptionAndThrow(true);
		xmlRpcSerializer.setReturnValueAsList(false);
		lvResult = xmlRpcSerializer.deserialize(lvResult);
		assertEquals("aa", lvResult);
		
		String faultString = "<fault><value><struct>" +
								"<member><name>faultCode</name><value><int>23</int></value></member>" +
								"<member><name>faultString</name><value><string>Unknown stock symbol ABCD</string></value></member>" +
							 "</struct></value></fault>";
	
		try {
			xmlRpcSerializer.deserialize(faultString);
			fail("Must thrown XmlRpcException");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}


		xmlRpcSerializer.setConvertResult2XmlRpcExceptionAndThrow(false);
		Map<?, ?> lvMap = (Map<?, ?>) xmlRpcSerializer.deserialize(faultString);
		assertEquals(new Integer("23"), lvMap.get("faultCode"));
		assertEquals("Unknown stock symbol ABCD", lvMap.get("faultString"));
	}
	
	public void testThrownXmlRpcExceptionNotXmlRpcString() throws Exception {
		String s = "<no-xml-rpc><param><value><string>aa</string></value></param></no-xml-rpc>";
		try {
			xmlRpcSerializer.deserialize(s);
			fail("Must thrown XmlRpcException");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
		
	}
	
	public void testSpecialXmlChars() throws Exception {
		Object lvResult = xmlRpcSerializer.serialize("test < -- > string");
		String s = "<params><param><value><string>test &lt; -- &gt; string</string></value></param></params>";
		assertEquals(s, lvResult);
		
//		lvResult = xmlRpcSerializer.deserialize(lvResult);
	}
	
	
	public void testSerializeWithClassPathFilter() throws Exception {
		Serializer lvSerializer = new XmlRpcSerializer(false);
		ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).removeProperty("build").removeProperty("description");
		lvSerializer.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(lvFilter));
		
		Car lvCar = new Car("MyCar");
		lvCar.setBuild(new Date(123456789));
		lvCar.setDescription("This is my car");
		
		Object o = lvSerializer.serialize(lvCar);
		Car lvCarAfter = (Car)  lvSerializer.deserialize(o);
		
		assertNull(lvCarAfter.getName());
		assertEquals( (long) new Date(123456789).getTime() / 1000, lvCarAfter.getBuild().getTime() / 1000);
		assertEquals("This is my car", lvCarAfter.getDescription());
	}

	public void testDeSerializeWithOutRootClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setDescription("This BMW is my Car");
		
		Serializer lvSerializer = new XmlRpcSerializer();
		String lvXmlRpcStr = 	"<params><param><value><struct>" +
									"<member><name>description</name><value><string>This BMW is my Car</string></value></member>" +
									"<member><name>name</name><value><string>BMW</string></value></member>" +
								"</struct></value></param></params>";
		Object o = lvSerializer.deserialize(lvXmlRpcStr);
		Map<?, ?> lvMap = (Map<?, ?>) o;
		assertEquals("BMW", lvMap.get("name"));
		assertEquals("This BMW is my Car", lvMap.get("description"));
		assertFalse("Map don't contains class attribute", lvMap.containsKey("class"));
	}

	public void testDeSerializeWithRootClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setDescription("This BMW is my Car");
		
		Serializer lvSerializer = new XmlRpcSerializer();
		String lvXmlRpcStr = 	"<params><param><value><struct>" +
									"<member><name>description</name><value><string>This BMW is my Car</string></value></member>" +
									"<member><name>name</name><value><string>BMW</string></value></member>" +
								"</struct></value></param></params>";
		Object o = lvSerializer.deserialize(lvXmlRpcStr, Car.class);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals("This BMW is my Car", lvCarAfter.getDescription());
	}

	public void testDeSerializeException() throws Exception {
		Serializer lvSerializer = new XmlRpcSerializer();
		Exception lvException = new ConversionException("JUnit-Test-Exception");
		Object lvXmlRpcStr = lvSerializer.serialize(lvException);
		ConversionException e = (ConversionException) lvSerializer.deserialize(lvXmlRpcStr);
		assertEquals("JUnit-Test-Exception", e.getMessage());
		assertNull(e.getCause());
		assertTrue(5 < e.getStackTrace().length);
	}
	
	public void testDeSerializeNestedException() throws Exception {
		Serializer lvSerializer = new XmlRpcSerializer();
		Exception lvException = new ConversionException("JUnit-Test-Exception", new NullPointerException("Nested"));
		Object lvXmlRpcStr = lvSerializer.serialize(lvException);
		ConversionException e = (ConversionException) lvSerializer.deserialize(lvXmlRpcStr);
		assertEquals("JUnit-Test-Exception", e.getMessage());
		assertTrue(5 < e.getStackTrace().length);
		assertNotNull(e.getCause());
		Throwable lvNestedExc = e.getCause();
		assertEquals("Nested", lvNestedExc.getMessage());
		assertTrue(5 < lvNestedExc.getStackTrace().length);
	}

	
	public void testSerializeWithPropertyFilter() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		Serializer lvSerializer = new XmlRpcSerializer();
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
		
		Serializer lvSerializer = new XmlRpcSerializer();
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
		
		Serializer lvSerializer = new XmlRpcSerializer();
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

	public void testSerializeURLpropety() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		String lvUrlStr = "http://myurl.net";
		lvBean.setUrl(new URL(lvUrlStr));
		
		Serializer lvSerializer = new XmlRpcSerializer();
		String lvXmlStr = (String) lvSerializer.serialize(lvBean);
		assertTrue(lvXmlStr.indexOf(lvUrlStr) > 0);
		
		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) lvSerializer.deserialize(lvXmlStr);
		assertEquals(lvBean.getUrl(), lvBeanAfter.getUrl());
		assertEquals(lvUrlStr, lvBeanAfter.getUrl().toString());
		assertNull(lvBeanAfter.getObject());
	}

	public void testSerializeObject2FloatProperty() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		Float lvValue = new Float(47.11);
		lvBean.setObject(lvValue);
		
		Serializer lvSerializer = new XmlRpcSerializer();
		String lvXmlStr = (String) lvSerializer.serialize(lvBean);
		assertTrue(lvXmlStr.indexOf(lvValue.toString()) > 0);

		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) lvSerializer.deserialize(lvXmlStr);
		assertEquals(lvBean.getObject(), lvBeanAfter.getObject());
		assertEquals(lvValue, lvBeanAfter.getObject());
		assertNull(lvBeanAfter.getUrl());
	}
	
	public void testRenameKeyWordClass() throws Exception {
		Util.setKeyWordClass("clazz");
		assertEquals("clazz", Util.getKeyWordClass());
		
		Bean lvBean = new Bean("name");
		Timestamp lvTimeStamp = new Timestamp(new Date().getTime());
		lvBean.setTimestamp(lvTimeStamp);
		Serializer lvSerializer = new XmlRpcSerializer();
		String lvXmlStr = (String) lvSerializer.serialize(lvBean);
		assertTrue(lvXmlStr.indexOf("clazz") > 0);
		
		// !!! reset key word to class !!!
		Util.resetKeyWordClass();
		assertEquals(Util.DEFAULT_KEY_WORD_CLASS, Util.getKeyWordClass());
	}

	public void testMultipleReferences() throws Exception {
	    BBean b = new BBean();
	    ABean a = new ABean();
	    a.setFirstRef(b);
	    a.setSecondRef(b);
	    a.setThirdRef(b);
	    a.setFourthRef(b);
	    
	    Serializer lvSerializer = new XmlRpcSerializer();

	    String lvStr = lvSerializer.serialize(a).toString();
	    ABean ades = (ABean) lvSerializer.deserialize(lvStr);
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
		
		Serializer s = new XmlRpcSerializer();
		Object object = s.serialize(root);
		DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) s.deserialize(object);

		assertEquals("ROOT", rootAfter.getUserObject());
		assertEquals(1, rootAfter.getChildCount());
		assertEquals("Child", ((DefaultMutableTreeNode) rootAfter.getChildAt(0)).getUserObject());

		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

	public void testWithNullValueInMap() {
	    Map<String, String> map = new LinkedHashMap<>();

	    map.put("First Key", "First Value");
	    map.put("Second Key", null);
	    map.put("Third Key", "Third Value");
        Object serMap = xmlRpcSerializer.serialize(map);
        assertNotNull(serMap);

        Object deserMap = xmlRpcSerializer.deserialize(serMap);
        assertEquals(map, deserMap);
    }

    public static class CharPrimitive {
	    private char charVal;

        public void setCharVal(char charVal) {
            this.charVal = charVal;
        }

        public char getCharVal() {
            return charVal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CharPrimitive that = (CharPrimitive) o;
            return charVal == that.charVal;
        }

        @Override
        public int hashCode() {
            return Objects.hash(charVal);
        }
    }
}
