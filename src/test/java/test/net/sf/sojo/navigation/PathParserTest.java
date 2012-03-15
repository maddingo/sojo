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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.navigation.PathAction;
import net.sf.sojo.navigation.PathExecuter;
import net.sf.sojo.navigation.PathParseException;
import net.sf.sojo.navigation.PathParser;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;

public class PathParserTest extends TestCase {
	
	public void testParseWithNullPath() throws Exception {
		PathAction lvAction[] = PathParser.parse(null);
		assertNotNull(lvAction);
		assertEquals(0, lvAction.length);
	}
	
	public void testParseWithEmptyPath() throws Exception {
		PathAction lvAction[] = PathParser.parse("");
		assertNotNull(lvAction);
		assertEquals(0, lvAction.length);
		
		lvAction = PathParser.parse(" ");
		assertNotNull(lvAction);
		assertEquals(0, lvAction.length);
	}
	
	public void testParseWithEmptyPathButOnePoint() throws Exception {
		PathAction lvAction[] = PathParser.parse(".");
		assertNotNull(lvAction);
		assertEquals(0, lvAction.length);
		
		lvAction = PathParser.parse(" . ");
		assertNotNull(lvAction);
		assertEquals(0, lvAction.length);
	}
	
	public void testParseWithSimpleAction() throws Exception {
		PathAction lvAction[] = PathParser.parse("name");
		assertNotNull(lvAction);
		assertEquals(1, lvAction.length);
		assertEquals("name", lvAction[0].getPath());
		assertEquals("name", lvAction[0].getProperty());
		assertNull(lvAction[0].getKey());
		assertEquals(PathAction.ACTION_TYPE_SIMPLE, lvAction[0].getType());
	}

	public void testParseWithMoreThanOneBracketKey() throws Exception {
		try {
			PathParser.parse("fail(aa(1)");
			fail("It is only one open bracket allowed.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
		
		try {
			PathParser.parse("fail)aa(1)");
			fail("It is only one closed bracket allowed.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
		try {
			PathParser.parse("failaa)1(");
			fail("It is only one open bracket allowed.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
		PathAction lvAction[] = PathParser.parse("long(2).path(1).name");
		assertEquals(3, lvAction.length);
	}
	
	public void testParseWithMoreThanOneBracketIndex() throws Exception {
		try {
			PathParser.parse("fail[aa[1]");
			fail("It is only one open bracket allowed.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
		
		try {
			PathParser.parse("fail]aa[1]");
			fail("It is only one closed bracket allowed.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
		try {
			PathParser.parse("failaa]1[");
			fail("It is only one open bracket allowed.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
		PathAction lvAction[] = PathParser.parse("long[2].path[1].name");
		assertEquals(3, lvAction.length);
	}


	public void testParseWithIndexAction() throws Exception {
		PathAction lvAction[] = PathParser.parse("address[0].city");
		assertNotNull(lvAction);
		assertEquals(2, lvAction.length);
		assertEquals("address[0]", lvAction[0].getPath());
		assertEquals("address", lvAction[0].getProperty());
		assertEquals(0, lvAction[0].getIndex());
		assertEquals(PathAction.ACTION_TYPE_INDEX, lvAction[0].getType());
		
		assertEquals("city", lvAction[1].getPath());
		assertEquals("city", lvAction[1].getProperty());
		assertNull(lvAction[1].getKey());
		assertEquals(PathAction.ACTION_TYPE_SIMPLE, lvAction[1].getType());
	}
	
	public void testParseWithIndexActionInValid() throws Exception {
		try {
			PathParser.parse("address0].city");
			fail("Missing open bracket.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
	}


	public void testParseWithKeyAction() throws Exception {
		PathAction lvAction[] = PathParser.parse("address(London).postcode");
		assertNotNull(lvAction);
		assertEquals(2, lvAction.length);
		assertEquals("address(London)", lvAction[0].getPath());
		assertEquals("address", lvAction[0].getProperty());
		assertEquals("London", lvAction[0].getKey());
		assertEquals(PathAction.ACTION_TYPE_KEY, lvAction[0].getType());
		
		assertEquals("postcode", lvAction[1].getPath());
		assertEquals("postcode", lvAction[1].getProperty());
		assertNull(lvAction[1].getKey());
		assertEquals(PathAction.ACTION_TYPE_SIMPLE, lvAction[1].getType());
	}
	
	public void testParseWithKeyActionInValid() throws Exception {
		try {
			PathParser.parse("addressLondon).postcode");
			fail("Missing open bracket.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
	}

	public void testIndexIsNotInteger() throws Exception {
		try {
			PathParser.parse("get[a]");
			fail("Index must be a integer.");
		} catch (PathParseException e) {
			assertNotNull(e);
		}
	}

	public void testIndexIsEmpty() throws Exception {
		PathAction[] lvActions = PathParser.parse("get[]");
		assertEquals(1, lvActions.length);
		assertEquals("get", lvActions[0].getProperty());
		assertEquals("get[]", lvActions[0].getPath());
		assertEquals(-1, lvActions[0].getIndex());
		assertEquals(PathAction.ACTION_TYPE_INDEX, lvActions[0].getType());
		assertNull(lvActions[0].getKey());
	}
	
	public void testIndexWithWhitespaces() throws Exception {
		PathAction[] lvActions = PathParser.parse("get[ 1 ]");
		assertEquals(1, lvActions.length);
		assertEquals("get", lvActions[0].getProperty());
		assertEquals("get[ 1 ]", lvActions[0].getPath());
		assertEquals(1, lvActions[0].getIndex());
		assertEquals(PathAction.ACTION_TYPE_INDEX, lvActions[0].getType());
		assertNull(lvActions[0].getKey());		
	}
	
		
	public void testKeyIsEmpty() throws Exception {
		PathAction[] lvActions = PathParser.parse("get()");
		assertEquals(1, lvActions.length);
		assertEquals("get", lvActions[0].getProperty());
		assertEquals("get()", lvActions[0].getPath());
		assertEquals(-1, lvActions[0].getIndex());
		assertEquals(PathAction.ACTION_TYPE_KEY, lvActions[0].getType());
		assertEquals("", lvActions[0].getKey());
	}
	
	public void testKeyWithWhitespaces() throws Exception {
		PathAction[] lvActions = PathParser.parse("get( 1 )");
		assertEquals(1, lvActions.length);
		assertEquals("get", lvActions[0].getProperty());
		assertEquals("get( 1 )", lvActions[0].getPath());
		assertEquals(-1, lvActions[0].getIndex());
		assertEquals(PathAction.ACTION_TYPE_KEY, lvActions[0].getType());
		assertEquals(" 1 ", lvActions[0].getKey());		
	}
	
	
	public void testExceuteSimpleFromObject() throws Exception {
		PathAction lvAction[] = PathParser.parse("name");
		Car lvCar = new Car("Ferrari");
		Object lvResult = PathExecuter.getNestedProperty(lvCar, lvAction[0]);
		assertEquals(lvCar.getName(), lvResult);
	}

	
	public void testExceuteSimpleFromConvertedObject() throws Exception {
		PathAction lvAction[] = PathParser.parse("name");
		Car lvCar = new Car("Ferrari");
		
		Converter c = new Converter();
		c.addConversion(new ComplexBean2MapConversion());
		Object lvSimple = c.convert(lvCar);
		assertNotNull(lvSimple);
		Map<?, ?> lvMap = (Map<?, ?>) lvSimple;
		assertEquals(lvCar.getName(), lvMap.get("name"));
		Object lvResult = PathExecuter.getNestedProperty(lvSimple, lvAction[0]);
		assertEquals(lvCar.getName(), lvResult);
	}

	public void testExceuteIndexFromList() throws Exception {
		PathAction lvAction = new PathAction();
		lvAction.setIndex(0);
		lvAction.setType(PathAction.ACTION_TYPE_INDEX);
		
		List<String> lvList = new ArrayList<String>();
		lvList.add("TestString_1");
		Object lvResult = PathExecuter.getNestedProperty(lvList, lvAction);
		assertEquals("TestString_1", lvResult);
		
		lvList.add("TestString_2");
		lvList.add("TestString_3");
		lvAction.setIndex(2);
		lvResult = PathExecuter.getNestedProperty(lvList, lvAction);
		assertEquals("TestString_3", lvResult);
	}

	public void testExceuteIndexFromListWithPropertyName() throws Exception {
		PathAction lvAction[] = PathParser.parse("children[0]");
		Node lvNode = new Node("Node");
		lvNode.getChildren().add("TestString_1");
		lvNode.getChildren().add("TestString_2");
		lvNode.getChildren().add("TestString_3");
		lvNode.getChildren().add("TestString_4");
		Object lvResult = PathExecuter.getNestedProperty(lvNode, lvAction[0]);
		assertEquals("TestString_1", lvResult);
		
		lvAction = PathParser.parse("children[3]");
		lvResult = PathExecuter.getNestedProperty(lvNode, lvAction[0]);
		assertEquals("TestString_4", lvResult);
	}
	
	public void testExceuteIndexFromSet() throws Exception {
		PathAction lvAction = new PathAction();
		lvAction.setIndex(0);
		lvAction.setType(PathAction.ACTION_TYPE_INDEX);
		
		Set<String> lvSet = new LinkedHashSet<String>();
		lvSet.add("TestString_1");
		Object lvResult = PathExecuter.getNestedProperty(lvSet, lvAction);
		assertEquals("TestString_1", lvResult);
		
		lvSet.add("TestString_2");
		lvSet.add("TestString_3");
		lvAction.setIndex(2);
		lvResult = PathExecuter.getNestedProperty(lvSet, lvAction);
		assertEquals("TestString_3", lvResult);
	}
	
	public void testExceuteIndexFromSetWithPropertyName() throws Exception {
		PathAction lvAction[] = PathParser.parse("addresses[0]");
		Customer c = new Customer();
		c.setAddresses(new LinkedHashSet<Object>());
		c.getAddresses().add("TestString_1");
		Object lvResult = PathExecuter.getNestedProperty(c, lvAction[0]);
		assertEquals("TestString_1", lvResult);
		
		c.getAddresses().add("TestString_2");
		c.getAddresses().add("TestString_3");
		lvAction = PathParser.parse("addresses[2]");
		lvResult = PathExecuter.getNestedProperty(c, lvAction[0]);
		assertEquals("TestString_3", lvResult);
	}

	public void testExceuteKey() throws Exception {
		PathAction lvAction = new PathAction();
		lvAction.setKey("Key_1");
		lvAction.setType(PathAction.ACTION_TYPE_KEY);
		
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("Key_1", "TestString_1");
		Object lvResult = PathExecuter.getNestedProperty(lvMap, lvAction);
		assertEquals("TestString_1", lvResult);
		
		lvMap.put("Key_2", "TestString_2");
		lvMap.put("Key_3", "TestString_3");
		lvAction.setKey("Key_3");
		lvResult = PathExecuter.getNestedProperty(lvMap, lvAction);
		assertEquals("TestString_3", lvResult);
	}

	public void testExceuteKeyWithPropertyName() throws Exception {
		PathAction lvAction[] = PathParser.parse("namedChildren(Key_1)");
		Node lvNode = new Node();
		lvNode.getNamedChildren().put("Key_1", "Test_String_1");
		Object lvResult = PathExecuter.getNestedProperty(lvNode, lvAction[0]);
		assertEquals("Test_String_1", lvResult);
		
		lvNode.getNamedChildren().put("Key_2", "Test_String_2");
		lvNode.getNamedChildren().put("Key_3", "Test_String_3");
		lvAction = PathParser.parse("namedChildren(Key_3)");
		lvResult = PathExecuter.getNestedProperty(lvNode, lvAction[0]);
		assertEquals("Test_String_3", lvResult);
	}
	
	public void testExceuteKeyWithNestedPropertyNames() throws Exception {
		Node lvNode = new Node("Node_1");
		lvNode.getNamedChildren().put("Key_1", lvNode);
		Object lvResult = PathExecuter.getNestedProperty(lvNode, "namedChildren(Key_1).name");
		assertEquals("Node_1", lvResult);
		
		lvNode.getNamedChildren().put("Key_2", new Node("Node_2"));
		lvNode.getNamedChildren().put("Key_3", new Node("Node_3"));
		lvResult = PathExecuter.getNestedProperty(lvNode, "namedChildren(Key_3).name");
		assertEquals("Node_3", lvResult);

		Node lvNode4 = new Node("Node_4");
		lvNode4.setParent(lvNode);
		lvNode.getNamedChildren().put("Key_3", lvNode4);
		lvResult = PathExecuter.getNestedProperty(lvNode, "namedChildren(Key_3).parent.name");
		assertEquals("Node_1", lvResult);

	}

	public void testWithOutPropertyName() throws Exception {
		PathAction pa = PathParser.getActionByPath("(1)");
		assertNull(pa.getProperty());
		assertEquals(PathAction.ACTION_TYPE_KEY, pa.getType());
		assertEquals(-1, pa.getIndex());
		assertEquals("1", pa.getKey());
	}

	public void testWithOutPropertyName2() throws Exception {
		PathAction pa = PathParser.getActionByPath("[1]");
		assertNull(pa.getProperty());
		assertEquals(PathAction.ACTION_TYPE_INDEX, pa.getType());
		assertEquals(1, pa.getIndex());
		assertEquals(null, pa.getKey());
	}

}
