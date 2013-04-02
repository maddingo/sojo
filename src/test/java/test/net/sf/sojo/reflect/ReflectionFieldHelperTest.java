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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import net.sf.sojo.core.reflect.ReflectionFieldHelper;

import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.SubNode;
import junit.framework.TestCase;

public class ReflectionFieldHelperTest extends TestCase {

	private Field findFieldByName (Field pvField[], String pvFieldName) {
		Field lvField = null;
		for (int i = 0; i < pvField.length; i++) {
			if (pvField[i].getName().equals(pvFieldName)) {
				lvField = pvField[i];
				break;
			}
		}
		return lvField;
	}
	
	public void testFindFields() throws Exception {
		Field lvFields[] = ReflectionFieldHelper.getAllFieldsByClass(Node.class);

		Field lvField = findFieldByName(lvFields, "parent");
		assertEquals(lvField.getName(), "parent");
		assertEquals(lvField.getType(), Node.class);
		
		lvField = findFieldByName(lvFields, "name");
		assertEquals(lvField.getName(), "name");
		assertEquals(lvField.getType(), String.class);
		
		lvField = findFieldByName(lvFields, "children");
		assertEquals(lvField.getName(), "children");
		assertEquals(lvField.getType(), List.class);
		
		lvField = findFieldByName(lvFields, "namedChildren");
		assertEquals(lvField.getName(), "namedChildren");
		assertEquals(lvField.getType(), Map.class);
	}
	
	public void testFindFields2 () throws Exception {
		Field lvFields[] = ReflectionFieldHelper.getAllFieldsByClass(Node.class); 
		Field lvSubFields[] = ReflectionFieldHelper.getAllFieldsByClass(SubNode.class);
		
		assertTrue(lvFields.length < lvSubFields.length);
		assertEquals( (lvFields.length + 1), lvSubFields.length);
	}
	
	public void testContainsClass() throws Exception {
		boolean b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertFalse(b);
	}
	
	public void testAddAllFields2MapByClass() throws Exception {
		boolean b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertFalse(b);

		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertTrue(b);

		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertTrue(b);

		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
		b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertFalse(b);
	}
	
	public void testAddAllField2MapsByClass() throws Exception {
		Map<?, ?> lvFieldMap = ReflectionFieldHelper.getAllSetFieldMapsByClass(DefaultMutableTreeNode.class, null);
		assertNotNull(lvFieldMap);
		boolean b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertFalse(b);

		// add class
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertTrue(b);
		
		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
		b = ReflectionFieldHelper.containsClass(DefaultMutableTreeNode.class);
		assertFalse(b);
	}
	
	public void testFieldFilter() throws Exception {
		Map<?, ?> lvFieldMap = ReflectionFieldHelper.getAllSetFieldMapsByClass(DefaultMutableTreeNode.class, null);
		// The number of properties depends on the Jvm
		Assert.assertThat(Integer.valueOf(lvFieldMap.size()), CoreMatchers.anyOf(CoreMatchers.is(5), CoreMatchers.is(6)));
		
		// add class
		ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);
		String lvFilter[] = new String [] {"parent", "children", "userObject", "allowsChildren"};
		lvFieldMap = ReflectionFieldHelper.getAllSetFieldMapsByClass(DefaultMutableTreeNode.class, lvFilter);
		assertEquals(4, lvFieldMap.size());
		// remove class
		ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);
	}
	
	public void testSetterAndGetter() throws Exception {
		Map<?, ?> lvMapGet = ReflectionFieldHelper.getAllGetFieldMapsByClass(Car.class, null);
		Map<?, ?> lvMapSet = ReflectionFieldHelper.getAllSetFieldMapsByClass(Car.class, null);
		
		// get contains the same fields, plus the class attribute
		assertEquals(lvMapGet.size(), lvMapSet.size() + 1);
		// control sample
		assertTrue(lvMapGet.containsKey("name"));
		assertTrue(lvMapSet.containsKey("name"));
		
		assertFalse(ReflectionFieldHelper.containsClass(Car.class));
	}
}
