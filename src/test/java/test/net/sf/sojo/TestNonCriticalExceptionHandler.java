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
package test.net.sf.sojo;

import net.sf.sojo.core.NonCriticalExceptionHandler;

public class TestNonCriticalExceptionHandler extends NonCriticalExceptionHandler {

	private Class<?> thrownClasses = null;
	private Exception exception = null;
	private String message = null;
	
	@Override
	public void handleExceptionInternal(Class<?> pvThrownClasses, Exception pvException,String pvMessage) {
		thrownClasses = pvThrownClasses;
		exception = pvException;
		message = pvMessage;
	}

	@Override
	public void handleExceptionInternal(Class<?> pvThrownClasses, String pvMessage) {
		thrownClasses = pvThrownClasses;
		message = pvMessage;
	}
	
	public Class<?> getThrownClasses() {
		return thrownClasses;
	}
	public Exception getException() {
		return exception;
	}
	public String getMessage() {
		return message;
	}

}
