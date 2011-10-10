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
package net.sf.sojo.util;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An array want to handle how a collection, by iterat over all elements in the array.
 * By default the Array haven't an iterator. This implementation want to close this distance.
 * 
 * @author linke
 *
 */
public class ArrayIterator implements Iterator {

	private Object arrayObject = null;
	private int pos = 0;
	private int length = 0;
	
	public ArrayIterator(Object pvArrayObjects) {
		if (pvArrayObjects == null) {
			throw new IllegalArgumentException("Param ArrayObject must be not null");
		}
		if (pvArrayObjects.getClass().isArray() == false) {
			throw new IllegalArgumentException("Param ArrayObject must be an Array");
		}
		arrayObject = pvArrayObjects;
		length = Array.getLength(arrayObject);
		resetPosition();
	}
	
	public final void resetPosition() { pos = 0; }
	public int getLength() { return length; }
	public int getPos() { return pos; }
	
	public boolean hasNext() {
		return (pos < length);
	}

	public Object next() {
		if (pos >= length) {
			throw new NoSuchElementException("No more elements in the array (the pointer is after the last element).");
		}
		Object lvValue = Array.get (arrayObject, pos);
		pos++;
		return lvValue;
	}

	public void remove() {
		// do nothing
	}

}
