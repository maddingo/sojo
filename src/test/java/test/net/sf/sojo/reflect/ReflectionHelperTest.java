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
package test.net.sf.sojo.reflect;

import java.beans.beancontext.BeanContext;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;
import net.sf.sojo.core.reflect.ReflectionHelper;
import net.sf.sojo.util.ThrowableWrapper;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.NotJavaBean;
import test.net.sf.sojo.model.Primitive;
import test.net.sf.sojo.model.SubNode;
import test.net.sf.sojo.model.Test2Exception;
import test.net.sf.sojo.model.TestException;
import test.net.sf.sojo.model.TestExceptionWithBadConstructor;

public class ReflectionHelperTest extends TestCase {

	public void testIsSimpleDataType() throws Exception {
		assertTrue(ReflectionHelper.isSimpleType(String.class));
		assertTrue(ReflectionHelper.isSimpleType(Boolean.class));
		assertTrue(ReflectionHelper.isSimpleType(boolean.class));
		assertTrue(ReflectionHelper.isSimpleType(Byte.class));
		assertTrue(ReflectionHelper.isSimpleType(byte.class));
		assertTrue(ReflectionHelper.isSimpleType(Short.class));
		assertTrue(ReflectionHelper.isSimpleType(short.class));
		assertTrue(ReflectionHelper.isSimpleType(Integer.class));
		assertTrue(ReflectionHelper.isSimpleType(int.class));
		assertTrue(ReflectionHelper.isSimpleType(Long.class));
		assertTrue(ReflectionHelper.isSimpleType(long.class));
		assertTrue(ReflectionHelper.isSimpleType(Float.class));
		assertTrue(ReflectionHelper.isSimpleType(float.class));
		assertTrue(ReflectionHelper.isSimpleType(Double.class));
		assertTrue(ReflectionHelper.isSimpleType(double.class));
		assertTrue(ReflectionHelper.isSimpleType(Character.class));
		assertTrue(ReflectionHelper.isSimpleType(char.class));
		assertTrue(ReflectionHelper.isSimpleType(BigDecimal.class));
		assertTrue(ReflectionHelper.isSimpleType(StringBuffer.class));
		assertTrue(ReflectionHelper.isSimpleType(BigInteger.class));
		assertTrue(ReflectionHelper.isSimpleType(Class.class));
		assertTrue(ReflectionHelper.isSimpleType(java.sql.Date.class));
		assertTrue(ReflectionHelper.isSimpleType(java.util.Date.class));
		assertTrue(ReflectionHelper.isSimpleType(Time.class));
		assertTrue(ReflectionHelper.isSimpleType(Timestamp.class));		
	}
	
	public void testIsNotSimpleDataType() throws Exception {
		assertFalse(ReflectionHelper.isSimpleType(ArrayList.class));
		assertFalse(ReflectionHelper.isSimpleType(Math.class));
		
		assertFalse(ReflectionHelper.isSimpleType(new HashMap<Object, Object>()));
		assertFalse(ReflectionHelper.isSimpleType((Object) null));
	}
	
	public void testCreateNewSimpleObject() throws Exception {
		assertNull(ReflectionHelper.createNewSimpleObject(null, "8"));
		assertEquals("8", ReflectionHelper.createNewSimpleObject(Map.class, "8"));
		assertEquals(new Long(0), ReflectionHelper.createNewSimpleObject(Long.class, null));
		
		assertEquals("8", ReflectionHelper.createNewSimpleObject(String.class, "8"));
		assertEquals(Integer.valueOf("7"), ReflectionHelper.createNewSimpleObject(int.class, "7"));
		assertEquals(Integer.valueOf("7"), ReflectionHelper.createNewSimpleObject(Integer.class, "7"));

		assertEquals(new Short("5"), ReflectionHelper.createNewSimpleObject(short.class, "5"));
		assertEquals(new Short("4"), ReflectionHelper.createNewSimpleObject(Short.class, "4"));

		assertEquals(new Byte("3"), ReflectionHelper.createNewSimpleObject(byte.class, "3"));
		assertEquals(new Byte("2"), ReflectionHelper.createNewSimpleObject(Byte.class, "2"));

		assertEquals(new Long(1), ReflectionHelper.createNewSimpleObject(long.class, "1"));
		assertEquals(new Long(1), ReflectionHelper.createNewSimpleObject(Long.class, "1"));
		
		assertEquals(new Double("9"), ReflectionHelper.createNewSimpleObject(double.class, "9"));
		assertEquals(new Double("10"), ReflectionHelper.createNewSimpleObject(Double.class, "10"));

		assertEquals(new Float("9"), ReflectionHelper.createNewSimpleObject(float.class, "9"));
		assertEquals(new Float("10"), ReflectionHelper.createNewSimpleObject(Float.class, "10"));

		assertEquals(new Character('a'), ReflectionHelper.createNewSimpleObject(char.class, "a"));
		assertEquals(new Character('A'), ReflectionHelper.createNewSimpleObject(Character.class, "A"));

		assertEquals(Boolean.TRUE, ReflectionHelper.createNewSimpleObject(boolean.class, "true"));
		assertEquals(Boolean.FALSE, ReflectionHelper.createNewSimpleObject(Boolean.class, "false"));

		assertEquals(new BigDecimal("100.1"), ReflectionHelper.createNewSimpleObject(BigDecimal.class, "100.1"));
		assertEquals(new BigInteger("101"), ReflectionHelper.createNewSimpleObject(BigInteger.class, "101"));

		assertEquals(new StringBuffer("sb").toString(), ReflectionHelper.createNewSimpleObject(StringBuffer.class, "sb").toString());
		assertEquals(Time.class, ReflectionHelper.createNewSimpleObject(Class.class, Time.class.getName()));
		assertEquals(Timestamp.class, ReflectionHelper.createNewSimpleObject(Class.class, Timestamp.class));
		assertEquals(Class.class, ReflectionHelper.createNewSimpleObject(Class.class, Class.class));

		long lvDate = new Date().getTime();
		assertEquals(new java.sql.Date(lvDate), ReflectionHelper.createNewSimpleObject(java.sql.Date.class, "" + lvDate));
		assertEquals(new java.util.Date(lvDate), ReflectionHelper.createNewSimpleObject(java.util.Date.class, "" + lvDate));

		assertEquals(new java.sql.Time(lvDate), ReflectionHelper.createNewSimpleObject(java.sql.Time.class, "" + lvDate));
		assertEquals(new java.sql.Timestamp(lvDate), ReflectionHelper.createNewSimpleObject(java.sql.Timestamp.class, "" + lvDate));
	}
	
	public void testCreateNewSimpleObjectWithInvalidClass() throws Exception {
		try {
			ReflectionHelper.createNewSimpleObject(Class.class, "class invalid.Class");
			fail("The String is a invalid Class-name");
		} catch (InstantiationException e) {
			assertNotNull(e);
		}
	}
	
	public void testFindConstructorByParameterTypes() throws Exception {
		Constructor<?> lvConstructor = ReflectionHelper.findConstructorByParameterTypes(ArrayList.class, new Class [] { int.class });
		assertNotNull(lvConstructor);
		assertEquals(1, lvConstructor.getParameterTypes().length);
		assertEquals(int.class, lvConstructor.getParameterTypes()[0]);
		assertEquals(new ArrayList<Object>(), lvConstructor.newInstance(new Object[] { Integer.valueOf("0") }));
	}

	public void testNotFindConstructorByParameterTypes() throws Exception {
		Constructor<?> lvConstructor = ReflectionHelper.findConstructorByParameterTypes(ArrayList.class, new Class [] { String.class });
		assertNull(lvConstructor);
	}
	
	
	public void testIsMapTypeByClass() throws Exception {
		assertTrue(ReflectionHelper.isMapType(Map.class));
		assertTrue(ReflectionHelper.isMapType(HashMap.class));
		assertTrue(ReflectionHelper.isMapType(TreeMap.class));
		assertTrue(ReflectionHelper.isMapType(AbstractMap.class));
		
		assertFalse(ReflectionHelper.isMapType(List.class));
		assertFalse(ReflectionHelper.isMapType(ArrayList.class));
		
		assertFalse(ReflectionHelper.isMapType(null));
		assertFalse(ReflectionHelper.isMapType(String.class));
		
		assertFalse(ReflectionHelper.isMapType(Node.class));
	}
	
	public void testIsIterateableTypeByClass() throws Exception {
		assertTrue(ReflectionHelper.isIterateableType(List.class));
		assertTrue(ReflectionHelper.isIterateableType(ArrayList.class));
		assertTrue(ReflectionHelper.isIterateableType(ArrayList.class));
		assertTrue(ReflectionHelper.isIterateableType(Collection.class));
		assertTrue(ReflectionHelper.isIterateableType(Set.class));
		assertTrue(ReflectionHelper.isIterateableType(HashSet.class));
		assertTrue(ReflectionHelper.isIterateableType(BeanContext.class));
		
		assertFalse(ReflectionHelper.isIterateableType(Map.class));
		assertFalse(ReflectionHelper.isIterateableType(HashMap.class));
		assertFalse(ReflectionHelper.isIterateableType(TreeMap.class));
		assertFalse(ReflectionHelper.isIterateableType(AbstractMap.class));
		
		assertFalse(ReflectionHelper.isIterateableType(null));
		assertFalse(ReflectionHelper.isIterateableType(String.class));
		
		assertFalse(ReflectionHelper.isIterateableType(Node.class));
	}

	public void testIsComplexTypeByClass() throws Exception {
		assertTrue(ReflectionHelper.isComplexType(Node.class));
		
		assertFalse(ReflectionHelper.isComplexType(Map.class));
		assertFalse(ReflectionHelper.isComplexType(HashMap.class));
		
		assertFalse(ReflectionHelper.isComplexType(List.class));
		assertFalse(ReflectionHelper.isComplexType(ArrayList.class));
		
		assertFalse(ReflectionHelper.isComplexType(null));
		assertFalse(ReflectionHelper.isComplexType(String.class));
	}
	
	public void testIsSimpleWithNullValue() throws Exception {
		assertFalse(ReflectionHelper.isSimpleType((Class<?>) null));
	}
	
	public void testAddSimple() throws Exception {
		assertFalse(ReflectionHelper.isSimpleType(Node.class));
		
		ReflectionHelper.addSimpleType(null);
		assertFalse(ReflectionHelper.isSimpleType(Node.class));
		
		ReflectionHelper.addSimpleType(Node.class);
		assertTrue(ReflectionHelper.isSimpleType(Node.class));
		ReflectionHelper.removeSimpleType(Node.class);
		
		ReflectionHelper.removeSimpleType(null);
	}

	public void testIsIterateableType() throws Exception {
		Class<?> c[] = new Class[] { Node.class };
		assertTrue(ReflectionHelper.isIterateableType(c.getClass()));
	}

	public void testCreateBeanFromMapWithNullValue() throws Exception {
		try {
			ReflectionHelper.createBeanFromMap(null, null);
			fail("No map and no type are set!");
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
	}
	
	public void testCreateBeanFromMapWithEmptyMap() throws Exception {
		try {
			ReflectionHelper.createBeanFromMap(new HashMap<Object, Object>(), null);
			fail("No map and no type are set!");
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
		
		Object lvBean = ReflectionHelper.createBeanFromMap(new HashMap<Object, Object>(), Node.class);
		assertNotNull(lvBean);
		assertTrue(lvBean instanceof Node);
		
		lvBean = ReflectionHelper.createBeanFromMap(null, Node.class);
		assertNotNull(lvBean);
		assertTrue(lvBean instanceof Node);
	}

	public void testCreateBeanFromMapWithMap() throws Exception {
		Map<String, Serializable> lvMap = new HashMap<String, Serializable>();
		lvMap.put("class", Node.class.getName());
		Object lvBean = ReflectionHelper.createBeanFromMap(lvMap, null);
		assertNotNull(lvBean);
		assertTrue(lvBean instanceof Node);
		
		lvMap.put("class", Node.class);
		lvMap.put("name", "Test-Name");
		lvBean = ReflectionHelper.createBeanFromMap(lvMap, null);
		assertNotNull(lvBean);
		assertTrue(lvBean instanceof Node);
	}

	public void testCreateBeanFromMapWithInvalidClass() throws Exception {
		try {
			ReflectionHelper.createBeanFromMap(null, Long.class);
			fail("Long is not a Bean!");
		} catch (InstantiationException e) {
			assertNotNull(e);
		}
	}
	
	public void testCreateBeanFromMapWithInvalidClass2() throws Exception {
		try {
			Map<String, String> lvMap = new HashMap<String, String>();
			lvMap.put("class", "NotValidClass");
			ReflectionHelper.createBeanFromMap(lvMap, null);
			fail("Long is not a Bean!");
		} catch (InstantiationException e) {
			assertNotNull(e);
		}
	}

	public void testCreateBeanFromMapWithInvalidClass3() throws Exception {
		try {
			Map<String, String> lvMap = new HashMap<String, String>();
			lvMap.put("class", NotJavaBean.class.getName());
			ReflectionHelper.createBeanFromMap(lvMap, null);
			fail("Long is not a Bean!");
		} catch (IllegalAccessException e) {
			assertNotNull(e);
		}
	}

	public void testIsComplexMapType() throws Exception {
		assertFalse(ReflectionHelper.isComplexMapType(null));
		assertFalse(ReflectionHelper.isComplexMapType(String.class));
		assertFalse(ReflectionHelper.isComplexMapType(Map.class));
		assertFalse(ReflectionHelper.isComplexMapType(HashMap.class));
		assertFalse(ReflectionHelper.isComplexMapType(Collection.class));
		assertFalse(ReflectionHelper.isComplexMapType(List.class));
		assertFalse(ReflectionHelper.isComplexMapType(Set.class));
		assertFalse(ReflectionHelper.isComplexMapType(HashSet.class));
		
		assertTrue(ReflectionHelper.isComplexMapType(Node.class));
		assertTrue(ReflectionHelper.isComplexMapType(SubNode.class));
		assertTrue(ReflectionHelper.isComplexMapType(Primitive.class));
		assertTrue(ReflectionHelper.isComplexMapType(NotJavaBean.class));
	}

	public void testCreateNewThrowableInstanceForNPE() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new NullPointerException("JUnit-Test"));
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertNotNull(lvThrowable);
		assertEquals(lvThrowable.getClass(), NullPointerException.class);
		assertEquals("JUnit-Test", lvThrowable.getMessage());
	}

	public void testCreateNewThrowableInstanceForError() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Error("JUnit-Error"));
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertNotNull(lvThrowable);
		assertEquals(lvThrowable.getClass(), Error.class);
		assertEquals("JUnit-Error", lvThrowable.getMessage());
	}

	public void testCreateNewThrowableInstanceWithInvalidClassName() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Error("JUnit-Error"));
		lvWrapper.setExceptionClassName("not.valid.class.name");
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertNotNull(lvThrowable);
		assertEquals(lvThrowable.getClass(), InstantiationException.class);
		assertEquals("java.lang.ClassNotFoundException: not.valid.class.name", lvThrowable.getMessage());
	}
	
	public void testCreateNewThrowableInstanceWithTestException() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new TestException());
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertNotNull(lvThrowable);
		assertEquals(lvThrowable.getClass(), TestException.class);
		assertNull(lvThrowable.getMessage());
	}

	public void testCreateNewThrowableInstanceWithTest2Exception() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Test2Exception("JUnit-Message", new Exception("Cause")));
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertNotNull(lvThrowable);
		assertEquals(lvThrowable.getClass(), Test2Exception.class);
		assertEquals("JUnit-Message", lvThrowable.getMessage());
		
		Throwable lvCause = lvThrowable.getCause();
		assertNotNull(lvCause);
		assertEquals("Cause", lvCause.getMessage());
		assertNull(lvCause.getCause());
	}

	public void testCreateNewThrowableInstanceWithDoubleNestedException() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Test2Exception("JUnit-Message", 
															new Exception("Cause", 
																new NullPointerException("Cause/Cause"))));
		
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertNotNull(lvThrowable);
		assertEquals(lvThrowable.getClass(), Test2Exception.class);
		assertEquals("JUnit-Message", lvThrowable.getMessage());
		
		Throwable lvCause = lvThrowable.getCause();
		assertNotNull(lvCause);
		assertEquals("Cause", lvCause.getMessage());
		assertNotNull(lvCause.getCause());
	}

	public void testCreateNewThrowableInstanceWithTargetClass() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("Test-Exception"));
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertEquals(lvThrowable.getClass(), Exception.class);
		assertEquals("Test-Exception", lvThrowable.getMessage());
	}

	public void testCreateNewThrowableInstanceWithBadTargetClass() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("Test-Exception"));
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper, String.class);
		assertEquals(InstantiationException.class, lvThrowable.getClass());
		assertTrue(lvThrowable.getMessage().startsWith("The Class: " + String.class.getName()));
	}

	public void testExceptionWithoutConstructor() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new TestExceptionWithBadConstructor(new BigDecimal("0.07")));
		Throwable lvThrowable = ReflectionHelper.createThrowable(lvWrapper);
		assertEquals(InstantiationException.class, lvThrowable.getClass());
	}
	
	public void testCreateNewIteratableInstance() throws Exception {
		try {
			ReflectionHelper.createNewIterableInstance(Long.class, 0);
			fail("Long is a invalid Class");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}

	}
}
