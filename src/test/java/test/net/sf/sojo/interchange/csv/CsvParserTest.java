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
package test.net.sf.sojo.interchange.csv;

import java.util.List;

import net.sf.sojo.interchange.SerializerException;
import net.sf.sojo.interchange.csv.CsvParser;
import net.sf.sojo.interchange.csv.CsvParserException;
import net.sf.sojo.util.Table;
import junit.framework.TestCase;

public class CsvParserTest extends TestCase {

	public void testParseWithNullValue() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine(null);
		assertEquals(0, l.size());
	}

	public void testNoElementLine() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("");
		assertEquals(0, l.size());
	}

	public void testParseLine() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, b, c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineWithSemicolonAsSeperator() throws Exception {
		CsvParser lvParser = new CsvParser();
		lvParser.setSeperator(';');
		List<?> l = lvParser.parseAndGetFirstLine("a; b, c");
		assertEquals(2, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b, c", l.get(1));
	}

	public void testParseLineWithSemicolonAsSeperatorWithTwoRows() throws Exception {
		CsvParser lvParser = new CsvParser();
		lvParser.setSeperator(';');
		List<?> l = lvParser.parse("a;b " + CsvParser.CRLF + "1; 2 ").getRows();
		assertEquals(2, l.size());
		
		List<?> l1 = (List<?>) l.get(0);
		assertEquals("a", l1.get(0));
		assertEquals("b", l1.get(1));
		
		List<?> l2 = (List<?>) l.get(1);
		assertEquals("1", l2.get(0));
		assertEquals("2", l2.get(1));
	}

	public void testParseLineWithSemicolonAsSeperatorWithTwoRowsAndDoubleQuote() throws Exception {
		CsvParser lvParser = new CsvParser();
		lvParser.setSeperator(';');
		List<?> l = lvParser.parse("a; \"b \n s\" " + CsvParser.CRLF + "1; 2 ").getRows();
		assertEquals(2, l.size());
		
		List<?> l1 = (List<?>) l.get(0);
		assertEquals("a", l1.get(0));
		assertEquals("b \n s", l1.get(1));
		
		List<?> l2 = (List<?>) l.get(1);
		assertEquals("1", l2.get(0));
		assertEquals("2", l2.get(1));
	}

	public void testEmptyLine() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine(",,");
		assertEquals(3, l.size());
		assertEquals("", l.get(0));
		assertEquals("", l.get(1));
		assertEquals("", l.get(2));
		
		l = lvParser.parseAndGetFirstLine(",, ");
		assertEquals(3, l.size());
		assertEquals("", l.get(0));
		assertEquals("", l.get(1));
		assertEquals("", l.get(2));
		
		l = lvParser.parseAndGetFirstLine(", ,");
		assertEquals(3, l.size());
		assertEquals("", l.get(0));
		assertEquals("", l.get(1));
		assertEquals("", l.get(2));
		
		l = lvParser.parseAndGetFirstLine(" ,,");
		assertEquals(3, l.size());
		assertEquals("", l.get(0));
		assertEquals("", l.get(1));
		assertEquals("", l.get(2));		
	}
	
	public void testStringWithEmptyValue() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine(",Wed Jan 31 20:18:00 CET 2007,,BMW,0,test.net.sf.sojo.model.Car");
		assertEquals(6, l.size());
		assertEquals("", l.get(0));
		assertEquals("Wed Jan 31 20:18:00 CET 2007", l.get(1));
		assertEquals("", l.get(2));
		assertEquals("BMW", l.get(3));
		assertEquals("0", l.get(4));
		assertEquals("test.net.sf.sojo.model.Car", l.get(5));
	}
	
	public void testParseLineWithSpaceValue() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine(" , b, c");
		assertEquals(3, l.size());
		assertEquals("", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineWithEmptyValue() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine(", b, c");
		assertEquals(3, l.size());
		assertEquals("", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineWithEmptyValueInTheMiddle() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a,  , c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("", l.get(1));
		assertEquals("c", l.get(2));
	}
	
	public void testParseLineDoubleQuote() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("\" a,b \", b, c");
		assertEquals(3, l.size());
		assertEquals(" a,b ", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}
	
	public void testParseLineDoubleQuoteWithSpace() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("\" a, b \", b, c");
		assertEquals(3, l.size());
		assertEquals(" a, b ", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineDoubleQuoteWithSpace2() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, \"  b  \",  c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("  b  ", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineDoubleQuoteWithSpace3() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a,\"  b  \" ,  c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("  b  ", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineDoubleQuoteWithSpaceOnTheEndOfLine() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a,  c, \"  b  \"  ");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("c", l.get(1));
		assertEquals("  b  ", l.get(2));		
	}

	public void testParseLineWithSpaceInDoubleQuote() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("\" a b \", b, c");
		assertEquals(3, l.size());
		assertEquals(" a b ", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testParseLineDoubleQuoteWithInsideComma() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("ab ,\", \", cd, xyz");
		assertEquals(4, l.size());
		assertEquals("ab", l.get(0));
		assertEquals(", ", l.get(1));
		assertEquals("cd", l.get(2));
		assertEquals("xyz", l.get(3));
	}

	public void testParseLineDobleDoubleQuote() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("\"a \"\"bc\"\" d\", xyz");
		assertEquals(2, l.size());
		assertEquals("a \"bc\" d", l.get(0));
		assertEquals("xyz", l.get(1));
		
		l = lvParser.parseAndGetFirstLine("\"a \"\"bc\"\" d\", xyz");
		assertEquals(2, l.size());
		assertEquals("a \"bc\" d", l.get(0));
		assertEquals("xyz", l.get(1));
	}

	public void testParseLineDoubleQuoteWithoutClosedQuote() throws Exception {
		CsvParser lvParser = new CsvParser();
		try {
			lvParser.parseAndGetFirstLine("\" a, b , b, c");
			fail("Doble quote don't closed");
		} catch (SerializerException e) {
			assertNotNull(e);
		}
	}

	public void testParseLineDoubleQuoteWithoutClosedQuote2() throws Exception {
		CsvParser lvParser = new CsvParser();
		try {
			lvParser.parseAndGetFirstLine("\" a, b \"\" , b, c");
			fail("Doble quote don't closed");
		} catch (SerializerException e) {
			assertNotNull(e);
		}
	}

	public void testParseLineExtra() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("John,Doe,120 jefferson st.,Riverside, NJ, 08075");
		assertEquals(6, l.size());
		assertEquals("John", l.get(0));
		assertEquals("Doe", l.get(1));
		assertEquals("120 jefferson st.", l.get(2));
		assertEquals("Riverside", l.get(3));
		assertEquals("NJ", l.get(4));
		assertEquals("08075", l.get(5));
		
		l = lvParser.parseAndGetFirstLine("Jack,McGinnis,220 hobo Av.,Phila, PA,09119");
		assertEquals(6, l.size());
		assertEquals("Jack", l.get(0));
		assertEquals("McGinnis", l.get(1));
		assertEquals("220 hobo Av.", l.get(2));
		assertEquals("Phila", l.get(3));
		assertEquals("PA", l.get(4));
		assertEquals("09119", l.get(5));
		
		l = lvParser.parseAndGetFirstLine("\"John \"\"Da Man\"\"\",Repici,120 Jefferson St.,Riverside , NJ,08075");
		assertEquals(6, l.size());
		assertEquals("John \"Da Man\"", l.get(0));
		assertEquals("Repici", l.get(1));
		assertEquals("120 Jefferson St.", l.get(2));
		assertEquals("Riverside", l.get(3));
		assertEquals("NJ", l.get(4));
		assertEquals("08075", l.get(5));
	}

	public void testWithOneElementWithoutComma() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("A");
		assertEquals(1, l.size());
	}

	public void testWithEmptyCrOnTheEnd() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("\r");
		assertEquals(1, l.size());
	}

	public void testWithEmptyLfOnTheEnd() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("\n");
		assertEquals(1, l.size());
	}

	public void testWithCrOnTheEnd() throws Exception {
		try {
			new CsvParser().parseAndGetFirstLine("a, b, c\r");
			fail("String ends with CR and not with CR LF.");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}


	public void testWithLineFeedOnTheEnd() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, b, c\n");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));		
	}
	
	public void testWithCrLfOnTheEnd() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, b, c\r\n");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));		
	}

	public void testWithCrInTheCsvString() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, \" \rb\", c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals(" \rb", l.get(1));
		assertEquals("c", l.get(2));		
	}

	public void testWithLfInTheCsvString() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, \" \nb \", c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals(" \nb ", l.get(1));
		assertEquals("c", l.get(2));		
	}

	public void testWithCrLfInTheCsvString() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a, \" \r\nb \", c");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals(" \r\nb ", l.get(1));
		assertEquals("c", l.get(2));		
	}

	
	public void testParseLineWithBadDoubleQuote() throws Exception {
		CsvParser lvParser = new CsvParser();
		try {
			lvParser.parseAndGetFirstLine("a, x \"  b  \",  c");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	public void testParseLineWithBadDoubleQuoteWithSpaces() throws Exception {
		CsvParser lvParser = new CsvParser();
		try {
			lvParser.parseAndGetFirstLine("a,  x \"  b  \",  c");
		} catch (CsvParserException e) {
			assertNotNull(e);
		}
	}

	public void testManyCsvLines() throws Exception {
		CsvParser lvParser = new CsvParser();
		List<?> l = lvParser.parseAndGetFirstLine("a,b, c\r\n x,y,z");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
		
		l = lvParser.parse("a,b, c\r\n x,y,z").getRows();
		assertEquals(2, l.size());
		
		List<?> l1 = (List<?>) l.get(0);
		assertEquals(3, l1.size());
		assertEquals("a", l1.get(0));
		assertEquals("b", l1.get(1));
		assertEquals("c", l1.get(2));
		
		List<?> l2 = (List<?>) l.get(1);
		assertEquals(3, l2.size());
		assertEquals("x", l2.get(0));
		assertEquals("y", l2.get(1));
		assertEquals("z", l2.get(2));
	}

	public void testParsCsvStringWithCrLf() throws Exception {
		String str = "b,c\r\n2,3";
		List<?> l = new CsvParser().parse(str).getRows();
		assertEquals(2, l.size());
		
		List<?> l2 = (List<?>) l.get(0);
		assertEquals("b", l2.get(0));
		assertEquals("c", l2.get(1));
		
		l2 = (List<?>) l.get(1);
		assertEquals("2", l2.get(0));
		assertEquals("3", l2.get(1));
	}
	
	public void testname() throws Exception {
		String s = "description,build,properties,name,~unique-id~,class" + CsvParser.CRLF +
						"\"This is my car." + CsvParser.CRLF +
						"I love this car.\",,,BMW,0,test.net.sf.sojo.model.Car";
		List<?> lvRows = new CsvParser().parse(s).getRows();
		assertEquals(2, lvRows.size());
		
		List<?> lvRow1 = (List<?>) lvRows.get(0);
		assertEquals(6, lvRow1.size());
		assertEquals("description", lvRow1.get(0));
		assertEquals("build", lvRow1.get(1));
		assertEquals("name", lvRow1.get(3));
		assertEquals("class", lvRow1.get(5));

		List<?> lvRow2 = (List<?>) lvRows.get(1);
		assertEquals(6, lvRow2.size());
		assertEquals("This is my car." + CsvParser.CRLF + "I love this car.", lvRow2.get(0));
		assertEquals("", lvRow2.get(1));
		assertEquals("BMW", lvRow2.get(3));
		assertEquals("test.net.sf.sojo.model.Car", lvRow2.get(5));
	}
	
	public void testRFC_case_1() throws Exception {
		CsvParser lvParser = new CsvParser();
		String s = 	"aaa,bbb,ccc" + CsvParser.CRLF +
					"zzz,yyy,xxx"  + CsvParser.CRLF;
		List<?> l = lvParser.parse(s).getRows();
		assertEquals(2, l.size());
		
		s = 	"aaa,bbb,ccc" + CsvParser.CRLF +
				"zzz,yyy,xxx"  + CsvParser.CRLF +
				" ";
		l = lvParser.parse(s).getRows();
		assertEquals(2, l.size());
		
		s = 	"aaa,bbb,ccc" + CsvParser.CRLF +
				"zzz,yyy,xxx"  + CsvParser.CRLF +
				" \t";
		l = lvParser.parse(s).getRows();
		assertEquals(2, l.size());
		
		s = 	"aaa,bbb,ccc" + CsvParser.CRLF +
				"zzz,yyy,xxx"  + CsvParser.CRLF +
				" \t"   + CsvParser.CRLF;
		l = lvParser.parse(s).getRows();
		assertEquals(2, l.size());
	}
	
	public void testRFC_case_2() throws Exception {
		CsvParser lvParser = new CsvParser();
		String s = "aaa,bbb,ccc" + CsvParser.CRLF +
						"zzz,yyy,xxx";
		Table t = lvParser.parse(s);
		assertEquals(2, t.getRows().size());
		assertEquals(3, t.getNumberOfColumns());
		assertEquals("aaa,bbb,ccc", t.row2String(0));
		assertEquals("zzz,yyy,xxx", t.row2String(1));
	}

	public void testRFC_case_3() throws Exception {
		CsvParser lvParser = new CsvParser();
		String s = "field_name,field_name,field_name"  + CsvParser.CRLF +
						"aaa,bbb,ccc" + CsvParser.CRLF +
						"zzz,yyy,xxx"  + CsvParser.CRLF;
		Table t = lvParser.parse(s);
		assertEquals(3, t.getRows().size());
		assertEquals(3, t.getNumberOfColumns());
		assertEquals("field_name,field_name,field_name", t.getColumnNames());
		assertEquals("aaa,bbb,ccc", t.row2String(1));
		assertEquals("zzz,yyy,xxx", t.row2String(2));
	}


	public void testParseWithoutLineFeed_CRandLF() throws Exception {
		CsvParser lvParser = new CsvParser();
		lvParser.setWithLineFeed(false);
		List<?> l = lvParser.parse("a,b " + CsvParser.CRLF + "1, 2 ").getRows();
		assertEquals(2, l.size());
		
		List<?> l1 = (List<?>) l.get(0);
		assertEquals("a", l1.get(0));
		assertEquals("b", l1.get(1));
		
		List<?> l2 = (List<?>) l.get(1);
		assertEquals("1", l2.get(0));
		assertEquals("2", l2.get(1));
	}

	public void _testParseWithoutLineFeed_onlyLF() throws Exception {
		CsvParser lvParser = new CsvParser();
		lvParser.setWithLineFeed(false);
		List<?> l = lvParser.parse("a,b " + CsvParser.LF + "1, 2 ").getRows();
		assertEquals(1, l.size());
		
		List<?> l1 = (List<?>) l.get(0);
		assertEquals("a", l1.get(0));
		assertEquals("b", l1.get(1));
		
		List<?> l2 = (List<?>) l.get(1);
		assertEquals("1", l2.get(0));
		assertEquals("2", l2.get(1));
	}

}
