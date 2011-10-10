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
package test.net.sf.sojo.conversion;

import junit.framework.TestCase;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.NotSupportedClassConversion;

public class NotSupportedClassConversionTest extends TestCase {
	
	public void testObjectNotSupported() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NotSupportedClassConversion(new Class[] { Object.class }));
		try {
			c.convert(new Object());
			fail("Object is not supported");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}

	public void testObjectAndStringNotSupported() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NotSupportedClassConversion(new Class[] { Object.class, String.class }));
		try {
			c.convert(new Object());
			fail("Object is not supported");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
		
		try {
			c.convert("TestString");
			fail("String is not supported");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
		
	}

	public void testNullNotSupported() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NotSupportedClassConversion(new Class[] { null }));
		
		Object lvResult = c.convert("TestString");
		assertNotNull(lvResult);
		assertEquals("TestString", lvResult);
		
		try {
			c.convert(null);
			fail("Null is not supported");
		} catch (ConversionException e) {
			assertNotNull(e);
		}
	}
	

	public void testNullParamNotSupported() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NotSupportedClassConversion(null));
		
		Object lvResult = c.convert("TestString");
		assertNotNull(lvResult);
		assertEquals("TestString", lvResult);
		
		lvResult = c.convert(null);
		assertNull(lvResult);
	}
	
	public void testNullParamNotSupported2() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NotSupportedClassConversion());
		
		Object lvResult = c.convert("TestString");
		assertNotNull(lvResult);
		assertEquals("TestString", lvResult);
		
		lvResult = c.convert(null);
		assertNull(lvResult);
	}


	public void testEmptyParamNotSupported() throws Exception {
		Converter c = new Converter();
		c.addConversion(new NotSupportedClassConversion(new Class[0]));
		
		Object lvResult = c.convert("TestString");
		assertNotNull(lvResult);
		assertEquals("TestString", lvResult);
		
		lvResult = c.convert(null);
		assertNull(lvResult);
	}


}
