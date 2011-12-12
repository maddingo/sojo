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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import net.sf.sojo.util.StackTraceElementWrapper;
import net.sf.sojo.util.ThrowableWrapper;
import net.sf.sojo.util.Util;

/**
 * Helper class for use reflection for internal cases.
 * 
 * @author linke
 *
 */
public final class ReflectionHelper {

	private final static Map<Class<?>, Class<?>> isSimpleType = new HashMap<Class<?>, Class<?>>();
	
	static {
		isSimpleType.put(String.class, String.class);
		isSimpleType.put(Boolean.class, Boolean.class);
		isSimpleType.put(boolean.class, boolean.class);
		isSimpleType.put(Byte.class, Byte.class);
		isSimpleType.put(byte.class, byte.class);
		isSimpleType.put(Short.class, Short.class);
		isSimpleType.put(short.class, short.class);
		isSimpleType.put(Integer.class, Integer.class);
		isSimpleType.put(int.class, int.class);
		isSimpleType.put(Long.class, Long.class);
		isSimpleType.put(long.class, long.class);
		isSimpleType.put(Float.class, Float.class);
		isSimpleType.put(float.class, float.class);
		isSimpleType.put(Double.class, Double.class);
		isSimpleType.put(double.class, double.class);
		isSimpleType.put(Character.class, Character.class);
		isSimpleType.put(char.class, char.class);
		isSimpleType.put(BigDecimal.class, BigDecimal.class);
		isSimpleType.put(StringBuffer.class, StringBuffer.class);
		isSimpleType.put(BigInteger.class, BigInteger.class);
		isSimpleType.put(Class.class, Class.class);
		isSimpleType.put(java.sql.Date.class, java.sql.Date.class);
		isSimpleType.put(java.util.Date.class, java.util.Date.class);
		isSimpleType.put(Time.class, Time.class);
		isSimpleType.put(Timestamp.class, Timestamp.class);
		isSimpleType.put(Calendar.class, Calendar.class);
		isSimpleType.put(GregorianCalendar.class, GregorianCalendar.class);
		isSimpleType.put(URL.class, URL.class);
		isSimpleType.put(Object.class, Object.class);
	}
	
	protected ReflectionHelper() {}

	public static boolean isSimpleType(Object pvObject) {
		if (pvObject == null) { return false; }
		return isSimpleType(pvObject.getClass());
	}

	public static boolean isSimpleType(Class<?> pvClass) {
		if (pvClass == null) { return false; }
		return isSimpleType.containsKey(pvClass);
	}
	
	public static void addSimpleType(Class<?> pvSimpleType) {
		if (pvSimpleType != null) {
			isSimpleType.put(pvSimpleType, pvSimpleType);
		}
	}

	public static void removeSimpleType(Class<?> pvSimpleType) {
		if (pvSimpleType != null) {
			isSimpleType.remove(pvSimpleType);
		}
	}

	
	public static boolean isMapType(Class<?> pvType) {
		boolean lvReturn = false;
		if (pvType != null) {
			lvReturn = (Map.class.isAssignableFrom(pvType));
		}
		return lvReturn;
	}
	
	public static boolean isMapType(Object pvObject) {
		boolean lvReturn = false;
		if (pvObject == null) { 
			lvReturn = false;
		}
		else if (pvObject instanceof Map) {
			lvReturn = true;
		}

		return lvReturn;
	}
	
	public static boolean isIterableType(Object pvObject) {
		boolean lvReturn = false;
		if (pvObject == null) { 
			lvReturn = false;
		}
		else if (pvObject instanceof Collection) {
			lvReturn = true;
		}
		else if (pvObject.getClass().isArray()) {
			lvReturn = true;
		}

		return lvReturn;
	}

	public static boolean isIterateableType(Class<?> pvType) {
		boolean lvReturn = false;
		if (pvType == null) {
			lvReturn = false;
		}
		else if (Collection.class.isAssignableFrom(pvType)) {
			lvReturn = true;
		} else if (pvType.isArray()) {
			lvReturn = true;
		}
		return lvReturn;
	}

	public static boolean isComplexMapType(Class<?> pvType) {
		boolean lvReturn = true;
		if (pvType == null) { 
			lvReturn = false;
		}
		else if (isSimpleType(pvType)) {
			lvReturn = false;
		}
		else if (isIterateableType(pvType)) { 
			lvReturn = false;
		}
		else if (isMapType(pvType)) { 
			lvReturn = false;
		}

		return lvReturn;
	}
	
	public static boolean isComplexMapType(Object pvObject) {
		boolean lvReturn = isMapType(pvObject);
		if (lvReturn) {
			Map<?,?> lvMap = (Map<?,?>) pvObject;
			lvReturn = lvMap.containsKey(Util.getKeyWordClass());
		}
		return lvReturn;
	}

	public static boolean isComplexType(Object pvObject) {
		boolean lvReturn = true;
		if (pvObject == null) { 
			lvReturn = false;
		}
		else if (isSimpleType(pvObject)) {
			lvReturn = false;
		}
		else if (isIterableType(pvObject)) { 
			lvReturn = false;
		}
		else if (isMapType(pvObject)) { 
			lvReturn = false;
		}
		
		return lvReturn;
	}

	public static boolean isComplexType(Class<?> pvType) {
		boolean lvReturn = true;
		if (pvType == null) { 
			lvReturn = false;
		}
		else if (isSimpleType(pvType)) {
			lvReturn = false;
		}
		else if (isIterateableType(pvType)) {
			lvReturn = false;
		}
		else if (isMapType(pvType)) {
			lvReturn = false;
		}

		return lvReturn;
	}
	
	
	// TODO split the function in sub-functions 
	public static Object createNewSimpleObject(final Class<?> pvSimpleClass, final Object pvSimpleParamObject) throws InstantiationException {
		try {
	    	if (pvSimpleClass == null) { return null; }
	    	String lvSimpleParamObjectString = "0";
	    	if (pvSimpleParamObject != null && pvSimpleParamObject.toString().length() > 0) { 
	    		lvSimpleParamObjectString = pvSimpleParamObject.toString(); 
	    	}
	    	
	    	if (pvSimpleClass.equals(String.class)) { return lvSimpleParamObjectString; }
	    	else if (pvSimpleClass.equals(int.class)) { return Integer.valueOf(lvSimpleParamObjectString); }
	    	else if (pvSimpleClass.equals(Integer.class)) { return Integer.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(short.class)) { return Short.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(Short.class)) { return Short.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(byte.class))  { return Byte.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(Byte.class))  { return Byte.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(long.class))  { return Long.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(Long.class))  { return Long.valueOf(lvSimpleParamObjectString); }
	
	        
	      else if (pvSimpleClass.equals(double.class)) { return Double.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(Double.class)) { return Double.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(float.class)) { return Float.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(Float.class)) { return Float.valueOf(lvSimpleParamObjectString); }
	
	      else if (pvSimpleClass.equals(char.class)) { return new Character(lvSimpleParamObjectString.charAt(0)); }
	      else if (pvSimpleClass.equals(Character.class)) { return new Character(lvSimpleParamObjectString.charAt(0)); }
	        
	      else if (pvSimpleClass.equals(boolean.class)) { return Boolean.valueOf(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(Boolean.class)) { return Boolean.valueOf(lvSimpleParamObjectString); }
	    	
	      else if (pvSimpleClass.equals(BigDecimal.class)) { return new BigDecimal(lvSimpleParamObjectString); }
	      else if (pvSimpleClass.equals(BigInteger.class)) { return new BigInteger(lvSimpleParamObjectString); }
	    	
	      else if (pvSimpleClass.equals(StringBuffer.class)) { return new StringBuffer(lvSimpleParamObjectString); }
	    	
	      else if (pvSimpleClass.equals(URL.class)) {
	        	try {
	        		return new URL(lvSimpleParamObjectString);
	        	} catch (Exception e) {
	        		throw new InstantiationException("Can't create a new instance from type URL for String: " 
	        				+ lvSimpleParamObjectString + " (" + e + ")");
	        	}
	      }
	    	
	      else if (pvSimpleClass.equals(Class.class)) { 
	        	try {
	        		if (lvSimpleParamObjectString.startsWith("class ")) {
	        			lvSimpleParamObjectString = lvSimpleParamObjectString.substring("class ".length());
	        		}
    					Class<?> lvClass = ReflectionHelper.forName(lvSimpleParamObjectString);
    					return lvClass;
    				} catch (Exception e) {
    					throw new InstantiationException("Can't create a new instance from type class for String: " 
    							+ lvSimpleParamObjectString + " (" + e + ")");
    				}
	        }
	    	
	      else if (pvSimpleClass.equals(java.sql.Date.class)) { 
	        	java.util.Date lvDate = Util.string2Date(lvSimpleParamObjectString); 
	        	return new java.sql.Date(lvDate.getTime());
	      }
	      else if (pvSimpleClass.equals(java.util.Date.class)) { 
	        	return Util.string2Date(lvSimpleParamObjectString); 
	      }
	      else if (pvSimpleClass.equals(Time.class)) { 
	        	return new Time(new Long(lvSimpleParamObjectString).longValue()); }
	      else if (pvSimpleClass.equals(Timestamp.class)) { 
	        	long lvTime = Util.string2Date(lvSimpleParamObjectString).getTime();
	        	return new Timestamp(lvTime);
	      }
		} catch (NumberFormatException e) {
			throw new InstantiationException("Can't create a new instance from type: " + pvSimpleClass.getName() 
														+ " with constructor-argument: " + pvSimpleParamObject);
		}
    	
    	return pvSimpleParamObject;
	}
	

	
	public static Constructor<?> findConstructorByParameterTypes(Class<?> pvClass, Class<?> pvParameterTypes[]) {
		Constructor<?>[] lvConstructors = pvClass.getConstructors();
		for (int i = 0; i < lvConstructors.length; i++) {
			Class<?>[] lvParamTypes = lvConstructors[i].getParameterTypes();
			if (pvParameterTypes.length == lvParamTypes.length) {
				boolean lvFind = true;
				for (int j = 0; j < lvParamTypes.length; j++) {
					if ( ! (lvParamTypes[j].equals(pvParameterTypes[j])) ) {
						lvFind = false;
					}
				}
				if (lvFind) {
					return lvConstructors[i];
				}
			}
		}
		
		return null;
	}
	

	public static Object createNewIterableInstance(Class<?> pvNewInstanceClass, int pvSize) {
		Object lvRetObj = null;
		try {
			Constructor<?> lvConstructor = ReflectionHelper.findConstructorByParameterTypes(pvNewInstanceClass, new Class [] { int.class });
			if (lvConstructor != null) {
				lvRetObj = lvConstructor.newInstance(new Object[] { Integer.valueOf(Integer.toString(pvSize)) });
				
			} else {
				lvRetObj = pvNewInstanceClass.newInstance();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't create a new instance of class : " + pvNewInstanceClass);
		} 	
		return lvRetObj;
	}

	public static Object createBeanFromMap(final Map<?,?> pvMap, final Class<?> pvToType) throws InstantiationException, IllegalAccessException {
		Object lvClass = null;
		if (pvMap != null) {
			lvClass = pvMap.get(Util.getKeyWordClass());
		}
		Class<?> lvBeanType = pvToType;
		if (lvClass instanceof String) {
			try {
				lvBeanType = ReflectionHelper.forName(lvClass.toString());
			} catch (ClassNotFoundException e) {
				throw new InstantiationException("Can't create a class for String: " + lvClass);
			}
		} else if (lvClass instanceof Class) {
			lvBeanType = (Class<?>) lvClass;
		}
		
		if (lvBeanType == null) {
			throw new NullPointerException("No type (class) was set for create a Bean. Map is: " 
					+ pvMap + " an type is: " + pvToType);
		}
		
		Object lvBeanObject = null;
		try {
			lvBeanObject = lvBeanType.newInstance();
		} catch (InstantiationException e) {
			throw new InstantiationException("Can't create a Bean from class: " + lvBeanType);
		} catch (IllegalAccessException e) {
			throw new IllegalAccessException("Can't create a Bean from class: " + lvBeanType);
		}

		return lvBeanObject;
	}

	public static Class<?> mapFromSimpeToWrapper(Class<?> pvSimpleType ) {
		Class<?> lvReturnClass = pvSimpleType;
		if (byte.class.equals(pvSimpleType)) {
			lvReturnClass = Byte.class;
		} else if (short.class.equals(pvSimpleType)) {
			lvReturnClass = Short.class;
		} else if (int.class.equals(pvSimpleType)) {
			lvReturnClass = Integer.class;
		} else if (long.class.equals(pvSimpleType)) {
			lvReturnClass = Long.class;
		} else if (float.class.equals(pvSimpleType)) {
			lvReturnClass = Float.class;
		} else if (double.class.equals(pvSimpleType)) {
			lvReturnClass = Double.class;
		} else if (char.class.equals(pvSimpleType)) {
			lvReturnClass = Character.class;
		} else if (boolean.class.equals(pvSimpleType)) {
			lvReturnClass = Boolean.class;
		}      
		
		return lvReturnClass;
	}
	
	
	public static Throwable createThrowable(ThrowableWrapper pvThrowableWrapper) {
		return createThrowable(pvThrowableWrapper, null);
	}
	
	public static Throwable createThrowable(ThrowableWrapper pvThrowableWrapper, Class<?> pvToClass) {
		Throwable lvThrowable = null;
		
		try {
			Class<?> lvClass = pvToClass;
			if (lvClass == null) {
				lvClass = ReflectionHelper.forName(pvThrowableWrapper.getExceptionClassName());
			} else if ( ! Throwable.class.isAssignableFrom(lvClass)) {
				throw new InstantiationException("The Class: " + lvClass.getName() + " is not from type Throwable!");
			}
			
			Constructor<?> lvConstructor = findConstructorByParameterTypes(lvClass, new Class [] { String.class, Throwable.class});
			if (lvConstructor != null) {
				ThrowableWrapper lvCauseWrapper = pvThrowableWrapper.getCauseWrapper();
				if (lvCauseWrapper != null) {
					Throwable lvCause = createThrowable(lvCauseWrapper, null);
					lvThrowable = (Throwable) lvConstructor.newInstance(new Object [] { pvThrowableWrapper.getMessage(), lvCause });
				} else {
					lvThrowable = (Throwable) lvConstructor.newInstance(new Object [] { pvThrowableWrapper.getMessage(), null});
				}				
			}
			else {
				lvConstructor = findConstructorByParameterTypes(lvClass, new Class [] { String.class});
				if (lvConstructor != null) {	
					lvThrowable = (Throwable) lvConstructor.newInstance(new Object [] { pvThrowableWrapper.getMessage() });
				} else {
					lvConstructor = lvClass.getConstructor();
					if (lvConstructor != null) {	
						lvThrowable = (Throwable) lvConstructor.newInstance();
					} 
				}
			}
			

		} catch (Exception e) {
			if (e instanceof InstantiationException) {
				lvThrowable = e;
			} else {
				lvThrowable = new InstantiationException(e.toString());
			}
		}
		
		StackTraceElementWrapper lvElementWrapper[] = pvThrowableWrapper.getStackTraceElementWrapperList();
		StackTraceElement lvElement[] = new StackTraceElement[lvElementWrapper.length];
		for (int i = 0; i < lvElementWrapper.length; i++) {
			StackTraceElement lvSTE = lvElementWrapper[i].tryToCreateStackTraceElement();
			lvElement[i] = lvSTE;
		}
		lvThrowable.setStackTrace(lvElement);

		
		
		return lvThrowable;
	}
	
	public static Class<?> forName(String name) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(name, true, Thread.currentThread().getContextClassLoader());
		return clazz;
	}
}
