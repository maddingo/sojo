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

import java.util.Comparator;

/**
 * Is a implementation of <code>java.util.Comparator</code>, with delegate the compare-method to the 
 * <code>ObjectUtil.compareTo(obj1, obj2)</code> method.
 * 
 * @author linke
 *
 */
public final class GenericComparator implements Comparator<Object> {

	private ObjectUtil objectUtil = new ObjectUtil();
	/**
	 * This is importend for ordered Set. By equals result by compare-method
	 * is object NO added to Set. 
	 */
	private boolean getUnEqualsByEquals = false;
	
	public GenericComparator() {}
	public GenericComparator(boolean pvGetUnEqualsByEquals) {
		getUnEqualsByEquals = pvGetUnEqualsByEquals;
	}
	
	@Override
	public int compare(Object pvObject1, Object pvObject2) {
		int lvResult = objectUtil.compareTo(pvObject1, pvObject2);
		if (lvResult == 0 && getUnEqualsByEquals) {
			lvResult = pvObject1.hashCode() - pvObject2.hashCode(); 
		}
		return lvResult;
	}
	
}
