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

import junit.framework.TestCase;
import net.sf.sojo.common.CompareResult;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.conversion.SimpleFormatConversion;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.optional.filter.attributes.ClassPropertyFilterHanlderForAttributes;
import net.sf.sojo.util.StackTraceElementWrapper;
import net.sf.sojo.util.Util;
import test.net.sf.sojo.model.*;
import test.net.sf.sojo.optional.filter.model.Account;
import test.net.sf.sojo.optional.filter.model.MyAnnotationClass;
import test.net.sf.sojo.optional.filter.model.Person;

import javax.swing.tree.DefaultMutableTreeNode;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ObjectUtilTest extends TestCase {

	public void testCopySimple() {
		Object lvResult = new ObjectUtil().copy("Test-Copy-String");
		assertEquals("Test-Copy-String", lvResult);
		
		lvResult = new ObjectUtil().copy(789);
		assertEquals(789, lvResult);

		Date lvDate = new Date();
		lvResult = new ObjectUtil().copy(lvDate);
		assertEquals(lvDate, lvResult);
	}
	
	public void testCopyBean_Node() {
		Node lvNode = new Node("Test-Node");
		Node lvNodeCopy = (Node) new ObjectUtil().copy(lvNode);
		assertEquals("Test-Node", lvNodeCopy.getName());
		assertEquals(new ArrayList<>(), lvNodeCopy.getChildren());
		assertEquals(new HashMap<>(), lvNodeCopy.getNamedChildren());
		assertNull(lvNodeCopy.getParent());
		
	}
	
	public void testCopyBean_NodeWithParent() {
		Node lvNode = new Node("Test-Node");
		Node lvNodeParent = new Node("Parent-Node");
		lvNode.setParent(lvNodeParent);
		Node lvNodeCopy = (Node) new ObjectUtil().copy(lvNode);
		assertNotNull(lvNodeCopy.getParent());
		assertEquals("Parent-Node", lvNodeCopy.getParent().getName());
		assertEquals(new ArrayList<>(), lvNodeCopy.getParent().getChildren());
		assertEquals(new HashMap<>(), lvNodeCopy.getParent().getNamedChildren());
		
	}
	
	public void testCopyBean_NodeWithChildren() {
		Node lvNode = new Node("Test-Node");
		Node lvNodeChild1 = new Node("Test-Node-Child1");
		Node lvNodeChild2 = new Node("Test-Node-Child2");
		lvNode.getChildren().add(lvNodeChild1);
		lvNode.getChildren().add(lvNodeChild2);
		lvNode.getChildren().add(lvNode);
		
		Node lvNodeCopy = (Node) new ObjectUtil().copy(lvNode);
		assertEquals(3, lvNodeCopy.getChildren().size());
		assertEquals("Test-Node-Child1", ((Node) lvNodeCopy.getChildren().get(0)).getName());
		assertEquals("Test-Node-Child2", ((Node) lvNodeCopy.getChildren().get(1)).getName());
		assertEquals(lvNodeCopy, lvNodeCopy.getChildren().get(2));
	}


	public void testCopyBean_Customer() {
		Customer lvCustomer = new Customer("MyName");
		Date lvDate = new Date();
		lvCustomer.setBirthDate(lvDate);
		lvCustomer.setPartner(new Customer[] { new Customer("Partner"), lvCustomer } );
		
		Customer lvCustomerCopy = (Customer) new ObjectUtil().copy(lvCustomer);
		assertEquals("MyName", lvCustomerCopy.getLastName());
		assertEquals(Collections.<Address>emptySet(), lvCustomerCopy.getAddresses());
		assertEquals(lvDate, lvCustomerCopy.getBirthDate());
		assertEquals(2, lvCustomerCopy.getPartner().length);
		assertEquals("Partner", ((Customer) lvCustomerCopy.getPartner()[0]).getLastName());
		assertEquals(lvCustomerCopy, lvCustomerCopy.getPartner()[1]);
		assertSame(lvCustomerCopy, lvCustomerCopy.getPartner()[1]);
	}
	
	public void testEqualsWithNullValue() {
		assertFalse(new ObjectUtil().equals(null, "String-Other"));
		assertFalse(new ObjectUtil().equals("String", null));
		assertFalse(new ObjectUtil().equals(null, null));
	}
	
	public void testEqualsSimple() {
		assertTrue(new ObjectUtil().equals("String", "String"));
		assertTrue(new ObjectUtil().equals(2, 2));
		Date lvDate = new Date();
		assertTrue(new ObjectUtil().equals(lvDate, lvDate));
		assertTrue(new ObjectUtil().equals(23.45, 23.45));
		
		assertFalse(new ObjectUtil().equals("String", "String-Other"));
		assertFalse(new ObjectUtil().equals(2, 333));
		assertFalse(new ObjectUtil().equals(2, 2L));
		assertFalse(new ObjectUtil().equals(2, new Node()));
		assertFalse(new ObjectUtil().equals(new Node(), 2));
		assertFalse(new ObjectUtil().equals(23.45, 98.76));
	}
	
	public void testEqualsSimpleSameNodeBean() {
		Node lvNode = new Node("Test-Node");
		assertTrue(new ObjectUtil().equals(lvNode, lvNode));
	}

	public void testEqualsBeanWithFilter() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar1 = new Car("MyCar");
		lvCar1.setDescription("special car");

		Car lvCar2 = new Car("MyCar");
		assertFalse(lvObjectUtil.equals(lvCar2, lvCar1));

		
		ClassPropertyFilter cpf = new ClassPropertyFilter(Car.class, new String [] { "description" });
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(cpf));

		assertTrue(lvObjectUtil.equals(lvCar2, lvCar1));
	}


	public void testEqualsSimpleDifferentCarBeanWithDiffNumberProperties() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar1 = new Car("MyCar");
		lvCar1.setDescription("special car");

		Car lvCar2 = new Car("MyCar");
		
		assertFalse(lvObjectUtil.equals(lvCar2, lvCar1));
		assertFalse(lvObjectUtil.equals(lvCar1, lvCar2));
	}

	public void testEqualsSimpleDifferentCarBeanWithEqualsNumberProperties() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar1 = new Car("MyCar");
		lvCar1.setDescription("special car");

		Car lvCar2 = new Car("MyCar");
		lvCar2.setBuild(new Date());
		
		assertFalse(lvObjectUtil.equals(lvCar2, lvCar1));
		
		assertFalse(lvObjectUtil.equals(lvCar1, lvCar2));
	}

	public void testCompareAllSimpleDifferentCarBeanWithDiffNumberProperties() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar1 = new Car("MyCar");
		lvCar1.setDescription("special car");

		Car lvCar2 = new Car("MyCar");
		
		CompareResult[] cr = lvObjectUtil.compareAll(lvCar2, lvCar1);
		assertEquals(1, cr.length);
		assertEquals("description", cr[0].differentPath);
		assertNull(cr[0].differentValue1);
		assertEquals("special car", cr[0].differentValue2);

		cr = lvObjectUtil.compareAll(lvCar1, lvCar2);
		assertEquals(1, cr.length);
		assertEquals("description", cr[0].differentPath);
		assertEquals("special car", cr[0].differentValue1);
		assertNull(cr[0].differentValue2);
	}

	public void testCompareAllSimpleDifferentCarBeanWithEqualsNumberProperties() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar1 = new Car("MyCar");
		lvCar1.setDescription("special car");

		Car lvCar2 = new Car("MyCar");
		Date lvDate = new Date();
		lvCar2.setBuild(lvDate);
		
		CompareResult[] lvResult = lvObjectUtil.compareAll(lvCar1, lvCar2);
		assertEquals(2, lvResult.length);

		for (CompareResult cr : lvResult) {
			if ("description".equals(cr.differentPath)) {
				assertEquals("special car", cr.differentValue1);
				assertNull(cr.differentValue2);
			} else if ("build".equals(cr.differentPath)) {
				assertNull(cr.differentValue1);
				assertEquals(lvDate, cr.differentValue2);
			} else {
				fail("Unexpected result: " + cr.differentPath);
			}
		}

		lvResult = lvObjectUtil.compareAll(lvCar2, lvCar1);

		assertEquals(2, lvResult.length);
		for (CompareResult cr : lvResult) {
			if ("description".equals(cr.differentPath)) {
				assertEquals("special car", cr.differentValue2);
				assertNull(cr.differentValue1);
			} else if ("build".equals(cr.differentPath)) {
				assertNull(cr.differentValue2);
				assertEquals(lvDate, cr.differentValue1);
			} else {
				fail("Unexpected result: " + cr.differentPath);
			}
		}
	}

	public void testEqualsDifferentNodeBean() {
		Node lvNode1 = new Node("Test-Node");
		lvNode1.setParent(lvNode1);
		Node lvNode2 = new Node("Test-Node");
		assertFalse(new ObjectUtil().equals(lvNode1, lvNode2));
	}

	public void testEqualsSimpleDifferentNodeBean() {
		Node lvNode1 = new Node("Test-Node");
		Node lvNode2 = new Node("Test-Node");
		assertTrue(new ObjectUtil().equals(lvNode1, lvNode2));
	}
	
	public void testEqualsSimpleDifferentNodeBeanWithPathAndValues() {
		Node lvNode1 = new Node("Test-Node");
		Node lvNode2 = new Node("Test-Node-OTHER");
		assertFalse(new ObjectUtil().equals(lvNode1, lvNode2));
	}

	
	public void testEqualsSimpleDifferentNodeBeanWithChildren() {
		Node lvNode1 = new Node("Test-Node");
		lvNode1.getChildren().add(new Node("child-1"));
		lvNode1.getChildren().add(new Node("child-2"));
		Node lvNode2 = new Node("Test-Node");
		lvNode2.getChildren().add(new Node("child-1"));
		lvNode2.getChildren().add(new Node("child-2"));

		assertTrue(new ObjectUtil().equals(lvNode1, lvNode2));
	}

	public void testNotEqualsSimpleDifferentNodeBeanWithChildren() {
		Node lvNode1 = new Node("Test-Node");
		lvNode1.getChildren().add(new Node("child-1"));
		lvNode1.getChildren().add(new Node("child-2"));
		Node lvNode2 = new Node("Test-Node");
		lvNode2.getChildren().add(new Node("child-1"));
		lvNode2.getChildren().add(new Node("child-2-X"));

		assertFalse(new ObjectUtil().equals(lvNode1, lvNode2));
	}

	public void testNotEqualsSimpleDifferentNodeBeanWithChildren2() {
		Node lvNode1 = new Node("Test-Node");
		lvNode1.getChildren().add(new Node("child-1"));
		lvNode1.getChildren().add(new Node("child-2"));
		lvNode1.getNamedChildren().put("key-1", "value-1");
		lvNode1.getNamedChildren().put("key-2", "value-2");
		Node lvNode2 = new Node("Test-Node");
		lvNode2.getChildren().add(new Node("child-1"));
		lvNode2.getChildren().add(new Node("child-2"));
		lvNode2.getNamedChildren().put("key-1", "value-1");
		lvNode2.getNamedChildren().put("key-2", "value-2-X");

		assertFalse(new ObjectUtil().equals(lvNode1, lvNode2));
		assertFalse(new ObjectUtil().equals(lvNode2, lvNode1));
		
		assertTrue(new ObjectUtil().equals(lvNode1, lvNode1));
		assertTrue(new ObjectUtil().equals(lvNode2, lvNode2));
	}

	public void testNotEqualsSimpleDifferentNodeBeanWithChildren3() {
		Node lvNode1 = new Node("Test-Node");
		lvNode1.getChildren().add(new Node("child-1"));
		lvNode1.getChildren().add(new BigDecimal("23.006"));
		Node lvNode2 = new Node("Test-Node");
		lvNode2.getChildren().add(new BigDecimal("23.006"));
		lvNode2.getChildren().add(new Node("child-1"));

		assertFalse(new ObjectUtil().equals(lvNode1, lvNode2));
		assertFalse(new ObjectUtil().equals(lvNode2, lvNode1));
	}

	public void testNotEqualsSimpleDifferentNodeBeanWithChildren4() {
		Node lvNode1 = new Node("Test-Node");
		lvNode1.getChildren().add(new Node("child-1"));
		lvNode1.getChildren().add(new BigDecimal("23.006"));
		Node lvNode2 = new Node("Test-Node");
		lvNode2.getChildren().add(new Node("child-1"));
		lvNode2.getChildren().add(new BigDecimal("23.007"));


		assertFalse(new ObjectUtil().equals(lvNode1, lvNode2));
		assertFalse(new ObjectUtil().equals(lvNode2, lvNode1));
	}
	
	public void testEqualsCar() {
		Car lvCar1 = new Car();
		lvCar1.setDescription("Description");
		lvCar1.setName("BMW");
		
		Car lvCar2 = new Car();
		lvCar2.setDescription("Description");
		lvCar2.setName("BMW");
		
		assertTrue(new ObjectUtil().equals(lvCar1, lvCar2));
		assertTrue(new ObjectUtil().equals(lvCar2, lvCar1));
	}

	public void testEqualsCar2() {
		Car lvCar1 = new Car();
		lvCar1.setDescription("Description");
		lvCar1.setName("BMW");
		lvCar1.setProperties(new Properties());
		lvCar1.getProperties().put("key1", new Date(1234567890));
		lvCar1.getProperties().put("key2", new Time(1234567890));
		
		Car lvCar2 = new Car();
		lvCar2.setDescription("Description");
		lvCar2.setName("BMW");
		lvCar2.setProperties(new Properties());
		lvCar2.getProperties().put("key1", new Date(1234567890));
		lvCar2.getProperties().put("key2", new Time(1234567890));
		
		assertTrue(new ObjectUtil().equals(lvCar1, lvCar2));
		assertTrue(new ObjectUtil().equals(lvCar2, lvCar1));
	}

	public void testEqualsDifferentCar() {
		Car lvCar1 = new Car();
		lvCar1.setDescription("Description");
		lvCar1.setName("BMW");
		
		Car lvCar2 = new Car();
		lvCar2.setDescription("Description");
		lvCar2.setName("Audi");
		
		assertFalse(new ObjectUtil().equals(lvCar1, lvCar2));
		assertFalse(new ObjectUtil().equals(lvCar2, lvCar1));
	}

	public void testEqualsDifferentCar2() {
		Car lvCar1 = new Car();
		lvCar1.setDescription("Description");
		lvCar1.setName("BMW");
		lvCar1.setProperties(new Properties());
		lvCar1.getProperties().put("key1", new Date(1234567890));
		lvCar1.getProperties().put("key2", new Time(1234567890));
		
		Car lvCar2 = new Car();
		lvCar2.setDescription("Description");
		lvCar2.setName("BMW");
		lvCar2.setProperties(new Properties());
		lvCar2.getProperties().put("key1", new Date(123));
		lvCar2.getProperties().put("key2", new Time(1234567890));
		
		assertFalse(new ObjectUtil().equals(lvCar1, lvCar2));
		assertFalse(new ObjectUtil().equals(lvCar2, lvCar1));
	}
	
	public void testEqualsList() {
		List<Comparable<?>> lvList1 = new ArrayList<>();
		lvList1.add("Sojo");
		lvList1.add(4711);
		lvList1.add(new Double("47.11"));
		lvList1.add(new Date(987654321));
		lvList1.add('D');
		lvList1.add(Boolean.TRUE);

		List<Comparable<?>> lvList2 = new ArrayList<>();
		lvList2.add("Sojo");
		lvList2.add(4711);
		lvList2.add(new Double("47.11"));
		lvList2.add(new Date(987654321));
		lvList2.add('D');
		lvList2.add(Boolean.TRUE);

		assertTrue(new ObjectUtil().equals(lvList1, lvList2));
		assertTrue(new ObjectUtil().equals(lvList2, lvList1));
	}
	
	public void testEqualsDifferentListBoolean() {
		List<Comparable<?>> lvList1 = new ArrayList<>();
		lvList1.add("Sojo");
		lvList1.add(4711);
		lvList1.add(new Double("47.11"));
		lvList1.add(new Date(987654321));
		lvList1.add('D');
		lvList1.add(Boolean.TRUE);

		List<Comparable<?>> lvList2 = new ArrayList<>();
		lvList2.add("Sojo");
		lvList2.add(4711);
		lvList2.add(new Double("47.11"));
		lvList2.add(new Date(987654321));
		lvList2.add('D');
		lvList2.add(Boolean.FALSE);

		assertFalse(new ObjectUtil().equals(lvList1, lvList2));
		assertFalse(new ObjectUtil().equals(lvList2, lvList1));
	}

	public void testEqualsDifferentListCharacter() {
		List<Comparable<?>> lvList1 = new ArrayList<>();
		lvList1.add("Sojo");
		lvList1.add(4711);
		lvList1.add(new Double("47.11"));
		lvList1.add(new Date(987654321));
		lvList1.add('D');
		lvList1.add(Boolean.TRUE);

		List<Comparable<?>> lvList2 = new ArrayList<>();
		lvList2.add("Sojo");
		lvList2.add(4711);
		lvList2.add(new Double("47.11"));
		lvList2.add(new Date(987654321));
		lvList2.add('d');
		lvList2.add(Boolean.TRUE);

		assertFalse(new ObjectUtil().equals(lvList1, lvList2));
		assertFalse(new ObjectUtil().equals(lvList2, lvList1));
	}

	public void testEqualsDifferentListDouble() {
		List<Comparable<?>> lvList1 = new ArrayList<>();
		lvList1.add("Sojo");
		lvList1.add(4711);
		lvList1.add(new Double("47.11"));
		lvList1.add(new Date(987654321));
		lvList1.add('D');
		lvList1.add(Boolean.TRUE);

		List<Comparable<?>> lvList2 = new ArrayList<>();
		lvList2.add("Sojo");
		lvList2.add(4711);
		lvList2.add(new Double("47.111"));
		lvList2.add(new Date(987654321));
		lvList2.add('D');
		lvList2.add(Boolean.TRUE);

		assertFalse(new ObjectUtil().equals(lvList1, lvList2));
		assertFalse(new ObjectUtil().equals(lvList2, lvList1));
	}
	
	public void testEqualsMap() {
		Map<Comparable<?>, Comparable<?>> lvMap1 = new HashMap<>();
		lvMap1.put("sojo", "sojo");
		lvMap1.put(4711, 4711);
		lvMap1.put(new Double("47.11"), new Double("47.11"));
		lvMap1.put(new Date(987654321), new Date(987654321));
		
		Map<Comparable<?>, Comparable<?>> lvMap2 = new HashMap<>();
		lvMap2.put("sojo", "sojo");
		lvMap2.put(4711, 4711);
		lvMap2.put(new Double("47.11"), new Double("47.11"));
		lvMap2.put(new Date(987654321), new Date(987654321));

		assertTrue(new ObjectUtil().equals(lvMap1, lvMap2));
		assertTrue(new ObjectUtil().equals(lvMap2, lvMap1));
	}

	public void testEqualsMapDifferentIntegerKey() {
		Map<Comparable<?>, Comparable<?>> lvMap1 = new HashMap<>();
		lvMap1.put("sojo", "sojo");
		lvMap1.put(471199, 4711);
		lvMap1.put(new Double("47.11"), new Double("47.11"));
		lvMap1.put(new Date(987654321), new Date(987654321));
		
		Map<Comparable<?>, Comparable<?>> lvMap2 = new HashMap<>();
		lvMap2.put("sojo", "sojo");
		lvMap2.put(4711, 4711);
		lvMap2.put(new Double("47.11"), new Double("47.11"));
		lvMap2.put(new Date(987654321), new Date(987654321));

		assertFalse(new ObjectUtil().equals(lvMap1, lvMap2));
		assertFalse(new ObjectUtil().equals(lvMap2, lvMap1));
	}

	public void testEqualsMapDifferentDateValue() {
		Map<Comparable<?>, Comparable<?>> lvMap1 = new HashMap<>();
		lvMap1.put("sojo", "sojo");
		lvMap1.put(4711, 4711);
		lvMap1.put(new Double("47.11"), new Double("47.11"));
		lvMap1.put(new Date(987654321), new Date(987654));
		
		Map<Comparable<?>, Comparable<?>> lvMap2 = new HashMap<>();
		lvMap2.put("sojo", "sojo");
		lvMap2.put(4711, 4711);
		lvMap2.put(new Double("47.11"), new Double("47.11"));
		lvMap2.put(new Date(987654321), new Date(987654321));

		assertFalse(new ObjectUtil().equals(lvMap1, lvMap2));
		assertFalse(new ObjectUtil().equals(lvMap2, lvMap1));
	}

	public void testCompareSimple() {
		CompareResult lvResult = new ObjectUtil().compare("aaa", "aaa");
		assertNull(lvResult);
		
		lvResult = new ObjectUtil().compare("aaa", "bbb");
		assertNotNull(lvResult);
		assertEquals("", lvResult.differentPath);
		assertEquals("aaa", lvResult.differentValue1);
		assertEquals("bbb", lvResult.differentValue2);
		assertEquals(1, lvResult.numberOfRecursion);
	}
	
	public void testCompareSimpleNodeBean() {
		Node lvNode1 = new Node("Test-Node");
		Node lvNode2 = new Node("Test-Node");
		CompareResult lvResult = new ObjectUtil().compare(lvNode1, lvNode2);
		assertNull(lvResult);

		lvNode2 = new Node("Test-Node-OTHER");
		lvResult = new ObjectUtil().compare(lvNode1, lvNode2);
		assertNotNull(lvResult);
		assertEquals("name", lvResult.differentPath);
		assertEquals("Test-Node", lvResult.differentValue1);
		assertEquals("Test-Node-OTHER", lvResult.differentValue2);
		assertEquals(1, lvResult.numberOfRecursion);
	}
	
	public void testCompareSimpleCarBean() {
		Car lvCar1 = new Car("BMW");
		lvCar1.setBuild(new Date(987654321));
		lvCar1.setDescription("my car");
		lvCar1.setProperties(new Properties());
		lvCar1.getProperties().put("key", "value");
		Car lvCar2 = new Car("BMW");
		lvCar2.setBuild(new Date(987654321));
		lvCar2.setDescription("my car");
		lvCar2.setProperties(new Properties());
		lvCar2.getProperties().put("key", "value");
		CompareResult lvResult = new ObjectUtil().compare(lvCar1, lvCar2);
		assertNull(lvResult);

		lvCar2.getProperties().put("key", "value-NEW");
		lvResult = new ObjectUtil().compare(lvCar1, lvCar2);
		assertNotNull(lvResult);
		assertEquals("properties(key)", lvResult.differentPath);
		assertEquals("value", lvResult.differentValue1);
		assertEquals("value-NEW", lvResult.differentValue2);
		assertEquals(1, lvResult.numberOfRecursion);

		lvResult = new ObjectUtil().compare(lvCar2, lvCar1);
		assertNotNull(lvResult);
		assertEquals("properties(key)", lvResult.differentPath);
		assertEquals("value-NEW", lvResult.differentValue1);
		assertEquals("value", lvResult.differentValue2);
		assertEquals(1, lvResult.numberOfRecursion);
	}

	public void testCompareAllSimpleCarBean() {
		Car lvCar1 = new Car("BMW");
		lvCar1.setBuild(new Date(987654321));
		lvCar1.setDescription("my car");
		lvCar1.setProperties(new Properties());
		lvCar1.getProperties().put("key", "value");
		Car lvCar2 = new Car("BMW");
		lvCar2.setBuild(new Date(987654321));
		lvCar2.setDescription("my car");
		lvCar2.setProperties(new Properties());
		lvCar2.getProperties().put("key", "value");
		CompareResult[] lvResult = new ObjectUtil().compareAll(lvCar1, lvCar2);
		assertNull(lvResult);

		lvCar2.getProperties().put("key", "value-NEW");
		lvResult = new ObjectUtil().compareAll(lvCar1, lvCar2);
		assertNotNull(lvResult);
		assertEquals(1, lvResult.length);
		assertEquals("properties(key)", lvResult[0].differentPath);
		assertEquals("value", lvResult[0].differentValue1);
		assertEquals("value-NEW", lvResult[0].differentValue2);
		assertEquals(1, lvResult[0].numberOfRecursion);

		lvResult = new ObjectUtil().compareAll(lvCar2, lvCar1);
		assertNotNull(lvResult);
		assertEquals(1, lvResult.length);
		assertEquals("properties(key)", lvResult[0].differentPath);
		assertEquals("value-NEW", lvResult[0].differentValue1);
		assertEquals("value", lvResult[0].differentValue2);
		assertEquals(1, lvResult[0].numberOfRecursion);
	}
	
	public void testCompareAllSimpleCarBeanWithMoreDifferences() {
		Car lvCar1 = new Car("Audi");
		lvCar1.setBuild(new Date(987654321));
		lvCar1.setDescription("my car");
		lvCar1.setProperties(new Properties());
		lvCar1.getProperties().put("key", "value");
		Car lvCar2 = new Car("BMW");
		lvCar2.setBuild(new Date(987654321));
		lvCar2.setDescription("your car");
		lvCar2.setProperties(new Properties());
		lvCar2.getProperties().put("key", "value");
		CompareResult[] lvResult = new ObjectUtil().compareAll(lvCar1, lvCar2);
		assertNotNull(lvResult);
		assertEquals(2, lvResult.length);

		for (CompareResult cr : lvResult) {
			if ("name".equals(cr.differentPath)) {
				assertEquals("Audi", cr.differentValue1);
				assertEquals("BMW", cr.differentValue2);
			} else if ("description".equals(cr.differentPath)) {
				assertEquals("my car", cr.differentValue1);
				assertEquals("your car", cr.differentValue2);
			} else {
				fail("Should have name or description path");
			}
		}
	}

	public void testCompareAllSimpleCarBeanWithMoreDifferencesInverse() {
		Car lvCar1 = new Car("Audi");
		lvCar1.setBuild(new Date(987654321));
		lvCar1.setDescription("my car");
		lvCar1.setProperties(new Properties());
		lvCar1.getProperties().put("key", "value");
		Car lvCar2 = new Car("BMW");
		lvCar2.setBuild(new Date(987654321));
		lvCar2.setDescription("your car");
		lvCar2.setProperties(new Properties());
		lvCar2.getProperties().put("key", "value");
		CompareResult[] lvResult = new ObjectUtil().compareAll(lvCar2, lvCar1);
		assertNotNull(lvResult);
		assertEquals(2, lvResult.length);

		for (CompareResult cr : lvResult) {
			if ("name".equals(cr.differentPath)) {
				assertEquals("Audi", cr.differentValue2);
				assertEquals("BMW", cr.differentValue1);
			} else if ("description".equals(cr.differentPath)) {
				assertEquals("my car", cr.differentValue2);
				assertEquals("your car", cr.differentValue1);
			} else {
				fail("Should have name or description path");
			}
		}
	}

	public void testCompareAllSimpleCarBeanWithMoreDifferencesInverseBug() {
		Node lvNode1 = new Node();
		lvNode1.setName("Node 1");
		lvNode1.getNamedChildren().put("key", "value");
		lvNode1.getChildren().add("Avihai");
		lvNode1.getChildren().add("AvihaiAdd");

		Node lvNode2 = new Node();
		lvNode2.setName("Node 2");
		lvNode2.getNamedChildren().put("key", "value");
		lvNode2.getChildren().add("Avihai2");
		
		
		CompareResult[] lvResult = new ObjectUtil().compareAll(lvNode2, lvNode1);
		
		assertNotNull(lvResult);
		assertEquals(3, lvResult.length);
		
		for (CompareResult cr : lvResult) {
			if ("name".equals(cr.differentPath)) {
				assertEquals("Node 2", cr.differentValue1);
				assertEquals("Node 1", cr.differentValue2);

			} else if ("children[0]".equals(cr.differentPath)) {
				assertEquals("Avihai2", cr.differentValue1);
				assertEquals("Avihai", cr.differentValue2);

			} else if ("children[1]".equals(cr.differentPath)) {
				assertNull(cr.differentValue1);
				assertEquals("AvihaiAdd", cr.differentValue2);
			} else {
				fail("unexpected path: " + cr.differentPath);
			}
		}
	}

	public void testCompareRecursion() {
		Node n1 = new Node("n1");
		Node n2 = new Node("n2");

		CompareResult lvResult[] = new ObjectUtil().compareAll(n1, n2);
		assertNotNull(lvResult);
		assertEquals(1, lvResult.length);
		assertEquals(1, lvResult[0].numberOfRecursion);
		assertEquals("name", lvResult[0].differentPath);
		assertEquals("n1", lvResult[0].differentValue1);
		assertEquals("n2", lvResult[0].differentValue2);
		assertEquals(-1, lvResult[0].index);
		assertEquals(null, lvResult[0].key);

		n1 = new Node("n1");
		n1.getNamedChildren().put("child", new Node("n11"));
		n2 = new Node("n1");
		n2.getNamedChildren().put("child", new Node("n21"));

		lvResult = new ObjectUtil().compareAll(n1, n2);

		assertEquals(1, lvResult.length);
		assertEquals(2, lvResult[0].numberOfRecursion);
		assertEquals("namedChildren(child).name", lvResult[0].differentPath);
		assertEquals("n11", lvResult[0].differentValue1);
		assertEquals("n21", lvResult[0].differentValue2);
		assertEquals(-1, lvResult[0].index);
		assertNull(lvResult[0].key);

		n1 = new Node("n1");
		Node n11 = new Node("n11");
		n1.getNamedChildren().put("child", n11);
		n2 = (Node) new ObjectUtil().copy(n1);
		((Node)n1.getNamedChildren().get("child")).getNamedChildren().put("child", new Node("n111"));
		((Node)n2.getNamedChildren().get("child")).getNamedChildren().put("child", new Node("n211"));

		lvResult = new ObjectUtil().compareAll(n1, n2);

		assertEquals(1, lvResult.length);
		assertEquals(3, lvResult[0].numberOfRecursion);
		assertEquals("namedChildren(child).namedChildren(child).name", lvResult[0].differentPath);
		assertEquals("n111", lvResult[0].differentValue1);
		assertEquals("n211", lvResult[0].differentValue2);
		assertEquals(-1, lvResult[0].index);
		assertNull(lvResult[0].key);

	}

	public void testCompareNullAndNull() throws Exception {
		CompareResult lvResult = new ObjectUtil().compare(null, null);
		assertNull(lvResult);
		
		CompareResult[] lvResults = new ObjectUtil().compareAll(null, null);
		assertNull(lvResults);
	}

	public void testChangeWithSimpleKeyMapper() {
		ObjectUtil lvUtil = new ObjectUtil();
		Map<String, String> lvMap = new HashMap<>();
		lvMap.put("key_1", "value_1");
		lvMap.put("key_2", "value_2");
		
		lvUtil.setWithSimpleKeyMapper(false);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvUtil.makeComplex(lvMap);
		assertEquals(lvMap, lvMapAfter);
		
		lvUtil.setWithSimpleKeyMapper(true);
		try {
			lvUtil.makeComplex(lvMap);
			fail("This Map can't work with WithSimpleKeyMapper!");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
		
		lvUtil.setWithSimpleKeyMapper(true);
		assertTrue(lvUtil.getWithSimpleKeyMapper());
		try {
			lvUtil.makeComplex(lvMap);
			fail("This Map can't work with WithSimpleKeyMapper!");
		} catch (ConversionException e) {
			assertNotNull(e);
		}

		lvUtil.setWithSimpleKeyMapper(false);
		lvMapAfter = (Map<?, ?>) lvUtil.makeComplex(lvMap);
		assertEquals(lvMap, lvMapAfter);
	}
	
	public void testWithCycleDetection() {
		ObjectUtil lvUtil = new ObjectUtil();
		assertEquals(5, lvUtil.getConverter().getConversionHandler().size());
		lvUtil.getConverter().clearConversion();
		
		assertEquals(0, lvUtil.getConverter().getConversionHandler().size());
		lvUtil.getConverter().addConversion(new Simple2SimpleConversion(String.class));
		
		assertFalse(lvUtil.getWithCycleDetection());
		lvUtil.setWithCycleDetection(true);
		assertFalse(lvUtil.getWithCycleDetection());
	}

	public void testCycleDetectionInSimpleList() {
		ObjectUtil lvUtil = new ObjectUtil();
		assertFalse(lvUtil.getWithCycleDetection());
		
		lvUtil.setWithCycleDetection(true);
		assertTrue(lvUtil.getWithCycleDetection());
		
		List<List<?>> lvList = new ArrayList<>();
		lvList.add(lvList);
		try {
			lvUtil.makeSimple(lvList);
			fail("Cycle detection on, must find a cycle.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
		
		try {
			lvUtil.makeComplex(lvList);
			fail("Cycle detection on, must find a cycle.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testCycleDetectionInNestedList() {
		ObjectUtil lvUtil = new ObjectUtil();
		lvUtil.setWithCycleDetection(true);
		
		List<List<List<List<List<?>>>>> lvList1 = new ArrayList<>();
		List<List<List<List<?>>>> lvList2 = new ArrayList<>();
		List<List<List<?>>> lvList3 = new ArrayList<>();
		List<List<?>> lvList4 = new ArrayList<>();
		lvList1.add(lvList2);
		lvList2.add(lvList3);
		lvList3.add(lvList4);
		lvUtil.makeSimple(lvList1);
		
		lvList4.add(lvList2);
		try {
			lvUtil.makeSimple(lvList1);
			fail("Cycle detection on, must find a cycle.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testCycleDetectionInSimpleMap() {
		ObjectUtil lvUtil = new ObjectUtil();
		lvUtil.setWithCycleDetection(true);

		Map<String, Map<?,?>> lvMap = new HashMap<>();
		lvUtil.makeSimple(lvMap);
		
		lvMap.put("key", lvMap);
		try {
			lvUtil.makeSimple(lvMap);
			fail("Cycle detection on, must find a cycle.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testCycleDetectionInNestedMap() {
		ObjectUtil lvUtil = new ObjectUtil();
		lvUtil.setWithCycleDetection(true);

		Map<String, Object> lvMap1 = new HashMap<>();
		Map<String, Map<String, Map<?,?>>> lvMap2 = new HashMap<>();
		Map<String, Map<?,?>> lvMap3 = new HashMap<>();
		lvMap1.put("k1", "v1");
		lvMap1.put("map2", lvMap2);
		lvMap2.put("map3", lvMap3);
		
		lvUtil.makeSimple(lvMap1);
		
		lvMap3.put("map1", lvMap1);
		try {
			lvUtil.makeSimple(lvMap1);
			fail("Cycle detection on, must find a cycle.");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testTwiceCollections() {
		ObjectUtil lvUtil = new ObjectUtil();
		
		List<Object> l = new ArrayList<>();
		l.add("foo");
		l.add(new BigDecimal("0.07"));
		
		Node n1 = new Node("n1");
		n1.setChildren(l);
		Node n2 = new Node("n2");
		n2.setChildren(l);
		
		List<Node> mainList = new ArrayList<>();
		mainList.add(n1);
		mainList.add(n2);
		
		Object o = lvUtil.makeSimple(mainList);
		List<?> lAfter = (List<?>) lvUtil.makeComplex(o);
		Node n1After = (Node) lAfter.get(0);
		Node n2After = (Node) lAfter.get(1);
		
		assertEquals("n1", n1After.getName());
		assertEquals("n2", n2After.getName());
		
		assertEquals(n1After.getChildren(), n2After.getChildren());
		assertEquals(n1After.getChildren().size(), n2After.getChildren().size());
		assertNotSame(n1After.getChildren(), n2After.getChildren());
	}
	
	public void testDoubleCallFromMakeSimple() {
		ObjectUtil lvUtil = new ObjectUtil();
		
		Car lvCar = new Car("MyCar");
		lvCar.setDescription("This is really my car");
		Map<?, ?> lvMap = (Map<?, ?>) lvUtil.makeSimple(lvCar);
		assertTrue(lvMap.containsKey("class"));
		assertEquals(Car.class.getName(), lvMap.get("class"));
		
		Car lvCarAfter = (Car) lvUtil.makeComplex(lvMap);
		assertEquals("MyCar", lvCarAfter.getName());
		assertEquals("This is really my car", lvCarAfter.getDescription());
		
		lvCarAfter = (Car) lvUtil.makeComplex(lvMap);
		assertEquals("MyCar", lvCarAfter.getName());
		assertEquals("This is really my car", lvCarAfter.getDescription());
		
		lvMap = (Map<?, ?>) lvUtil.makeSimple(lvCar);
		assertTrue(lvMap.containsKey("class"));
		assertEquals(Car.class.getName(), lvMap.get("class"));
		
		lvMap = (Map<?, ?>) lvUtil.makeSimple(lvCar);
		assertTrue(lvMap.containsKey("class"));
		assertEquals(Car.class.getName(), lvMap.get("class"));
	}
	
	public void testFilterUniqueID() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);

		Car lvCar = new Car("MyCar");
		lvCar.setDescription("This is really my car");
		Map<?, ?> lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);

		assertEquals("MyCar", lvMap.get("name"));
		assertEquals("This is really my car", lvMap.get("description"));
		assertTrue(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));
		assertEquals("0", lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));

		// set filter for unique id
		ClassPropertyFilterHandlerImpl lvFilterHandler = new ClassPropertyFilterHandlerImpl(
							new ClassPropertyFilter (Car.class, new String [] { UniqueIdGenerator.UNIQUE_ID_PROPERTY  })
		);
		lvObjectUtil.setClassPropertyFilterHandler(lvFilterHandler);
		
		lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);

		assertEquals("MyCar", lvMap.get("name"));
		assertEquals("This is really my car", lvMap.get("description"));
		assertFalse(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));
		assertNull(lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));
	}
	
	public void testFilterUniqueIDByCommonAttributes() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHanlderForAttributes());
		
		Person lvPerson = new Person();
		lvPerson.setFirstName("Mario");
		Map<?, ?> lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvPerson);

		assertEquals("Mario", lvMap.get("firstName"));
		assertFalse(lvMap.containsKey("lastName"));
		assertTrue(lvMap.containsKey("birthDay"));
		assertNull(lvMap.get("birthDay"));
		assertFalse(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));
		assertNull(lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));

		
		Account lvAccount = new Account();
		lvAccount.setAccountNumber("007");
		lvMap = (Map<?, ?>) lvObjectUtil.makeComplex(lvAccount);
		
		assertEquals("007", lvMap.get("accountNumber"));
		assertTrue(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));
		assertEquals("0", lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));
	}

	public void testFilterClassProperty() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);

		Car lvCar = new Car("MyCar");
		Map<?, ?> lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);
		assertTrue(lvMap.containsKey("class"));
		assertEquals(Car.class.getName(), lvMap.get("class"));

		// set filter for unique id
		ClassPropertyFilter lvClassPropertyFilter = new ClassPropertyFilter (Car.class);
		lvClassPropertyFilter.setSupport4AddClassProperty(true);
		lvClassPropertyFilter.addProperties(new String [] { UniqueIdGenerator.UNIQUE_ID_PROPERTY , "class" });
		
		assertTrue(lvClassPropertyFilter.isKnownProperty("class"));
		assertTrue(lvClassPropertyFilter.isKnownProperty(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertEquals(2, lvClassPropertyFilter.getPropertySize());

		ClassPropertyFilterHandlerImpl lvFilterHandler = new ClassPropertyFilterHandlerImpl(lvClassPropertyFilter);
		lvObjectUtil.setClassPropertyFilterHandler(lvFilterHandler);
		
		lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);
		assertFalse(lvMap.containsKey("class"));
	}

	public void testFilterClassPropertyByCommonAttributes() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);

		MyAnnotationClass lvAnnotationClass = new MyAnnotationClass("My Annotation");
		assertEquals("My Annotation", lvAnnotationClass.getName());
		Map<?, ?> lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvAnnotationClass);
		assertTrue(lvMap.containsKey("class"));
		assertTrue(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertEquals("My Annotation", lvMap.get("name"));

		// set filter for unique id
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHanlderForAttributes());
		
		lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvAnnotationClass);
		assertFalse(lvMap.containsKey("class"));
		assertFalse(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertEquals("My Annotation", lvMap.get("name"));
		assertEquals(1, lvMap.size());
	}
	
	public void testMakeComplexWithoutClassInSimpleRepresentation() {
		Map<String, String> lvMap = new HashMap<>();
		lvMap.put("name", "My Car");
		lvMap.put("description", "the description");
		
		Object o = new ObjectUtil().makeComplex(lvMap, Car.class);
		assertNotNull(o);
		assertTrue("Class: " + o.getClass().getName(), o instanceof Car);
		Car lvCar = (Car) o;
		assertEquals("My Car", lvCar.getName());
		assertEquals("the description", lvCar.getDescription());
		assertNull(lvCar.getBuild());
	}

	public void testMakeComplexWithoutClassInSimpleRepresentationMoreComplexExample() {
		Map<String, Object> lvMapNode = new HashMap<>();
		lvMapNode.put("name", "Node");
		Map<String, String> lvMapParent = new HashMap<>();
		lvMapParent.put("name", "Parent");
		lvMapNode.put("parent", lvMapParent);
		
		Object o = new ObjectUtil().makeComplex(lvMapNode, Node.class);
		assertNotNull(o);
		assertTrue("Class: " + o.getClass().getName(), o instanceof Node);
		Node lvNode = (Node) o;
		assertEquals("Node", lvNode.getName());
		assertEquals("Parent", lvNode.getParent().getName());
	}

	public void testMakeComplexWithoutClassInSimpleRepresentationIncompatibleProperty() {
		Map<String, String> lvMap = new HashMap<>();
		lvMap.put("name", "Node");
		lvMap.put("description", "the description");
		
		Object o = new ObjectUtil().makeComplex(lvMap, Node.class);
		assertNotNull(o);
		assertTrue("Class: " + o.getClass().getName(), o instanceof Node);
		Node lvNode = (Node) o;
		assertEquals("Node", lvNode.getName());
		assertNull(lvNode.getParent());
	}

	public void testIgnoreAllNullValues() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);
		
		Car lvCar = new Car("MyCar");
		Map<?, ?> lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);

		assertEquals("MyCar", lvMap.get("name"));
		assertTrue(lvMap.containsKey("description"));
		assertNull(lvMap.get("description"));
		assertEquals("0", lvMap.get(UniqueIdGenerator.UNIQUE_ID_PROPERTY ));

		// set filter for unique id
		ClassPropertyFilter lvClassPropertyFilter = new ClassPropertyFilter (Car.class, new String [] { UniqueIdGenerator.UNIQUE_ID_PROPERTY  });
		ClassPropertyFilterHandlerImpl lvFilterHandler = new ClassPropertyFilterHandlerImpl(lvClassPropertyFilter);
		lvObjectUtil.setClassPropertyFilterHandler(lvFilterHandler);
		lvObjectUtil.setIgnoreAllNullValues(true);
		
		lvMap = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);

		assertEquals("MyCar", lvMap.get("name"));
		assertFalse(lvMap.containsKey("description"));
	}
	
	public void testIgonoreNullValuesInCollection() {
		Customer lvCustomer = new Customer("LastName");
		Address lvAddress = new Address();
		lvAddress.setCity("Nuernberg");
		lvCustomer.getAddresses().add(lvAddress);
		
		ObjectUtil lvUtil = new ObjectUtil();
		Map<?, ?> lvMap = (Map<?, ?>) lvUtil.makeSimple(lvCustomer);
		
		assertTrue(lvMap.containsKey("firstName"));
		assertNull(lvMap.get("firstName"));
		
		Map<?, ?> lvMapAddress = (Map<?, ?>) ((List<?>) lvMap.get("addresses")).get(0);
		assertTrue(lvMapAddress.containsKey("postcode"));
		assertNull(lvMapAddress.get("postcode"));
		

		lvUtil.setIgnoreAllNullValues(true);
		lvMap = (Map<?, ?>) lvUtil.makeSimple(lvCustomer);
		
		assertFalse(lvMap.containsKey("firstName"));
		assertEquals("LastName", lvMap.get("lastName"));
		
		lvMapAddress = (Map<?, ?>) ((List<?>) lvMap.get("addresses")).get(0);
		assertFalse(lvMapAddress.containsKey("postcode"));
		assertEquals("Nuernberg", lvMapAddress.get("city"));
	}
	
	public void testIgnoreAllNullValuesSimpe() {
		ObjectUtil lvUtil = new ObjectUtil();
		assertFalse(lvUtil.getIgnoreAllNullValues());
		
		lvUtil.setIgnoreAllNullValues(true);
		assertTrue(lvUtil.getIgnoreAllNullValues());
	}
	
	public void testTransformThrowable2ThrowableWrapper() {
		ObjectUtil lvUtil = new ObjectUtil();
		Object o = lvUtil.makeSimple(new ConversionException("JUnit-Test-ConversionException"));
		assertTrue("Keine ThrowableWrapper: " + o, o instanceof Map);
		Map<?, ?> lvExceptionMap = (Map<?, ?>) o;
		assertEquals("JUnit-Test-ConversionException", lvExceptionMap.get("message"));
		assertEquals(ConversionException.class.getName(), lvExceptionMap.get("exceptionClassName"));
		assertNull(lvExceptionMap.get("causeWrapper"));
		
		List<?> lvList = (List<?>) lvExceptionMap.get("stackTraceElementWrapperList");
		assertTrue(15 < lvList.size());
		Map<?, ?> lvStackTraceElementMap = (Map<?, ?>) lvList.get(0);
		assertEquals("ObjectUtilTest.java", lvStackTraceElementMap.get("fileName"));
		assertEquals(StackTraceElementWrapper.class.getName(), lvStackTraceElementMap.get("class"));
		assertEquals(this.getClass().getName(), lvStackTraceElementMap.get("className"));
		assertEquals(Boolean.FALSE, lvStackTraceElementMap.get("nativeMethod"));
		assertEquals("testTransformThrowable2ThrowableWrapper", lvStackTraceElementMap.get("methodName"));
	}
	
	public void testTransformThrowable2ThrowableWrapperAndBack() {
		ObjectUtil lvUtil = new ObjectUtil();
		Object o = lvUtil.makeSimple(new ConversionException("JUnit-Test-ConversionException"));
		assertTrue("Keine ThrowableWrapper: " + o, o instanceof Map);
		o = lvUtil.makeComplex(o);
		assertTrue("Keine ThrowableWrapper: " + o, o instanceof ConversionException);
		assertEquals("JUnit-Test-ConversionException", ((ConversionException) o).getMessage());
	}

	public void testTransformThrowable2ThrowableWrapperAndBackWithTwiceCallMakeSimple() {
		ObjectUtil lvUtil = new ObjectUtil();
		Object o = lvUtil.makeSimple(new ConversionException("JUnit-Test-ConversionException"));
		assertTrue("Keine ThrowableWrapper: " + o, o instanceof Map);
		o = lvUtil.makeSimple(o);
		assertTrue("Keine ThrowableWrapper: " + o, o instanceof Map);
		Map<?, ?> lvExceptionMap = (Map<?, ?>) o;
		assertEquals("JUnit-Test-ConversionException", lvExceptionMap.get("message"));
		assertEquals(ConversionException.class.getName(), lvExceptionMap.get("exceptionClassName"));
	}
	
	public void testTransformError() {
		ObjectUtil lvUtil = new ObjectUtil();
		Object o = lvUtil.makeSimple(new Error("JUnit-Test-Error"));
		Error lvError = (Error) lvUtil.makeComplex(o);
		assertEquals("JUnit-Test-Error", lvError.getMessage());
	}

	public void testTransformThrowable2OtherException() {
		ObjectUtil lvUtil = new ObjectUtil();
		Object o = lvUtil.makeSimple(new ConversionException("JUnit-Test-ConversionException"));
		NullPointerException lvThrowable = (NullPointerException) lvUtil.makeComplex(o, NullPointerException.class);
		assertEquals("JUnit-Test-ConversionException", lvThrowable.getMessage());
	}

	public void testTransformError2OtherException() {
		ObjectUtil lvUtil = new ObjectUtil();
		Object o = lvUtil.makeSimple(new Error("JUnit-Test-Error"));
		NullPointerException lvThrowable = (NullPointerException) lvUtil.makeComplex(o, NullPointerException.class);
		assertEquals("JUnit-Test-Error", lvThrowable.getMessage());
	}


	public void testTransformWithDateFormat() {
		SimpleFormatConversion lvConversion = new SimpleFormatConversion();
		SimpleDateFormat lvDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		lvConversion.addFormatter(Date.class, lvDateFormat);
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.getConverter().addConversion(lvConversion);

                Calendar calendar = Calendar.getInstance();
                calendar.set(1970, Calendar.JANUARY, 2, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                
		Date lvDate = calendar.getTime();
		Object lvDateStr = lvObjectUtil.makeComplex(lvDate);
		assertEquals("02-01-1970", lvDateStr);
		
		Date lvDateAfter = (Date) lvObjectUtil.makeComplex(lvDateStr, Date.class);
		assertEquals(lvDate, lvDateAfter);
	}

	public void testTransformBadWithDateFormat() {
		SimpleFormatConversion lvConversion = new SimpleFormatConversion();
		SimpleDateFormat lvDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		lvConversion.addFormatter(Date.class, lvDateFormat);
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.getConverter().addConversion(lvConversion);

		try {
			String lvDateStr = "AA-01-1970"; 
			lvObjectUtil.makeComplex(lvDateStr, Date.class);			
			fail("Invalid Date: " + lvDateStr);
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	
	public void testAddAndRemoveFormatterForType() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		int lvSize = lvObjectUtil.getConverter().getConversionHandler().size();
		lvObjectUtil.addFormatterForType(new SimpleDateFormat("dd-MM-yyyy"), Date.class);
		int lvSizeAfterAdd = lvObjectUtil.getConverter().getConversionHandler().size();
		assertEquals(lvSizeAfterAdd, (lvSize + 1));
		
		lvObjectUtil.addFormatterForType(NumberFormat.getNumberInstance(), Double.class);
		lvSizeAfterAdd = lvObjectUtil.getConverter().getConversionHandler().size();
		assertEquals(lvSizeAfterAdd, (lvSize + 1));
		
		lvObjectUtil.removeFormatterByType(Double.class);
		assertEquals(lvSizeAfterAdd, (lvSize + 1));
		
		lvObjectUtil.removeFormatterByType(Date.class);
		int lvSizeAfterRemove = lvObjectUtil.getConverter().getConversionHandler().size();
		assertEquals(lvSizeAfterRemove, lvSize);
	}

	public void testTransformWithNumberFormat() {
		SimpleFormatConversion lvConversion = new SimpleFormatConversion();
		lvConversion.addFormatter(Double.class, NumberFormat.getInstance(Locale.GERMAN));
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.getConverter().addConversion(lvConversion);

		Double lvDouble = new Double("4711.007");
		Object lvDoubleStr = lvObjectUtil.makeComplex(lvDouble);
		assertEquals("4.711,007", lvDoubleStr);
		
		Double lvDoubleAfter = (Double) lvObjectUtil.makeComplex(lvDoubleStr, Double.class);
		assertEquals(lvDouble, lvDoubleAfter);
	}

	public void testTransformBadWithDateFormatButDoubleAsInput() {
		SimpleFormatConversion lvConversion = new SimpleFormatConversion();
		SimpleDateFormat lvDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		lvConversion.addFormatter(Date.class, lvDateFormat);
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.getConverter().addConversion(lvConversion);

		Double lvDouble = new Double("4711.007");
		Object lvDoubleStr = lvObjectUtil.makeComplex(lvDouble);
		assertEquals(new Double("4711.007"), lvDoubleStr);
		
		lvDoubleStr = lvObjectUtil.makeComplex("4711.007");
		assertEquals("4711.007", lvDoubleStr);
	}
	
	public void testTransformWithInvalidToType() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.addFormatterForType(SimpleDateFormat.getDateInstance(), Date.class);
		
		Object o = lvObjectUtil.makeComplex("01.04.2007", Double.class);
		assertNotNull(o);
		assertEquals("01.04.2007", o);
	}

	public void testHashCodeWithNullValue() {
		int lvHashCode = new ObjectUtil().hashCode(null);
		assertEquals(17, lvHashCode);
	}
	
	public void testHashCodeByString() {
		String lvString = "My String";
		int lvHashCode = new ObjectUtil().hashCode(lvString);
		assertEquals((17 * 37 + lvString.hashCode()), lvHashCode);
	}
	
	public void testHashCodeByLong() {
		Long lvLong = 47711L;
		int lvHashCode = new ObjectUtil().hashCode(lvLong);
		assertEquals((17 * 37 + lvLong.hashCode()), lvHashCode);
	}

	public void testHashCodeByCollection() {
		Long lvLong = 47711L;
		String lvString = "My String";
		Collection<Comparable<?>> lvCollection = new ArrayList<>();
		lvCollection.add(lvLong);
		lvCollection.add(lvString);
		int lvHashCode = new ObjectUtil().hashCode(lvCollection);
		
		int lvCalcHashCode = 17 * 37 + lvLong.hashCode();
		lvCalcHashCode = lvCalcHashCode * 37 + lvString.hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}

	public void testHashCodeByCollectionWithNullValue() {
		Long lvLong = 47711L;
		String lvString = "My String";
		Collection<Comparable<?>> lvCollection = new ArrayList<>();
		lvCollection.add(lvLong);
		lvCollection.add(null);
		lvCollection.add(lvString);
		int lvHashCode = new ObjectUtil().hashCode(lvCollection);
		
		int lvCalcHashCode = 17 * 37 + lvLong.hashCode();
		lvCalcHashCode = lvCalcHashCode * 37 + lvString.hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}

	public void testHashCodeByMap() {
		Long lvLong = 47711L;
		String lvString = "My String";
		Map<Comparable<?>, Comparable<?>> lvMap = new HashMap<>();
		lvMap.put(lvLong, lvLong);
		lvMap.put(lvString, lvString);
		int lvHashCode = new ObjectUtil().hashCode(lvMap);
		
		int lvCalcHashCode = 17 * 37 + lvLong.hashCode();
		lvCalcHashCode = lvCalcHashCode * 37 + lvString.hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}

	public void testHashCodeByMapWithNullValue() {
		Long lvLong = 47711L;
		String lvString = "My String";
		Map<Comparable<?>, Comparable<?>> lvMap = new HashMap<>();
		lvMap.put(lvLong, lvLong);
		lvMap.put("key", null);
		lvMap.put(lvString, lvString);
		int lvHashCode = new ObjectUtil().hashCode(lvMap);
		
		int lvCalcHashCode = 17 * 37 + lvLong.hashCode();
		lvCalcHashCode = lvCalcHashCode * 37 + lvString.hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}
	
	public void testHashCodeByBean() {
		Date lvDate = new Date();
		String lvName = "MyCar";
		Car lvCar = new Car(lvName);
		lvCar.setBuild(lvDate);
		
		int lvHashCode = new ObjectUtil().hashCode(lvCar);
		
		int lvCalcHashCode = 17 * 37 + lvDate.hashCode();
		lvCalcHashCode = lvCalcHashCode * 37 + lvCar.getClass().getName().hashCode();
		lvCalcHashCode = lvCalcHashCode * 37 + lvName.hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}
	
	public void testHashCodeByBeanWithFilter() {
		Date lvDate = new Date();
		String lvName = "MyCar";
		Car lvCar = new Car(lvName);
		lvCar.setBuild(lvDate);
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		ClassPropertyFilter cpf = new ClassPropertyFilter(Car.class, new String[] {"build", "name"});
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(cpf));
		int lvHashCode = lvObjectUtil.hashCode(lvCar);
		
		int lvCalcHashCode = 17 * 37 + lvCar.getClass().getName().hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}

	public void testHashCodeByBeanWithFilterForClass() {
		Date lvDate = new Date();
		String lvName = "MyCar";
		Car lvCar = new Car(lvName);
		lvCar.setBuild(lvDate);
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		ClassPropertyFilter cpf = new ClassPropertyFilter(Car.class);
		cpf.setSupport4AddClassProperty(true);
		cpf.addProperties(new String [] { "build", "class" });
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(cpf));

		int lvHashCode = lvObjectUtil.hashCode(lvCar);
		
		int lvCalcHashCode = 17 * 37 + lvName.hashCode();
		assertEquals(lvCalcHashCode, lvHashCode);
	}


	public void testCompareToByString() {
		String s1 = "MyString 1";
		String s2 = "MyString 2";
		assertEquals(s1.compareTo(s2), new ObjectUtil().compareTo(s1, s2));
		assertEquals(s2.compareTo(s1), new ObjectUtil().compareTo(s2, s1));
		s2 = s1;
		assertEquals(s1.compareTo(s2), new ObjectUtil().compareTo(s1, s2));
	}

	public void testCompareToByLong() {
		Long l1 = new Long("6");
		Long l2 = new Long("3");
		assertEquals(l1.compareTo(l2), new ObjectUtil().compareTo(l1, l2));
		assertEquals(l2.compareTo(l1), new ObjectUtil().compareTo(l2, l1));
		l1 = l2;
		assertEquals(l1.compareTo(l2), new ObjectUtil().compareTo(l1, l2));
	}

	public void testCompareToByDate() {
		Date d1 = new Date(987654321);
		Date d2 = new Date(987654355);
		assertEquals(d1.compareTo(d2), new ObjectUtil().compareTo(d1, d2));
		assertEquals(d2.compareTo(d1), new ObjectUtil().compareTo(d2, d1));
		assertEquals(0, d2.compareTo(d1) + new ObjectUtil().compareTo(d1, d2));
		d1 = d2;
		assertEquals(d1.compareTo(d2), new ObjectUtil().compareTo(d1, d2));
	}

	public void testCompareToByBigDecimal() {
		BigDecimal bd1 = new BigDecimal("47.11");
		BigDecimal bd2 = new BigDecimal("47.12");
		assertEquals(bd1.compareTo(bd2), new ObjectUtil().compareTo(bd1, bd2));
		assertEquals(bd2.compareTo(bd1), new ObjectUtil().compareTo(bd2, bd1));
		bd1 = bd2;
		assertEquals(bd1.compareTo(bd2), new ObjectUtil().compareTo(bd1, bd2));
	}
	
	public void testCompareToByBeanWithOneProperty() {
		Car c1 = new Car("Car 1");
		Car c2 = new Car("Car 2");
		assertEquals(-1, new ObjectUtil().compareTo(c1, c2));
		assertEquals(1, new ObjectUtil().compareTo(c2, c1));
		
		assertEquals(0, new ObjectUtil().compareTo(c1, c1));
		c1 = c2;
		assertEquals(0, new ObjectUtil().compareTo(c1, c2));
	}

	public void testCompareToByBeanWithTwoProperty() {
		Car c1 = new Car("Car 1");
		c1.setDescription("Desc 2");
		Car c2 = new Car("Car 2");
		c2.setDescription("Desc 1");
		assertEquals(0, new ObjectUtil().compareTo(c1, c2));
		assertEquals(0, new ObjectUtil().compareTo(c2, c1));
		
		assertEquals(0, new ObjectUtil().compareTo(c1, c1));
		c1 = c2;
		assertEquals(0, new ObjectUtil().compareTo(c1, c2));
	}

	public void testCompareResult() {
		CompareResult cr = new CompareResult();
		cr.differentValue1 = new Node("N1");
		cr.differentValue2 = new Node("N2");
		assertEquals(0, cr.getCompareToValue());
	}
	
	public void testMakeSimpleWithPropertyFilter() {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		Object lvTemp = lvObjectUtil.makeSimple(lvCar, new String[] { "build" });
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(lvTemp);
		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	public void testMakeSimpleWithPropertyFilterAndObjectIsNull() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		Object lvTemp = lvObjectUtil.makeSimple(null, new String[] { "build" });
		assertNull(lvTemp);
	}

	public void testMakeSimpleWithPropertyFilterAndPropertiesAreNull() {
		Car lvCar = new Car("BMW");
		Date d = new Date(1);
		lvCar.setBuild(d);
		
		assertNotNull(lvCar.getBuild());
		assertEquals(d, lvCar.getBuild());
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		Object lvTemp = lvObjectUtil.makeSimple(lvCar, new String[0]);
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(lvTemp);

		assertNotNull(lvCarAfter.getBuild());
		assertEquals(d, lvCarAfter.getBuild());
	}

	public void testSerializeWithPropertyFilterAndFilteringClass() {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		ObjectUtil lvObjectUtil = new ObjectUtil(false);
		Object lvTemp = lvObjectUtil.makeSimple(lvCar, new String[] { "build", "class" });
		Object lvResult = lvObjectUtil.makeComplex(lvTemp);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey("class"));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(lvTemp, Car.class); 		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}
	
	public void testSaveNullClassPropertyFilterHandler() {
		ObjectUtil lvObjectUtil = new ObjectUtil(false);
		assertNull(lvObjectUtil.getClassPropertyFilterHandler());

		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		
		lvObjectUtil.makeSimple(lvCar, new String[] { "build", "class" });

		assertNull(lvObjectUtil.getClassPropertyFilterHandler());
	}

	public void testSaveClassPropertyFilterHandler() {
		ObjectUtil lvObjectUtil = new ObjectUtil(false);
		ClassPropertyFilter lvClassPropertyFilter = new ClassPropertyFilter (Car.class);
		lvClassPropertyFilter.setSupport4AddClassProperty(true);
		ClassPropertyFilterHandlerImpl lvFilterHandler = new ClassPropertyFilterHandlerImpl(lvClassPropertyFilter);
		lvObjectUtil.setClassPropertyFilterHandler(lvFilterHandler);
		
		assertNotNull(lvObjectUtil.getClassPropertyFilterHandler());
		assertSame(lvFilterHandler, lvObjectUtil.getClassPropertyFilterHandler());

		
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		
		lvObjectUtil.makeSimple(lvCar, new String[] { "build", "class" });

		assertNotNull(lvObjectUtil.getClassPropertyFilterHandler());
		assertSame(lvFilterHandler, lvObjectUtil.getClassPropertyFilterHandler());
	}

	public void testStringArray2DoubleArray() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		String[] lvStringArray = new String[] { "1", "2.3", "0.07"};
		Object o = lvObjectUtil.makeSimple(lvStringArray);
		o = lvObjectUtil.makeComplex(o, Double[].class);
		assertTrue("o ist not a Double-Array: " + o.getClass(), o instanceof Double[]);
		Double[] d = (Double[]) o;
		assertEquals(1.0, d[0]);
		assertEquals(2.3, d[1]);
		assertEquals(0.07, d[2]);
	}
	
	public void testTryConvertStringArray2DoubleArray() {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		String[] lvStringArray = new String[] { "1", "A", "0.07"};
		Object o = lvObjectUtil.makeSimple(lvStringArray);
		try {
			lvObjectUtil.makeComplex(o, Double[].class);
			fail("A is not a Double value!");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	public void testConvertUrlProperty() throws Exception {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		String lvUrlStr = "https://myurl.net";
		lvBean.setUrl(new URL(lvUrlStr));
		
		ObjectUtil ou = new ObjectUtil();
		Map<?, ?> lvResult = (Map<?, ?>) ou.makeSimple(lvBean);
		assertEquals(lvResult.get("class"), SpecialTypeBean.class.getName());
		assertEquals(lvResult.get("url"), lvUrlStr);
		assertNull(lvResult.get("object"));
		assertEquals(4, lvResult.size());
		
		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) ou.makeComplex(lvResult);
		assertEquals(lvBean.getUrl(), lvBeanAfter.getUrl());
		assertNull(lvBeanAfter.getObject());
	}
	
	public void testConvertObjectWithStringValuepropety() {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		String lvTestString = "A Test-String";
		lvBean.setObject(lvTestString);
		
		ObjectUtil ou = new ObjectUtil();
		Map<?, ?> lvResult = (Map<?, ?>) ou.makeSimple(lvBean);
		assertEquals(lvResult.get("class"), SpecialTypeBean.class.getName());
		assertEquals(lvResult.get("object"), lvTestString);
		assertNull(lvResult.get("url"));
		assertEquals(4, lvResult.size());

		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) ou.makeComplex(lvResult);
		assertEquals(lvBean.getObject(), lvBeanAfter.getObject());
		assertEquals(lvTestString, lvBeanAfter.getObject());
		assertNull(lvBeanAfter.getUrl());
	}
	
	public void testConvertObjectWithLongValuepropety() {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		Long lvTestLong = 4711L;
		lvBean.setObject(lvTestLong);
		
		ObjectUtil ou = new ObjectUtil();
		Map<?, ?> lvResult = (Map<?, ?>) ou.makeSimple(lvBean);
		assertEquals(lvResult.get("class"), SpecialTypeBean.class.getName());
		assertEquals(lvResult.get("object"), lvTestLong);
		assertNull(lvResult.get("url"));
		assertEquals(4, lvResult.size());

		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) ou.makeComplex(lvResult);
		assertEquals(lvBean.getObject(), lvBeanAfter.getObject());
		assertEquals(lvTestLong, lvBeanAfter.getObject());
		assertNull(lvBeanAfter.getUrl());
	}

	public void testConvertObjectWithDateValuepropety() {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		Date lvTestDate = new Date();
		lvBean.setObject(lvTestDate);
		
		ObjectUtil ou = new ObjectUtil();
		Map<?, ?> lvResult = (Map<?, ?>) ou.makeSimple(lvBean);
		assertEquals(lvResult.get("class"), SpecialTypeBean.class.getName());
		assertEquals(lvResult.get("object"), lvTestDate);
		assertNull(lvResult.get("url"));
		assertEquals(4, lvResult.size());

		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) ou.makeComplex(lvResult);
		assertEquals(lvBean.getObject(), lvBeanAfter.getObject());
		assertEquals(lvTestDate, lvBeanAfter.getObject());
		assertNull(lvBeanAfter.getUrl());
	}

	public void testConvertObjectWithBigDecimalValuepropety() {
		SpecialTypeBean lvBean = new SpecialTypeBean();
		BigDecimal lvValue = new BigDecimal("47.11");
		lvBean.setObject(lvValue);
		
		ObjectUtil ou = new ObjectUtil();
		Map<?, ?> lvResult = (Map<?, ?>) ou.makeSimple(lvBean);
		assertEquals(lvResult.get("class"), SpecialTypeBean.class.getName());
		assertEquals(lvResult.get("object"), lvValue);
		assertNull(lvResult.get("url"));
		assertEquals(4, lvResult.size());

		SpecialTypeBean lvBeanAfter = (SpecialTypeBean) ou.makeComplex(lvResult);
		assertEquals(lvBean.getObject(), lvBeanAfter.getObject());
		assertEquals(lvValue, lvBeanAfter.getObject());
		assertNull(lvBeanAfter.getUrl());
	}

	public void testNewKeyWordClass() {
		Util.setKeyWordClass("clazz");
		assertEquals("clazz", Util.getKeyWordClass());
		
		Bean lvBean = new Bean("name");
		Timestamp lvTimeStamp = new Timestamp(new Date().getTime());
		lvBean.setTimestamp(lvTimeStamp);
		ObjectUtil ou = new ObjectUtil();
		Map<?, ?> lvResult = (Map<?, ?>) ou.makeSimple(lvBean);
		assertEquals(lvResult.get("clazz"), Bean.class.getName());
		assertEquals(lvResult.get("myProp"), "name");
		assertEquals(lvResult.get("timestamp"), lvTimeStamp);

		Bean lvBeanAfter = (Bean) ou.makeComplex(lvResult);
		assertEquals(lvTimeStamp, lvBeanAfter.getTimestamp());
		assertEquals("name", lvBeanAfter.getMyProp());
		assertNull(lvBeanAfter.getDate());
		
		Util.resetKeyWordClass();
		assertEquals(Util.DEFAULT_KEY_WORD_CLASS, Util.getKeyWordClass());
	}
	
	public void testTransformDefaultMutableTreeNodeWithLoseChild() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);
		
		ObjectUtil ou = new ObjectUtil();
		Object o = ou.makeSimple(root);
		DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) ou.makeComplex(o);
		
		assertEquals("ROOT", rootAfter.getUserObject());
		// expected 1 child, but the count is 0, the property access over method lack
		assertEquals(0, rootAfter.getChildCount());
	}
	
	public void testTransformDefaultMutableTreeNode() {
		String[] lvFilter = new String [] {"class", "parent", "children", "userObject", "allowsChildren"};
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, lvFilter);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);
		
		ObjectUtil ou = new ObjectUtil();
		Object o = ou.makeSimple(root);
		DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) ou.makeComplex(o);
		
		assertEquals("ROOT", rootAfter.getUserObject());
		assertEquals(1, rootAfter.getChildCount());
		assertEquals("Child", ((DefaultMutableTreeNode) rootAfter.getChildAt(0)).getUserObject());
		
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

}
