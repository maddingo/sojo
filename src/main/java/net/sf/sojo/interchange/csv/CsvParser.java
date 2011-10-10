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
package net.sf.sojo.interchange.csv;

import java.util.ArrayList;
import java.util.List;

import net.sf.sojo.util.Table;

public class CsvParser {

	public static final char COMMA = ',';
	public static final char SPACE = ' ';
	public static final char DQUOTE = '"';
	public static final char CR =  0x0D; // '\r';
	public static final char LF = 0x0A; // '\n';
	public static final String CRLF = "\r\n"; //0x0D + 0x0A; 

	private int charPointer = 0;
	private char seperator = COMMA;
	private Table table = null;
	private boolean withLineFeed = true;
		
	public char getSeperator() { return seperator; }
	public void setSeperator(char pvSeperator) { seperator = pvSeperator; }
	
	public void setWithLineFeed(boolean pvWithLineFeed) { withLineFeed = pvWithLineFeed; }
	public boolean getWithLineFeed() { return withLineFeed; }
	
	private char getNextCahr(final char pvChars[],  char pvElse) {
		if (pvChars.length > (charPointer + 1)) {
			return pvChars[charPointer + 1];
		} else {
			return pvElse;
		}

	}
	
	private void escaped(final char pvChars[]) {
		StringBuffer sb = new StringBuffer();
		int charLength = pvChars.length;
		boolean cancle = false;
		while (cancle == false) {
			if (charPointer < charLength) {
				char c = pvChars[charPointer];
				if (DQUOTE == c ) {
					char nextChar = getNextCahr(pvChars, ' ');
					if (DQUOTE == nextChar) {
						sb.append(c);
						charPointer++;
					} else {
						cancle = true;
					}
				} else {
					sb.append(c);
				}
				charPointer++;
			} else {
				throw new CsvParserException("Missing closed double quote.");
			}
		}
		table.addValue2CurrentRow(sb.toString());
		
		// after to find double quote, ignore spaces 
		cancle = false;
		while (cancle == false) {
			if (charPointer < charLength) {
				char c = pvChars[charPointer];
				if (getSeperator() == c || CR == c) {
					cancle = true;
				}  else {
					charPointer++;
				}
			} else {
				cancle = true;
			}
		}
	}
	
	private boolean nonEscaped(final char pvChars[]) {
		StringBuffer sb = new StringBuffer();
		int charLength = pvChars.length;
		boolean cancel = false;
		boolean lvReturn = false;
		int startCharPointer = charPointer + 1;
		boolean lvAddEmptyString = false;
		while (cancel == false) {
			if (charPointer < charLength) {
				char c = pvChars[charPointer];
				if (getSeperator() == c ) {
					cancel = true;
					char nextChar = getNextCahr(pvChars, CR);
					// comma is on the last position, add imitation
					if (charPointer == (charLength -1) || getSeperator() == nextChar || CR == nextChar) {
						lvAddEmptyString = true;
					}
				}
				else if (CR == c) {
					
					if (getWithLineFeed() == true) {
						char nextChar = getNextCahr(pvChars, ' ');
						if (LF == nextChar) {
							cancel = true;
							lvReturn = true;
						} else {
							throw new CsvParserException("Expected LF and not -" + nextChar + "-");
						}
					} else {
						cancel = true;
						lvReturn = true;
					}
					
				}
				else if (DQUOTE == c) {
					for (int i=startCharPointer; i<charPointer; i++) {
						if (SPACE != pvChars[i]) {
							throw new CsvParserException("Invalid char: " + pvChars[i] + " on column "  + i + " in csv string: " + new String(pvChars));
						}
					}
					// nonEscaped is a mistake, in the char array is a double quote
					return false;
				}
				else {
					sb.append(c);
				}
				charPointer++;
			} else {
				cancel = true;
			}
		}
		table.addValue2CurrentRow(sb.toString().trim());
		if (lvAddEmptyString) {
			table.addValue2CurrentRow("");
		}
		return lvReturn;
	}

	public List parseAndGetFirstLine(final String pvLineString) {
		Table lvTable = parse(pvLineString);
		if (lvTable.getCurrentRowNumber() > 0) {
			return (List) lvTable.getRows().get(0);
		} else {
			return new ArrayList();
		}
	}
	
	public Table parse(final String pvLineString) {
		table = new Table();
		
		if (pvLineString != null) {
			char lvChars[] = pvLineString.toCharArray();
			int charLength = lvChars.length;
			boolean cancel = false;
			charPointer = 0;
	
			if (charLength > 0) {
				
				// special case, comma on the first position
				if (getSeperator() == lvChars[0]) { 
					table.addValue2CurrentRow("");
				}
				
				while (cancel == false) {
					// on the end, break the while
					if  (charLength <= charPointer) {
						cancel = true;
						table.newRow();
					} else {
						char c  = lvChars[charPointer];
						if (getSeperator() == c) { 
							char nextChar = getNextCahr(lvChars, CR);
							if (getSeperator() == nextChar || CR == nextChar) {
								table.addValue2CurrentRow("");
							}
							// ignore comma
							charPointer++;
						}
						else if (CR == c) {
							if (charLength == 1) {
								table.addValue2CurrentRow("");
								charPointer++;
							}
							
							if (getWithLineFeed() == true) {
								char nextChar = getNextCahr(lvChars, ' ');
								if (LF == nextChar) {
									charPointer++;
									table.newRow();
								}
							} else {
								charPointer++;
								table.newRow();								
							}

						}
						else if (DQUOTE == c) {
							charPointer++;
							escaped(lvChars);
						}
						else {
							boolean isCrLf = nonEscaped(lvChars);
							if (isCrLf) {
								table.newRow();
							}
						}
					}
				} // WHILE
			}
		} // IF NULL
		
		table.removeRow( (table.getCurrentRowNumber() - 1) );
		try {
			table.validateAndRemoveEmptyRows();	
		} catch (IllegalStateException e) {
			throw new CsvParserException(e.getMessage());
		}
		
		return table;
	}
		
}