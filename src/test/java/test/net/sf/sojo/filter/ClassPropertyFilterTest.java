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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.UniqueIdGenerator;
import net.sf.sojo.core.conversion.ComplexBean2MapConversion;
import net.sf.sojo.core.conversion.IterateableMap2BeanConversion;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.core.filter.ClassPropertyFilterHandlerImpl;
import net.sf.sojo.core.filter.ClassPropertyFilterHelper;
import net.sf.sojo.optional.filter.attributes.ClassPropertyFilterHanlderForAttributes;
import test.net.sf.sojo.model.Car;
import test.net.sf.sojo.model.Node;

public class ClassPropertyFilterTest extends TestCase {

	public void testSetClassName() throws Exception {
		ClassPropertyFilter lvFilter = new ClassPropertyFilter(Long.class);
		assertEquals(Long.class,lvFilter.getFilterClass());
		
		lvFilter = new ClassPropertyFilter(Car.class);
		assertEquals(Car.class, lvFilter.getFilterClass());
	}
	
	public void testSetPropertiesWithConstructor() throws Exception {
		ClassPropertyFilter lvFilter = new ClassPropertyFilter(null, null);
		assertEquals(0, lvFilter.getPropertySize());
		assertNull(lvFilter.getFilterClass());
		
		lvFilter = new ClassPropertyFilter(String.class, null);
		assertEquals(String.class, lvFilter.getFilterClass());
	}

	public void testSetPropertiesWithConstructor2() throws Exception {
		ClassPropertyFilter lvFilter = new ClassPropertyFilter(String.class, new String[0]);
		assertEquals(0, lvFilter.getPropertySize());
		assertEquals(String.class, lvFilter.getFilterClass());
		
		lvFilter = new ClassPropertyFilter(String.class, new String[] { "name", "bar" });
		assertEquals(String.class, lvFilter.getFilterClass());
		assertEquals(2, lvFilter.getPropertySize());
		assertTrue(lvFilter.isKnownProperty("bar"));
		
		lvFilter.removeProperty("bar");
		assertEquals(1, lvFilter.getPropertySize());
		assertFalse(lvFilter.isKnownProperty("bar"));
	}
	
	public void testGetAllProperties() throws Exception {
		ClassPropertyFilter lvFilter = new ClassPropertyFilter();
		assertEquals(0, lvFilter.getAllProperties().length);
		
		lvFilter = new ClassPropertyFilter(Car.class);
		assertEquals(0, lvFilter.getAllProperties().length);
		
		lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class);
		assertEquals(4, lvFilter.getAllProperties().length);
	}
	
	public void testCreateClassPropertyFilterByClass() throws Exception {
		ClassPropertyFilter lvClassPropertyFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class);
		assertEquals(Car.class,lvClassPropertyFilter.getFilterClass());
		assertEquals(4, lvClassPropertyFilter.getPropertySize());		
	}
	
	public void testCreateClassPropertyFilterByClassAndRemoveOneProperty() throws Exception {
		ClassPropertyFilter lvClassPropertyFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).removeProperty("build");
		assertEquals(3, lvClassPropertyFilter.getPropertySize());		
	}

	public void testCreateClassPropertyFilterByClassAndAddOneProperty() throws Exception {
		ClassPropertyFilter lvClassPropertyFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).addProperty("xyz");
		assertEquals(5, lvClassPropertyFilter.getPropertySize());		
	}
	
	public void testConvertWithClassPropertyFilter() throws Exception {
		Converter lvConverter = new Converter();
		ClassPropertyFilterHandlerImpl lvHandler = new ClassPropertyFilterHandlerImpl();
		ClassPropertyFilter lvClassPropertyFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).removeProperty("build");
		lvHandler.addClassPropertyFilter(lvClassPropertyFilter);
		lvConverter.setClassPropertyFilterHandler(lvHandler);
		
		Date d = new Date(987654321);
		Car lvCar = new Car("MyCar");
		lvCar.setBuild(d);
		
		lvConverter.addConversion(new ComplexBean2MapConversion());
		lvConverter.addConversion(new IterateableMap2BeanConversion());
		Object o = lvConverter.convert(lvCar);
		Car lvCarAfter = (Car) lvConverter.convert(o);
		
		assertNull(lvCarAfter.getName());
		assertEquals(d, lvCarAfter.getBuild());
		

		lvHandler.removeClassPropertyFilterByClassName(Car.class);
		o = lvConverter.convert(lvCar);
		lvCarAfter = (Car)  lvConverter.convert(o);

		assertEquals("MyCar", lvCarAfter.getName());
		assertNotNull(lvCarAfter.getBuild());

	}
	
	public void testConvertWithObjectUtilAndWithClassPropertyFilter() throws Exception {
		ObjectUtil lvObjectUtil = new ObjectUtil();

		ClassPropertyFilter lvClassPropertyFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class).removeProperty("name");
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(lvClassPropertyFilter));

		Car lvCar = new Car("BMW");
		lvCar.setBuild(new Date());
		assertNotNull(lvCar.getBuild());
		
		Object o = lvObjectUtil.makeSimple(lvCar);
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(o);
		
		assertEquals("BMW", lvCarAfter.getName());
		assertNull(lvCarAfter.getBuild());
	}
	
	public void testValidateClassPropertyFilterWithNull() throws Exception {
		String lvMessage = ClassPropertyFilterHelper.validateClassPropertyFilter(null);
		assertNull(lvMessage);
	}
	
	public void testValidateClassPropertyFilterNotValidProperty() throws Exception {
		ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class);
		String lvMessage = ClassPropertyFilterHelper.validateClassPropertyFilter(lvFilter);
		assertNull(lvMessage);
		
		lvFilter.addProperty("foo");
		lvMessage = ClassPropertyFilterHelper.validateClassPropertyFilter(lvFilter);
		assertNotNull(lvMessage);
		assertTrue(lvMessage.indexOf("not a valid property") > 0);
	}
	
	public void testValidateClassPropertyFilterNotValidClassName() throws Exception {
		ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class);
		String lvMessage = ClassPropertyFilterHelper.validateClassPropertyFilter(lvFilter);
		assertNull(lvMessage);
	}

	public void testRemovePropertyClass() throws Exception {
		ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(Car.class);
		assertEquals(4, lvFilter.getPropertySize());
		
		lvFilter.removeProperty("class");
		assertEquals(4, lvFilter.getPropertySize());
	}

	public void testFilterPropertiesByHashMap() throws Exception {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar = new Car("a car");
		lvCar.setBuild(new Date(12345678));
		Map<?, ?> lvSimpe = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);
		assertNotNull(lvSimpe.get("build"));
		assertEquals(new Date(12345678), lvSimpe.get("build"));
		
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(new ClassPropertyFilter(HashMap.class, new String [] { "build" })));
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(lvSimpe);
		
		assertNull(lvCarAfter.getBuild());
		assertEquals("a car", lvCarAfter.getName());
	}
	
	public void testFilterPropertiesByInterfaceMap() throws Exception {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar = new Car("a car");
		lvCar.setBuild(new Date(12345678));
		Map<?, ?> lvSimpe = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);
		assertNotNull(lvSimpe.get("build"));
		assertEquals(new Date(12345678), lvSimpe.get("build"));
		
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(new ClassPropertyFilter(Map.class, new String [] { "build" })));
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(lvSimpe);
		
		assertNull(lvCarAfter.getBuild());
		assertEquals("a car", lvCarAfter.getName());
	}

	public void testFilterPropertiesByInterfaceMapWithAssignableFilterClassesEqualsFalse() throws Exception {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		
		Car lvCar = new Car("a car");
		lvCar.setBuild(new Date(12345678));
		Map<?, ?> lvSimpe = (Map<?, ?>) lvObjectUtil.makeSimple(lvCar);
		assertNotNull(lvSimpe.get("build"));
		assertEquals(new Date(12345678), lvSimpe.get("build"));
		
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(new ClassPropertyFilter(Map.class, new String [] { "build" })));
		((ClassPropertyFilterHandlerImpl) lvObjectUtil.getClassPropertyFilterHandler()).setWithAssignableFilterClasses(false);
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(lvSimpe);
		
		assertFalse(((ClassPropertyFilterHandlerImpl) lvObjectUtil.getClassPropertyFilterHandler()).getWithAssignableFilterClasses());
		assertNotNull(lvCarAfter.getBuild());
		assertEquals(new Date(12345678), lvCarAfter.getBuild());
		assertEquals("a car", lvCarAfter.getName());
	}

	public void testFilterPropertiesWithMapAndKeysAreNotStrings() throws Exception {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);
		lvObjectUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHandlerImpl(new ClassPropertyFilter(Map.class, new String [] { "4711" })));
		
		Map<Comparable<?>, Comparable<?>> lvMap = new HashMap<Comparable<?>, Comparable<?>>();
		lvMap.put("foo", "foo");
		Date lvDate = new Date();
		lvMap.put(lvDate, new Date());
		lvMap.put(new Long(4711), new Long(4711));
		lvMap.put("4711", "4711");		
		assertEquals(4, lvMap.size());
		assertEquals(new Long(4711),lvMap.get(new Long(4711)));
		
		Map<?, ?> lvMapAfter = (Map<?, ?>) lvObjectUtil.makeSimple(lvMap);
		assertEquals(2, lvMapAfter.size());
		assertFalse(lvMapAfter.containsKey(new Long(4711)));
		assertTrue(lvMapAfter.containsKey("foo"));
		assertTrue(lvMapAfter.containsKey(lvDate));
	}

	public void testPropertyFilterPattern() throws Exception {
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setWithSimpleKeyMapper(false);

		ClassPropertyFilterHandlerImpl lvFilterHandler = new ClassPropertyFilterHandlerImpl();
		ClassPropertyFilter lvFilter = new ClassPropertyFilter (Car.class);
		lvFilterHandler.addClassPropertyFilter(lvFilter);
		lvFilter.addProperty("n.*");
		lvObjectUtil.setClassPropertyFilterHandler(lvFilterHandler);
		
		Car lvCar = new Car("MyCar");
		lvCar.setDescription("This is really my car");
		Object o = lvObjectUtil.makeSimple(lvCar);
		Car lvCarAfter = (Car) lvObjectUtil.makeComplex(o);
		
		assertNotNull(lvCarAfter);
		assertEquals("This is really my car", lvCarAfter.getDescription());
		assertNull(lvCarAfter.getName());
	}

	public void testClassPropertyFilterHelperWithNullKey() throws Exception {
		boolean b = ClassPropertyFilterHelper.isPropertyToFiltering(new ClassPropertyFilterHanlderForAttributes(), Car.class, null);
		assertFalse(b);
	}
	
	public void testCycleWithUniqueId() throws Exception {
		ClassPropertyFilterHandlerImpl lvHandler = new ClassPropertyFilterHandlerImpl();
		ClassPropertyFilter lvClassPropertyFilter = new ClassPropertyFilter(Node.class)
					.addProperty("name");
		// if filter UNIQUE_ID_PROPERTY, than you get StackOverflowError
		// .addProperty(UniqueIdGenerator.UNIQUE_ID_PROPERTY);
		lvHandler.addClassPropertyFilter(lvClassPropertyFilter);
		
		Node nRoot = new Node("ROOT");
		Node nChild1 = new Node("Child 1");
		Node nChild2 = new Node("Child 2");
		nRoot.getChildren().add(nChild1);
		nRoot.getChildren().add(nChild2);
		nChild2.setParent(nRoot);
		
		ObjectUtil lvObjectUtil = new ObjectUtil();
		lvObjectUtil.setClassPropertyFilterHandler(lvHandler);
		
		Object lvSimple = lvObjectUtil.makeSimple(nRoot);
		assertNotNull(lvSimple);
		assertTrue("Is not a Map: " + lvSimple.getClass().getName(), lvSimple instanceof Map);
		Map<?, ?> lvMap = (Map<?, ?>) lvSimple;
		assertTrue(lvMap.containsKey("class"));
		assertFalse(lvMap.containsKey("name"));
		assertTrue(lvMap.containsKey(UniqueIdGenerator.UNIQUE_ID_PROPERTY));
	}
}
