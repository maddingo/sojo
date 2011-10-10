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
package test.net.sf.sojo.optional.filter;

import java.util.Date;

import junit.framework.TestCase;
import net.sf.sojo.common.ObjectUtil;
import net.sf.sojo.core.filter.ClassPropertyFilter;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.json.JsonSerializer;
import net.sf.sojo.interchange.object.ObjectSerializer;
import net.sf.sojo.interchange.xmlrpc.XmlRpcSerializer;
import net.sf.sojo.optional.filter.attributes.ClassPropertyFilterHanlderForAttributes;
import test.net.sf.sojo.model.Customer;
import test.net.sf.sojo.optional.filter.model.Account;

public class ClassPropertyFilterHanlderForAttributesTest extends TestCase {

	private ClassPropertyFilterHanlderForAttributes filterHanlder = new ClassPropertyFilterHanlderForAttributes();
	
	public void testfindFilterClass() throws Exception {
		ClassPropertyFilter lvFilter = filterHanlder.getClassPropertyFilterByClass(Account.class);
		assertEquals(Account.class, lvFilter.getFilterClass());
	}
	
	public void testFindFilterProperties() throws Exception {
		ClassPropertyFilter lvFilter = filterHanlder.getClassPropertyFilterByClass(Account.class);
		assertEquals(3, lvFilter.getPropertySize());
		assertTrue(lvFilter.isKnownProperty("amount"));
		assertFalse(lvFilter.isKnownProperty("accountNumber"));
		assertTrue(lvFilter.isKnownProperty("aAccountNumber"));
		assertTrue(lvFilter.isKnownProperty("createDate"));
	}
	
	public void testFindFilterByClassWithoutAnnotaions() throws Exception {
		ClassPropertyFilter lvFilter = filterHanlder.getClassPropertyFilterByClass(Customer.class);
		assertNull(lvFilter);
		
		// get from cache, must be null too
		 lvFilter = filterHanlder.getClassPropertyFilterByClass(Customer.class);
		assertNull(lvFilter);
	}

	public void testFilterPropertiesWithObjectUtil() throws Exception {
		ObjectUtil lvUtil = new ObjectUtil(); 
		Account lvAccount = new Account();
		lvAccount.setAccountNumber("007");
		lvAccount.setAmount(47.11);
		assertNull(lvAccount.getCreateDate());
		Date lvDate = new Date();
		lvAccount.setCreateDate(lvDate);
		
		Object o = lvUtil.makeSimple(lvAccount);
		Account lvAccountAfter = (Account) lvUtil.makeComplex(o);
		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(47.11, lvAccountAfter.getAmount(), 0.0);
		assertEquals(lvDate, lvAccountAfter.getCreateDate());
		
		// set filter
		lvUtil.setClassPropertyFilterHandler(new ClassPropertyFilterHanlderForAttributes());
		o = lvUtil.makeSimple(lvAccount);
		lvAccountAfter = (Account) lvUtil.makeComplex(o);		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(0.0, lvAccountAfter.getAmount(), 0.0);
		assertNull(lvAccountAfter.getCreateDate());
		
		// remove filter
		lvUtil.setClassPropertyFilterHandler(null);
		o = lvUtil.makeSimple(lvAccount);
		lvAccountAfter = (Account) lvUtil.makeComplex(o);
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(47.11, lvAccountAfter.getAmount(), 0.0);
		assertEquals(lvDate, lvAccountAfter.getCreateDate());
	}

	public void testFilterPropertiesWithJsonSerializer() throws Exception {
		Serializer  lvSerializer = new JsonSerializer();
		Account lvAccount = new Account();
		lvAccount.setAccountNumber("007");
		lvAccount.setAmount(47.11);
		
		Object o = lvSerializer.serialize(lvAccount);
		Account lvAccountAfter = (Account) lvSerializer.deserialize(o);
		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(47.11, lvAccountAfter.getAmount(), 0.0);

		// set filter
		lvSerializer.setClassPropertyFilterHandler(new ClassPropertyFilterHanlderForAttributes());
		o = lvSerializer.serialize(lvAccount);
		lvAccountAfter = (Account) lvSerializer.deserialize(o);		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(0.0, lvAccountAfter.getAmount(), 0.0);
		
		// remove filter
		lvSerializer.setClassPropertyFilterHandler(null);
		o = lvSerializer.serialize(lvAccount);
		lvAccountAfter = (Account) lvSerializer.deserialize(o);
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(47.11, lvAccountAfter.getAmount(), 0.0);
	}

	public void testFilterPropertiesWithXmlRpcSerializer() throws Exception {
		XmlRpcSerializer  lvSerializer = new XmlRpcSerializer();
		lvSerializer.setReturnValueAsList(false);
		
		Account lvAccount = new Account();
		lvAccount.setAccountNumber("007");
		lvAccount.setAmount(47.11);
		
		Object o = lvSerializer.serialize(lvAccount);
		Account lvAccountAfter = (Account) lvSerializer.deserialize(o);
		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(47.11, lvAccountAfter.getAmount(), 0.0);
		
		lvSerializer.setClassPropertyFilterHandler(new ClassPropertyFilterHanlderForAttributes());
		o = lvSerializer.serialize(lvAccount);
		lvAccountAfter = (Account) lvSerializer.deserialize(o);
		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(0.0, lvAccountAfter.getAmount(), 0.0);
	}

	public void testFilterPropertiesWithObjectSerializer() throws Exception {
		Serializer  lvSerializer = new ObjectSerializer();
		Account lvAccount = new Account();
		lvAccount.setAccountNumber("007");
		lvAccount.setAmount(47.11);
		
		Object o = lvSerializer.serialize(lvAccount);
		Account lvAccountAfter = (Account) lvSerializer.deserialize(o);
		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(47.11, lvAccountAfter.getAmount(), 0.0);
		
		lvSerializer.setClassPropertyFilterHandler(new ClassPropertyFilterHanlderForAttributes());
		o = lvSerializer.serialize(lvAccount);
		lvAccountAfter = (Account) lvSerializer.deserialize(o);
		
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(0.0, lvAccountAfter.getAmount(), 0.0);
	}

	public void testRemoveFilterProperties() throws Exception {
		ClassPropertyFilter lvFilter = filterHanlder.getClassPropertyFilterByClass(Account.class);
		assertEquals(3, lvFilter.getPropertySize());
		
		lvFilter.removeProperty("aAccountNumberXXX");
		assertEquals(3, lvFilter.getPropertySize());

		lvFilter.removeProperty("aAccountNumber");
		assertEquals(2, lvFilter.getPropertySize());
		assertFalse(lvFilter.isKnownProperty("accountNumber"));

		assertTrue(lvFilter.isKnownProperty("createDate"));
		lvFilter.removeProperty("createDate");
		assertEquals(1, lvFilter.getPropertySize());
		assertFalse(lvFilter.isKnownProperty("createDate"));

		
		ClassPropertyFilter lvFilter2 = filterHanlder.getClassPropertyFilterByClass(Account.class);
		assertSame(lvFilter, lvFilter2);
		
		ObjectUtil lvUtil = new ObjectUtil();
		lvUtil.setClassPropertyFilterHandler(filterHanlder);
		Account lvAccount = new Account();
		lvAccount.setAccountNumber("007");
		lvAccount.setAmount(47.11);
		assertNull(lvAccount.getCreateDate());
		Date lvDate = new Date();
		lvAccount.setCreateDate(lvDate);
		
		Object o = lvUtil.makeSimple(lvAccount);
		Account lvAccountAfter = (Account) lvUtil.makeComplex(o);
		
		assertEquals(lvDate, lvAccountAfter.getCreateDate());
		assertEquals("007", lvAccountAfter.getAccountNumber());
		assertEquals(0.0, lvAccountAfter.getAmount(), 0.0);
	}
	
}
