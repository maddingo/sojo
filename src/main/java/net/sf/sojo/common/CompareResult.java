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
 * Result of call the <code>compare</code> - method from the <code>ObjectUtil</code>.
 *  
 * @author linke
 *
 */
public class CompareResult {

	public int numberOfRecursion = -1;
	/**
	 * Path to the different property.
	 */
	public String differentPath = null;
	/**
	 * Different value from object one. 
	 */
	public Object differentValue1 = null;
	/**
	 * Different value from object two. 
	 */
	public Object differentValue2 = null;
	/**
	 * Key of the <code>Map</code>.
	 */
	public Object key = null;
	/**
	 * Index of the <code>Array/Collection</code>.
	 */
	public int index = Constants.INVALID_INDEX;
	
	/**
	 * First precondition <code>differentValue1 != null</code> and <code>differentValue2 != null</code> and
	 * second precondition <code>differentValue1 instanceof Comparable</code> and <code>differentValue2 instanceof Comparable</code>.
	 * Then compare <code>differentValue1</code> and <code>differentValue1</code>.
	 * 
	 * @return If precondition is <code>true</code> is the value the compare result, else 0.
	 */
	@SuppressWarnings("unchecked")
	public int getCompareToValue() {
		if ((differentValue1 != null && differentValue2 != null) 
			&& (differentValue1 instanceof Comparable && differentValue2 instanceof Comparable)) 
		{
				return ((Comparable<Object>) differentValue1).compareTo(differentValue2);
			
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return "Path: " + differentPath + ": " + differentValue1 + " <--> " + differentValue2;
	}
}
