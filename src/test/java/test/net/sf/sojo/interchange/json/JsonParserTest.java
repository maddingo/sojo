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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;
import net.sf.sojo.common.ObjectGraphWalker;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.interchange.json.JsonParser;
import net.sf.sojo.interchange.json.JsonParserException;
import net.sf.sojo.interchange.json.JsonSerializer;
import net.sf.sojo.interchange.json.JsonWalkerInterceptor;
import test.net.sf.sojo.model.Address;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.Primitive;

public class JsonParserTest extends TestCase {

	public void testNull() throws Exception {
		Object lvResult = new JsonParser().parse(null);
		assertNull(lvResult);
	}

	public void testSimpleEmptyString() throws Exception {
		String s = "\"\"";
		Object lvResult = new JsonParser().parse(s);
		assertEquals("", lvResult);
	}

	public void testSimpleString() throws Exception {
		String s = "\"Simple-Test-String\"";
		Object lvResult = new JsonParser().parse(s);
		assertEquals("Simple-Test-String", lvResult);
	}

	public void testSimpleStringWithSemiColonOnEnd() throws Exception {
		String s = "\"Simple-Test-String\";";
		Object lvResult = new JsonParser().parse(s);
		assertEquals("Simple-Test-String", lvResult);
	}

	public void testSimpleSpecialString() throws Exception {
		JsonParser lvJsonParser = new JsonParser();
		String s = "\"Test \\ String\";";
		Object lvResult = lvJsonParser.parse(s);
		// remove single beck slash
		assertEquals("Test  String", lvResult);
		
		s = "\"Test \\n String\";";
		lvResult = lvJsonParser.parse(s);
		assertEquals("Test \n String", lvResult);
		
		s = "\"Test \f String\";";
		lvResult = lvJsonParser.parse(s);
		assertEquals("Test \f String", lvResult);
		
		s = "\"Test \b String\";";
		lvResult = lvJsonParser.parse(s);
		assertEquals("Test \b String", lvResult);

		s = "\"Test \r String\";";
		lvResult = lvJsonParser.parse(s);
		assertEquals("Test \r String", lvResult);

		s = "\"Test \t String\";";
		lvResult = lvJsonParser.parse(s);
		assertEquals("Test \t String", lvResult);

		s = "\"Test / String\";";
		lvResult = lvJsonParser.parse(s);
		assertEquals("Test / String", lvResult);
	}

	public void testInvalidSimpleString() throws Exception {
		String s = "Invalid-String";
		try {
			new JsonParser().parse(s);
			fail("By the String fails the quota.");
		} catch (JsonParserException e) {
			assertNotNull(e);
		}		
	}
	
	public void testInvalidSimpleString2() throws Exception {
		String s = "\"Invalid-String";
		try {
			new JsonParser().parse(s);
			fail("By the String fails the closed quota.");
		} catch (JsonParserException e) {
			assertNotNull(e);
		}		
	}


	public void testSimpleBoolean() throws Exception {
		String s = "true";
		Object lvResult = new JsonParser().parse(s);
		assertEquals(Boolean.TRUE, lvResult);
		
		s = "false";
		lvResult = new JsonParser().parse(s);
		assertEquals(Boolean.FALSE, lvResult);
	}

	public void testSimpleNullString() throws Exception {
		String s = "null";
		Object lvResult = new JsonParser().parse(s);
		assertNull(lvResult);
	}


	public void testSimpleLong() throws Exception {
		String s = "4711";
		JsonParser lvJsonParser = new JsonParser();
		Object lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("4711"), lvResult);
		
		s = "-4711";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("-4711"), lvResult);
		
		s = "0";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("0"), lvResult);

		s = "-0";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("0"), lvResult);

		s = "01";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("1"), lvResult);
		
		s = "1i";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("1"), lvResult);

		s = "01z";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Long("1"), lvResult);
	}

	public void testSimpleDouble() throws Exception {
		String s = "47.11";
		JsonParser lvJsonParser = new JsonParser();
		Object lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("47.11"), lvResult);
		
		s = "-47.11";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("-47.11"), lvResult);
		
		s = "0.0";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("0"), lvResult);

		s = "-0.0";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("-0.0"), lvResult);

		s = "01.0";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("1"), lvResult);
		
		s = ".01";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double(".01"), lvResult);

		s = "-.01";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("-.01"), lvResult);
		
		s = "47.11d";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("47.11"), lvResult);

		s = "47.11D";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("47.11"), lvResult);

		s = "47.11f";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("47.11"), lvResult);

		s = "47.11F";
		lvResult = lvJsonParser.parse(s);
		assertEquals(new Double("47.11"), lvResult);

	}

	public void testEmptyArray() throws Exception {
		String s = "[]";
		Object lvResult = new JsonParser().parse(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(0, lvList.size());
	}
	
	public void testSimpleArray() throws Exception {
		String s = "[true, false, true, null]";
		Object lvResult = new JsonParser().parse(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(4, lvList.size());
		assertEquals(Boolean.TRUE, lvList.get(0));
		assertEquals(Boolean.FALSE, lvList.get(1));
		assertEquals(Boolean.TRUE, lvList.get(2));
		assertNull(lvList.get(3));
	}

	public void testSimpleArray2() throws Exception {
		String s = "[1, 22, 33.3, \"nUll\"]";
		Object lvResult = new JsonParser().parse(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(4, lvList.size());
		assertEquals(new Long("1"), lvList.get(0));
		assertEquals(new Long("22"), lvList.get(1));
		assertEquals(new Double("33.3"), lvList.get(2));
		assertEquals("nUll", lvList.get(3));
	}

	public void testNestedArray() throws Exception {
		String s = "[2.005, .017, \"a\", [9087, 987654, null] ]";
		Object lvResult = new JsonParser().parse(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(4, lvList.size());
		assertEquals(new Double("2.005"), lvList.get(0));
		assertEquals(new Double("0.017"), lvList.get(1));
		assertEquals("a", lvList.get(2));
		List<?> lvInnerList = (List<?>) lvList.get(3);
		assertEquals(3, lvInnerList.size());
	}

	public void testNestedArray2() throws Exception {
		String s = "[2.005, .017, \"a\", [9087, 987654, null], 54.32 ]";
		Object lvResult = new JsonParser().parse(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(5, lvList.size());
		assertEquals(new Double("2.005"), lvList.get(0));
		assertEquals(new Double("0.017"), lvList.get(1));
		assertEquals("a", lvList.get(2));
		List<?> lvInnerList = (List<?>) lvList.get(3);
		assertEquals(3, lvInnerList.size());
		
		assertEquals(new Double("54.32"), lvList.get(4));
	}

	public void testEmptyObject() throws Exception {
		String s = "{}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(0, lvMap.size());
	}
	
	public void testEmptyListInObject() throws Exception {
		String s = "{\"empty\" : []}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(1, lvMap.size());
		assertEquals(Collections.emptyList(), lvMap.get("empty"));
	}

	public void testEmptyObjectInListIn() throws Exception {
		String s = "[ { } ]";
		Object lvResult = new JsonParser().parse(s);
		List<?> lvList = (List<?>) lvResult;
		assertEquals(1, lvList.size());
		assertEquals(new HashMap<Object, Object>(), lvList.get(0));
	}


	public void testSimpleObject() throws Exception {
		String s = "{\"k1\" : true, \"k2\" : false, \"k3\" : true, \"k4\" : null}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(4, lvMap.size());
		assertEquals(Boolean.TRUE, lvMap.get("k1"));
		assertEquals(Boolean.FALSE, lvMap.get("k2"));
		assertEquals(Boolean.TRUE, lvMap.get("k3"));
		assertNull(lvMap.get("k4"));
		assertTrue(lvMap.containsKey("k4"));
	}

	public void testSimpleObject2() throws Exception {
		String s = "{\"k1\" : 1, \"k2\" : 22, \"k3\" : 33.3, \"k4\" : \"VaLue\"}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(4, lvMap.size());
		assertEquals(new Long("1"), lvMap.get("k1"));
		assertEquals(new Long("22"), lvMap.get("k2"));
		assertEquals(new Double("33.3"), lvMap.get("k3"));
		assertEquals("VaLue", lvMap.get("k4"));
	}

	public void testSimpleObject3() throws Exception {
		String s = "{\"k1\" : 1, \"k1\" : 22, \"k3\" : null, \"k4\" : \"VaLue\"}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(3, lvMap.size());
		assertNull(lvMap.get("k2"));
		assertEquals(new Long("22"), lvMap.get("k1"));
		assertNull(lvMap.get("k3"));
		assertTrue(lvMap.containsKey("k3"));
		assertEquals("VaLue", lvMap.get("k4"));
	}

	public void testNestedObject() throws Exception {
		String s = "{\"k1\" : 1, \"k2\" : 22, \"k3\" : 33.3, \"k4\" : {\"k1\" : \"VaLue\"}}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(4, lvMap.size());
		assertEquals(new Long("1"), lvMap.get("k1"));
		assertEquals(new Long("22"), lvMap.get("k2"));
		assertEquals(new Double("33.3"), lvMap.get("k3"));
		Map<?, ?> lvInnerMap = (Map<?, ?>) lvMap.get("k4");
		assertEquals("VaLue", lvInnerMap.get("k1"));
	}

	public void testNestedObject2() throws Exception {
		String s = "{\"k1\" : 1, \"k2\" : 22, \"k3\" : 33.3, \"k4\" : {\"k1\" : \"VaLue\"}, \"k5\" : \"c\"}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(5, lvMap.size());
		assertEquals(new Long("1"), lvMap.get("k1"));
		assertEquals(new Long("22"), lvMap.get("k2"));
		assertEquals(new Double("33.3"), lvMap.get("k3"));
		Map<?, ?> lvInnerMap = (Map<?, ?>) lvMap.get("k4");
		assertEquals("VaLue", lvInnerMap.get("k1"));
		
		assertEquals("c", lvMap.get("k5"));
	}

	public void testArrayInObject() throws Exception {
		String s = "{\"k1\" : 1, \"k2\" : 22, \"k3\" : 33.3, " +
				"\"array\" : [2.005, .017, \"a\", [9087, 987654, null] ], " +
				"\"k4\" : {\"k1\" : \"VaLue\"}, \"k5\" : \"c\"}";
		Object lvResult = new JsonParser().parse(s);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(6, lvMap.size());
		assertEquals(new Long("1"), lvMap.get("k1"));
		assertEquals(new Long("22"), lvMap.get("k2"));
		assertEquals(new Double("33.3"), lvMap.get("k3"));
		Map<?, ?> lvInnerMap = (Map<?, ?>) lvMap.get("k4");
		assertEquals("VaLue", lvInnerMap.get("k1"));
		
		assertEquals("c", lvMap.get("k5"));
		
		List<?> lvList = (List<?>) lvMap.get("array");
		assertEquals(new Double("2.005"), lvList.get(0));
		assertEquals(new Double("0.017"), lvList.get(1));
		assertEquals("a", lvList.get(2));
		List<?> lvInnerList = (List<?>) lvList.get(3);
		assertEquals(3, lvInnerList.size());
		
		assertEquals(new Long("9087"), lvInnerList.get(0));
		assertEquals(new Long("987654"), lvInnerList.get(1));
		assertNull(lvInnerList.get(2));
	}
	
	public void testFromSimpleObject2JsonAndBack() throws Exception {
		Car lvCar = new Car();
		lvCar.setName("BMW");
		lvCar.setDescription("My car");
		lvCar.setBuild(new Date(987654321));
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvCar);
		String lvJsonStr = jsonInterceptor.getJsonString();
		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil().makeComplex(o);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals("My car", lvCarAfter.getDescription());
		assertEquals(new Date(987654321).getTime() / 1000, lvCarAfter.getBuild().getTime() / 1000);
	}
	
	public void testFromSimpleObjectWithProperties2JsonAndBack() throws Exception {
		Car lvCar = new Car();
		lvCar.setName("BMW");
		lvCar.setDescription("My car");
		lvCar.setBuild(new Date(987654321));
		lvCar.setProperties(new Properties());
		lvCar.getProperties().put("key1", "value1");
		lvCar.getProperties().put("key2", new Integer(4711));
		lvCar.getProperties().put("key3", Boolean.TRUE);
		lvCar.getProperties().put("key4", new Float("47.11"));
		lvCar.getProperties().put("key5", new Object[] { new Float(".03"), new Byte("123"), new Character('Z')});
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvCar);
		String lvJsonStr = jsonInterceptor.getJsonString();
		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Car lvCarAfter = (Car) o;
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals("My car", lvCarAfter.getDescription());
		assertEquals(new Date(987654321).getTime() / 1000, lvCarAfter.getBuild().getTime() / 1000);
		
		assertEquals("value1", lvCarAfter.getProperties().get("key1"));
		assertEquals(new Long(4711), lvCarAfter.getProperties().get("key2"));
		assertEquals(Boolean.TRUE, lvCarAfter.getProperties().get("key3"));
		assertEquals(new Double("47.11"), lvCarAfter.getProperties().get("key4"));
		List<?> lvList = (List<?>) lvCarAfter.getProperties().get("key5");
		assertEquals(3, lvList.size());
		assertEquals(new Double("0.03"), lvList.get(0));
		assertEquals(new Long("123"), lvList.get(1));
		assertEquals("Z", lvList.get(2));
	}
	
	public void testSimpleNode2JsonAndBack() throws Exception {
		Node lvNode = new Node("N1");
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvNode);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Node lvNodeAfter = (Node) o;
		assertEquals("N1", lvNodeAfter.getName());
		assertEquals(new ArrayList<Object>(), lvNodeAfter.getChildren());
		assertEquals(new HashMap<Object, Object>(), lvNodeAfter.getNamedChildren());
		assertNull(lvNodeAfter.getParent());
	}

	public void testNodeWithChilds2JsonAndBack() throws Exception {
		Node lvNode = new Node("N1");
		Node lvNode1 = new Node("N1-1");
		Node lvNode2 = new Node("N1-2");
		lvNode.getChildren().add(lvNode1);
		lvNode.getChildren().add(lvNode2);
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvNode);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Node lvNodeAfter = (Node) o;
		assertEquals("N1", lvNodeAfter.getName());
		assertEquals(new HashMap<Object, Object>(), lvNodeAfter.getNamedChildren());
		assertNull(lvNodeAfter.getParent());
		assertEquals(2, lvNodeAfter.getChildren().size());
		assertEquals("N1-1", ((Node) lvNodeAfter.getChildren().get(0)).getName());
		assertEquals("N1-2", ((Node) lvNodeAfter.getChildren().get(1)).getName());
	}

	public void testNodeWithNamedChilds2JsonAndBack() throws Exception {
		Node lvNode = new Node("N1");
		Node lvNode1 = new Node("N1-1");
		Node lvNode2 = new Node("N1-2");
		lvNode.getNamedChildren().put("N1-1",lvNode1);
		lvNode.getNamedChildren().put("N1-2",lvNode2);
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvNode);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Node lvNodeAfter = (Node) o;
		assertEquals("N1", lvNodeAfter.getName());
		assertNull(lvNodeAfter.getParent());
		assertEquals(new ArrayList<Object>(), lvNodeAfter.getChildren());
		assertEquals(2, lvNodeAfter.getNamedChildren().size());
		assertEquals("N1-1", ((Node) lvNodeAfter.getNamedChildren().get("N1-1")).getName());
		assertEquals("N1-2", ((Node) lvNodeAfter.getNamedChildren().get("N1-2")).getName());
	}
	
	public void testNodeWithCycle2Json() throws Exception {
		Node lvNode = new Node("N1");
		Node lvNode1 = new Node("N1-1");
		Node lvNode2 = new Node("N1-2");
		lvNode.getChildren().add(lvNode1);
		lvNode.getChildren().add(lvNode2);
		lvNode.getChildren().add(lvNode);
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvNode);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Node lvNodeAfter = (Node) o;
		assertEquals("N1", lvNodeAfter.getName());
		assertEquals(new HashMap<Object, Object>(), lvNodeAfter.getNamedChildren());
		assertNull(lvNodeAfter.getParent());
		assertEquals(3, lvNodeAfter.getChildren().size());
		assertEquals("N1-1", ((Node) lvNodeAfter.getChildren().get(0)).getName());
		assertEquals("N1-2", ((Node) lvNodeAfter.getChildren().get(1)).getName());
		assertEquals(lvNodeAfter, lvNodeAfter.getChildren().get(2));
	}

	public void testNodeWithCycle2Json2() throws Exception {
		Node lvNode = new Node("N1");
		lvNode.setParent(lvNode);
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvNode);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Node lvNodeAfter = (Node) o;

		assertEquals(lvNodeAfter, lvNodeAfter.getParent());
	}
	
	public void testPrimitive() throws Exception {
		Primitive lvPrimitive = Primitive.createPrimitiveExample();
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvPrimitive);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Primitive lvPrimitiveAfter = (Primitive) o;
		assertEquals(lvPrimitive, lvPrimitiveAfter);
	}

	public void testCustomer2JsonAndBack() throws Exception {
		Customer lvCustomer = new Customer();
		lvCustomer.setLastName("Json");
		lvCustomer.setFirstName("Sojo");
		Address a1 = new Address();
		a1.setCity("City 1");
		a1.setPostcode("12345");
		a1.setCustomer(lvCustomer);
		lvCustomer.getAddresses().add(a1);
		Address a2 = new Address();
		a2.setCity("City 2");
		a2.setPostcode("54321");
		a2.setCustomer(lvCustomer);
		lvCustomer.getAddresses().add(a2);
		
		lvCustomer.setPartner(new Address[] {a1, a2});
		
		ObjectGraphWalker walker = new ObjectGraphWalker();
		JsonWalkerInterceptor jsonInterceptor = new JsonWalkerInterceptor();
		walker.addInterceptor(jsonInterceptor);	
		
		walker.walk(lvCustomer);
		String lvJsonStr = jsonInterceptor.getJsonString();

		Object o = new JsonParser().parse(lvJsonStr);
		o = new ObjectUtil(false).makeComplex(o);
		Customer lvCustomerAfter = (Customer) o;
		assertEquals(lvCustomer.getLastName(), lvCustomerAfter.getLastName());
		assertEquals(lvCustomer.getFirstName(), lvCustomerAfter.getFirstName());

		Address lvAddressArray[] = (Address[]) lvCustomerAfter.getAddresses().toArray(new Address[0]);
		Address a1After = null;
		Address a2After = null;
		if (lvAddressArray[0].getCity().equals(a1.getCity())) {
			a1After = lvAddressArray[0];
			a2After = lvAddressArray[1];
		} else {
			a1After = lvAddressArray[1];
			a2After = lvAddressArray[0];
		}
		
		assertEquals(a1.getCity(), a1After.getCity());
		assertEquals(a1.getPostcode(), a1After.getPostcode());
		assertEquals(lvCustomerAfter, a1After.getCustomer());
		
		assertEquals(a2.getCity(), a2After.getCity());
		assertEquals(a2.getPostcode(), a2After.getPostcode());
		assertEquals(lvCustomerAfter, a2After.getCustomer());
		
		o = lvCustomerAfter.getPartner();
		Object lvAddress[] = lvCustomerAfter.getPartner();
		assertEquals(2, lvAddress.length);
		assertEquals(a1After, lvAddress[0]);
		assertEquals(a2After, lvAddress[1]);
	}
	
	public void testDoubleBackslashAndQuota() throws Exception {
		String jsonString = "{ \"str1\": \"abc\\\\\", \"str2\": \"def\" }";
        JsonSerializer serializer = new JsonSerializer();
        Object o = serializer.deserialize(jsonString);
        assertTrue(Map.class.isAssignableFrom(o.getClass()));

        Map<?, ?> lvMap = (Map<?, ?>) o;
        assertEquals(lvMap.get("str1"), "abc\\");
        assertEquals(lvMap.get("str2"), "def");
	}

}
