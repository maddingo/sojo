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

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.sf.sojo.core.NonCriticalExceptionHandler;
import net.sf.sojo.core.reflect.ReflectionMethodHelper;

/**
 * Helper/Util - class. This are functions, which can't clear assigned ;-)
 * 
 * @author linke
 *
 */
public final class Util {

	public static final String DEFAULT_KEY_WORD_CLASS = "class";
	
	private static String keyWordClass = DEFAULT_KEY_WORD_CLASS;
	private final static List<DateFormat> dateFormatList = new ArrayList<DateFormat>();
	
	static {
		addDateFormat2List(new SimpleDateFormat("EEE MMM dd HH:mm:ss 'CEST' yyyy", Locale.ENGLISH));
		addDateFormat2List(new SimpleDateFormat("EEE MMM dd HH:mm:ss 'CET' yyyy", Locale.ENGLISH));
		addDateFormat2List(new SimpleDateFormat("yyyy-MM-dd"));
		addDateFormat2List(DateFormat.getDateInstance(DateFormat.MEDIUM));
	}
	
	public static void addDateFormat2List(DateFormat pvDateFormat) {
		dateFormatList.add(pvDateFormat);
	}

	public static void removeDateFormat2List(DateFormat pvDateFormat) {
		dateFormatList.remove(pvDateFormat);
	}

	
	protected Util() {}
	
	public static void setKeyWordClass(String pvKeyWordClass) { 
		if (pvKeyWordClass != null && pvKeyWordClass.length() > 0) {
			keyWordClass = pvKeyWordClass;
			ReflectionMethodHelper.clearPropertiesCache();
		}
	}
	public static String getKeyWordClass() {
		return keyWordClass; 
	}
	public static void resetKeyWordClass() {
		setKeyWordClass(DEFAULT_KEY_WORD_CLASS); 
	}
	
	
	public static boolean initJdkLogger () {
    	return initJdkLogger (Util.class.getResourceAsStream("jdk14-logging.properties"));
	}

	public static boolean initJdkLogger (InputStream pvInputStream) {
		try {
			LogManager.getLogManager().readConfiguration(pvInputStream);
			Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 
			LogManager.getLogManager().addLogger(logger);
			logger.finest("Jdk14Logger initialisiert ...");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return false;
	}
	
	public static String createNumberOfBlank(int pvNumberOfBlank, int pvMultiplier ) {
		StringBuffer sb =  new StringBuffer();
		for (int i=0; i<pvNumberOfBlank; i++) {
			for (int j=0; j<pvMultiplier; j++) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	/**
	 * Fill the milliseconds by a timestamp with zeros.
	 * Example: <code>2007-03-29 19:20:56.39</code> to  <code>2007-03-29 19:20:56.39</code><b>0</b>.
	 * 
	 * @param pvTimestampStr The timestamp string
	 * @return The converted timestamp strin.
	 */
	public static String fillMillisecondsWithZero(final String pvTimestampStr) {
		if (pvTimestampStr == null) {
			return null;
		}
		
		String str = pvTimestampStr;
		
		int index = str.lastIndexOf('.');
		if (index >= 0) {
			String strBeforePoint = str.substring(0, index);
			String strAfterPoint = str.substring(index+1);
			if (strAfterPoint.length() == 2) {
				strAfterPoint = strAfterPoint + "0";
			}
			else if (strAfterPoint.length() == 1) {
				strAfterPoint = strAfterPoint + "00";
			}
			else if (strAfterPoint.length() == 0) {
				strAfterPoint = "000";
			}
			str = strBeforePoint + "." + strAfterPoint;
		}
		return str;
	}
	
	/**
	 * Convert a Date-String to a Date. The Converter <b>ignored the Millisecond</b>.
	 * Example: Thu Aug 11 19:30:57 CEST 2005
	 * 
	 * @param pvDateString The Date-String (unequal null).
	 * @return Valid <code>java.util.Date</code>.
	 */
	public static Date string2Date (String pvDateString) {
		if (pvDateString == null) { throw new IllegalArgumentException ("The Date-String was null by string2Date."); }
		
		Date date = null;
		
		// 0. Versuch, Datums-String ist eine ganze Zahl
		try {
			date = new Date(new Long(pvDateString).longValue());
			return date;
		} catch (Exception e) {
			if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
				NonCriticalExceptionHandler.handleException(Util.class, e, "First try to convert by string2Date: " + pvDateString);
			}
		}
		
		// 1. Versuch, Timestamp-Format
		try { 
			String lvTimestampStr = fillMillisecondsWithZero(pvDateString);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			date = df.parse(lvTimestampStr); 
			return date;
		} catch (ParseException e) {
			if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
				NonCriticalExceptionHandler.handleException(Util.class, e, "Forth try to convert by string2Date: " + pvDateString);
			}
		}

		
		// 2. Versuch, mit allen bekannten Timestamp-Formaten		 
		Iterator<?> lvIterator = dateFormatList.iterator();
		while (lvIterator.hasNext()) {
			DateFormat df = (DateFormat) lvIterator.next();
			try {
				date = df.parse(pvDateString); 
				return date;
			} catch (ParseException e) {
				if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
					NonCriticalExceptionHandler.handleException(Util.class, e, "Fifth try to convert by string2Date: " + pvDateString);
				}
			}
		}
		
		throw new IllegalStateException ("The String: \"" + pvDateString + "\" is not valid date."); 
	}

	
    /**
     * Analysed the objects in an Array/Collection. If all Object from the same class type,
     * then is the return value this class. If are several class types in the Array,
     * then ist the return class from type Object.
     * @param pvListObj
     * @return Class, that are in the Array or Collection
     */
    public static Class<?> getArrayType (Object pvListObj) {
    	Class<?> lvType = Object.class;
    	if (pvListObj == null) { return lvType; }
    	if (pvListObj.getClass().isArray()) {
    		Object o[] = (Object[]) pvListObj;
    		if (o.length > 0) {
	    		Class<?> lvClass = o[0].getClass(); 
	    		
	    		// !!!!! Specialfall ?????
	    		if(Map.class.isAssignableFrom(lvClass)) {
	    			return Object.class;
	    		}
	    		
	    		for (int i = 0; i < o.length; i++) {
					if (!lvClass.equals(o[i].getClass())) {
						return lvType;
					}
				}
	    		return lvClass;
    		} else {
    			return o.getClass().getComponentType();
    		}
    	}
    	else if (pvListObj instanceof Collection) {
    		Collection<?> coll = (Collection<?>) pvListObj;
    		if (coll.size() > 0) {
    			Class<?> lvClass = coll.iterator().next().getClass();
    			Iterator<?> it = coll.iterator();
    			while (it.hasNext()) {
					if (!lvClass.equals(it.next().getClass())) {
						return lvType;
					}
    			}
    			return lvClass;
    		} else {
    			return lvType;
    		}
    	}
    	return lvType;
    }
    
	public static void delLastComma (StringBuffer s) {
		int lvPos = s.length() - 1;
		if (lvPos > 0 && s.charAt(lvPos) == ',') {
			s.deleteCharAt(lvPos);
		}
	}

	public static boolean isStringInArray (String[] pvList, String pvSearchString) {
		boolean lvReturn = false;
		for (int i = 0; i < pvList.length; i++) {
			if (pvList[i].equals(pvSearchString)) {
				return true;
			}
		}
		return lvReturn;
	}
	
	public static Map<String, Object> filterMapByKeys (Map<String, Object> pvSearchMap, String[] pvList) {
		if (pvList == null || pvList.length == 0) {
			return pvSearchMap;
		} else {
			Map<String,Object> lvReturnMap = new TreeMap<String, Object>();
			for (Entry<String, Object> entry : pvSearchMap.entrySet()) {
        if (isStringInArray(pvList, entry.getKey())) {
          lvReturnMap.put(entry.getKey(), entry.getValue());
        }
      }
			return lvReturnMap;
		}
	}

}
