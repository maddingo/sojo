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

import java.util.HashMap;
import java.util.Map;

import net.sf.sojo.core.reflect.ClassPropertiesCache;

import test.net.sf.sojo.model.Node;
import junit.framework.TestCase;

public class ClassPropertiesCacheTest extends TestCase {
	
	public void testAllClassPropertiesMap() throws Exception {
		ClassPropertiesCache lvCache = new ClassPropertiesCache();
		lvCache.addClassPropertiesMap(Node.class, new HashMap<Object, Object>());
		assertEquals(1, lvCache.size());
	}
	
		    
	public void testGetClassPropertiesMapByClass() throws Exception {
		ClassPropertiesCache lvCache = new ClassPropertiesCache();
		lvCache.addClassPropertiesMap(Node.class, new HashMap<Object, Object>());
		assertEquals(1, lvCache.size());
		 
		 Map<?, ?> lvMap = lvCache.getClassPropertiesMapByClass(Node.class);
		 assertNotNull(lvMap);
		 assertEquals(0, lvMap.size());
	}


}
