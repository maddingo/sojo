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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.AccessController;

import net.sf.sojo.core.reflect.AccessiblePrivilegedAction;

/**
 * An element in a stack trace, as returned by <code>Throwable.getStackTrace()</code>. 
 * Each element represents a wrapper for a <code>java.lang.StackTraceElement</code>. 
 * 
 * @author linke
 *
 */
public final class StackTraceElementWrapper implements Serializable {
	
	private static final long serialVersionUID = 6249331607491652074L;
	
	private String className = null;
	private String methodName = null;
	private String fileName = null;
	private int lineNumber = -1;
	private boolean nativeMethod = false;
	
	public StackTraceElementWrapper() { }
	
	public StackTraceElementWrapper(StackTraceElement pvStackTraceElement) {
		setClassName(pvStackTraceElement.getClassName());
		setMethodName(pvStackTraceElement.getMethodName());
		setFileName(pvStackTraceElement.getFileName());
		setLineNumber(pvStackTraceElement.getLineNumber());
		setNativeMethod(pvStackTraceElement.isNativeMethod());
	}

	public String getClassName() { return className; }
	public void setClassName(String pvClassName) { className = pvClassName; }

	public String getFileName() { return fileName; }
	public void setFileName(String pvFileName) { fileName = pvFileName; }

	public int getLineNumber() { return lineNumber; }
	public void setLineNumber(int pvLineNumber) { lineNumber = pvLineNumber; }

	public String getMethodName() { return methodName; }
	public void setMethodName(String pvMethodName) { methodName = pvMethodName; }

	public boolean isNativeMethod() { return nativeMethod; }
	public void setNativeMethod(boolean pvNativeMethod) { nativeMethod = pvNativeMethod; }
	public boolean getNativeMethod() { return nativeMethod; }

	
	@Override
	public String toString() {
		return getClassName() + "." + getMethodName() + " (" + getFileName() + ":" + getLineNumber() + ")";
	}

	/**
	 * Two variants, to create a {@code StackTraceElement}. 
	 * The first variant is for jdk 1.4 and the second is greate 1.4, for example by
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/StackTraceElement.html#StackTraceElement(java.lang.String,%20java.lang.String,%20java.lang.String,%20int)">
	 * Java 2 Platform Standard Edition 5.0
	 * </a> A <code>StackTraceElement</code> instance or <code>null</code>.
	 */
	private StackTraceElement tryToCreateStackTraceElementInstanceIntern() {
		StackTraceElement lvStackTraceElement = null;
		try {
			Constructor<StackTraceElement> lvConstructor = StackTraceElement.class.getDeclaredConstructor();
			AccessController.doPrivileged(new AccessiblePrivilegedAction(lvConstructor));
			lvStackTraceElement = (StackTraceElement) lvConstructor.newInstance();
		} catch (Exception e) {
			try {
				Constructor<?> lvConstructor = StackTraceElement.class.getDeclaredConstructors()[0];
				AccessController.doPrivileged(new AccessiblePrivilegedAction(lvConstructor));
				lvStackTraceElement = (StackTraceElement) lvConstructor.newInstance(new Object[] { 
									getClassName(), getMethodName(), getFileName(), new Integer(getLineNumber()) });
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return lvStackTraceElement;
	}
	/**
	 * It is not guaranteed, that is possible to create a instance of the <code>StackTraceElement</code>.
	 * This method want to try to create a instance. By success, than get the instance, 
	 * else get <code>null</code>.
	 * 
	 * @return A <code>StackTraceElement</code> instance or <code>null</code>.
	 */
	public StackTraceElement tryToCreateStackTraceElement() {
		StackTraceElement lvStackTraceElement = null;
		try {
			lvStackTraceElement = tryToCreateStackTraceElementInstanceIntern();
			
			Field lvField = StackTraceElement.class.getDeclaredField("declaringClass");
			AccessController.doPrivileged(new AccessiblePrivilegedAction(lvField));
			lvField.set(lvStackTraceElement, getClassName());
			lvField = StackTraceElement.class.getDeclaredField("methodName");
			AccessController.doPrivileged(new AccessiblePrivilegedAction(lvField));
			lvField.set(lvStackTraceElement, getMethodName());
			lvField = StackTraceElement.class.getDeclaredField("fileName");
			AccessController.doPrivileged(new AccessiblePrivilegedAction(lvField));
			lvField.set(lvStackTraceElement, getFileName());
			lvField = StackTraceElement.class.getDeclaredField("lineNumber");
			AccessController.doPrivileged(new AccessiblePrivilegedAction(lvField));
			lvField.set(lvStackTraceElement, new Integer(getLineNumber()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lvStackTraceElement;
	}
}
