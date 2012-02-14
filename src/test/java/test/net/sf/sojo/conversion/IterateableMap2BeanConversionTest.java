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
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.core.conversion.Iterateable2IterateableConversion;
import net.sf.sojo.core.conversion.IterateableMap2BeanConversion;
import net.sf.sojo.core.conversion.IterateableMap2MapConversion;
import test.net.sf.sojo.model.Node;

public class IterateableMap2BeanConversionTest extends TestCase {
	
	public void testSimpleBean() throws Exception {
		Converter c = new Converter();	
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		
		String lvName = "Test-Node";
		Object lvNodeMap = c.convert(new Node(lvName));
		Object lvNode = c.convert(lvNodeMap);
		assertNotNull(lvNode);
		assertTrue(lvNode instanceof Node);
		Node n = (Node) lvNode;
		assertEquals(lvName, n.getName());
	}

	public void testListSimpleBeans() throws Exception {
		Converter c = new Converter();	
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new Iterateable2IterateableConversion());
		
		List<Node> lvList = new ArrayList<Node>();
		lvList.add(new Node("Node1"));
		lvList.add(new Node("Node2"));
		
		Object lvNodeMap = c.convert(lvList);
		Object lvNode = c.convert(lvNodeMap);
		assertNotNull(lvNode);
		assertTrue(lvNode instanceof List);
		List<?> lvListNodes = (List<?>) lvNode;
		assertEquals(lvList.size(), lvListNodes.size());
		assertEquals("Node1", ((Node) lvListNodes.get(0)).getName()); 
		assertEquals("Node2", ((Node) lvListNodes.get(1)).getName());
	}
	
	public void testListWithSameSimpleBeans() throws Exception {
		Converter c = new Converter();	
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		c.addConversion(new Iterateable2IterateableConversion());
		
		List<Node> lvList = new ArrayList<Node>();
		Node n1 = new Node("Node1and3");
		lvList.add(n1);
		lvList.add(new Node("Node2"));
		lvList.add(n1);
		
		Object lvNodeMap = c.convert(lvList);
		Object lvNode = c.convert(lvNodeMap);
		assertNotNull(lvNode);
		assertTrue(lvNode instanceof List);
		List<?> lvListNodes = (List<?>) lvNode;
		assertEquals(lvList.size(), lvListNodes.size());
		Node n1After = (Node) lvListNodes.get(0);
		assertEquals("Node1and3", n1After.getName()); 
		assertEquals("Node2", ((Node) lvListNodes.get(1)).getName());
		Object n3After = lvListNodes.get(2);
		assertEquals("Node1and3", ((Node) n3After).getName());
		assertEquals(n1After, n3After);
	}
	
	public void testMixedMapAndBeans() throws Exception {
		Converter c = new Converter();	
		c.addConversion(new IterateableMap2MapConversion(HashMap.class));
		c.addConversion(new Iterateable2IterateableConversion());
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		
		List<Object> lvList = new ArrayList<Object>();
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("1", "1");
		lvList.add(lvMap);
		lvList.add(new Node("Node"));

		Object lvSimple = c.convert(lvList);
		Object lvComplex = c.convert(lvSimple);

		assertNotNull(lvComplex);
		assertTrue(lvComplex instanceof List);
		List<?> lvListAfter = (List<?>) lvComplex;
		assertEquals(lvList.size(), lvListAfter.size());
		
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvListAfter.get(0);
		assertEquals(lvMap.get("1"), lvMapAfter.get("1"));
		assertTrue(lvMapAfter instanceof HashMap);
		
		Node lvNodeAfter = (Node) lvListAfter.get(1);
		assertEquals("Node", lvNodeAfter.getName());
	}
	
	public void testBean2MapAndBack() throws Exception {
		Converter c = new Converter();	
		c.addConversion(new IterateableMap2MapConversion(HashMap.class));
		c.addConversion(new ComplexBean2MapConversion());
		c.addConversion(new IterateableMap2BeanConversion());
		
		Node n = new Node("MyTestNode");
		Object lvResult = c.convert(n);	
		assertNotNull(lvResult);
		
		Object lvNodeAfter = c.convert(lvResult);
		assertNotNull(lvNodeAfter);
		assertEquals("MyTestNode", ((Node) lvNodeAfter).getName());
	}

	public void testInvalidParamType() throws Exception {
		try {
			Converter c = new Converter();
			c.addConversion(new IterateableMap2BeanConversion());

			Map<String, Comparable<?>> lvMap = new HashMap<String, Comparable<?>>();
			lvMap.put("class", Node.class.getName());
			lvMap.put("children", new Long(77));
			
			c.convert(lvMap);
			fail("Long and String are not assignable.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testIgnoreNullValues() throws Exception {
		Converter c = new Converter();
		IterateableMap2BeanConversion lvConversion = new IterateableMap2BeanConversion();
		lvConversion.setIgnoreNullValues(false);
		c.addConversion(lvConversion);
		
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("class", Node.class.getName());
		lvMap.put("children", null);
		lvMap.put("name", "MyNode");
		
		Node n = (Node) c.convert(lvMap);
		assertNull(n.getChildren());		
	}

}
