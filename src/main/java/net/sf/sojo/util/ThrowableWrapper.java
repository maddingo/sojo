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

import java.io.PrintStream;
import java.io.Serializable;

/**
 * This class wrap object of type <code>Throwable</code>.
 * You can use this wrapper, if you want to send a <code>Throwable</code> object,
 * e.g. from server to the client, where the client the <code>Throwable</code> class
 * don't know. This means, you can do a <code>printStackTrace</code> on the client - side,
 * where the StackTrace is showing the fault from the server - side.
 * 
 * @author linke
 *
 */
public final class ThrowableWrapper implements Serializable {

	private static final long serialVersionUID = -9139232450743630651L;

	private String message = "no message available";
	private String exceptionClassName = "no exception class name available";
	private StackTraceElementWrapper stackTraceElementWrapperList[] = new StackTraceElementWrapper[0];
	private ThrowableWrapper causeWrapper = null;

	public ThrowableWrapper() {
	}
	
	public ThrowableWrapper(Throwable pvThrowable) {
		if (pvThrowable == null) {
			throw new NullPointerException("The parameter Throwable must be different from value null.");
		}
		setMessage(pvThrowable.getMessage());
		setExceptionClassName(pvThrowable.getClass().getName());
		copyStackTraceElement(pvThrowable.getStackTrace());
		convertCause2Wrapper(pvThrowable, this);
	}
	
	public void setMessage(String pvMessage) { message = pvMessage; }
	public String getMessage() { return message; }
	
	public void setExceptionClassName(String pvExceptionClassName) { exceptionClassName = pvExceptionClassName; }
	public String getExceptionClassName() { return exceptionClassName; }
	
	public ThrowableWrapper getCauseWrapper() { return causeWrapper; }
	public void setCauseWrapper(ThrowableWrapper pvCauseWrapper) { causeWrapper = pvCauseWrapper; }

	public StackTraceElementWrapper[] getStackTraceElementWrapperList() {
		return stackTraceElementWrapperList; 
	}
	public void setStackTraceElementWrapperList(StackTraceElementWrapper[] pvStackTraceElementWrappers) {
		this.stackTraceElementWrapperList = pvStackTraceElementWrappers;
	}
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream pvPrintStream) {
		int lvSize = stackTraceElementWrapperList.length;
		pvPrintStream.println(getExceptionClassName() + ": " + getMessage());
		for (int i=0; i<lvSize; i++) {
			pvPrintStream.println("  at " + stackTraceElementWrapperList[i]);
		}		
	}

	
	private void convertCause2Wrapper(Throwable pvThrowable, ThrowableWrapper pvThrowableWrapper) {
		Throwable lvCause = pvThrowable.getCause();
		if (lvCause != null) {
			ThrowableWrapper lvWrapper = new ThrowableWrapper(lvCause);
			pvThrowableWrapper.setCauseWrapper(lvWrapper);
			convertCause2Wrapper(lvCause, lvWrapper);
		}
	}
	
	private void copyStackTraceElement(StackTraceElement[] pvStackTraceElements) {
		int lvArraySize = pvStackTraceElements.length;
		stackTraceElementWrapperList = new StackTraceElementWrapper[lvArraySize];
		for (int i=0; i<lvArraySize; i++) {
			stackTraceElementWrapperList[i] = new StackTraceElementWrapper(pvStackTraceElements[i]);
		}
	}

}
