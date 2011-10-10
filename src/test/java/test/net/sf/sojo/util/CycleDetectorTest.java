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
package test.net.sf.sojo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import test.net.sf.sojo.model.Node;

import junit.framework.TestCase;
import net.sf.sojo.util.CycleDetector;

public class CycleDetectorTest extends TestCase {
	
	private CycleDetector cycleDetector = new CycleDetector();

	public void testNull() throws Exception {
		assertFalse(cycleDetector.cycleDetection(null));
	}

	public void testSimpleCycleInList() throws Exception {
		List lvList1 = new ArrayList();
		assertFalse(cycleDetector.cycleDetection(lvList1));
		
		lvList1.add(lvList1);
		assertTrue(cycleDetector.cycleDetection(lvList1));
	}

	public void testNoCycleInList() throws Exception {
		List lvList1 = new ArrayList();
		List lvList2 = new ArrayList();
		lvList2.add("a");
		lvList1.add(lvList2);
		lvList1.add(lvList2);
		assertFalse(cycleDetector.cycleDetection(lvList1));
	}
	
	public void testNestedCycleInList() throws Exception {
		List lvList1 = new ArrayList();
		List lvList2 = new ArrayList();
		List lvList3 = new ArrayList();
		lvList1.add("a");
		lvList1.add(lvList2);
		lvList2.add(lvList3);
		assertFalse(cycleDetector.cycleDetection(lvList1));
		
		lvList3.add(lvList1);
		assertTrue(cycleDetector.cycleDetection(lvList1));
		assertTrue(cycleDetector.cycleDetection(lvList2));
		assertTrue(cycleDetector.cycleDetection(lvList3));
	}
	
	public void testNestedCycleInList2() throws Exception {
		List lvList1 = new ArrayList();
		List lvList2 = new ArrayList();
		List lvList3 = new ArrayList();
		List lvList4 = new ArrayList();
		lvList1.add(lvList2);
		lvList2.add(lvList3);
		lvList3.add(lvList4);
		assertFalse(cycleDetector.cycleDetection(lvList1));
		
		lvList4.add(lvList2);
		assertFalse(cycleDetector.cycleDetection(lvList1));
		assertEquals(100, cycleDetector.getCounter());

		cycleDetector.setMaxCounter(30);
		assertFalse(cycleDetector.cycleDetection(lvList1));
		assertEquals(30, cycleDetector.getMaxCounter());
		assertEquals(30, cycleDetector.getCounter());

		assertTrue(cycleDetector.cycleDetection(lvList2));
		assertTrue(cycleDetector.cycleDetection(lvList3));
	}

	
	public void testNoCycleOverBeanInList() throws Exception {
		List lvList1 = new ArrayList();
		Node n = new Node("N");
		Node n1 = new Node("N1");
		n1.setParent(n);
		n.getChilds().add(n1);
		lvList1.add(n);
		assertEquals(1, lvList1.size());
		assertFalse(cycleDetector.cycleDetection(lvList1));
		
		lvList1.add(n);
		assertEquals(2, lvList1.size());
		assertFalse(cycleDetector.cycleDetection(lvList1));
	}
	
	public void testSimpleCycleInMap() throws Exception {
		Map lvMap = new HashMap();
		assertFalse(cycleDetector.cycleDetection(lvMap));
		
		lvMap.put("key", lvMap);
		assertTrue(cycleDetector.cycleDetection(lvMap));

		lvMap = new HashMap();
		lvMap.put(lvMap, "value");
		assertTrue(cycleDetector.cycleDetection(lvMap));

		lvMap = new HashMap();
		lvMap.put(lvMap, lvMap);
		assertTrue(cycleDetector.cycleDetection(lvMap));
	}

	public void testNoCycleInMap() throws Exception {
		Map lvMap1 = new HashMap();
		Map lvMap2 = new HashMap();
		lvMap1.put("map2", lvMap2);
		lvMap1.put("map2-1", lvMap2);
		assertFalse(cycleDetector.cycleDetection(lvMap1));
	}
	public void testCycleInMap() throws Exception {
		Map lvMap1 = new HashMap();
		Map lvMap2 = new HashMap();
		lvMap1.put("k1", "v1");
		lvMap1.put("map2", lvMap2);
		lvMap2.put("map1", lvMap1);
		assertTrue(cycleDetector.cycleDetection(lvMap1));
	}

	public void testNestedCycleInMap() throws Exception {
		Map lvMap1 = new HashMap();
		Map lvMap2 = new HashMap();
		Map lvMap3 = new HashMap();
		lvMap1.put("k1", "v1");
		lvMap1.put("map2", lvMap2);
		lvMap2.put("map3", lvMap3);
		assertFalse(cycleDetector.cycleDetection(lvMap1));
		
		lvMap3.put("map1", lvMap1);
		assertTrue(cycleDetector.cycleDetection(lvMap1));
		assertTrue(cycleDetector.cycleDetection(lvMap2));
		assertTrue(cycleDetector.cycleDetection(lvMap3));
	}

	public void testNestedCycleInMap2() throws Exception {
		Map lvMap1 = new HashMap();
		Map lvMap2 = new HashMap();
		Map lvMap3 = new HashMap();
		Map lvMap4 = new HashMap();
		lvMap1.put("m2", lvMap2);
		lvMap2.put("m3", lvMap3);
		lvMap3.put("m4", lvMap4);
		assertFalse(cycleDetector.cycleDetection(lvMap1));
		
		lvMap4.put("m2", lvMap2);
		assertFalse(cycleDetector.cycleDetection(lvMap1));
		assertEquals(100, cycleDetector.getCounter());

		cycleDetector.setMaxCounter(30);
		assertFalse(cycleDetector.cycleDetection(lvMap1));
		assertEquals(30, cycleDetector.getMaxCounter());
		assertEquals(30, cycleDetector.getCounter());

		assertTrue(cycleDetector.cycleDetection(lvMap2));
		assertTrue(cycleDetector.cycleDetection(lvMap3));
	}

	public void testNoCycleOverBeanInMap() throws Exception {
		Map lvMap1 = new HashMap();
		Node n = new Node("N");
		Node n1 = new Node("N1");
		n1.setParent(n);
		n.getChilds().add(n1);
		lvMap1.put("N", n);
		assertEquals(1, lvMap1.size());
		assertFalse(cycleDetector.cycleDetection(lvMap1));
		
		lvMap1.put("N-1", n);
		assertEquals(2, lvMap1.size());
		assertFalse(cycleDetector.cycleDetection(lvMap1));
	}
	
	public void testSetInMapWithCycle() throws Exception {
		Map lvMap = new HashMap();
		Set lvSet = new HashSet();
		lvMap.put("set", lvSet);
		
		assertFalse(cycleDetector.cycleDetection(lvMap));

		lvSet.add(lvSet);
		assertTrue(cycleDetector.cycleDetection(lvSet));
		assertEquals(1, cycleDetector.getCounter());
		
		assertFalse(cycleDetector.cycleDetection(lvMap));
		assertEquals(100, cycleDetector.getCounter());
	}

	public void testMapInSetWithCycle() throws Exception {
		Map lvMap = new HashMap();
		Set lvSet = new HashSet();
		lvSet.add(lvMap);
		
		assertFalse(cycleDetector.cycleDetection(lvSet));

		lvMap.put("map", lvMap);
		assertTrue(cycleDetector.cycleDetection(lvMap));
		assertEquals(1, cycleDetector.getCounter());
		
		assertFalse(cycleDetector.cycleDetection(lvSet));
		assertEquals(100, cycleDetector.getCounter());
	}
	
	public void testNestedListInMapWithCycle() throws Exception {
		Map lvMap = new HashMap();
		List lvList1 = new ArrayList();
		List lvList2 = new Vector();
		List lvList3 = new Vector();
		lvMap.put("l1", lvList1);
		lvMap.put("l2", lvList2);
		lvMap.put("l3", lvList3);
		lvList1.add(lvList2);
		lvList2.add(lvList3);
		
		assertFalse(cycleDetector.cycleDetection(lvMap));
		assertEquals(8, cycleDetector.getCounter());
		
		lvList3.add(lvList2);
		assertFalse(cycleDetector.cycleDetection(lvMap));
		assertEquals(102, cycleDetector.getCounter());
		
		assertFalse(cycleDetector.cycleDetection(lvList1));
		
		assertTrue(cycleDetector.cycleDetection(lvList2));
		assertTrue(cycleDetector.cycleDetection(lvList3));
	}

}
