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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.sojo.core.NonCriticalExceptionHandler;
import net.sf.sojo.util.Util;

/**
 * Find all methods of the search classes (go upward in the class hierachy, how to the <code>java.lang.Object</code>).
 * 
 * @author linke
 *
 */
public final class ReflectionMethodHelper {

	public final static int GET_METHOD = 1;
	public final static int SET_METHOD = 2;

	protected static final ClassPropertiesCache classPropertiesCacheGetter = new ClassPropertiesCache();
	protected static final ClassPropertiesCache classPropertiesCacheSetter = new ClassPropertiesCache();

	protected ReflectionMethodHelper() { }
	
	public static void clearPropertiesCache() {
		classPropertiesCacheGetter.clear();
		classPropertiesCacheSetter.clear();
	}
	
	/**
	 * Find all getter-method from a Class.
	 * @param pvClass Class to analyse.
	 * @return Map all getter-Method (key=property name, value=method).
	 */
	public static Map getAllGetterMethod(Class pvClass) {
		return getAllGetterAndSetterMethod(pvClass, GET_METHOD);
	}
	
	/**
	 * Find all setter-method from a Class.
	 * @param pvClass Class to analyse.
	 * @return Map all setter-Method (key=property name, value=method).
	 */
	public static Map getAllSetterMethod(Class pvClass) {
		return getAllGetterAndSetterMethod(pvClass, SET_METHOD);
	}

	
	/**
	 * Remove all getter-method where no setter-method exist. 
	 * If more setter-method as getter-method, they wasn't removed.
	 */
	public static Map getAllNotEqualsGetterAndSetterAndRemoveThisProperties(Map pvGetterMap, Map pvSetterMap) {
		Map lvMap = new TreeMap();
		Iterator it = new ArrayList(pvGetterMap.keySet()).iterator();
		lvMap.put(Util.getKeyWordClass(), pvGetterMap.get(Util.getKeyWordClass()));
		while (it.hasNext()) {
			Object lvGetterProp = it.next();
			if (pvSetterMap.containsKey(lvGetterProp)) {
				lvMap.put(lvGetterProp, pvGetterMap.get(lvGetterProp));
			}
		}
		return Collections.unmodifiableMap(lvMap);
	}
	
	/**
	 * Find all getter-method from a Class and remove all getter-method where no setter-method exist.
	 * @param pvClass Class to anaylse.
	 * @return Map from getter-method (key=property name, value=method).
	 */
	public static Map getAllGetterMethodWithCache(Class pvClass, String pvFilter[]) {
		Map lvGetterMap = classPropertiesCacheGetter.getClassPropertiesMapByClass(pvClass);
		if (lvGetterMap == null) {
			lvGetterMap = getAllGetterMethod(pvClass);
			Map lvSetterMap = getAllSetterMethodWithCache(pvClass, pvFilter);
			lvGetterMap = getAllNotEqualsGetterAndSetterAndRemoveThisProperties (lvGetterMap, lvSetterMap);
			classPropertiesCacheGetter.addClassPropertiesMap(pvClass, lvGetterMap);
		}
		return lvGetterMap;
	}

	/**
	 * Find all setter-method from a Class.
	 * @param pvClass Class to analyse.
	 * @return Map all setter-Method (key=property name, value=method).
	 */
	public static Map getAllSetterMethodWithCache(Class pvClass, String pvFilter[]) {
		Map lvMap = classPropertiesCacheSetter.getClassPropertiesMapByClass(pvClass);
		if (lvMap == null) {
			lvMap = getAllSetterMethod(pvClass);
			classPropertiesCacheSetter.addClassPropertiesMap(pvClass, lvMap);
		}
		lvMap = Util.filterMapByKeys(lvMap, pvFilter);
		return lvMap;
	}

	
	/**
	 * Get all set/get methods from a Class. With methods from all super classes.
	 * @param pvClass Analyse Class.
	 * @return All finded methods.
	 */
	public static Method[] getAllMethodsByClass (Class pvClass) {
		return (Method[]) getAllMethodsByClassIntern(pvClass, new Hashtable()).toArray(new Method [0]);
	}

	/**
	 * Recursive search alle method from the Class in the Class Hierarchy to Object.class.
	 * @param pvClass Search class.
	 * @param pvMethodsMap Method map (key=property name, value=method).
	 * @return All finded methods.
	 */
	private static Collection getAllMethodsByClassIntern (Class pvClass, Map pvMethodsMap) {
		putAllMethodsIntern( pvClass.getMethods(), pvMethodsMap) ;		
		putAllMethodsIntern( pvClass.getDeclaredMethods(), pvMethodsMap);
		
		if (!(pvClass.getSuperclass().equals(Object.class))) {
			getAllMethodsByClassIntern(pvClass.getSuperclass(), pvMethodsMap);
		}
		
		return pvMethodsMap.values();
	}
	
	private static void putAllMethodsIntern (Method pvAllMethods[], Map pvMethodsMap) {
		for (int i = 0; i < pvAllMethods.length; i++) {
			String lvMethodName = pvAllMethods[i].getName();
			if (lvMethodName.startsWith("set") || lvMethodName.startsWith("get") || lvMethodName.startsWith("is")) {
				pvMethodsMap.put(pvAllMethods[i], pvAllMethods[i]);
			}
		}		
	}
	


	/**
	 * 
	 * @param pvClass Find all get or set method from a Class.
	 * @param pvMethodType get or set
	 * @return Method map (key=property name, value=method).
	 */
	public static Map getAllGetterAndSetterMethod(Class pvClass, int pvMethodType) {
		Method lvAllMethods[] = getAllMethodsByClass(pvClass);
		Map lvGetterOrSetter = new TreeMap();
        for (int i = 0; i < lvAllMethods.length; i++) {
        	Method lvMethod = null;
        	String lvPropName = lvAllMethods[i].getName();
        	switch (pvMethodType) {
				case GET_METHOD:
					if (lvPropName.startsWith("get") || lvPropName.startsWith("is")) {
						lvMethod = lvAllMethods[i];
						
						if ((NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) && (isMethodSetterAndGetterCompliant(lvMethod.getReturnType()) == false)) {
							NonCriticalExceptionHandler.handleException(ReflectionMethodHelper.class, "The method: " + lvMethod + " is not valid getter-method (bean complaint)");
						}
					}
					break;
				case SET_METHOD:
					if (lvPropName.startsWith("set")) {
						lvMethod = lvAllMethods[i];
						
						if ((NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) && (lvMethod.getParameterTypes().length != 1 && isMethodSetterAndGetterCompliant(lvMethod.getParameterTypes()[0]))) {
								NonCriticalExceptionHandler.handleException(ReflectionMethodHelper.class, "The method: " + lvMethod + " is not valid setter-method (bean complaint)");
						}
					}
					break;
				default:
					break;
			}
        	if (lvMethod != null) {
        		AccessController.doPrivileged(new AccessiblePrivilegedAction(lvMethod));
        		if (lvPropName.startsWith("is")) {
        			lvPropName = lvPropName.substring(2);
        		} else {
        			lvPropName = lvPropName.substring(3);
        		}
	        	// PropName muss aus set oder get UND einen Namen bestehen
	        	if (lvPropName.length() > 0) {
		        	lvPropName = lvPropName.substring(0, 1).toLowerCase()+ lvPropName.substring(1);
		        	if (lvPropName.equals(Util.DEFAULT_KEY_WORD_CLASS)) {
		        		lvGetterOrSetter.put(Util.getKeyWordClass(), pvClass.getName());
		        	} else {
						lvGetterOrSetter.put(lvPropName, lvMethod);
		        	}
	        	} else {
	        		if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
	        			NonCriticalExceptionHandler.handleException(ReflectionMethodHelper.class, "Invalid Property-Name: '" + lvAllMethods[i].getName() 
	        																							+ "' (Valid Property-Name is: set[name] or get[name], eg. setYear and getYear).");
	        		}
	        	}
        	} // if method != null
		} // for
        return Collections.unmodifiableMap(lvGetterOrSetter);
	}
	
	public static boolean isMethodSetterAndGetterCompliant (Class pvClass) {
		boolean lvReturn = false;
		
		if (ReflectionHelper.isSimpleType(pvClass)) {
			lvReturn = true;
		}
		else if (pvClass.isInterface()) {
			lvReturn = true;
		}

		// if is a class and is from java package (java.util.Vector) and not a interface (case before)
		// it is a bad case
		else if (pvClass.getName().startsWith("java.util.")) {
			lvReturn = false;
		}

		// find bean with constructor with out parameter 
		else if (pvClass.getConstructors().length > 0) {
			Constructor lvConstructor[] = pvClass.getConstructors();
			for (int i = 0; i < lvConstructor.length; i++) {
				if (lvConstructor[i].getParameterTypes().length == 0) {
					lvReturn = true;
					break;
				}
			}
		}

		return lvReturn;
	}

}
