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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.sf.sojo.util.Table;
import org.junit.Test;

import static org.junit.Assert.*;

public class TableTest {

	@Test
	public void testColumnNumber() throws Exception {
		Table lvTable = new Table();
		assertEquals(1, lvTable.getCurrentRowNumber());
		assertEquals(-1, lvTable.getCurrentColumnNumber());
		
		lvTable.addValue2CurrentRow("Dummy");
		assertEquals(1, lvTable.getCurrentRowNumber());
		assertEquals(0, lvTable.getCurrentColumnNumber());
	}

	@Test
	public void testRowNumber() throws Exception {
		Table lvTable = new Table();
		assertEquals(1, lvTable.getCurrentRowNumber());
		assertEquals(-1, lvTable.getCurrentColumnNumber());
		
		lvTable.newRow();
		assertEquals(2, lvTable.getCurrentRowNumber());
	}

	@Test
	public void testAddValue2CurrentRow() throws Exception {
		Table lvTable = new Table();
		assertEquals(1, lvTable.getCurrentRowNumber());
		assertEquals(-1, lvTable.getCurrentColumnNumber());

		lvTable.addValue2CurrentRow("val", "name");
		assertEquals(1, lvTable.getCurrentRowNumber());
		assertEquals(0, lvTable.getCurrentColumnNumber());
	}

	@Test
	public void testAddValue2NewRow() throws Exception {
		Table lvTable = new Table();
		assertEquals(1, lvTable.getCurrentRowNumber());
		assertEquals(-1, lvTable.getCurrentColumnNumber());
		
		lvTable.addValue2NewRow("value");
		assertEquals(2, lvTable.getCurrentRowNumber());
		assertEquals(0, lvTable.getCurrentColumnNumber());
	}

	@Test
	public void testColumnNames() throws Exception {
		Table lvTable = new Table();
		lvTable.addValue2CurrentRow("Name", "Mario");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("27"));
		
		String s = lvTable.getColumnNames();
		assertEquals("Name,Age", s);
	}

	@Test
	public void testColumnValues() throws Exception {
		Table lvTable = new Table();
		lvTable.addValue2CurrentRow("Name", "Mario");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("27"));
		
		assertEquals(1, lvTable.getCurrentRowNumber());
		String s = lvTable.row2String(0);
		assertEquals("Mario,27", s);
	}

	@Test
	public void testColumnValuesInList() throws Exception {
		Table lvTable = new Table();
		lvTable.addValue2CurrentRow("Name", "Mario");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("27"));
		int lvRowNumber = lvTable.newRow();
		assertEquals(2, lvRowNumber);
		lvTable.addValue2CurrentRow("Name", "Ilka");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("7"));
		
		assertEquals(2, lvTable.getCurrentRowNumber());
		String s = lvTable.row2String(0);
		assertEquals("Mario,27", s);
		
		s = lvTable.row2String(1);
		assertEquals("Ilka,7", s);
		
		lvTable.clear();
		assertEquals(0, lvTable.getCurrentRowNumber());
	}

	@Test
	public void testCurrentColumnNumber() throws Exception {
		Table lvTable = new Table();
		assertEquals(-1, lvTable.getCurrentColumnNumber());
		
		lvTable.addValue2CurrentRow("Value 1");
		assertEquals(0, lvTable.getCurrentColumnNumber());
		
		lvTable.addValue2CurrentRow("Value 2");
		assertEquals(1, lvTable.getCurrentColumnNumber());
	}

	@Test
	public void testChangeDelimiter() throws Exception {
		Table lvTable = new Table();
		lvTable.setDelimiter(";");
		lvTable.addValue2CurrentRow("Name", "Mario");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("27"));
		
		assertEquals(1, lvTable.getCurrentRowNumber());
		String s = lvTable.row2String(0);
		assertEquals("Mario;27", s);
	}

	@Test
	public void testChangeDelimiterWithMoreThanOneCharacter() throws Exception {
		Table lvTable = new Table();
		lvTable.setDelimiter("@#");
		lvTable.addValue2CurrentRow("Name", "Mario");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("27"));
		lvTable.newRow();
		lvTable.addValue2CurrentRow("Name", "Ilka");
		lvTable.addValue2CurrentRow("Age", Integer.valueOf("7"));
		
		assertEquals(2, lvTable.getCurrentRowNumber());
		String s = lvTable.row2String(0);
		assertEquals("Mario@#27", s);
		s = lvTable.row2String(1);
		assertEquals("Ilka@#7", s);
	}

	@Test
	public void testGetRows() throws Exception {
		Table lvTable = new Table();
		Date d = new Date();
		lvTable.addValue2CurrentRow("Date", d);
		lvTable.addValue2CurrentRow("Long", Long.valueOf("27"));
		lvTable.addValue2CurrentRow("BigDecimal", BigDecimal.valueOf(27));
		lvTable.addValue2CurrentRow("Double", Double.valueOf("0.07"));
		lvTable.newRow();
		Date d2 = new Date();
		lvTable.addValue2CurrentRow(d2);
		lvTable.addValue2CurrentRow(Long.valueOf("29"));
		lvTable.addValue2CurrentRow(BigDecimal.valueOf(27));
		lvTable.addValue2CurrentRow(Double.valueOf("47.11"));

		assertEquals(2, lvTable.getCurrentRowNumber());
		String s = lvTable.getColumnNames();
		assertEquals("Date,Long,BigDecimal,Double", s);
		s = lvTable.row2String(0);
		assertEquals(d.toString() + ",27,27,0.07", s);
		s = lvTable.row2String(1);
		assertEquals(d.toString() + ",29,27,47.11", s);
		
		assertEquals(2, lvTable.getRows().size());
		
		List lvLastRow = lvTable.getLastRow();
		assertEquals(d2.toString(), lvLastRow.get(0).toString());
		assertEquals("29", lvLastRow.get(1).toString());
		assertEquals("27", lvLastRow.get(2).toString());
		assertEquals("29", lvLastRow.get(1).toString());
	}

	@Test
	public void testNotSameNumberOfColumnsByAllRows() throws Exception {
		Table lvTable = new Table();
		lvTable.addValue2CurrentRow("1");
		lvTable.addValue2CurrentRow("2");
		lvTable.newRow();
		lvTable.addValue2CurrentRow("3");
		try {
			lvTable.validateAndRemoveEmptyRows();
			fail("The second row has only one column in contrast to the first row with two columns.");
		} catch (IllegalStateException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testSameNumberOfColumnsByAllRows() throws Exception {
		Table lvTable = new Table();
		lvTable.addValue2CurrentRow("");
		lvTable.newRow();
		lvTable.addValue2CurrentRow("1");
		lvTable.addValue2CurrentRow("2");
		lvTable.newRow();
		lvTable.addValue2CurrentRow("");
		lvTable.newRow();
		lvTable.addValue2CurrentRow("3");
		lvTable.addValue2CurrentRow("3");
		lvTable.newRow();
		lvTable.addValue2CurrentRow("");
		
		assertEquals(5, lvTable.getCurrentRowNumber());
		
		lvTable.validateAndRemoveEmptyRows();
		assertEquals(2, lvTable.getCurrentRowNumber());
	}

}
