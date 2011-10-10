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

import test.net.sf.sojo.model.Node;
import net.sf.sojo.core.Conversion;
import net.sf.sojo.core.ConversionHandler;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.conversion.NullConversion;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.conversion.SimpleFormatConversion;
import junit.framework.TestCase;

public class ConversionHandlerTest extends TestCase {
	
	public void testAddConversionWithNullValue() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		try {
			lvConversionHandler.addConversion(null);
			fail("Conversion must be different from null.");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
	
	public void testAddConversion() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		lvConversionHandler.addConversion(new Simple2SimpleConversion(String.class));
		assertNull(lvConversionHandler.getConversion("TEST", Node.class));
	}

	public void testConversion() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		assertEquals(0, lvConversionHandler.size());
		
		Simple2SimpleConversion lvSimple2SimpleConversion = new Simple2SimpleConversion(String.class);
		lvConversionHandler.addConversion(lvSimple2SimpleConversion);
		assertEquals(1, lvConversionHandler.size());
		assertNotNull(lvConversionHandler.getConversionByPosition(0));
		assertEquals(lvSimple2SimpleConversion, lvConversionHandler.getConversionByPosition(0));
		
		
		lvConversionHandler.clear();
		assertEquals(0, lvConversionHandler.size());
	}


	public void testReplaceConversion() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		assertEquals(0, lvConversionHandler.size());
		
		Simple2SimpleConversion lvSimple2SimpleConversion = new Simple2SimpleConversion(String.class);
		lvConversionHandler.addConversion(lvSimple2SimpleConversion);
		assertEquals(1, lvConversionHandler.size());
		
		lvSimple2SimpleConversion = new Simple2SimpleConversion(Long.class);
		lvConversionHandler.replaceConversion(lvSimple2SimpleConversion);
		assertEquals(1, lvConversionHandler.size());
		
		Conversion lvConversion = lvConversionHandler.getConversionByPosition(0);
		assertEquals(lvConversion, lvSimple2SimpleConversion);
		assertSame(lvConversion, lvSimple2SimpleConversion);
	}

	public void testReplaceConversion2() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		assertEquals(0, lvConversionHandler.size());
		
		Simple2SimpleConversion lvSimple2SimpleConversion1 = new Simple2SimpleConversion(String.class);
		Simple2SimpleConversion lvSimple2SimpleConversion2 = new Simple2SimpleConversion(Long.class);
		lvConversionHandler.addConversion(lvSimple2SimpleConversion1);
		lvConversionHandler.addConversion(lvSimple2SimpleConversion2);
		assertEquals(2, lvConversionHandler.size());
		
		Simple2SimpleConversion lvSimple2SimpleConversionNew = new Simple2SimpleConversion(Double.class);
		lvConversionHandler.replaceConversion(lvSimple2SimpleConversionNew);
		assertEquals(2, lvConversionHandler.size());
		
		Conversion lvConversion = lvConversionHandler.getConversionByPosition(0);
		assertEquals(lvConversion, lvSimple2SimpleConversionNew);
		assertSame(lvConversion, lvSimple2SimpleConversionNew);
		
		lvConversion = lvConversionHandler.getConversionByPosition(1);
		assertFalse(lvConversion.equals(lvSimple2SimpleConversionNew));
		assertNotSame(lvConversion, lvSimple2SimpleConversionNew);
	}

	public void testReplaceAllConversion() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		assertEquals(0, lvConversionHandler.size());
		
		Simple2SimpleConversion lvSimple2SimpleConversion1 = new Simple2SimpleConversion(String.class);
		Simple2SimpleConversion lvSimple2SimpleConversion2 = new Simple2SimpleConversion(Long.class);
		lvConversionHandler.addConversion(lvSimple2SimpleConversion1);
		lvConversionHandler.addConversion(lvSimple2SimpleConversion2);
		assertEquals(2, lvConversionHandler.size());
		
		Simple2SimpleConversion lvSimple2SimpleConversionNew = new Simple2SimpleConversion(Double.class);
		lvConversionHandler.replaceAllConversion(lvSimple2SimpleConversionNew);
		assertEquals(2, lvConversionHandler.size());
		
		Conversion lvConversion = lvConversionHandler.getConversionByPosition(0);
		assertEquals(lvConversion, lvSimple2SimpleConversionNew);
		assertSame(lvConversion, lvSimple2SimpleConversionNew);
		
		lvConversion = lvConversionHandler.getConversionByPosition(1);
		assertEquals(lvConversion, lvSimple2SimpleConversionNew);
		assertSame(lvConversion, lvSimple2SimpleConversionNew);
	}

	public void testRemoveConversion() throws Exception {
		Converter lvConverter = new Converter();
		assertEquals(0, lvConverter.getConversionHandler().size());
		
		NullConversion lvNullConversion = new NullConversion(null);
		lvConverter.addConversion(lvNullConversion);
		Simple2SimpleConversion lvSimple2SimpleConversion = new Simple2SimpleConversion(String.class);
		lvConverter.addConversion(lvSimple2SimpleConversion);
		assertEquals(2, lvConverter.getConversionHandler().size());
		
		Conversion lvConversion = lvConverter.removeConversion(lvNullConversion);
		assertEquals(1, lvConverter.getConversionHandler().size());
		assertEquals(lvNullConversion, lvConversion);
		
		lvConversion = lvConverter.removeConversion(lvSimple2SimpleConversion);
		assertEquals(0, lvConverter.getConversionHandler().size());
		assertEquals(lvSimple2SimpleConversion, lvConversion);

		lvConversion = lvConverter.removeConversion(lvSimple2SimpleConversion);
		assertEquals(0, lvConverter.getConversionHandler().size());
		assertNull(lvConversion);
	}

	public void testDoubleConversion() throws Exception {
		ConversionHandler lvConversionHandler = new ConversionHandler();
		assertEquals(0, lvConversionHandler.size());

		NullConversion lvNullConversion = new NullConversion(null);
		lvConversionHandler.addConversion(lvNullConversion);
		assertEquals(1, lvConversionHandler.size());
		assertTrue(lvConversionHandler.containsConversion(lvNullConversion));

		lvConversionHandler.addConversion(lvNullConversion);
		assertEquals(1, lvConversionHandler.size());

		Simple2SimpleConversion lvSimple2SimpleConversion = new Simple2SimpleConversion(Long.class);
		assertFalse(lvConversionHandler.containsConversion(lvSimple2SimpleConversion));
		lvConversionHandler.addConversion(lvSimple2SimpleConversion);
		assertEquals(2, lvConversionHandler.size());
		assertTrue(lvConversionHandler.containsConversion(lvSimple2SimpleConversion));
	}
	
	public void testSimpleFormatConversion() throws Exception {
		SimpleFormatConversion lvConversion = new SimpleFormatConversion();
		Object o = lvConversion.convert("47.11", Double.class);
		assertNotNull(o);
		assertEquals("47.11", o);
	}
}
