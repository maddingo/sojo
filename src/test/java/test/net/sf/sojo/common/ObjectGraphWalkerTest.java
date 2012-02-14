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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import junit.framework.TestCase;
import net.sf.sojo.common.ObjectGraphWalker;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.common.PathRecordWalkerInterceptor;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.navigation.PathExecuter;
import test.net.sf.sojo.model.Address;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;

public class ObjectGraphWalkerTest extends TestCase {

	public void testInterceptor() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		assertEquals(0, lvWalker.getInterceptorSize());
		
		lvWalker.addInterceptor(lvInterceptor);
		assertEquals(1, lvWalker.getInterceptorSize());
		
		lvWalker.removeInterceptorByNumber(0);
		assertEquals(0, lvWalker.getInterceptorSize());
	}
	
	public void testWalkSimpleString() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		String s = "Test-String";
		lvWalker.walk(s);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		String lvSearchPath = lvPathes.keySet().iterator().next().toString();
		assertEquals(0, lvSearchPath.length());
		Object lvResult = PathExecuter.getNestedProperty(s, lvSearchPath);
		assertEquals(s, lvResult);
	}
	
	public void testWalkSimpleInteger() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Integer i = new Integer(4711);
		lvWalker.walk(i);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		String lvSearchPath = lvPathes.keySet().iterator().next().toString();
		assertEquals(0, lvSearchPath.length());
		Object lvResult = PathExecuter.getNestedProperty(i, lvSearchPath);
		assertEquals(i, lvResult);
	}

	public void testWalkSimpleDouble() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Double d = new Double(0.07);
		lvWalker.walk(d);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		String lvSearchPath = lvPathes.keySet().iterator().next().toString();
		assertEquals(0, lvSearchPath.length());
		Object lvResult = PathExecuter.getNestedProperty(d, lvSearchPath);
		assertEquals(d, lvResult);
	}
	
	public void testWalkSimpleDate() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Date lvDate = new Date();
		lvWalker.walk(lvDate);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		String lvSearchPath = lvPathes.keySet().iterator().next().toString();
		assertEquals(0, lvSearchPath.length());
		Object lvResult = PathExecuter.getNestedProperty(lvDate, lvSearchPath);
		assertEquals(lvDate, lvResult);
	}
	
	public void testWalkNull() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(null);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		String lvSearchPath = lvPathes.keySet().iterator().next().toString();
		assertEquals(0, lvSearchPath.length());
		Object lvResult = PathExecuter.getNestedProperty(null, lvSearchPath);
		assertNull(lvResult);
	}



	public void testWalkEmptyList() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		List<?> lvList = new ArrayList<Object>();
		lvWalker.walk(lvList);
				
		Object lvPathValue = PathExecuter.getNestedProperty(lvList, "[]");
		assertEquals(new ArrayList<Object>(), lvPathValue);		
	}

	public void testWalkList() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		List<String> lvList = new ArrayList<String>();
		lvList.add("aaa");
		lvList.add("bbb");
		lvWalker.walk(lvList);
				
		Object lvPathValue = PathExecuter.getNestedProperty(lvList, "[0]");
		assertEquals("aaa", lvPathValue);
		
		lvPathValue = PathExecuter.getNestedProperty(lvList, "[1]");
		assertEquals("bbb", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvList, "[]");
		assertEquals(lvList, lvPathValue);
		
		assertEquals(3, lvInterceptor.getAllRecordedPaths().size());
	}

	public void testWalkObjectArray() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Date lvDate = new Date();
		Object o[] = new Object[] { "aaa", new Integer(7), lvDate};
		lvWalker.walk(o);
		
		assertEquals(4, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(o, "[0]");
		assertEquals("aaa", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(o, "[1]");
		assertEquals(new Integer(7), lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(o, "[2]");
		assertEquals(lvDate, lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(o, "[]");
		assertEquals(o, lvPathValue);
	}

	public void testWalkStringArray() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		String s[] = new String[] { "aaa", "bbb", "ccc"};
		lvWalker.walk(s);
		
		assertEquals(4, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(s, "[0]");
		assertEquals("aaa", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(s, "[1]");
		assertEquals("bbb", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(s, "[2]");
		assertEquals("ccc", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(s, "[]");
		assertEquals(s, lvPathValue);
	}

	public void testWalkEmptyMap() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Map<?, ?> lvMap = new HashMap<Object, Object>();
		lvWalker.walk(lvMap);

		assertEquals(1, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(lvMap, "()");
		assertEquals(new HashMap<Object, Object>(), lvPathValue);
	}
	
	public void testWalkEmptyListInMap() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Map<String, ArrayList<?>> lvMap = new HashMap<String, ArrayList<?>>();
		lvMap.put("empty", new ArrayList<Object>());
		lvWalker.walk(lvMap);

		assertEquals(2, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(lvMap, "(empty)");
		assertEquals(new ArrayList<Object>(), lvPathValue);
	}
	
	public void testWalkEmptyMapInList() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		List<HashMap<?, ?>> lvList = new ArrayList<HashMap<?, ?>>();
		lvList.add(new HashMap<Object, Object>());
		lvWalker.walk(lvList);

		assertEquals(2, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(lvList, "[0]");
		assertEquals(new HashMap<Object, Object>(), lvPathValue);
	}



	public void testWalkMap() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("key1", "val1");
		lvMap.put("key2", "val2");
		lvWalker.walk(lvMap);

		assertEquals(3, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(lvMap, "(key1)");
		assertEquals("val1", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvMap, "(key2)");
		assertEquals("val2", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvMap, "()");
		assertEquals(lvMap, lvPathValue);
	}

	public void testWalkNestedListInMap() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Map<String, Object> lvMap = new HashMap<String, Object>();
		lvMap.put("key1", "val1");
		lvMap.put("key2", "val2");
		List<String> lvList = new ArrayList<String>();
		lvList.add("aaa");
		lvList.add("bbb");
		lvMap.put("list", lvList);
		lvWalker.walk(lvMap);
		
		assertEquals(6, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(lvMap, "(key1)");
		assertEquals("val1", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvMap, "(key2)");
		assertEquals("val2", lvPathValue);
		
		lvPathValue = PathExecuter.getNestedProperty(lvMap, "()");
		assertEquals(lvMap, lvPathValue);
		
		lvPathValue = PathExecuter.getNestedProperty(lvMap, "(list).[]");
		assertEquals(lvList, lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvMap, "(list).[0]");
		assertEquals("aaa", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvMap, "(list).[1]");
		assertEquals("bbb", lvPathValue);
	}

	public void testWalkNestedMapInList() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("key1", "val1");
		lvMap.put("key2", "val2");
		List<Object> lvList = new ArrayList<Object>();
		lvList.add("aaa");
		lvList.add("bbb");
		lvList.add(lvMap);
		lvWalker.walk(lvList);
		
		assertEquals(6, lvInterceptor.getAllRecordedPaths().size());
		
		Object lvPathValue = PathExecuter.getNestedProperty(lvList, "[]");
		assertEquals(lvList, lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvList, "[0]");
		assertEquals("aaa", lvPathValue);
		
		lvPathValue = PathExecuter.getNestedProperty(lvList, "[1]");
		assertEquals("bbb", lvPathValue);
		
		lvPathValue = PathExecuter.getNestedProperty(lvList, "[2]");
		assertEquals(lvMap, lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvList, "[2].()");
		assertEquals(lvMap, lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvList, "[2].(key1)");
		assertEquals("val1", lvPathValue);

		lvPathValue = PathExecuter.getNestedProperty(lvList, "[2].(key2)");
		assertEquals("val2", lvPathValue);
	}


	
	public void testWalkBeanSimple() throws Exception {
		Node lvNode = new Node("Test-Node");
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvInterceptor.setFilterUniqueIdProperty(true);
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(lvNode);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		
		assertNotNull(lvPathes.get("children[]"));
		assertNotNull(lvPathes.get("namedChildren()"));
		assertNotNull(lvPathes.get("class"));
		assertNotNull(lvPathes.get("name"));
		assertNull(lvPathes.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertNull(lvPathes.get("parent"));
		
		Iterator<?> lvIterator = lvPathes.entrySet().iterator();
		while (lvIterator.hasNext()) {
			Entry<?, ?> lvEntry = (Entry<?, ?>) lvIterator.next();
			if (lvEntry.getKey().equals("children[]")) {
				Object lvResult = PathExecuter.getNestedProperty(lvNode, lvEntry.getKey().toString());
				assertEquals(new ArrayList<Object>(), lvResult);
			}
			else if (lvEntry.getKey().equals("namedChildren()")) {
				Object lvResult = PathExecuter.getNestedProperty(lvNode, lvEntry.getKey().toString());
				assertEquals(new HashMap<Object, Object>(), lvResult);
			}
			else if (lvEntry.getKey().equals("class")) {
				Object lvResult = PathExecuter.getNestedProperty(lvNode, lvEntry.getKey().toString());
				assertEquals(Node.class, lvResult);
			}
			else if (lvEntry.getKey().equals("name")) {
				Object lvResult = PathExecuter.getNestedProperty(lvNode, lvEntry.getKey().toString());
				assertEquals("Test-Node", lvResult);
			}
			else if (lvEntry.getKey().equals("parent")) {
				Object lvResult = PathExecuter.getNestedProperty(lvNode, lvEntry.getKey().toString());
				assertNull(lvResult);
			}
		}
	}
	
	public void testWalkBeanSimpleNumberOfPropertiesNode() throws Exception {
		Node lvNode = new Node("Test-Node");
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvInterceptor.setFilterUniqueIdProperty(true);
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(lvNode);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		Map<?, ?> lvNodeMap = (Map<?, ?>) new ObjectUtil().makeSimple(new Node());
		lvNodeMap.remove(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(lvPathes.size(), lvNodeMap.size());

		String lvPath = "name";
		assertNotNull(lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node", lvResult);		
	}
	
	public void testWalkBeanSimpleNumberOfPropertiesCustomer() throws Exception {
		Customer lvCustomer = new Customer("Test-Name");
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvInterceptor.setFilterUniqueIdProperty(true);
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(lvCustomer);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();
		Map<?, ?> lvCustomerMap = (Map<?, ?>) new ObjectUtil().makeSimple(new Customer());
		lvCustomerMap.remove(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(lvPathes.size() + 3, lvCustomerMap.size());
	}

	public void testWalkBeanSimpleInList() throws Exception {
		Node lvNode1 = new Node("Test-Node-1");
		lvNode1.getChildren().add("child-1");
		Node lvNode2 = new Node("Test-Node-2");
		lvNode2.getNamedChildren().put("key-1", "value-1");
		List<Node> lvList = new ArrayList<Node>();
		lvList.add(lvNode1);
		lvList.add(lvNode2);
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvInterceptor.setFilterUniqueIdProperty(true);
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(lvList);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		Map<?, ?> lvNodeMap = (Map<?, ?>) new ObjectUtil().makeSimple(new Node());
		lvNodeMap.remove(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		assertEquals(10, lvNodeMap.size() * 2);
		
		String lvPath = "[0].name";
		assertNotNull(lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvList, lvPath);
		assertNotNull(lvResult);
		assertEquals("Test-Node-1", lvResult);

		lvPath = "[0].children[0]";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvList, lvPath);
		assertNotNull(lvResult);
		assertEquals("child-1", lvResult);

		lvPath = "[1].name";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvList, lvPath);
		assertNotNull(lvResult);
		assertEquals("Test-Node-2", lvResult);

		lvPath = "[1].namedChildren(key-1)";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvList, lvPath);
		assertNotNull(lvResult);
		assertEquals("value-1", lvResult);
	}

	public void testWalkBeanComplex() throws Exception {
		Node lvNode = new Node("Test-Node");
		Node lvNodeParent = new Node("Test-Node-Parent");
		lvNode.setParent(lvNodeParent);
		lvNode.getChildren().add("child-1");
		lvNode.getChildren().add("child-2");
		lvNode.getNamedChildren().put("child-key-1", "child-value-1");
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(lvNode);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		String lvPath = "name";
		assertEquals("Test-Node", lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node", lvResult);

		lvPath = "children[0]";
		assertEquals("child-1", lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("child-1", lvResult);

		lvPath = "children[]";
		assertEquals(lvNode.getChildren(), lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals(lvNode.getChildren().size(), ((List<?>) lvResult).size());
		assertEquals(lvNode.getChildren(), lvResult);
		
		lvPath = "namedChildren(child-key-1)";
		assertEquals(lvNode.getNamedChildren().get("child-key-1"), lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("child-value-1", lvResult);

		lvResult = PathExecuter.getNestedProperty(lvNode, "namedChildren(key-not-found)");
		assertNull(lvResult);
	}
	
	public void testWalkBeanComplexWihLongWay() throws Exception {
		Node lvNode = new Node("Test-Node");
		Node lvNodeParent = new Node("Test-Node-Parent");
		lvNode.setParent(lvNodeParent);
		lvNode.getChildren().add("child-1");
		lvNode.getChildren().add("child-2");
		lvNode.getNamedChildren().put("key", "value");
		lvNode.getNamedChildren().put("self", lvNode);
		
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);
		
		lvWalker.walk(lvNode);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		String lvPath = "name";
		assertEquals("Test-Node", lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node", lvResult);
		
		lvPath = "namedChildren(key)";
		assertEquals("value", lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("value", lvResult);

		lvPath = "namedChildren(self)";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals(lvNode, lvResult);

		lvPath = "namedChildren(self).name";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node", lvResult);

		lvPath = "namedChildren(self).namedChildren(self).name";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node", lvResult);

		lvPath = "namedChildren(self).namedChildren(self).namedChildren(self).name";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node", lvResult);

		lvPath = "namedChildren(self).children[0]";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("child-1", lvResult);

		lvPath = "namedChildren(self).children[1]";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("child-2", lvResult);

		lvPath = "namedChildren(self).children[]";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals(lvNode.getChildren(), lvResult);
		
		lvPath = "namedChildren(self).parent.name";
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node-Parent", lvResult);

		lvPath = "namedChildren(self).namedChildren(self).namedChildren(self).parent.name";
		lvResult = PathExecuter.getNestedProperty(lvNode, lvPath);
		assertEquals("Test-Node-Parent", lvResult);
	}

	
	public void testLongWayByCustomer() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);

		Customer lvCustomer = new Customer();
		lvCustomer.setBirthDate(new Date());
		lvCustomer.setFirstName("First");
		lvCustomer.setLastName("Last");
		lvCustomer.setPartner(new Customer[] { new Customer("NEW"),  lvCustomer } );
		Address a1 = new Address();
		a1.setCity("city 1");
		a1.setPostcode("12345");
		a1.setCustomer(lvCustomer);
		Address a2 = new Address();
		a2.setCity("city 2");
		a2.setPostcode("98765");
		lvCustomer.getAddresses().add(a1);
		lvCustomer.getAddresses().add(a2);
		
		lvWalker.walk(lvCustomer);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		String lvPath = "firstName";
		assertNotNull(lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals("First", lvResult);
		
		lvPath = "lastName";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals("Last", lvResult);

		lvPath = "birthDate";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer.getBirthDate(), lvResult);

		lvPath = "partner[]";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer.getPartner().length, ((Customer[]) lvResult).length);

		lvPath = "partner[1]";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer, lvResult);
		assertSame(lvCustomer, lvResult);

		lvPath = "partner[0].lastName";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals("NEW", lvResult);

		lvPath = "partner[0].firstName";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertNull(lvResult);

		lvPath = "addresses[]";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer.getAddresses(), lvResult);

		lvPath = "addresses[0].city";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals("city 1", lvResult);

		lvPath = "addresses[0].customer";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer, lvResult);
		assertSame(lvCustomer, lvResult);
		
		lvPath = "addresses[0].customer.addresses[0].customer";
		assertNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer, lvResult);
		assertSame(lvCustomer, lvResult);

		lvPath = "addresses[1].city";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals("city 2", lvResult);
	}

	public void testEmptyArrayByCustomer() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);

		Customer lvCustomer = new Customer();
		lvCustomer.setLastName("Last-Name");
		lvCustomer.setPartner(new Customer[0]);

		lvWalker.walk(lvCustomer);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		String lvPath = "lastName";
		assertNotNull(lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals("Last-Name", lvResult);

		lvPath = "partner[]";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(lvCustomer.getPartner(), lvResult);

		lvCustomer.setPartner(null);
		lvPath = "partner[]";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertNull(lvResult);
		
		lvCustomer.setPartner(new Object[] { new ArrayList<Object>() });
		lvPath = "partner[0]";
		lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(new ArrayList<Object>(), lvResult);
	}

	public void testEmptyMapByCustomer() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);

		Customer lvCustomer = new Customer();
		lvCustomer.setLastName("Last-Name");
		lvCustomer.setPartner(new Object[] { new HashMap<Object, Object>() });

		lvWalker.walk(lvCustomer);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		String lvPath = "partner[0].()";
		assertNotNull(lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvCustomer, lvPath);
		assertEquals(new HashMap<Object, Object>(), lvResult);
	}


	public void testBeanCar() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvWalker.addInterceptor(lvInterceptor);

		Car lvCar = new Car("MyCar");
		lvCar.setBuild(new Date());
		lvCar.setProperties(new Properties());
		lvCar.getProperties().put("key", "value");
		lvCar.getProperties().put("self", lvCar);
		
		lvWalker.walk(lvCar);
		Map<?, ?> lvPathes = lvInterceptor.getAllRecordedPaths();

		String lvPath = "name";
		assertNotNull(lvPathes.get(lvPath));
		Object lvResult = PathExecuter.getNestedProperty(lvCar, lvPath);
		assertEquals("MyCar", lvResult);
		
		lvPath = "build";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCar, lvPath);
		assertEquals(lvCar.getBuild(), lvResult);

		lvPath = "properties()";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCar, lvPath);
		assertEquals(lvCar.getProperties(), lvResult);

		lvPath = "properties(key)";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCar, lvPath);
		assertEquals("value", lvResult);

		lvPath = "properties(self)";
		assertNotNull(lvPathes.get(lvPath));
		lvResult = PathExecuter.getNestedProperty(lvCar, lvPath);
		assertEquals(lvCar, lvResult);

		lvPath = "properties(self).name";
		lvResult = PathExecuter.getNestedProperty(lvCar, lvPath);
		assertEquals("MyCar", lvResult);

	}

	public void testSimpleNumberOfRecursion() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		assertEquals(0, lvWalker.getNumberOfRecursion());
		lvWalker.walk("Test");
		assertEquals(1, lvWalker.getNumberOfRecursion());
	}

	public void testNullNumberOfRecursion() throws Exception {
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		assertEquals(0, lvWalker.getNumberOfRecursion());
		lvWalker.walk(null);
		assertEquals(1, lvWalker.getNumberOfRecursion());
	}

	public void testBeanNumberOfRecursion() throws Exception {
		Car lvCar = new Car("MyCar");	
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		assertEquals(0, lvWalker.getNumberOfRecursion());
		lvWalker.walk(lvCar);
		assertEquals(5, lvWalker.getNumberOfRecursion());
	}
	
	public void testListNumberOfRecursion() throws Exception {
		List<Comparable<?>> lvList = new ArrayList<Comparable<?>>();
		lvList.add("aa");
		lvList.add(new Long(4711));
		
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		assertEquals(0, lvWalker.getNumberOfRecursion());
		lvWalker.walk(lvList);
		assertEquals(3, lvWalker.getNumberOfRecursion());
		
		lvList.add(new Double(47.11));
		lvWalker.walk(lvList);
		assertEquals(4, lvWalker.getNumberOfRecursion());
	}


	public void testWalkInterceptorByList() throws Exception {
		List<String> lvList = new ArrayList<String>();
		lvList.add("aaa");
		lvList.add("bbb");
		lvList.add("ccc");
		
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		TestWalkerInterceptor lvInterceptor = new TestWalkerInterceptor("bbb"); 
		lvWalker.addInterceptor(lvInterceptor);
		lvWalker.walk(lvList);
		assertNotNull(lvInterceptor);
		assertEquals("bbb", lvInterceptor.getWhenThisObjectThanCanelWalk());
		
		lvWalker = new ObjectGraphWalker();
		lvInterceptor = new TestWalkerInterceptor(lvList); 
		lvWalker.addInterceptor(lvInterceptor);
		lvWalker.walk(lvList);
		assertNotNull(lvInterceptor);
		assertEquals(lvList, lvInterceptor.getWhenThisObjectThanCanelWalk());
	}
	
	public void testWalkInterceptorByMap() throws Exception {
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("k-aaa", "v-aaa");
		lvMap.put("k-bbb", "v-bbb");
		
		ObjectGraphWalker lvWalker = new ObjectGraphWalker();
		TestWalkerInterceptor lvInterceptor = new TestWalkerInterceptor(lvMap); 
		lvWalker.addInterceptor(lvInterceptor);
		lvWalker.walk(lvMap);
		assertNotNull(lvInterceptor);
		assertEquals(lvMap, lvInterceptor.getWhenThisObjectThanCanelWalk());
	}

}
