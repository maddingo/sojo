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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.sojo.core.reflect.ReflectionMethodHelper;

import junit.framework.TestCase;
import test.net.sf.sojo.model.BadJavaBean;
import test.net.sf.sojo.model.Bean;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.NotJavaBean;
import test.net.sf.sojo.model.SubNode;

public class ReflectionMethodHelperTest extends TestCase {
	
	public void testIsMethodSetterAndGetterCompliantSimpleType() throws Exception {
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(String.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(Long.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(long.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(Integer.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(int.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(java.util.Date.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(java.sql.Date.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(java.sql.Time.class));
	}
	
	public void testIsMethodSetterAndGetterCompliantBean() throws Exception {
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(Node.class));
	}


	public void testIsMethodSetterAndGetterCompliantList() throws Exception {
		assertFalse(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(ArrayList.class));
		assertFalse(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(HashMap.class));
		
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(List.class));
		assertTrue(ReflectionMethodHelper.isMethodSetterAndGetterCompliant(Set.class));
	}
	
	public void testGetAllSetterMethodWithCache() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllSetterMethodWithCache(Node.class, null);
		assertNotNull(lvMap);
		assertTrue(lvMap.size() > 1);
		
		lvMap = ReflectionMethodHelper.getAllSetterMethodWithCache(Node.class, null);
		assertNotNull(lvMap);
		assertTrue(lvMap.size() > 1);
	}
	
	public void testGetAllNotEqualsGetterAndSetterAndRemoveThisProperties() throws Exception {
		Map<Object, Object> lvMapSetter = ReflectionMethodHelper.getAllSetterMethod(SubNode.class);
		Map<Object, Object> lvMapGetter = ReflectionMethodHelper.getAllGetterMethod(SubNode.class);
		assertNotNull(lvMapSetter);
		assertNotNull(lvMapGetter);
		
		assertTrue( (lvMapSetter.size() + 1) == lvMapGetter.size());
		assertTrue(lvMapGetter.containsKey("class"));
		assertFalse(lvMapSetter.containsKey("class"));

		assertTrue(lvMapGetter.containsKey("withOutGetMethod"));
		assertFalse(lvMapSetter.containsKey("withOutGetMethod"));

		assertFalse(lvMapGetter.containsKey("ohterWithOutGetMethod"));
		assertTrue(lvMapSetter.containsKey("ohterWithOutGetMethod"));
		
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllNotEqualsGetterAndSetterAndRemoveThisProperties(lvMapGetter, lvMapSetter);
		// one getter was removed: getWithOutGetMethod
		assertEquals(lvMap.size() + 1, lvMapGetter.size());
		assertEquals(lvMap.size(), lvMapSetter.size());
		
		assertFalse(lvMap.containsKey("withOutGetMethod"));
		assertFalse(lvMap.containsKey("ohterWithOutGetMethod"));
		assertTrue(lvMapSetter.containsKey("ohterWithOutGetMethod"));
	}

	public void testGetAllGetterAndSetterMethod() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(Node.class, -7);
		assertNotNull(lvMap);
		assertEquals(0, lvMap.size());
	}
	
	public void testIsMethodSetterAndGetterCompliant() throws Exception {
		boolean lvComplaint = ReflectionMethodHelper.isMethodSetterAndGetterCompliant(Node.class);
		assertTrue(lvComplaint);
		
		lvComplaint = ReflectionMethodHelper.isMethodSetterAndGetterCompliant(SubNode.class);
		assertTrue(lvComplaint);
		
		lvComplaint = ReflectionMethodHelper.isMethodSetterAndGetterCompliant(NotJavaBean.class);
		assertFalse(lvComplaint);
		
		lvComplaint = ReflectionMethodHelper.isMethodSetterAndGetterCompliant(ReflectionMethodHelper.class);
		assertFalse(lvComplaint);
	}
	
	public void testTryToModifyGetterMap() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(Car.class, ReflectionMethodHelper.GET_METHOD);
		try {
			lvMap.remove("class");
			fail("Changes in the map are not supported!");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testTryToModifySetterMap() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(Car.class, ReflectionMethodHelper.SET_METHOD);
		try {
			lvMap.remove("name");
			fail("Changes in the map are not supported!");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}
		
	}

	public void testTryToModifyGetterMapFromCache() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterMethodWithCache(Car.class, null);
		try {
			lvMap.remove("class");
			fail("Changes in the map are not supported!");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}
	
	public void testTryToModifySetterMapFromCache() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterMethodWithCache(Car.class, null);
		try {
			lvMap.remove("name");
			fail("Changes in the map are not supported!");
		} catch (UnsupportedOperationException e) {
			assertNotNull(e);
		}
	}

	public void testGetAllGetterAndSetterMethodFromBadJavaBeanForGet() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(BadJavaBean.class, ReflectionMethodHelper.GET_METHOD);
		assertEquals(1, lvMap.size());
		assertEquals(BadJavaBean.class.getName(), lvMap.get("class"));
	}

	public void testGetAllGetterAndSetterMethodFromBadJavaBeanForSet() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(BadJavaBean.class, ReflectionMethodHelper.SET_METHOD);
		assertEquals(0, lvMap.size());
		assertFalse(lvMap.containsKey("class"));
	}
	
	public void testBeanBooleanIsProperty() throws Exception {
		Map<?, ?> lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(Bean.class, ReflectionMethodHelper.SET_METHOD);
		assertEquals(4, lvMap.size());
		assertFalse(lvMap.containsKey("class"));
		assertTrue(lvMap.containsKey("retired"));
		
		lvMap = ReflectionMethodHelper.getAllGetterAndSetterMethod(Bean.class, ReflectionMethodHelper.GET_METHOD);
		assertEquals(5, lvMap.size());
		assertTrue(lvMap.containsKey("class"));
		assertTrue(lvMap.containsKey("retired"));
	}

}
