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
package test.net.sf.sojo.navigation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.navigation.PathAction;
import net.sf.sojo.navigation.PathExecuteException;
import net.sf.sojo.navigation.PathExecuter;
import net.sf.sojo.navigation.PathParser;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;

public class PathExecuterTest extends TestCase {
	
	public void testGetSimplePropertyBean() throws Exception {
		Node lvNode = new Node("Test-Node");
		Object lvResult = PathExecuter.getSimpleProperty(lvNode, "name");
		assertNotNull(lvResult);
		assertEquals("Test-Node", lvResult);
	}
	
	public void testGetSimplePropertyFromNullValue() throws Exception {
		try {
			PathExecuter.getSimpleProperty(null, null);
			fail("Null value by path is not supported.");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}
	}

	public void testGetSimplePropertyFromBadObject() throws Exception {
		try {
			PathExecuter.getSimpleProperty("", "chars");
			fail("GetChars for String-object is not a valid Bean-Method.");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}
	}

	public void testGetSimplePropertyWithPathNoLength() throws Exception {
		Object lvResult = PathExecuter.getSimpleProperty("Test-String", "");
		assertNotNull(lvResult);
		assertEquals("Test-String", lvResult);		
		
		Node lvNode = new Node("Test-Node");
		lvResult = PathExecuter.getSimpleProperty(lvNode, "");
		assertNotNull(lvResult);
		assertEquals(lvNode, lvResult);
		assertSame(lvNode, lvResult);
	}

	public void testGetSimplePropertyMap() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("name", "Test-Node");
		Object lvResult = PathExecuter.getSimpleProperty(lvMap, "name");
		assertNotNull(lvResult);
		assertEquals("Test-Node", lvResult);
	}

	public void testSetSimplePropertyBeanString() throws Exception {
		Node lvNode = new Node();
		assertNull(lvNode.getName());
		PathExecuter.setSimpleProperty(lvNode, "name", "Test-Node");
		assertEquals("Test-Node", lvNode.getName());
	}
	
	public void testSetSimplePropertyWithException() throws Exception {
		try {
			PathExecuter.setSimpleProperty("bla bla", null, "abc");
			fail("Path is null.");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}
	}

	public void testSetSimplePropertyBeanWithNoPath() throws Exception {
		Node lvNode = new Node();
		assertNull(lvNode.getName());
		PathExecuter.setSimpleProperty(lvNode, "", "Test-Node");
		assertEquals(lvNode, lvNode);
		assertSame(lvNode, lvNode);
	}


	public void testSetSimplePropertyBean() throws Exception {
		Car lvCar = new Car();
		assertNull(lvCar.getBuild());
		PathExecuter.setSimpleProperty(lvCar, "build", new Date(987654321));
		assertEquals(new Date(987654321), lvCar.getBuild());
	}

	public void testSetSimplePropertyMap() throws Exception {
		Map lvMap = new Hashtable();
		assertNull(lvMap.get("name"));
		PathExecuter.setSimpleProperty(lvMap, "name", "Test-Node");
		assertEquals("Test-Node", lvMap.get("name"));
	}
	
	public void testGetIndexPropertyFromList() throws Exception {
		List lvList = new ArrayList();
		lvList.add(new Node("N1"));
		lvList.add(new Node("N2"));
		lvList.add(new Node("N3"));
		
		Object lvResult = PathExecuter.getIndexProperty(lvList, 0);
		assertNotNull(lvResult);
		assertEquals("N1", ((Node) lvResult).getName());
		
		lvResult = PathExecuter.getIndexProperty(lvList, 2);
		assertNotNull(lvResult);
		assertEquals("N3", ((Node) lvResult).getName());
	}
	
	public void testGetIndexPropertyFromListWithinValidIndex() throws Exception {
		Set lvSet = new HashSet();
		lvSet.add(new Node("N1"));
		
		PathExecuter.getIndexProperty(lvSet, 2);
	}

	
	public void testGetIndexPropertyFromBean() throws Exception {
		Node lvNode = new Node("Test-Node");
		lvNode.getChilds().add("Test_1");
		lvNode.getChilds().add("Test_2");
		lvNode.getChilds().add("Test_3");

		Object lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("childs[0]")[0]);
		assertNotNull(lvResult);
		assertEquals("Test_1", lvResult);
		
		lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("childs[2]")[0]);
		assertNotNull(lvResult);
		assertEquals("Test_3", lvResult);
	}
	
	public void testSetIndexPropertyToList() throws Exception {
		List lvList = new ArrayList();
		lvList.add(new Node("N1"));
		lvList.add(new Node("N2"));
		assertEquals(2, lvList.size());
		
		PathExecuter.setIndexProperty(lvList, new Node("N3"));
		assertEquals(3, lvList.size());
		
		Object lvResult = PathExecuter.getIndexProperty(lvList, 2);
		assertNotNull(lvResult);
		assertEquals("N3", ((Node) lvResult).getName());
	}
	
	public void testSetIndexPropertyToSet() throws Exception {
		Set lvSet = new HashSet();
		lvSet.add("N1");
		lvSet.add("N2");
		assertEquals(2, lvSet.size());
		
		PathExecuter.setIndexProperty(lvSet, "N3");
		assertEquals(3, lvSet.size());
	}

	public void testSetIndexPropertyFromBean() throws Exception {
		Node lvNode = new Node("Test-Node");
		lvNode.getChilds().add("Test_1");
		lvNode.getChilds().add("Test_2");
		assertEquals(2, lvNode.getChilds().size());
		
		PathExecuter.setNestedProperty(lvNode, "childs", "Test_3");
		assertEquals(3, lvNode.getChilds().size());		
		
		Object lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("childs[2]")[0]);
		assertNotNull(lvResult);
		assertEquals("Test_3", lvResult);
		
		PathExecuter.setNestedProperty(lvNode, "childs[-1]", "Test_4");
		assertEquals(4, lvNode.getChilds().size());
		
		lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("childs[3]")[0]);
		assertNotNull(lvResult);
		assertEquals("Test_4", lvResult);
		
		PathExecuter.setNestedProperty(lvNode, "childs[2]", "Test_5");
		assertEquals(5, lvNode.getChilds().size());
		
		lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("childs[2]")[0]);
		assertNotNull(lvResult);
		assertEquals("Test_5", lvResult);

		PathExecuter.setNestedProperty(lvNode, "childs[]", "Test_4");
		assertEquals(6, lvNode.getChilds().size());
		
		PathExecuter.setNestedProperty(lvNode, "childs[]", new Node("N1-List"));
		assertEquals(7, lvNode.getChilds().size());
		assertEquals("N1-List", ((Node) lvNode.getChilds().get(6)).getName());
	}

	public void testGetIndexPropertyArray() throws Exception {
		Object lvArray[] = new Object[] { "Obj1" , new Integer(2), "Obj3" };
		Object lvResult = PathExecuter.getIndexProperty(lvArray, 1);
		assertEquals(new Integer(2), lvResult);
		lvResult = PathExecuter.getNestedProperty(lvArray, "[0]");
		assertEquals("Obj1", lvResult);
	}
	
	public void testSetKeyPropertyMap() throws Exception {
		Map lvMap = new HashMap();
		lvMap.put("k1", "v1");
		lvMap.put("k2", "v2");

		assertEquals("v1", lvMap.get("k1"));
		assertEquals("v2", lvMap.get("k2"));
		assertEquals(2, lvMap.size());
		
		PathExecuter.setNestedProperty(lvMap, "(k1)", "New");
		assertEquals("New", lvMap.get("k1"));
		assertEquals("v2", lvMap.get("k2"));
		assertEquals(2, lvMap.size());
		
		PathExecuter.setNestedProperty(lvMap, "(k-New)", "New");
		assertEquals(3, lvMap.size());
		assertEquals("New", lvMap.get("k1"));
		assertEquals("v2", lvMap.get("k2"));
		assertEquals("New", lvMap.get("k-New"));
	}
	
	public void testSetIndexPropertyArray() throws Exception {
		Object lvArray[] = new Object[] { "Obj1" , new Integer(2), "Obj3" };
		assertEquals("Obj3", lvArray[2]);
		assertEquals(new Integer(2), lvArray[1]);
		
		PathExecuter.setIndexProperty(lvArray, 1, new Integer(5));
		assertEquals(new Integer(5), lvArray[1]);

		PathExecuter.setNestedProperty(lvArray, "[2]", "Obj3-1");
		assertEquals("Obj3-1", lvArray[2]);
		
		Customer lvCustomer = new Customer();
		lvCustomer.setPartner(lvArray);
		assertEquals("Obj1", lvCustomer.getPartner()[0]);
		PathExecuter.setNestedProperty(lvCustomer, "partner[0]", "Obj-1-1");
		assertEquals("Obj-1-1", lvCustomer.getPartner()[0]);
		
		try {
			PathExecuter.setNestedProperty(lvCustomer, "partner", "Obj-1-1");
			fail("Missing index for array.");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}

	}

	public void testSetIndexPropertyListAddElement() throws Exception {
		List lvList = new ArrayList();
		lvList.add("Obj-1");
		lvList.add(new Integer(2));
		lvList.add("Obj-3");
		assertEquals("Obj-1", lvList.get(0));
		assertEquals(new Integer(2), lvList.get(1));
		assertEquals("Obj-3", lvList.get(2));
		assertEquals(3, lvList.size());
		
		PathExecuter.setNestedProperty(lvList, "[]", "New-Obj");
		assertEquals(4, lvList.size());
		assertEquals("New-Obj", lvList.get(3));
	}
	
	public void testGetKeyPropertyFromMap() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("N1", new Node("N1"));
		lvMap.put("N2", new Node("N2"));
		lvMap.put("N3", new Node("N3"));
		
		Object lvResult = PathExecuter.getKeyProperty(lvMap, "N1");
		assertNotNull(lvResult);
		assertEquals("N1", ((Node) lvResult).getName());
		
		lvResult = PathExecuter.getKeyProperty(lvMap, "N3");
		assertNotNull(lvResult);
		assertEquals("N3", ((Node) lvResult).getName());
	}

	public void testKeyIndexPropertyFromBean() throws Exception {
		Node lvNode = new Node("Test-Node");
		lvNode.getNamedChilds().put("N1", new Node("N1"));
		lvNode.getNamedChilds().put("N2", new Node("N2"));
		lvNode.getNamedChilds().put("N3", new Node("N3"));

		Object lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("namedChilds(N1)")[0]);
		assertNotNull(lvResult);
		assertEquals("N1", ((Node) lvResult).getName());
		
		lvResult = PathExecuter.getNestedProperty(lvNode, PathParser.parse("namedChilds(N3)")[0]);
		assertNotNull(lvResult);
		assertEquals("N3", ((Node) lvResult).getName());
	}

	public void testSetKeyPropertyToMap() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put("N1", new Node("N1"));
		lvMap.put("N2", new Node("N2"));
		assertEquals(2, lvMap.size());
		
		PathExecuter.setKeyProperty(lvMap, "N3", new Node("N3"));
		assertEquals(3, lvMap.size());
		
		Object lvResult = PathExecuter.getKeyProperty(lvMap, "N3");
		assertNotNull(lvResult);
		assertEquals("N3", ((Node) lvResult).getName());
	}

	public void testSetKeyPropertyToBean() throws Exception {
		Node lvNode = new Node("Test-Node");
		lvNode.getNamedChilds().put("N1", new Node("N1"));
		lvNode.getNamedChilds().put("N2", new Node("N2"));
		assertEquals(2, lvNode.getNamedChilds().size());
		
		PathExecuter.setNestedProperty(lvNode, "namedChilds(N3)", new Node("N3"));
		assertEquals(3, lvNode.getNamedChilds().size());		
		
		PathAction pv = PathParser.parse("namedChilds(N3)")[0];
		Object lvResult = PathExecuter.getNestedProperty(lvNode, pv);
		assertNotNull(lvResult);
		assertEquals("N3", ((Node) lvResult).getName());
	}
	
	public void testSetNestedSimplePropertyWithList() throws Exception {
		Node lvNode = new Node ("root-node");
		Node lvNodeChild1 = new Node ("node-1");
		Node lvNodeChild2 = new Node ("node-2");
		
		lvNode.getChilds().add(lvNodeChild1);
		lvNode.getChilds().add(lvNodeChild2);
		lvNode.getChilds().add(lvNodeChild1);
		
		lvNodeChild1.setParent(lvNode);
		lvNodeChild2.setParent(lvNode);
		assertEquals("root-node", lvNode.getName());
		assertEquals("node-1", lvNodeChild1.getName());
		
		
		PathExecuter.setNestedProperty(lvNode, "childs[0].name", "new-node-1");
		assertEquals("new-node-1", ((Node) lvNode.getChilds().get(0)).getName()); 
		assertEquals("new-node-1", ((Node) lvNode.getChilds().get(2)).getName());
		
		PathExecuter.setNestedProperty(lvNode, "childs[1].parent.name", "new-root-node");
		assertEquals("new-root-node", lvNode.getName());
		
		PathExecuter.setNestedProperty(lvNode, "childs[0].parent.childs[0].parent.name", "new-root-node-second");
		assertEquals("new-root-node-second", lvNode.getName());
	}

	public void testSetNestedSimplePropertyWithMap() throws Exception {
		Node lvNode = new Node ("root-node");
		Node lvNodeChild1 = new Node ("node-1");
		Node lvNodeChild2 = new Node ("node-2");
		
		lvNode.getNamedChilds().put("n1", lvNodeChild1);
		lvNode.getNamedChilds().put("n2", lvNodeChild2);
		lvNode.getNamedChilds().put("n1-1", lvNodeChild1);
		
		lvNodeChild1.setParent(lvNode);
		lvNodeChild2.setParent(lvNode);
		assertEquals("root-node", lvNode.getName());
		assertEquals("node-1", lvNodeChild1.getName());
		
		
		PathExecuter.setNestedProperty(lvNode, "namedChilds(n1).name", "new-node-1");
		assertEquals("new-node-1", ((Node) lvNode.getNamedChilds().get("n1")).getName()); 
		assertEquals("new-node-1", ((Node) lvNode.getNamedChilds().get("n1-1")).getName());
		
		PathExecuter.setNestedProperty(lvNode, "namedChilds(n2).parent.name", "new-root-node");
		assertEquals("new-root-node", lvNode.getName());
		
		PathExecuter.setNestedProperty(lvNode, "namedChilds(n1-1).parent.namedChilds(n1).parent.name", "new-root-node-second");
		assertEquals("new-root-node-second", lvNode.getName());
	}

	public void testSetNestedSimplePropertyWithMapWithKeyTypeNotString() throws Exception {
		Node lvNode = new Node ("root-node");
		Node lvNodeChild1 = new Node ("node-1");
		Node lvNodeChild2 = new Node ("node-2");
		
		lvNode.getNamedChilds().put(new Long(1), lvNodeChild1);
		lvNode.getNamedChilds().put(new Long(2), lvNodeChild2);
		
		lvNodeChild1.setParent(lvNode);
		lvNodeChild2.setParent(lvNode);
		assertEquals("root-node", lvNode.getName());
		assertEquals("node-1", lvNodeChild1.getName());
		
		
		PathExecuter.setNestedProperty(lvNode, "namedChilds(1).name", "new-node-1");
		assertEquals("new-node-1", ((Node) lvNode.getNamedChilds().get(new Long(1))).getName()); 
		
		PathExecuter.setNestedProperty(lvNode, "namedChilds(2).parent.name", "new-root-node");
		assertEquals("new-root-node", lvNode.getName());
	}

	public void testSetNestedPropertyWithInvalidType() throws Exception {
		try {
			PathExecuter.setNestedProperty("Dummy", new PathAction(-1), "Dummy-Value");
			fail("PathAction with type = -1 is invalid");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}
	}
	
	public void testGetNestedPropertyWithInvalidType() throws Exception {
		try {
			PathExecuter.getNestedProperty("Dummy", new PathAction(-1));
			fail("PathAction with type = -1 is invalid");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}
	}

	
	public void testGetSimplePropertyWithFail() throws Exception {
		try {
			PathExecuter.getSimpleProperty(new Node(), "bad-path");
			fail("bad-path is a not valid property from Node");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}		
	}
	
	public void testSetSimplePropertyWithFail() throws Exception {
		try {
			PathExecuter.setSimpleProperty(new Node(), "bad-path", "dummy-value");
			fail("bad-path is a not valid property from Node");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}		
	}
	
	public void testSetSimplePropertyWithFail2() throws Exception {
		try {
			PathExecuter.setSimpleProperty(new Node(), "name", new Long(1));
			fail("For property name is the type String and not Long");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}		
	}

	public void testGetIndexPropertyWithFail() throws Exception {
		try {
			PathExecuter.getIndexProperty(new Node(), 0);
			fail("Node is not a Collection");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}				
	}

	public void testSetIndexPropertyWithFail() throws Exception {
		try {
			PathExecuter.setIndexProperty(new Node(), 0, "dummy-value");
			fail("Node is not a Collection");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}				
	}

	public void testGetKeyPropertyWithFail() throws Exception {
		try {
			PathExecuter.getKeyProperty(new Node(), "dummy-key");
			fail("Node is not a Map");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}				
	}

	public void testSetKeyPropertyWithFail() throws Exception {
		try {
			PathExecuter.setKeyProperty(new Node(), "dummy-key", "dummy-value");
			fail("Node is not a Map");
		} catch (PathExecuteException e) {
			assertNotNull(e);
		}				
	}

	public void testGetKeyPropertyWithBadKey() throws Exception {
		Map lvMap = new Hashtable();
		lvMap.put(new Double(12.34), "12.34");
		assertEquals("12.34", PathExecuter.getKeyProperty(lvMap, new Double(12.34)));
		assertNull(PathExecuter.getKeyProperty(lvMap, new Double(34.56)));
	}
	
	public void testMapInList() throws Exception {
		List lvList = new ArrayList();
		Map lvMap = new Hashtable();
		lvMap.put("key", "value");
		lvList.add(lvMap);
		
		Object lvValue = PathExecuter.getNestedProperty(lvList, "[0].(key)");
		assertEquals("value", lvValue);
	}
	
	public void testListInList() throws Exception {
		List lvList = new ArrayList();
		List lvList2 = new Vector();
		lvList2.add("value1");
		lvList2.add("value2");
		lvList.add(lvList2);
		lvList.add("value0");
		
		Object lvValue = PathExecuter.getNestedProperty(lvList, "[0].[0]");
		assertEquals("value1", lvValue);
		lvValue = PathExecuter.getNestedProperty(lvList, "[0].[1]");
		assertEquals("value2", lvValue);
		lvValue = PathExecuter.getNestedProperty(lvList, "[1]");
		assertEquals("value0", lvValue);
	}

	public void testSetSimplePropertyBeanWithFieldProperty() throws Exception {
		DefaultMutableTreeNode lvNode = new DefaultMutableTreeNode();
		assertNull(lvNode.getUserObject());
		// add class
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		PathExecuter.setSimpleProperty(lvNode, "userObject", "Test-Node");
		assertEquals(lvNode.getUserObject(), "Test-Node");
		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

	public void testGetSimplePropertyBeanWithFieldProperty() throws Exception {
		DefaultMutableTreeNode lvNode = new DefaultMutableTreeNode("Root");
		assertEquals("Root", lvNode.getUserObject());
		// add class
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		Object lvValue = PathExecuter.getSimpleProperty(lvNode, "userObject");
		assertEquals("Root", lvValue);
		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

	public void testGetNestedPropertyBeanWithFieldProperty() throws Exception {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);
		assertEquals("Root", root.getUserObject());
		assertEquals("Child", ((DefaultMutableTreeNode) root.getChildAt(0)).getUserObject());
		// add class
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		Object lvValue = PathExecuter.getNestedProperty(root, "children[0]");
		assertEquals("Child", ((DefaultMutableTreeNode) lvValue).getUserObject());
		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

	public void testSetNestedPropertyBeanWithFieldProperty() throws Exception {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);
		assertEquals("Root", root.getUserObject());
		assertEquals("Child", ((DefaultMutableTreeNode) root.getChildAt(0)).getUserObject());
		// add class
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		PathExecuter.setNestedProperty(root, "children[0].userObject", "Child-NEW");
		assertEquals("Child-NEW", ((DefaultMutableTreeNode) root.getChildAt(0)).getUserObject());
		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

}
