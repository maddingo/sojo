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
package test.net.sf.sojo.common;

import junit.framework.TestCase;
import net.sf.sojo.common.PathRecordWalkerInterceptor;
import net.sf.sojo.core.Constants;
import net.sf.sojo.core.UniqueIdGenerator;

public class PathRecordWalkerInterceptorTest extends TestCase {

	public void testFilterUniqueIdProperty() throws Exception {
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		lvInterceptor.setFilterUniqueIdProperty(true);
		lvInterceptor.visitElement(null, Constants.INVALID_INDEX, "value", Constants.TYPE_SIMPLE, "mypath", 1);
		try {
			lvInterceptor.visitElement(null, Constants.INVALID_INDEX, "value", Constants.TYPE_SIMPLE, "mypath", 1);
			fail("Same path can't added.");
		} catch (Exception e) {
			assertNotNull(e);
		}
	}
	
	public void testAddToPathesOnlySimple() throws Exception {
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		assertFalse(lvInterceptor.getOnlySimpleProperties());
		assertFalse(lvInterceptor.getFilterUniqueIdProperty());
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_MAP, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_NULL, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, ""));
		
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_MAP, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_NULL, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, "name"));

		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_MAP, null));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, null));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_NULL, null));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, null));

		
		lvInterceptor.setOnlySimpleProperties(true);
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_MAP, ""));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, ""));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_NULL, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, ""));
		
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_MAP, "name"));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, "name"));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_NULL, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, "name"));

		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_MAP, null));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, null));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_NULL, null));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, null));
	}
	
	public void testAddToPathesFilterUniqueIdProperty() throws Exception {
		PathRecordWalkerInterceptor lvInterceptor = new PathRecordWalkerInterceptor();
		assertFalse(lvInterceptor.getOnlySimpleProperties());
		assertFalse(lvInterceptor.getFilterUniqueIdProperty());
		
		lvInterceptor.setFilterUniqueIdProperty(true);
		assertTrue(lvInterceptor.getFilterUniqueIdProperty());
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_MAP, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_NULL, ""));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, ""));

		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_MAP, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_NULL, "name"));
		assertEquals(true, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, "name"));

		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_MAP, UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_ITERATEABLE, UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_NULL, UniqueIdGenerator.UNIQUE_ID_PROPERTY));
		assertEquals(false, lvInterceptor.addToPaths(Constants.TYPE_SIMPLE, UniqueIdGenerator.UNIQUE_ID_PROPERTY));
	}
	
}
