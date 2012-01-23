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
package net.sf.sojo.core.reflect;

import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;

/**
 * Set the accessible flag for this object to the indicated boolean value. 
 * A value of true indicates that the reflected object should suppress Java language access checking when it is used. 
 * 
 * @author linke
 *
 */
public class AccessiblePrivilegedAction implements PrivilegedAction<Object> {
 
	private final AccessibleObject accessibleObject;
	
	public AccessiblePrivilegedAction(final AccessibleObject pvAccessibleObject) {
		accessibleObject = pvAccessibleObject;
	}
	
	@Override
	public Object run() {
		accessibleObject.setAccessible(true);
		return null;
	}
	

}
