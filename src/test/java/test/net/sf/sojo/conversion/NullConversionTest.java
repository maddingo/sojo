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
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.NullConversion;

public class NullConversionTest extends TestCase {

	public void testNullReplaceString() throws Exception {
		String lvNullReplaceString = "~null~replace~string~";
		Converter c = new Converter();
		c.addConversion(new NullConversion(lvNullReplaceString));
		Object lvResult = c.convert(null);
		assertNotNull(lvResult);
		assertEquals(lvNullReplaceString, lvResult);
	}

	public void testNullReplaceStringAndConvertBack() throws Exception {
		String lvNullReplaceString = "~null~replace~string~";
		Converter c = new Converter();
		c.addConversion(new NullConversion(lvNullReplaceString));
		Object lvResult = c.convert(null);
		assertNotNull(lvResult);
		assertEquals(lvNullReplaceString, lvResult);
		
		lvResult = c.convert(lvResult);
		assertNull(lvResult);
	}

	public void testNullReplaceString2() throws Exception {
		String lvNullReplaceString = null;
		Converter c = new Converter();
		c.addConversion(new NullConversion(lvNullReplaceString));
		Object lvResult = c.convert(null);
		assertNull(lvResult);
		assertEquals(lvNullReplaceString, lvResult);
	}


	public void testNullReplaceStringMultiple() throws Exception {
		Converter c = new Converter();
		Object lvResult = c.convert(null);
		assertNull(lvResult);
		
		String lvNullReplaceString = "~null~replace~string~";
		c.addConversion(new NullConversion(lvNullReplaceString));
		lvResult = c.convert(null);
		assertNotNull(lvResult);
		assertEquals(lvNullReplaceString, lvResult);
	}

}
