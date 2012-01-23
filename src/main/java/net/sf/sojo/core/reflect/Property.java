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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;


/**
 * Property is a Proxy for a method (java.lang.reflect.Method) or a field (java.lang.reflect.Field).
 * So SOJO can switch transparently between this two constructs.
 * 
 * @author linke
 *
 */
public class Property {

	public static final int PROPERTY_TYPE_INVALIDE = -1;
	public static final int PROPERTY_TYPE_METHOD = 0;
	public static final int PROPERTY_TYPE_FIELD = 1;

	/** The Method or Field Object */
	private AccessibleObject accessibleObject = null;
	private int propertyType = PROPERTY_TYPE_INVALIDE;
	private String toString = null;

	
	public Property(AccessibleObject pvAccessibleObject) {
		accessibleObject = pvAccessibleObject;
		detectPropertyType();
		toString = accessibleObject.toString();
	}
	
	public int getPropertyType() { return propertyType; }
	
	/**
	 * Call the setter Method or set value from Field.
	 * 
	 * @param pvObject Object, on which the value is set
	 * @param pvArgs the set value
	 */
	public void executeSetValue(Object pvObject, Object pvArgs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		AccessController.doPrivileged(new AccessiblePrivilegedAction(accessibleObject));
		if (propertyType == PROPERTY_TYPE_METHOD) {
			((Method) accessibleObject).invoke(pvObject, new Object[] { pvArgs });	
		} else {
			((Field) accessibleObject).set(pvObject, pvArgs);
		}
	}


	/**
	 * Call the getter Method or get value from Field.
	 * 
	 * @param pvObject Object, on which the value is get
	 */
	public Object executeGetValue(Object pvObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object lvReturnValue = null;
		AccessController.doPrivileged(new AccessiblePrivilegedAction(accessibleObject));
		if (propertyType == PROPERTY_TYPE_METHOD) {
			lvReturnValue = ((Method) accessibleObject).invoke(pvObject, (Object)null);	
		} else {
			lvReturnValue = ((Field) accessibleObject).get(pvObject);
		}
		return lvReturnValue;
	}

	/**
	 * Return the type of parameter (index 0) from the method or the type from the field.
	 * @return The type (class)
	 */
	public Class<?> getParameterType() {
		Class<?> lvParamType = null;
		if (propertyType == PROPERTY_TYPE_METHOD) {
			Method lvMethod = (Method) accessibleObject;
			Class<?>[] lvParameterTypes = lvMethod.getParameterTypes();
			if (lvParameterTypes != null && lvParameterTypes.length > 0) {
				lvParamType = lvMethod.getParameterTypes()[0];
			}
		} else {
			lvParamType = ((Field) accessibleObject).getType();
		}
		return lvParamType;
	}
	
	
	private void detectPropertyType() {
		if (accessibleObject instanceof Method) {
			propertyType = PROPERTY_TYPE_METHOD;
		} else if (accessibleObject instanceof Field) {
			propertyType = PROPERTY_TYPE_FIELD;
		} else {
			throw new  IllegalArgumentException("Only Method or Field are allowed: " + accessibleObject);
		}
	}
	
	@Override
	public String toString() {
		return toString;
	}
}
