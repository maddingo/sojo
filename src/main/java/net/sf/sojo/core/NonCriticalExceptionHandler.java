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
package net.sf.sojo.core;

/**
 * Handle non critical Exceptions, there are thrown by interact with no "SOJO" - conform JavaBeans.
 * This is a possibillity to log information and get internal information about possible problems.
 * The default is the output on the console of the message and the stack trace (to <code>System.out</code>).
 * Default is the handler disabled. For more information must the handler enabled.
 * 
 * If you want your own implementation (with log - API) then extend this class and
 * override the both methods <code>handleExceptionInternal()</code>.
 *  
 * @author linke
 *
 */
public class NonCriticalExceptionHandler {

	private static boolean isNonCriticalExceptionHandlerEnabled = false;
	private static NonCriticalExceptionHandler nonCriticalExceptionHandler = new NonCriticalExceptionHandler();
	
	
	
	public static boolean isNonCriticalExceptionHandlerEnabled() { return isNonCriticalExceptionHandlerEnabled; }
	public static void setNonCriticalExceptionHandlerEnabled(boolean pvEnable) { isNonCriticalExceptionHandlerEnabled = pvEnable; }

	
	public final static void setNonCriticalExceptionHandler(NonCriticalExceptionHandler pvCriticalExceptionHandler) {
		nonCriticalExceptionHandler = pvCriticalExceptionHandler;
	}
	public final static NonCriticalExceptionHandler getNonCriticalExceptionHandler() {
		return nonCriticalExceptionHandler;
	}
	
	public final static void handleException(Class<?> pvThrownClasses, Exception pvException, String pvMessage) {
		nonCriticalExceptionHandler.handleExceptionInternal(pvThrownClasses, pvException, pvMessage);
	}
	
	public final static void handleException(Class<?> pvThrownClasses, String pvMessage) {
		nonCriticalExceptionHandler.handleExceptionInternal(pvThrownClasses, pvMessage);
	}

	protected void handleExceptionInternal(Class<?> pvThrownClasses, String pvMessage) { 
		handleExceptionInternal(pvThrownClasses, null, pvMessage);
	}

	protected void handleExceptionInternal(Class<?> pvThrownClasses, Exception pvException, String pvMessage) { 
		System.out.println("MESSAGE: " + pvMessage + " -> from class: " + pvThrownClasses);
		if (pvException != null) {
			pvException.printStackTrace(System.out);
		}
	}
	
}
