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
package test.net.sf.sojo.common;

import java.util.HashSet;
import java.util.Set;

import test.net.sf.sojo.model.Node;
import net.sf.sojo.common.GenericComparator;
import net.sf.sojo.common.IterableUtil;
import junit.framework.TestCase;

public class GenericComparatorTest extends TestCase {

	public void testComapreNull() throws Exception {
		GenericComparator lvComparator = new GenericComparator();
		try {
			lvComparator.compare(null, null);
			fail("Null-value are not supported");
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
		

		try {
			lvComparator.compare(null, "Node");
			fail("Null-value are not supported");
		} catch (NullPointerException e) {
			assertNotNull(e);
		}

		try {
			lvComparator.compare("Node", null);
			fail("Null-value are not supported");
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
	}

	public void testComapreSimpleString() throws Exception {
		GenericComparator lvComparator = new GenericComparator();
		int lvCompareResult = lvComparator.compare("Node1", "Node1");
		assertEquals(0, lvCompareResult);
		
		lvCompareResult = lvComparator.compare("Node1", "Node2");
		assertEquals(-1, lvCompareResult);
		
		lvCompareResult = lvComparator.compare("Node2", "Node1");
		assertEquals(1, lvCompareResult);
	}

	public void testComapreSimpleLongGetUnEqualsByEquals() throws Exception {
		GenericComparator lvComparator = new GenericComparator();
		int lvCompareResult = lvComparator.compare(new Long("-1"), new Long("-1"));
		assertEquals(0, lvCompareResult);

		lvComparator = new GenericComparator(true);
		lvCompareResult = lvComparator.compare(new Long("-1"), new Long("-1"));
		assertEquals(0, lvCompareResult);

		Long l = new Long("4711");
		lvCompareResult = lvComparator.compare(l, l);
		assertEquals(0, lvCompareResult);

	}

	public void testComapreSimpleLong() throws Exception {
		GenericComparator lvComparator = new GenericComparator();
		int lvCompareResult = lvComparator.compare(new Long("-1"), new Long("-1"));
		assertEquals(0, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(new Long("-1"), new Long("1"));
		assertEquals(-1, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(new Long("1"), new Long("-1"));
		assertEquals(1, lvCompareResult);
	}

	public void testComapreSimpleDouble() throws Exception {
		GenericComparator lvComparator = new GenericComparator();
		int lvCompareResult = lvComparator.compare(new Double("4711"), new Double("4711"));
		assertEquals(0, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(new Double("0.07"), new Double("4711"));
		assertEquals(-1, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(new Double("4711"), new Double("0.07"));
		assertEquals(1, lvCompareResult);
	}

	public void testComapreNodeBean() throws Exception {
		Node n1 = new Node("Node1");
		Node n2 = new Node("Node2");
		GenericComparator lvComparator = new GenericComparator();
		
		int lvCompareResult = lvComparator.compare(n1, n1);
		assertEquals(0, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(n2, n2);
		assertEquals(0, lvCompareResult);

		lvCompareResult = lvComparator.compare(n1, n2);
		assertEquals(-1, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(n2, n1);
		assertEquals(1, lvCompareResult);
	}

	public void testComapreNodeBeanWithCycle() throws Exception {
		Node n0 = new Node("Node0");
		Node n1 = new Node("Node1");
		n1.setParent(n0);
		n0.setParent(n1);
		Node n2 = new Node("Node2");
		n2.setParent(n0);

		GenericComparator lvComparator = new GenericComparator(true);

		int lvCompareResult = lvComparator.compare(n1, n1);
		assertEquals(0, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(n2, n2);
		assertEquals(0, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(n1, n2);
		assertEquals(-1, lvCompareResult);
		
		lvCompareResult = lvComparator.compare(n2, n1);
		assertEquals(1, lvCompareResult);
	}
	
	public void testSortedSet() throws Exception {
		Node n0 = new Node("Node0");
		Node n1 = new Node("Node1");

		Set<Object> lvSet = new HashSet<Object>();
		lvSet.add("String-Value");
		lvSet.add(n1);
		lvSet.add(n0);
		
		assertEquals(3, lvSet.size());
		
		Set<?> lvSetAfter = IterableUtil.sort(lvSet);
		assertEquals(3, lvSetAfter.size());
	}

}
