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
package test.net.sf.sojo.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import test.net.sf.sojo.model.Test2Exception;

import junit.framework.TestCase;
import net.sf.sojo.util.StackTraceElementWrapper;
import net.sf.sojo.util.ThrowableWrapper;

public class ThrowableWrapperTest extends TestCase {

	public void testMessage() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("JUnit-Test"));
		assertEquals("JUnit-Test", lvWrapper.getMessage());
	}
	
	public void testCauseWrapper() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("JUnit-Test"));
		assertNull(lvWrapper.getCauseWrapper());
		
		lvWrapper = new ThrowableWrapper(new Exception("JUnit-Test",new Exception("Cause")));
		assertNotNull(lvWrapper.getCauseWrapper());
		assertEquals("Cause", lvWrapper.getCauseWrapper().getMessage());
		assertNull(lvWrapper.getCauseWrapper().getCauseWrapper());
	}

	public void testExceptionClassName() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("JUnit-Test"));
		assertEquals(Exception.class.getName(), lvWrapper.getExceptionClassName());
		
		lvWrapper = new ThrowableWrapper(new Error("JUnit-Test"));
		assertEquals(Error.class.getName(), lvWrapper.getExceptionClassName());
	}

	public void testStackTraceElementWrapperList() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("JUnit-Test"));
		StackTraceElementWrapper element[] = lvWrapper.getStackTraceElementWrapperList();
		assertTrue(1 < element.length);
	}
	
	public void testPrintStackTrace() throws Exception {
		PrintStream lvErr = System.err;
		System.setErr(new PrintStream(new ByteArrayOutputStream(0)));
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Exception("JUnit-Test"));
		lvWrapper.printStackTrace();
		System.setErr(lvErr);
	}
	
	public void testInvalidConstructorCall() throws Exception {
		try {
			new ThrowableWrapper((Throwable) null);
			fail("Null value is not supported.");
		} catch (NullPointerException e) {
			assertNotNull(e);
		}
	}
	
	public void testStackTraceElementWrapper() throws Exception {
		StackTraceElementWrapper lvStackTraceElementWrapper = new StackTraceElementWrapper();
		assertFalse(lvStackTraceElementWrapper.isNativeMethod());
		assertFalse(lvStackTraceElementWrapper.getNativeMethod());
		
		lvStackTraceElementWrapper.setNativeMethod(true);
		assertTrue(lvStackTraceElementWrapper.isNativeMethod());
		assertTrue(lvStackTraceElementWrapper.getNativeMethod());
	}
	
	
	public void testTryToCreateStackTraceElement() throws Exception {
		StackTraceElementWrapper lvElementWrapper = new StackTraceElementWrapper();
		lvElementWrapper.setClassName(this.getClass().getName());
		lvElementWrapper.setFileName("FileName.java");
		lvElementWrapper.setLineNumber(23);
		lvElementWrapper.setMethodName("testTryToCreateStackTraceElement");
		StackTraceElement lvElement = lvElementWrapper.tryToCreateStackTraceElement();
		assertNotNull(lvElement);
		assertEquals(this.getClass().getName(), lvElement.getClassName());
		assertEquals("FileName.java", lvElement.getFileName());
		assertEquals(23, lvElement.getLineNumber());
		assertEquals("testTryToCreateStackTraceElement", lvElement.getMethodName());
	}
	
	public void testThrowableIWrapperWithDoubleNestedException() throws Exception {
		ThrowableWrapper lvWrapper = new ThrowableWrapper(new Test2Exception("JUnit-Message", 
															new Exception("Cause", 
																new NullPointerException("Cause/Cause"))));
		
		assertEquals(Test2Exception.class.getName(), lvWrapper.getExceptionClassName());
		
		assertNotNull(lvWrapper.getCauseWrapper());
		assertEquals("JUnit-Message", lvWrapper.getMessage());
		assertEquals(Test2Exception.class.getName(), lvWrapper.getExceptionClassName());
		
		ThrowableWrapper lvCauseWrapper = lvWrapper.getCauseWrapper();
		assertEquals(Exception.class.getName(), lvCauseWrapper.getExceptionClassName());
		assertEquals("Cause", lvCauseWrapper.getMessage());
		
		ThrowableWrapper lvCauseWrapper2 = lvCauseWrapper.getCauseWrapper();
		assertEquals(NullPointerException.class.getName(), lvCauseWrapper2.getExceptionClassName());
		assertEquals("Cause/Cause", lvCauseWrapper2.getMessage());
		assertNull(lvCauseWrapper2.getCauseWrapper());
	}
}
