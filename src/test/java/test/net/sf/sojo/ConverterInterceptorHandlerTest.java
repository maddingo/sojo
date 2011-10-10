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

import net.sf.sojo.core.ConversionContext;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.ConverterInterceptorHandler;
import junit.framework.TestCase;

public class ConverterInterceptorHandlerTest extends TestCase {
	
	public void testAddConverterInterceptor() throws Exception {
		ConverterInterceptorHandler lvHandler = new ConverterInterceptorHandler ();
		try {
			lvHandler.addConverterInterceptor(null);
			fail("Can't add null value");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	public void testFireOnError() throws Exception {
		TestConverterInterceptorRecursive lvInterceptor = new TestConverterInterceptorRecursive();
		ConverterInterceptorHandler lvHandler = new ConverterInterceptorHandler ();
		assertEquals(0, lvHandler.size());
		lvHandler.addConverterInterceptor(lvInterceptor);
		
		assertEquals(1, lvHandler.size());
		assertEquals(lvInterceptor, lvHandler.getConverterInterceptorByPosition(0));
		assertEquals(0, lvInterceptor.onError);
		
		lvHandler.fireOnError(new ConversionException("TEST"));
		assertEquals(1, lvInterceptor.onError);
	}

	public void testFireBeforeAndAfterConvertRecursion() throws Exception {
		TestConverterInterceptor lvInterceptor = new TestConverterInterceptor();
		ConverterInterceptorHandler lvHandler = new ConverterInterceptorHandler ();
		assertEquals(0, lvHandler.size());
		lvHandler.addConverterInterceptor(lvInterceptor);
		
		assertEquals(0, lvInterceptor.onError);
		
		lvHandler.fireOnError(new ConversionException("TEST"));
		assertEquals(1, lvInterceptor.onError);
		
		lvHandler.fireBeforeConvertRecursion(new ConversionContext());
		lvHandler.fireAfterConvertRecursion(new ConversionContext());
	}

}
