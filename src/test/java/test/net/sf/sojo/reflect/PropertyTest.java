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
import java.lang.reflect.Method;

import net.sf.sojo.core.reflect.Property;

import test.net.sf.sojo.model.Car;

import junit.framework.TestCase;

public class PropertyTest extends TestCase {
	
	public void testConstructorMethod() throws Exception {
		Method lvMethod = Car.class.getMethod("setName", new Class [] { String.class});
		Property lvProperty = new Property(lvMethod);
		assertEquals(Property.PROPERTY_TYPE_METHOD, lvProperty.getPropertyType());
	}

	public void testConstructorField() throws Exception {
		Field lvField = Car.class.getDeclaredField("name");
		Property lvProperty = new Property(lvField);
		assertEquals(Property.PROPERTY_TYPE_FIELD, lvProperty.getPropertyType());
	}

	public void testConstructorInvalidObject() throws Exception {
		try {
			new Property(Car.class.getConstructor());
			fail("Expected Method or Field and not an Constructor");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testExecuteSetPropertyMethod() throws Exception {
		Method lvMethod = Car.class.getMethod("setName", new Class [] { String.class});
		Property lvProperty = new Property(lvMethod);
		Car lvCar = new Car();
		lvProperty.executeSetValue(lvCar, "BMW");
		assertEquals("BMW", lvCar.getName());
	}

	public void testExecuteSetPropertyField() throws Exception {
		Field lvField = Car.class.getDeclaredField("name");
		Property lvProperty = new Property(lvField);
		Car lvCar = new Car();
		lvProperty.executeSetValue(lvCar, "Audi");
		assertEquals("Audi", lvCar.getName());
	}

	public void testExecuteGetPropertyMethod() throws Exception {
		Method lvMethod = Car.class.getMethod("getName");
		Property lvProperty = new Property(lvMethod);
		Object lvValue = lvProperty.executeGetValue(new Car("Trabant"));
		assertEquals("Trabant", lvValue);
	}

	public void testExecuteGetPropertyField() throws Exception {
		Field lvField = Car.class.getDeclaredField("name");
		Property lvProperty = new Property(lvField);
		Object lvValue = lvProperty.executeGetValue(new Car("Jaguar"));
		assertEquals("Jaguar", lvValue);
	}

	
	public void testGetParameterTypesMethod() throws Exception {
		Method lvMethod = Car.class.getMethod("getName");
		Property lvProperty = new Property(lvMethod);
		assertEquals(null, lvProperty.getParameterType());
		
		lvMethod = Car.class.getMethod("setName", new Class[] { String.class });
		lvProperty = new Property(lvMethod);
		assertEquals(String.class, lvProperty.getParameterType());
	}

	public void testGetParameterTypesField() throws Exception {
		Field lvField = Car.class.getDeclaredField("name");
		Property lvProperty = new Property(lvField);
		assertEquals(String.class, lvProperty.getParameterType());
	}

}
