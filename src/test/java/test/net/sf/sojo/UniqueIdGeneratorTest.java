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
package test.net.sf.sojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.net.sf.sojo.model.Node;
import net.sf.sojo.core.UniqueIdGenerator;
import junit.framework.TestCase;

public class UniqueIdGeneratorTest extends TestCase {

	public void testMinimalUniqueID() throws Exception {
		UniqueIdGenerator lvIdGenerator = new UniqueIdGenerator();
		assertEquals(UniqueIdGenerator.MINIMAL_UNIQUE_ID, lvIdGenerator.getMinimalUniqueID());
		
		lvIdGenerator.setMinimalUniqueID(-1);
		assertEquals(-1, lvIdGenerator.getMinimalUniqueID());
	}
	
	public void testithHashCodeInUniqueId() throws Exception {
		UniqueIdGenerator lvIdGenerator = new UniqueIdGenerator();
		assertFalse(lvIdGenerator.getWithHashCodeInUniqueId());
		String lvId = lvIdGenerator.getUniqueId(new Node());
		assertEquals("0", lvId);
		
		lvIdGenerator.setWithHashCodeInUniqueId(true);
		assertTrue(lvIdGenerator.getWithHashCodeInUniqueId());
		Node n = new Node();
		lvId = lvIdGenerator.getUniqueId(n);
		assertEquals("1-" + n.hashCode(), lvId);
	}
	
	public void testUniqueId() throws Exception {
		UniqueIdGenerator lvIdGenerator = new UniqueIdGenerator();
		assertNull(lvIdGenerator.getUniqueId(null));
		
		assertNotNull(lvIdGenerator.getUniqueId("--"));
		assertEquals("0", lvIdGenerator.getUniqueId("--"));
	}
	
	public void testUniqueIdOfCollection() throws Exception {
		UniqueIdGenerator lvIdGenerator = new UniqueIdGenerator();
		assertNull(lvIdGenerator.getUniqueId(new ArrayList<Object>()));

		List<Object> l = new ArrayList<Object>();
		l.add("aa");
		l.add(new Double(0.07));
		assertNull(lvIdGenerator.getUniqueId(l));
		assertEquals(0, lvIdGenerator.getCurrentUniqueID());
	}
	
	public void testUniqueIdOfMap() throws Exception {
		UniqueIdGenerator lvIdGenerator = new UniqueIdGenerator();
		assertNull(lvIdGenerator.getUniqueId(new HashMap<Object, Object>()));

		Map<Comparable<?>, Comparable<?>> m = new HashMap<Comparable<?>, Comparable<?>>();
		m.put("aa", "aa");
		m.put(new Double(0.07), new Double(0.07));
		assertNull(lvIdGenerator.getUniqueId(m));
		assertEquals(0, lvIdGenerator.getCurrentUniqueID());
	}

}
