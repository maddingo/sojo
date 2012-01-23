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
package net.sf.sojo.interchange;

import net.sf.sojo.core.filter.ClassPropertyFilterHandler;

/**
 * Describe, how <code>serialize</code> a complex object graph into desired representation  and back to the object graph with the method <code>deserialize</code>.
 * 
 * @author linke
 *
 */
public interface Serializer {
	
	public Object serialize(Object pvRootObject);
	
	public Object serialize(Object pvRootObject, String[] pvExcludedProperties);
	
	public Object deserialize(Object pvSourceObject);
	
	public Object deserialize(Object pvSourceObject, Class<?> pvRootClass);

	public void setClassPropertyFilterHandler(ClassPropertyFilterHandler pvClassPropertyFilterHandler);
	public ClassPropertyFilterHandler getClassPropertyFilterHandler();
}
