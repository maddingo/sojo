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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;

import net.sf.sojo.common.ObjectGraphWalker;
import net.sf.sojo.core.Constants;
import net.sf.sojo.interchange.xmlrpc.XmlRpcException;
import net.sf.sojo.interchange.xmlrpc.XmlRpcWalkerInterceptor;
import junit.framework.TestCase;

public class XmlRpcWalkerInterceptorTest extends TestCase {

	private ObjectGraphWalker walker = new ObjectGraphWalker();
	private XmlRpcWalkerInterceptor xmlRpcWalker = new XmlRpcWalkerInterceptor();
	
	public XmlRpcWalkerInterceptorTest() {
		walker.addInterceptor(xmlRpcWalker);
	}
	
	public void testXmlRpcWalkerInterceptor() throws Exception {
		XmlRpcWalkerInterceptor lvInterceptor = new XmlRpcWalkerInterceptor();
		assertEquals("", lvInterceptor.getXmlRpcString());
		
		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), -1, "", -1);
		assertEquals("", lvInterceptor.getXmlRpcString());

		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), Constants.ITERATOR_END, "", -1);
		assertEquals("", lvInterceptor.getXmlRpcString());
				
		lvInterceptor.visitIterateableElement(new HashMap<Object, Object>(), -1, "", Constants.ITERATOR_END);
		assertEquals("", lvInterceptor.getXmlRpcString());
	}
	
	public void testSimpleString() throws Exception {
		walker.walk("MyTestString");
		String s = "<params><param><value><string>MyTestString</string></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void testSimpleInteger() throws Exception {
		walker.walk(new Integer(4711));
		String s = "<params><param><value><i4>4711</i4></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void testSimpleBoolean() throws Exception {
		walker.walk(Boolean.TRUE);
		String s = "<params><param><value><boolean>1</boolean></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
		
		walker.walk(Boolean.FALSE);
		s = "<params><param><value><boolean>0</boolean></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void testSimpleDouble() throws Exception {
		walker.walk(new Double(4711));
		String s = "<params><param><value><double>4711.0</double></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
		
		walker.walk(new Double(47.11));
		s = "<params><param><value><double>47.11</double></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());

	}

	public void testSimpleDate() throws Exception {
                Calendar calendar = Calendar.getInstance();
                calendar.set(1970, 0, 9, 3, 26, 40);
		walker.walk(calendar.getTime());
		String s = "<params><param><value><dateTime.iso8601>19700109T03:26:40</dateTime.iso8601></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void testArrayWithSimpleStringElements() throws Exception {
		walker.walk(new Object[] { "sojo"});
		String s = "<params><param><value><string>sojo</string></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());

		walker.walk(new Object[] { "sojo1", "sojo2" });
		s = "<params>" +
				"<param><value><string>sojo1</string></value></param>" +
				"<param><value><string>sojo2</string></value></param>" +
			"</params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void testArrayWithSimpleMixedElements() throws Exception {
		walker.walk(new Object[] { Boolean.TRUE, new Double ("56.12") });
		String s = "<params>" +
						"<param><value><boolean>1</boolean></value></param>" +
						"<param><value><double>56.12</double></value></param>" +
					"</params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());

                Calendar calendar = Calendar.getInstance();
                calendar.set(1970, 0, 9, 3, 26, 40);
		walker.walk(new Object[] { Boolean.FALSE, calendar.getTime() });
		s = "<params>" +
				"<param><value><boolean>0</boolean></value></param>" +
				"<param><value><dateTime.iso8601>19700109T03:26:40</dateTime.iso8601></value></param>" +
			"</params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void __testMapWithStringElements() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("key", "value");
		walker.walk(lvMap);
		String s = "<params><param><value><struct>" +
						"<member><name>key</name><value><string>value</string></value></member>" +
					"</struct></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());


		lvMap.put("key2", "value2");
		walker.walk(lvMap);
		s = "<params><param><value><struct>" +
				"<member><name>key</name><value><string>value</string></value></member>" +
				"<member><name>key2</name><value><string>value2</string></value></member>" +
			"</struct></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void __testMapWithDifferentsElements() throws Exception {
		Map<String, Comparable<?>> lvMap = new HashMap<String, Comparable<?>>();
		lvMap.put("key-int", new Integer(1234));
		walker.walk(lvMap);
		
		String s = "<params><param>" +
						"<value><struct><member>" +
							"<name>key-int</name><value><i4>1234</i4></value>" +
						"</member></struct></value>" +
					"</param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());


		lvMap.put("key-double", new Double(".1234"));
		lvMap.put("key-boolean", Boolean.TRUE);
		lvMap.put("key-date", new Date(700000000));
		walker.walk(lvMap);
		
		s = "<params><param><value><struct>" +
				"<member><name>key-date</name><value><dateTime.iso8601>19700109T03:26:40</dateTime.iso8601></value></member>" +
				"<member><name>key-int</name><value><i4>1234</i4></value></member>" +
				"<member><name>key-boolean</name><value><boolean>1</boolean></value></member>" +
				"<member><name>key-double</name><value><double>0.1234</double></value></member>" +
			"</struct></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void testKeyIsNotFromTypeString() throws Exception {
		Map<Integer, Integer> lvMap = new HashMap<Integer, Integer>();
		lvMap.put(new Integer(1), new Integer(1234));
		try {
			walker.walk(lvMap);
			fail("Key must be a String and not Integer.");
		} catch (XmlRpcException e) {
			assertNotNull(e);
		}
	}
	
	public void __testSimpleBean() throws Exception {
		Car lvCar = new Car();
		lvCar.setName("BMW");
		walker.walk(lvCar);

		String s = "<params><param><value><struct>" +
						"<member><name>name</name><value><string>BMW</string></value></member>" +
						"<member><name>~unique-id~</name><value><string>0</string></value></member>" +
						"<member><name>class</name><value><string>test.net.sf.sojo.model.Car</string></value></member>" +
					"</struct></value></param></params>";
		
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void testMapInArray() throws Exception {
		Map<String, Comparable<?>> lvMap = new LinkedHashMap<String, Comparable<?>>();
		lvMap.put("k1", "v1");
		lvMap.put("k2", new Double("1.00005"));
		Object lvArray[] = new Object[] { new Integer(7), lvMap , "my-test-string" };
		
		walker.walk(lvArray);
		
		// TODO HashMap does not guarantee to keep the order of elements. 
		String s = "<params><param>" +
						"<value><i4>7</i4></value>" +
					"</param>" +
					"<param><value><struct>" +
						"<member><name>k1</name><value><string>v1</string></value></member>" +						
						"<member><name>k2</name><value><double>1.00005</double></value></member>" +	
					"</struct></value></param>" +
					"<param><value><string>my-test-string</string></value>" +
					"</param></params>";

		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void testEmptyArray() throws Exception {
		walker.walk(new Object[] {});
		String s = "<params></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void testEmptyArrayInMap() throws Exception {
		Map<String, Object[]> lvMap = new HashMap<String, Object[]>();
		lvMap.put("emptyArray", new Object[] {} );
		walker.walk(lvMap);
		String s = "<params><param><value>" +
						"<struct><member>" +
							"<name>emptyArray</name>" +
							"<value><array><data></data></array></value>" +
						"</member></struct>" +
					"</value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void testEmptyMap() throws Exception {
		walker.walk(new HashMap<Object, Object>());
		String s = "<params><param><value><struct></struct></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void testArrayInArray() throws Exception {
		Object lvArray = new Object[] { new Object[] { "string", Boolean.TRUE, new Double("12.009") } };
		walker.walk(lvArray);
		String s = "<params><param><value><array><data>" +
						"<value><string>string</string></value>" +
						"<value><boolean>1</boolean></value>" +
						"<value><double>12.009</double></value>" +
					"</data></array></value></param></params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}
	
	public void testArrayInArrayAndSimpleElements() throws Exception {
		Object lvArray = new Object[] { new Integer(23), 
										new Object[] { "string", Boolean.TRUE, new Double("12.009") },
										"text"
									  };
		walker.walk(lvArray);
		String s = "<params>" +
					"<param><value><i4>23</i4></value></param>" +
					"<param><value><array><data>" +
						"<value><string>string</string></value>" +
						"<value><boolean>1</boolean></value>" +
						"<value><double>12.009</double></value>" +
					"</data></array></value></param>" +
					"<param><value><string>text</string></value></param>" +
					"</params>";
		assertEquals(s, xmlRpcWalker.getXmlRpcString());
	}

	public void __testBeansInArray() throws Exception {
		Object lvArray = new Object[] { new Car("MyCar"), 
										new Object[] { "string", Boolean.TRUE, new Double("12.009") } , 
										new Customer("Customer_1")
									  };
		walker.walk(lvArray);
		
		String s = 
			"<params>" +
				"<param><value><struct>" +
					"<member><name>name</name><value><string>MyCar</string></value></member>" +
					"<member><name>~unique-id~</name><value><string>0</string></value></member>" +
					"<member><name>class</name><value><string>test.net.sf.sojo.model.Car</string></value></member>" +
				"</struct></value></param>" +
				"<param><value><array><data>" +
					"<value><string>string</string></value>" +
					"<value><boolean>1</boolean></value>" +
					"<value><double>12.009</double></value>" +
				"</data></array></value></param>" +
				"<param><value><struct>" +
					"<member><name>addresses</name><value><array><data></data></array></value></member>" +
					"<member><name>~unique-id~</name><value><string>1</string></value></member>" +
					"<member><name>lastName</name><value><string>Customer_1</string></value></member>" +
					"<member><name>class</name><value><string>test.net.sf.sojo.model.Customer</string></value></member>" +
				"</struct></value></param>" +
			"</params>";
		
		assertEquals(s, xmlRpcWalker.getXmlRpcString());		
	}
}
