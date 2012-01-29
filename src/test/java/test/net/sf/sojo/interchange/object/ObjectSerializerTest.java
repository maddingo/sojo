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
package test.net.sf.sojo.interchange.object;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.SerializerException;
import net.sf.sojo.interchange.object.ObjectSerializer;
import test.net.sf.sojo.model.ABean;
import test.net.sf.sojo.model.BBean;
import test.net.sf.sojo.model.Car;

public class ObjectSerializerTest extends TestCase {

	public void testSimpleStringSerialize() throws Exception {
		ObjectSerializer lvSerializer = new ObjectSerializer();
		Object lvResult = lvSerializer.serialize("Ferrari");
		
		Object o = lvSerializer.deserialize(lvResult);
		assertNotNull(o);
		assertEquals("Ferrari", o);
	}

	public void testSimpleStringSerializeWithOutConvert() throws Exception {
		ObjectSerializer lvSerializer = new ObjectSerializer();
		lvSerializer.setConvertBySerialization(false);
		Object lvResult = lvSerializer.serialize("Ferrari");
		
		Object o = lvSerializer.deserialize(lvResult);
		assertNotNull(o);
		assertEquals("Ferrari", o);
	}

	public void testSimpleObjectSerialize() throws Exception {
		Car lvCar = new Car("Ferrari");
		ObjectSerializer lvSerializer = new ObjectSerializer();
		Object lvResult = lvSerializer.serialize(lvCar);
		
		Object o = lvSerializer.deserialize(lvResult);
		assertNotNull(o);
		Car lvCarAfter = (Car) o;
		assertEquals(lvCar.getName(), lvCarAfter.getName());
	}
	
	public void testSerializeException() throws Exception {
		Car lvCar = new Car("Ferrari");
		ObjectSerializer lvSerializer = new ObjectSerializer();
		lvSerializer.setConvertBySerialization(false);
		try {
			lvSerializer.serialize(lvCar);
			fail("Car is not implement the Serializable-interface.");
		} catch (SerializerException e) {
			assertNotNull(e);
		}
	}
	
	public void testMessageForSerializerException() throws Exception {
		SerializerException lvException = new SerializerException("MyMessage");
		assertEquals("MyMessage", lvException.getMessage());
	}
	
	public void testSerializeInFile() throws Exception {
		String lvTempDir = System.getProperties().getProperty("java.io.tmpdir");
		String lvFile = lvTempDir + File.separatorChar + "Car.ser";
		
		Car lvCar = new Car("Ferrari");
		ObjectSerializer lvSerializer = new ObjectSerializer();
		lvSerializer.serializeToFile(lvCar, lvFile);
		
		Object o = lvSerializer.deserializeFromFile(lvFile);
		assertNotNull(o);
		Car lvCarAfter = (Car) o;
		assertEquals(lvCar.getName(), lvCarAfter.getName());
	}

	public void testSerializeInFileWithFaultInPath() throws Exception {
		String lvTempDir = System.getProperties().getProperty("java.io.tmpdir");
		String lvFile = lvTempDir + "bad-path/Car.ser";
		
		Car lvCar = new Car("Ferrari");
		ObjectSerializer lvSerializer = new ObjectSerializer();
		try {
			lvSerializer.serializeToFile(lvCar, lvFile);
			fail("Bad path to serialize: " + lvFile);
		} catch (IOException e) {
			assertNotNull(e);
		}
		
		try {
			lvSerializer.deserializeFromFile(lvFile);
			fail("Bad path to deserialize: " + lvFile);
		} catch (IOException e) {
			assertNotNull(e);
		}
	}

	public void testDeSerializeWithException() throws Exception {
		try {
			new ObjectSerializer().deserialize(null);
			fail("Can't deserialize null value.");
		} catch (SerializerException e) {
			assertNotNull(e);
		}		
	}

	public void testDeSerializeObjectWithException() throws Exception {
		try {
			new ObjectSerializer().deserialize("Dummy");
			fail("Can't deserialize a String value.");
		} catch (SerializerException e) {
			assertNotNull(e);
		}		
	}
	
	public void testSerializeWithClassPathFilter() throws Exception {
		Serializer lvSerializer = new ObjectSerializer();
		ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).removeProperty("build").removeProperty("description");
		lvSerializer.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(lvFilter));
		
		Car lvCar = new Car("MyCar");
		lvCar.setBuild(new Date(123456789));
		lvCar.setDescription("This is my car");
		
		Object o = lvSerializer.serialize(lvCar);
		Car lvCarAfter = (Car) lvSerializer.deserialize(o);
		
		assertNull(lvCarAfter.getName());
		assertEquals(new Date(123456789), lvCarAfter.getBuild());
		assertEquals("This is my car", lvCarAfter.getDescription());
	}

	public void testDeSerializeWithOutRootClass() throws Exception {
		Serializer lvSerializer = new ObjectSerializer();
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("name", "BMW");
		lvMap.put("description", "This BMW is my Car");
		
		Object o = lvSerializer.serialize(lvMap);
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvSerializer.deserialize(o);
		assertEquals("BMW", lvMapAfter.get("name"));
		assertEquals("This BMW is my Car", lvMapAfter.get("description"));
		assertFalse("Map don't contains class attribute", lvMap.containsKey("class"));
	}
	
	public void testDeSerializeWithRootClass() throws Exception {
		Serializer lvSerializer = new ObjectSerializer();
		Map<String, String> lvMap = new HashMap<String, String>();
		lvMap.put("name", "BMW");
		lvMap.put("description", "This BMW is my Car");
		
		Object o = lvSerializer.serialize(lvMap);
		Car lvCarAfter = (Car) lvSerializer.deserialize(o, Car.class);
		assertEquals("BMW", lvCarAfter.getName());
		assertEquals("This BMW is my Car", lvCarAfter.getDescription());
	}
	
	public void testDeSerializeException() throws Exception {
		Serializer lvSerializer = new ObjectSerializer();
		Exception lvException = new ConversionException("JUnit-Test-Exception");
		Object lvObject = lvSerializer.serialize(lvException);
		ConversionException e = (ConversionException) lvSerializer.deserialize(lvObject);
		assertEquals("JUnit-Test-Exception", e.getMessage());
		assertNull(e.getCause());
		assertTrue(5 < e.getStackTrace().length);
	}
	
	public void testDeSerializeNestedException() throws Exception {
		Serializer lvSerializer = new ObjectSerializer();
		Exception lvException = new ConversionException("JUnit-Test-Exception", new NullPointerException("Nested"));
		Object lvObject = lvSerializer.serialize(lvException);
		ConversionException e = (ConversionException) lvSerializer.deserialize(lvObject);
		assertEquals("JUnit-Test-Exception", e.getMessage());
		assertTrue(5 < e.getStackTrace().length);
		assertNotNull(e.getCause());
		Throwable lvNestedExc = e.getCause();
		assertEquals("Nested", lvNestedExc.getMessage());
		assertTrue(5 < lvNestedExc.getStackTrace().length);
	}


	public void testSerializeWithPropertyFilter() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		ObjectSerializer lvSerializer = new ObjectSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build" });
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp);
		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	public void testSerializeWithoutPropertyFilter() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date(1));
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		ObjectSerializer lvSerializer = new ObjectSerializer();
		// filter -> null Array
		Object lvTemp = lvSerializer.serialize(lvCar, null);
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp);
		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertEquals(new Date(1), lvCarAfter.getBuild());

		
		
		// filter -> empty Array
		lvTemp = lvSerializer.serialize(lvCar, new String[0]);
		lvCarAfter = (Car) lvSerializer.deserialize(lvTemp);
		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertEquals(new Date(1), lvCarAfter.getBuild());

	}

	public void testSerializeWithPropertyFilterAndFilteringClass() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		ObjectSerializer lvSerializer = new ObjectSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build", "class" });
		Object lvResult = lvSerializer.deserialize(lvTemp);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey("class"));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp, Car.class); 		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	public void testSerializeWithPropertyFilterAndFilteringUniqueId() throws Exception {
		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		lvCar.setDescription("This is my car");
		
		assertNotNull(lvCar.getBuild());
		
		ObjectSerializer lvSerializer = new ObjectSerializer();
		Object lvTemp = lvSerializer.serialize(lvCar, new String[] { "build", UniqueIdGenerator.UNIQUE_ID_PROPERTY});
		Object lvResult = lvSerializer.deserialize(lvTemp, HashMap.class);
		
		assertTrue("Is not a Map: " + lvResult.getClass().getName(), lvResult instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvResult;
		assertFalse(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertFalse(lvMap.containsKey("build"));
		assertTrue(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey("description"));
		assertTrue(lvMap.containsKey("class"));
		
		Car lvCarAfter = (Car) lvSerializer.deserialize(lvTemp, Car.class); 		
		assertEquals(lvCar.getName(), lvCarAfter.getName());
		assertEquals(lvCar.getDescription(), lvCarAfter.getDescription());
		assertNull(lvCarAfter.getBuild());
	}

	public void testMultipleReferences() throws Exception {
	    BBean b = new BBean();
	    ABean a = new ABean();
	    a.setFirstRef(b);
	    a.setSecondRef(b);
	    a.setThirdRef(b);
	    a.setFourthRef(b);
	    
	    Serializer lvSerializer = new ObjectSerializer();

	    Object lvResult = lvSerializer.serialize(a);
	    ABean ades = (ABean) lvSerializer.deserialize(lvResult);
	    BBean bBeanAfter = ades.getFirstRef();
	    assertTrue (bBeanAfter != null); 
	    assertTrue (ades.getSecondRef() != null); 
	    assertTrue (ades.getSecondRef() == bBeanAfter);
	    assertTrue (ades.getThirdRef() != null); 
	    assertTrue (ades.getThirdRef() == bBeanAfter);
	    assertTrue (ades.getFourthRef() != null);
	    assertTrue (ades.getFourthRef() == bBeanAfter);
	} 

	public void testTransformDefaultMutableTreeNode() throws Exception {
		String lvFilter[] = new String [] {"class", "parent", "children", "userObject", "allowsChildren"};
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, lvFilter);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
		root.add(child);
		
		Serializer s = new ObjectSerializer();
		Object object = s.serialize(root);
		DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) s.deserialize(object);

		assertEquals("ROOT", rootAfter.getUserObject());
		assertEquals(1, rootAfter.getChildCount());
		assertEquals("Child", ((DefaultMutableTreeNode) rootAfter.getChildAt(0)).getUserObject());

		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}

}
