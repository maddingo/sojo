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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.sojo.common.IterableUtil;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.Primitive;

public class IterateableUtilTest extends TestCase {
	
	public void testSortStringList() throws Exception {
		List<String> lvList = new ArrayList<String>(4);
		lvList.add("BMW");
		lvList.add("Mercedes");
		lvList.add("Audi");
		lvList.add("Ferrari");

		lvList = IterableUtil.sort(lvList);
		assertEquals("Audi", lvList.get(0));
		assertEquals("BMW", lvList.get(1));
		assertEquals("Ferrari", lvList.get(2));
		assertEquals("Mercedes", lvList.get(3));
	}

	public void testSortBeanList() throws Exception {
		List<Car> lvList = new ArrayList<Car>(3);
		lvList.add(new Car("BMW"));
		lvList.add(new Car("Audi"));
		lvList.add(new Car("Ferrari"));

		lvList = IterableUtil.sort(lvList);
		assertEquals("Audi", lvList.get(0).getName());
		assertEquals("BMW", lvList.get(1).getName());
		assertEquals("Ferrari", lvList.get(2).getName());
	}

	public void testSortPrimitiveBeanList() throws Exception {
		List<Primitive> lvList = new ArrayList<Primitive>();
		Primitive p1 = new Primitive();
		p1.setLongValue(5);
		Primitive p2 = new Primitive();
		p2.setLongValue(3);
		Primitive p3 = new Primitive();
		p3.setLongValue(1);

		lvList.add(p1);
		lvList.add(p2);
		lvList.add(p3);

		lvList = IterableUtil.sort(lvList);
		assertEquals(1, lvList.get(0).getLongValue());
		assertEquals(3, lvList.get(1).getLongValue());
		assertEquals(5, lvList.get(2).getLongValue());
	}

	public void testSortBigDecimalArray() throws Exception {
		Object lvObjArray[] = new Object [] { new BigDecimal("47.11"),  new BigDecimal("0.07"), new BigDecimal("08.15") } ;

		lvObjArray = IterableUtil.sort(lvObjArray);
		assertEquals(new BigDecimal("0.07"), lvObjArray[0]);
		assertEquals(new BigDecimal("8.15"), lvObjArray[1]);
		assertEquals(new BigDecimal("47.11"), lvObjArray[2]);
	}

	public void testSortBeanArray() throws Exception {
		Car cars[] = new Car [] { new Car("Ferrari"), new Car("Audi"), new Car("BMW") } ;

		cars = (Car[]) IterableUtil.sort(cars);
		assertEquals("Audi", cars[0].getName());
		assertEquals("BMW", cars[1].getName());
		assertEquals("Ferrari", cars[2].getName());
	}

	public void testSortPrimitiveBeanArray() throws Exception {
		Primitive p1 = new Primitive();
		p1.setLongValue(5);
		Primitive p2 = new Primitive();
		p2.setLongValue(3);
		Primitive p3 = new Primitive();
		p3.setLongValue(1);

		Object lvObjArray[] = new Object [] { p1, p2, p3 } ;

		lvObjArray = IterableUtil.sort(lvObjArray);
		assertEquals(1, ((Primitive) lvObjArray[0]).getLongValue());
		assertEquals(3, ((Primitive) lvObjArray[1]).getLongValue());
		assertEquals(5, ((Primitive) lvObjArray[2]).getLongValue());
	}

	public void testSortBeanSet() throws Exception {
		Set<Car> lvSet = new HashSet<Car>();
		lvSet.add(new Car("BMW"));
		lvSet.add(new Car("Audi"));
		lvSet.add(new Car("Ferrari"));

		lvSet = IterableUtil.sort(lvSet);
		Iterator<Car> it = lvSet.iterator();
		assertEquals("Audi", it.next().getName());
		assertEquals("BMW", it.next().getName());
		assertEquals("Ferrari", it.next().getName());
	}

	public void testSortBeanMap() throws Exception {
		Map<Car, String> lvMap = new HashMap<Car, String>();
		lvMap.put(new Car("BMW"), "Dummy1");
		lvMap.put(new Car("Audi"), "Dummy2");
		lvMap.put(new Car("Ferrari"), "Dummy3");

		lvMap = IterableUtil.sort(lvMap);
		Iterator<Car> it = lvMap.keySet().iterator();
		assertEquals("Audi", it.next().getName());
		assertEquals("BMW", it.next().getName());
		assertEquals("Ferrari", it.next().getName());
	}

	public void testSortBeanSetWithCycle() throws Exception {
		Set<Node> lvSet = new HashSet<Node>();
		Node n0 = new Node("Node 0");
		Node n1 = new Node("Node 1");
		n1.setParent(n0);
		n0.setParent(n1);
		Node n2 = new Node("Node 2");
		n2.setParent(n0);
		Node n3 = new Node("Node 3");
		n3.setParent(n0);
		
		lvSet.add(n2);
		lvSet.add(n1);
		lvSet.add(n3);

		assertEquals(3, lvSet.size());

		Set<Node> lvSetAfter = IterableUtil.sort(lvSet);
		assertEquals(3, lvSetAfter.size());
	}
}
