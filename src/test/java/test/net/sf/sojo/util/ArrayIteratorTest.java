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
package test.net.sf.sojo.util;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import net.sf.sojo.util.ArrayIterator;

import test.net.sf.sojo.model.Node;
import junit.framework.TestCase;

public class ArrayIteratorTest extends TestCase {

	public void testIterateWithLength0() throws Exception {
		String s[] = new String[] {};
		ArrayIterator lvIterator = new ArrayIterator(s);
		int pos = 1;
		while (lvIterator.hasNext()) {
			assertEquals(lvIterator.next(), Integer.toString(pos++));
		}
	}

	public void testIterateStringArray() throws Exception {
		String s[] = new String[] {"1", "2", "3"};
		ArrayIterator lvIterator = new ArrayIterator(s);
		int pos = 1;
		while (lvIterator.hasNext()) {
			assertEquals(lvIterator.next(), Integer.toString(pos++));
		}
	}
	
	public void testIterateNodeArray() throws Exception {
		Node s[] = new Node[] {new Node( "1"), new Node("2"), new Node("3") };
		ArrayIterator lvIterator = new ArrayIterator(s);
		int pos = 1;
		while (lvIterator.hasNext()) {
			Node n = (Node) lvIterator.next();
			assertEquals(n.getName(), Integer.toString(pos++));
		}
	}

	public void testIterateObjectArray() throws Exception {
		Object s[] = new Object[] {"1", new BigDecimal("2"), new Long("3") };
		ArrayIterator lvIterator = new ArrayIterator(s);
		int pos = 1;
		while (lvIterator.hasNext()) {
			assertEquals(lvIterator.next().toString(), Integer.toString(pos++));
		}
	}

	public void testIterateNullArray() throws Exception {
		try {
			new ArrayIterator(null);
			fail("Array must be different from NULL");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	public void testIterateInvalidArray() throws Exception {
		try {
			new ArrayIterator("");
			fail("Param must be an Array");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	public void testPos() throws Exception {
		ArrayIterator lvArrayIterator = new ArrayIterator(new String[] { "1" });
		assertEquals(0, lvArrayIterator.getPos());
		lvArrayIterator.next();
		assertEquals(1, lvArrayIterator.getPos());
	}
	
	public void testRemove() throws Exception {
		ArrayIterator lvArrayIterator = new ArrayIterator(new String[] { "1" });
		assertEquals(1, lvArrayIterator.getLength());
		lvArrayIterator.remove();
		assertEquals(1, lvArrayIterator.getLength());
	}
	
	public void testNextAfterLastPosition() throws Exception {
		ArrayIterator lvArrayIterator = new ArrayIterator(new String[] { "1" });
		assertEquals("1",lvArrayIterator.next());
		try {
			lvArrayIterator.next();
			fail("Read after last position not possible");
		} catch (NoSuchElementException e) {
			assertNotNull(e);
		}
	}

}
