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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import junit.framework.TestCase;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.core.conversion.IterateableMap2MapConversion;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Node;

public class ComplexBean2MapConversionTest extends TestCase {

	public void testSimpleBean() throws Exception {
		Converter c = new Converter(); 
		c.addConversion(new ComplexBean2MapConversion());
		
		String lvNodeName = "TestNode-1";
		Node n1 = new Node(lvNodeName);
		Object lvResult = c.convert(n1);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvMap.get("class"));
		assertEquals(lvNodeName, lvMap.get("name"));
		Object o = lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(UniqueIdGenerator.MINIMAL_UNIQUE_ID, Integer.valueOf(o.toString()).intValue());
	}
	
	public void testSimpleBeanWithRelation() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		
		String lvNodeName = "TestNode-2";
		Node n1 = new Node(lvNodeName);
		String lvNodeName2 = "TestNode-2_2";
		Node n2 = new Node(lvNodeName2);
		n2.setParent(n1);
		Object lvResult = c.convert(n2);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvMap.get("class"));
		assertEquals(lvNodeName2, lvMap.get("name"));
		Object o = lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(UniqueIdGenerator.MINIMAL_UNIQUE_ID, Integer.valueOf(o.toString()).intValue());

		assertNotNull(lvMap.get("parent"));
		Map<?, ?> lvMapParent = (Map<?, ?>) lvMap.get("parent");
		assertEquals(Node.class.getName(), lvMapParent.get("class"));
		assertEquals(lvNodeName, lvMapParent.get("name"));
		o = lvMapParent.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(1, Integer.valueOf(o.toString()).intValue());
	}

	public void testSimpleBeanWithRelationWithSameNode() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		
		String lvNodeName = "TestNode-2";
		Node n1 = new Node(lvNodeName);
		n1.setParent(n1);
		Object lvResult = c.convert(n1);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvMap.get("class"));
		assertEquals(lvNodeName, lvMap.get("name"));
		Object o = lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(UniqueIdGenerator.MINIMAL_UNIQUE_ID, Integer.valueOf(o.toString()).intValue());

		assertNotNull(lvMap.get("parent"));
		String lvUniqueId = (String) lvMap.get("parent");
		assertEquals(UniqueIdGenerator.UNIQUE_ID_PROPERTY + "0", lvUniqueId);
	}

	public void testNullValueForBean() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());

		Object lvResult = c.convert(null);
		assertNull(lvResult);
	}
	
	public void testNodeWithNamedChildren() throws Exception {		
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2MapConversion());
		
		String lvNodeName = "TestNode-1";
		Node n1 = new Node(lvNodeName);
		String lvNodeName2 = "TestNode-2";
		Node n2 = new Node(lvNodeName2);
		
		n1.getNamedChildren().put(lvNodeName2, n2);
		Object lvResult = c.convert(n1);
		assertNotNull(lvResult);
		Map<?, ?> lvMap1 = (Map<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvMap1.get("class"));
		assertEquals(lvNodeName, lvMap1.get("name"));
		assertTrue(lvMap1.get("namedChildren") instanceof Map);
		
		Map<?, ?> lvMap = (Map<?, ?>) lvMap1.get("namedChildren");
		Object o = lvMap.get(lvNodeName2);
		Map<?, ?> lvMap2 = (Map<?, ?>) o;
		assertEquals(Node.class.getName(), lvMap2.get("class"));
		assertEquals(lvNodeName2, lvMap2.get("name"));		
	}

	public void testNodeWithNamedChildrenWithSameReference() throws Exception {		
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2MapConversion());
		
		String lvNodeName = "TestNode-1";
		Node n1 = new Node(lvNodeName);
		String lvNodeName2 = "TestNode-2";
		Node n2 = new Node(lvNodeName2);
		
		n1.getNamedChildren().put(lvNodeName, n1);
		n1.getNamedChildren().put(lvNodeName2, n2);
		Object lvResult = c.convert(n1);
		assertNotNull(lvResult);
		Map<?, ?> lvMap1 = (Map<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvMap1.get("class"));
		assertEquals(lvNodeName, lvMap1.get("name"));
		assertTrue(lvMap1.get("namedChildren") instanceof Map);
		
		Map<?, ?> lvMap = (Map<?, ?>) lvMap1.get("namedChildren");
		Object o = lvMap.get(lvNodeName2);
		Map<?, ?> lvMap2 = (Map<?, ?>) o;
		assertEquals(Node.class.getName(), lvMap2.get("class"));
		assertEquals(lvNodeName2, lvMap2.get("name"));		
	}

	public void testConversionWithBadTotype() throws Exception {
		try {
			new ComplexBean2MapConversion(ArrayList.class);
			fail("ArrayList ist not assignable to Map!");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	public void testConversionWithBadTotype2() throws Exception {
		try {
			new ComplexBean2MapConversion(Map.class);
			fail("Map is a interface, but must be a implementatio!");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testConversionDirect() throws Exception {		
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		Node n = new Node("Node");
		Object lvResult = c.convert(n, HashMap.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof HashMap);
		HashMap<?, ?> lvHashMap = (HashMap<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvHashMap.get("class"));
		assertEquals("Node", lvHashMap.get("name"));
		assertEquals(new ArrayList<Object>(), lvHashMap.get("children"));
		assertNull(lvHashMap.get("abc"));
		
		lvResult = c.convert(n, WeakHashMap.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof WeakHashMap);
		WeakHashMap<?, ?> lvWeakHashMap = (WeakHashMap<?, ?>) lvResult;
		assertEquals(Node.class.getName(), lvWeakHashMap.get("class"));
		assertEquals("Node", lvWeakHashMap.get("name"));
		assertEquals(new ArrayList<Object>(), lvWeakHashMap.get("children"));
		assertNull(lvWeakHashMap.get("abc"));

	}

	public void testIgnoreNullValues() throws Exception {
		Converter c = new Converter();
		ComplexBean2MapConversion lvBean2MapConversion = new ComplexBean2MapConversion();
		c.addConversion(lvBean2MapConversion);
		
		Car lvCar = new Car("Audi");
		Map<?, ?> lvMap = (Map<?, ?>) c.convert(lvCar);
		assertEquals(6, lvMap.size());
		
		lvBean2MapConversion.setIgnoreNullValues(true);
		lvMap = (Map<?, ?>) c.convert(lvCar);
		assertEquals(3, lvMap.size());
	}
}
