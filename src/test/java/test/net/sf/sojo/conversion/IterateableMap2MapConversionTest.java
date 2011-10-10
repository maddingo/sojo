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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.core.conversion.IterateableMap2BeanConversion;
import net.sf.sojo.core.conversion.IterateableMap2MapConversion;
import test.net.sf.sojo.model.Node;

public class IterateableMap2MapConversionTest extends TestCase {
	
	public void testHashtable() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(Hashtable.class));
		Map lvMap = new HashMap();
		lvMap.put("Test-1", "Test-1");
		lvMap.put("Test-2", "Test-2");
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		assertTrue(lvResult instanceof Hashtable);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());		
		assertEquals(lvMap.get("Test-1"), lvMapAfter.get("Test-1"));
		assertEquals(lvMap.get("Test-2"), lvMapAfter.get("Test-2"));
	}

	public void testHashtableWithoutConversion() throws Exception {
		Converter c = new Converter();
		Map lvMap = new Hashtable();
		lvMap.put("Test-1", "Test-1");
		lvMap.put("Test-2", "Test-2");
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		assertTrue(lvResult instanceof Hashtable);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());		
		assertEquals(lvMap.get("Test-1"), lvMapAfter.get("Test-1"));
		assertEquals(lvMap.get("Test-2"), lvMapAfter.get("Test-2"));
	}

	public void testDoubleHashtable() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new IterateableMap2MapConversion(Hashtable.class));
		Map lvMap = new Hashtable();
		lvMap.put("Test-1", "Test-1");
		Map lvMap2 = new Hashtable();
		lvMap2.put("Test-2", "Test-2");
		lvMap.put("map-2", lvMap2);
		
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		assertTrue(lvResult instanceof Hashtable);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());		
		assertEquals(lvMap.get("Test-1"), lvMapAfter.get("Test-1"));
		assertTrue(lvMapAfter.get("map-2") instanceof Map);
		assertEquals(lvMap.get("map-2"), lvMapAfter.get("map-2"));
		
		Map m = (Map) lvMapAfter.get("map-2");
		assertEquals(lvMap2.size(), m.size());
		assertEquals(lvMap2.get("Test-2"), m.get("Test-2"));
	}

	public void testDefaultMapType() throws Exception {
		Converter c = new Converter();
		c.getConversionHandler().addConversion(new IterateableMap2MapConversion(null));
		Map lvMap = new Hashtable();
		lvMap.put("Test-1", "Test-1");
		lvMap.put("Test-2", "Test-2");
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		assertTrue(lvResult instanceof HashMap);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());		
		assertEquals(lvMap.get("Test-1"), lvMapAfter.get("Test-1"));
		assertEquals(lvMap.get("Test-2"), lvMapAfter.get("Test-2"));
	}


	public void testNullValueForMap() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(HashMap.class));

		Object lvResult = c.convert(null);
		assertNull(lvResult);
	}
	
	public void testInvalidMapType() throws Exception {
		try {
			Converter c = new Converter();
			c.addConversion(new IterateableMap2MapConversion(HashSet.class));
			fail("Invalid Map-Type: HashMap");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testInvalidMapImplType() throws Exception {
		try {
			Converter c = new Converter();
			c.addConversion(new IterateableMap2MapConversion(Map.class));
			fail("Map is not a implementation.");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testConvertBeansAsValue() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(Hashtable.class));
		c.addConversion(new ComplexBean2MapConversion(Hashtable.class));
		
		Map lvMap = new HashMap();
		lvMap.put("Node_1", new Node("1"));
		lvMap.put("Node_2", new Node("2"));
		
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());
		
		Map lvNode1Map = (Map) lvMapAfter.get("Node_1");
		assertEquals(Node.class.getName(), lvNode1Map.get("class"));
		assertEquals("1", lvNode1Map.get("name"));
		
		Map lvNode2Map = (Map) lvMapAfter.get("Node_2");
		assertEquals(Node.class.getName(), lvNode2Map.get("class"));
		assertEquals("2", lvNode2Map.get("name"));	
	}
	
	public void testConvertBeansAsKey() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(Hashtable.class));
		c.addConversion(new ComplexBean2MapConversion(Hashtable.class));
		
		Map lvMap = new HashMap();
		lvMap.put(new Node("1"), "Node_1");
		lvMap.put(new Node("2"), "Node_2");
		
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());
		
		Iterator iter = lvMapAfter.keySet().iterator();
		Map lvNode1Map = (Map) iter.next();
		assertEquals(Node.class.getName(), lvNode1Map.get("class"));
		assertEquals("1", lvNode1Map.get("name"));
		
		Map lvNode2Map = (Map) iter.next();
		assertEquals(Node.class.getName(), lvNode2Map.get("class"));
		assertEquals("2", lvNode2Map.get("name"));	
	}
	
	public void testConvertBeansWithSameKeyAndValue() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(Hashtable.class));
		c.addConversion(new ComplexBean2MapConversion(Hashtable.class));
		
		Map lvMap = new HashMap();
		Node n1 = new Node("1");
		Node n2 = new Node("2");
		lvMap.put(n1, n1);
		lvMap.put(n2, n2);
		
		Object lvResult = c.convert(lvMap);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		Map lvMapAfter = (Map) lvResult;
		assertEquals(lvMap.size(), lvMapAfter.size());
		
		// key
		Iterator iter = lvMapAfter.keySet().iterator();
		Map lvNode1Map = (Map) iter.next();
		assertEquals(Node.class.getName(), lvNode1Map.get("class"));
		assertEquals("1", lvNode1Map.get("name"));
		
		Map lvNode2Map = (Map) iter.next();
		assertEquals(Node.class.getName(), lvNode2Map.get("class"));
		assertEquals("2", lvNode2Map.get("name"));
		
		// value
		iter = lvMapAfter.values().iterator();
		Object v1 = iter.next();
		assertTrue(v1.toString().endsWith("1") || v1.toString().endsWith("0"));
		
		Object v2 = iter.next();
		assertTrue(v2.toString().endsWith("1") || v2.toString().endsWith("0"));
	}
	
	public void testToMapDirect() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		Map lvMap = new HashMap();
		lvMap.put("1", "1");
		lvMap.put("2", "2");
		Object lvResult = c.convert(lvMap, Hashtable.class);
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Hashtable);
		Hashtable lvHashtable = (Hashtable) lvResult;
		assertEquals(lvMap.size(), lvHashtable.size());
		assertEquals(lvMap.get("1"), lvHashtable.get("1"));
		assertEquals(lvMap.get("2"), lvHashtable.get("2"));
	}

	public void testToMapDirectWithComplexObjects() throws Exception {
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		Map lvMap = new HashMap();
		Node n1 = new Node("1");
		Node n2 = new Node("2");
		lvMap.put(n1, n1);
		lvMap.put(n2, n2);
		Object lvResult = c.convert(lvMap, Hashtable.class);
		
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Hashtable);
		Hashtable lvHashtable = (Hashtable) lvResult;
		assertEquals(lvMap.size(), lvHashtable.size());
		Iterator iter = lvHashtable.entrySet().iterator();
		Map.Entry lvEntry1 = (Map.Entry) iter.next();
		Map.Entry lvEntry2 = (Map.Entry) iter.next();
		assertEquals(((Node)  lvEntry1.getKey()).getName(), ((Node)  lvEntry1.getValue()).getName());
		assertEquals(((Node)  lvEntry2.getKey()).getName(), ((Node)  lvEntry2.getValue()).getName());
	}


	public void testMapWithKeyClass() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion(Hashtable.class));
		
		Object lvResult = c.convert(new Node("TestNode"));
		assertNotNull(lvResult);
		assertTrue(lvResult instanceof Map);
		assertTrue(lvResult instanceof Hashtable);
		assertNotNull( ((Map) lvResult).get("class") );
		
		c = new Converter();
		c.addConversion(new IterateableMap2MapConversion());
		Object lvResult_2 = c.convert(lvResult);
		assertTrue(lvResult_2 instanceof Map);
		// IterateableMap2MapConversion is not assignalble, because the key class is in the Map 
		assertTrue(lvResult_2 instanceof Hashtable);
		assertNotNull( ((Map) lvResult_2).get("class") );

	}
	
	public void testname() throws Exception {
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2MapConversion(HashMap.class));
		
		Node n = new Node("MyTestNode");
		Map lvMap = (Map) c.convert(n);
		
		c.addConversion(new IterateableMap2BeanConversion());
		Object o = c.convert(lvMap);
		assertNotNull(o);
		assertEquals("MyTestNode", ((Node) o).getName());
	}
	
	public void testMapWithNullValue() throws Exception {
		Map lvMap = new HashMap();
		lvMap.put("key", null);
		Converter c = new Converter();
		c.addConversion(new IterateableMap2MapConversion(HashMap.class, true));

		Object o = c.convert(lvMap);
		assertNotNull(o);
		Map lvMapAfter = (Map) o;
		assertEquals(0, lvMapAfter.size());
	}
	
	public void testIgnoreNullValues() throws Exception {
		Converter c = new Converter();
		IterateableMap2MapConversion lvConversion = new IterateableMap2MapConversion();
		c.addConversion(lvConversion);
		
		Map lvMap = new HashMap();
		lvMap.put("null", null);
		lvMap.put("key", "value");
		Object o = c.convert(lvMap);
		assertNotNull(o);
		Map lvMapAfter = (Map) o;
		assertEquals(2, lvMapAfter.size());
		assertEquals("value", lvMapAfter.get("key"));
		assertTrue(lvMapAfter.containsKey("null"));
		assertNull(lvMapAfter.get("null"));
		
		lvConversion.setIgnoreNullValues(true);
		o = c.convert(lvMap);
		assertNotNull(o);
		lvMapAfter = (Map) o;
		assertEquals(1, lvMapAfter.size());
		assertEquals("value", lvMapAfter.get("key"));
		assertFalse(lvMapAfter.containsKey("null"));
		assertNull(lvMapAfter.get("null"));
	}

}
