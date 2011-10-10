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
package net.sf.sojo.common;

import net.sf.sojo.core.Constants;

/**
 * It is a possibillity to get information of a object graph, by traverse over him.
 * 
 * @author linke
 *
 */
public interface WalkerInterceptor extends Constants {

	/**
	 * Begin with the start (bevor first object is visited).
	 * 
	 * @param pvStartObject The roort (start) object.
	 */
	public void startWalk(Object pvStartObject);
	
	/**
	 * After the traverse (all objects are visited).
	 *
	 */
	public void endWalk();
	
	/**
	 * Visit the current object.
	 * 
	 * @param pvKey By map, the key, else <code>null</code>.
	 * @param pvIndex By collection/array the index, else 0.
	 * @param pvValue The current value.
	 * @param pvType The type of value (simple, null, collection or map)
	 * @param pvPath Path to current object.
	 * @param pvNumberOfRecursion The number of recursion.
	 * @return if <code>true</code>, then cancel the wolk over the graph.
	 */
	public boolean visitElement(Object pvKey, int pvIndex, Object pvValue, int pvType, String pvPath, int pvNumberOfRecursion);

	/**
	 * A iterateable element (array, collection, map, ...) is visited.
	 * 
	 * @param pvValue The instance of Map or List.
	 * @param pvType Type (Map or List)
	 * @param pvPath The path to the object.
	 * @param pvTypeBeginOrEnd Start or end of the iterateable element (is the beginning and ending brace). 
	 */
	public void visitIterateableElement(Object pvValue, int pvType, String pvPath, int pvTypeBeginOrEnd);

}
