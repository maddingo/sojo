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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.sf.sojo.core.NonCriticalExceptionHandler;
import net.sf.sojo.util.Util;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class NonCriticalExceptionHandlerTest {

	@BeforeClass
	public static void setUp() throws Exception {
		NonCriticalExceptionHandler.setNonCriticalExceptionHandlerEnabled(true);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		NonCriticalExceptionHandler.setNonCriticalExceptionHandlerEnabled(false);
	}

	@Test
	public void testDefaultNonCriticalExceptionHandler() throws Exception {
		PrintStream lvOut = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream(0)));
		NonCriticalExceptionHandler.handleException(Util.class, "test message");
		Exception lvException = new NullPointerException("NPE");
		NonCriticalExceptionHandler.handleException(Util.class, lvException, "test message");
		System.setOut(lvOut);
	}

	@Test
	public void testIsNonCriticalExceptionHandlerEnabled() throws Exception {
		assertTrue(NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled());
		
		NonCriticalExceptionHandler.setNonCriticalExceptionHandlerEnabled(false);
		assertFalse(NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled());
	}

	@Test
	public void testname() throws Exception {
		TestNonCriticalExceptionHandler lvCriticalExceptionHandler = new TestNonCriticalExceptionHandler();
		NonCriticalExceptionHandler.setNonCriticalExceptionHandler(lvCriticalExceptionHandler);

		String lvMessage = "My Message";
		NonCriticalExceptionHandler.handleException(Util.class, lvMessage);
		assertEquals(lvMessage, lvCriticalExceptionHandler.getMessage());
		assertEquals(Util.class, lvCriticalExceptionHandler.getThrownClasses());
		assertNull(lvCriticalExceptionHandler.getException());
		
		lvMessage = "My Message 2";
		Exception lvException = new NullPointerException("NPE");
		NonCriticalExceptionHandler.handleException(Util.class, lvException, lvMessage);
		assertEquals(lvMessage, lvCriticalExceptionHandler.getMessage());
		assertEquals(Util.class, lvCriticalExceptionHandler.getThrownClasses());
		assertEquals(lvException, lvCriticalExceptionHandler.getException());
	}

	@Test
	public void testGetNonCriticalExceptionHandler() throws Exception {
		TestNonCriticalExceptionHandler lvCriticalExceptionHandler = new TestNonCriticalExceptionHandler();
		NonCriticalExceptionHandler.setNonCriticalExceptionHandler(lvCriticalExceptionHandler);
		assertEquals(lvCriticalExceptionHandler, NonCriticalExceptionHandler.getNonCriticalExceptionHandler());
	}
	
}
