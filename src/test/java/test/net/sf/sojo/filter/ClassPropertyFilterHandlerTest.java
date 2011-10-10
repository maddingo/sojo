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
package test.net.sf.sojo.filter;

import java.util.Date;

import junit.framework.TestCase;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.model.Node;
import test.net.sf.sojo.model.Primitive;

public class ClassPropertyFilterHandlerTest extends TestCase {

	public void testAddTwoClasses2ClassPropertyFilterHandler() throws Exception {
		ClassPropertyFilter lvFilter1 = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class);
		ClassPropertyFilter lvFilter2 = new ClassPropertyFilter(Customer.class, new String[] { "firstName", "lastName" });
		
		ClassPropertyFilterHandlerImpl lvHandler = new ClassPropertyFilterHandlerImpl();
		lvHandler.addClassPropertyFilter(lvFilter1);
		lvHandler.addClassPropertyFilter(lvFilter2);
		
		assertEquals(2, lvHandler.getClassPropertyFilterSize());
		assertEquals(4, lvFilter1.getPropertySize());
		assertEquals(2, lvFilter2.getPropertySize());
	}

	public void testAddClassPropertyFilterByClasses() throws Exception {
		ClassPropertyFilterHandlerImpl lvHandler = new ClassPropertyFilterHandlerImpl();
		assertEquals(0, lvHandler.getClassPropertyFilterSize());
		
		lvHandler.addClassPropertyFilterByFilterClasses(null);
		assertEquals(0, lvHandler.getClassPropertyFilterSize());
		
		lvHandler.addClassPropertyFilterByFilterClasses(new Class[] { Car.class, Node.class } );
		assertEquals(2, lvHandler.getClassPropertyFilterSize());
	}
	
	public void testAddClassesAndConvertNodeWithoutFilter() throws Exception {
		ClassPropertyFilterHandlerImpl lvHandler = new ClassPropertyFilterHandlerImpl();
		lvHandler.addClassPropertyFilterByFilterClasses(new Class[] { Car.class, Customer.class, Primitive.class });
	
		assertEquals(3, lvHandler.getClassPropertyFilterSize());
		
		Node n = new Node("Node");
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setClassPropertyFilterHandler(lvHandler);
		Object o = lvObjectUtil.makeSimple(n);
		Node lvNodeAfter = (Node) lvObjectUtil.makeComplex(o);
		
		assertEquals("Node", lvNodeAfter.getName());
	}
	
	public void testRemovePropertiesFromFilterHandler() throws Exception {
		ClassPropertyFilterHandlerImpl lvHandler = new ClassPropertyFilterHandlerImpl(new Class[] { Car.class, Customer.class, Primitive.class, Node.class, String.class });
		lvHandler.getClassPropertyFilterByClass(Node.class).removeProperties(null);
		lvHandler.getClassPropertyFilterByClass(Car.class).removeProperties(new String [] { "name", "build", "foo", "bar"});

		Car lvCar = new Car("MyCar");
		lvCar.setBuild(new Date(123456));
		lvCar.setDescription("bla bla");
		assertNotNull(lvCar.getDescription());
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setClassPropertyFilterHandler(lvHandler);
		Object o = lvObjectUtil.makeSimple(lvCar);
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(o);

		assertEquals("MyCar", lvCarAfter.getName());
		assertEquals(new Date(123456), lvCarAfter.getBuild());
		assertNull(lvCarAfter.getDescription());
	}

	public void testname() throws Exception {
		String lvMsg = ClassPropertyFilterHelper.validateClassPropertyFilter(new ClassPropertyFilter());
		assertNotNull(lvMsg);
		assertTrue(lvMsg.indexOf("is not a valid") > 0);
	}
}
