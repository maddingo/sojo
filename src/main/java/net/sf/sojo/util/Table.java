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
package net.sf.sojo.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sojo.interchange.csv.CsvParser;

/**
 * The table represent a structure with columns and rows.
 * By this structure should all numbers of columns the same size.
 * In the first row can be the head with the names for the columns.
 * <br/>
 * This class has methods, where can convert several rows or the total table
 * to a String representation. By this transformation are all columns separated 
 * by a delimiter (a possible sample is CSV).
 * <br/>
 * Example of a table:
 * <pre>
 * |---------------------|
 * | ColName1 | ColName2 |
 * |=====================|
 * | value11  | value12  |
 * |---------------------|
 * | value21  | value22  |
 * |---------------------|
 * </pre>
 * 
 * Transform to a String with comma - delimiter:
 * <pre>
 * ColName1,ColName2
 * value11,value12
 * value21,value22
 * </pre>
 * On the end of the row is CR LF.
 * 
 * @author linke
 *
 */
public class Table {

	public static final String DEFAULT_DELIMITER = ",";

	private List<List<?>> rows = new ArrayList<List<?>>();
	private List<Object> currentColumn = null;
	private List<String> columnNames = new ArrayList<String>();
	private boolean withColumnNames = true;
	private String delimiter = DEFAULT_DELIMITER; 
	private int numberOfColumns = -1;

	public Table() {
		newRow();
	}

	public void setWithColumnNames(boolean pvWithColumnNames) { withColumnNames = pvWithColumnNames; }
	public boolean getWithColumnNames() { return withColumnNames; }

	public String getDelimiter() { return delimiter; }
	public void setDelimiter(String pvDelimiter) { delimiter = pvDelimiter; }
	
	public void addValue2CurrentRow (Object pvValue) {
		addValue2CurrentRow( null, pvValue);
	}

	public void addValue2CurrentRow (String pvColumnName, Object pvValue) {
		currentColumn.add(pvValue);

		// define the size of columns
		int lvCurrentColumnNumber = currentColumn.size();
		if (numberOfColumns < lvCurrentColumnNumber) {
			numberOfColumns = lvCurrentColumnNumber;
		}

		// the first row contains the column names
		if (getWithColumnNames() && rows.size() == 1) {
			String lvColumnName = pvColumnName;
			if (lvColumnName == null && pvValue != null) {
				lvColumnName = pvValue.toString();
			}
			columnNames.add(lvColumnName);
		}
	}
	

	public void addValue2NewRow (Object pvValue) {
		newRow();
		addValue2CurrentRow(pvValue);
	}

	public int newRow() {
		currentColumn = new ArrayList<Object>();
		rows.add(currentColumn);
		return rows.size();
	}
	
	public void clear() {
		columnNames.clear();
		rows.clear();
	}
	

	public int getNumberOfColumns() { return numberOfColumns; }
	public int getCurrentColumnNumber() { return (currentColumn.size() - 1); }
	public int getCurrentRowNumber() { return rows.size(); }
	public List<List<?>> getRows() { return rows; }
	public List<?> getLastRow () { return rows.get(rows.size() - 1); }
	public void removeRow(int pvPos) { rows.remove(pvPos); }


	/**
	 * Transform the table in a CSV (String) representation.
	 * @return A String representation of the table.
	 */
	public String getRowsWithColumnsAsString() {
		StringBuffer sb = new StringBuffer();
		int lvSize = rows.size();
		for (int i=0; i<lvSize; i++) {
			String lvRow = row2String(i); 
			sb.append(lvRow);
			if (i < (lvSize-1) && lvRow.endsWith(CsvParser.CRLF) == false) {
				sb.append(CsvParser.CRLF);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Transform the table in a CSV (with selected delimiter) representation.
	 * @return A String representation of the table, with the column names in the head.
	 */
	public String getRowsWithColumnsAndColumnsNamesAsString() {
		StringBuffer sb = new StringBuffer();
		String lvColumnNames = getColumnNames();
		if (lvColumnNames.trim().length() > 0) {
			sb.append(lvColumnNames);
			sb.append(CsvParser.CRLF);
		}
		sb.append(getRowsWithColumnsAsString());
		return sb.toString();
	}
	
	/**
	 * Get all column - names as String, separated with a selected delimiter.
	 * @return All column - names as String.
	 */
	public String getColumnNames() {
		StringBuffer sb = new StringBuffer();
		int lvSize = columnNames.size();
		for (int i=0;i<lvSize; i++) {
			sb.append(columnNames.get(i));
			if (i < (lvSize-1)) {
				sb.append(getDelimiter());
			}			
		}
		return sb.toString();
	}

	/**
	 * Convert a row of the table in a String, 
	 * where the every column is seperated with a delimiter.
	 * @param pvRow Number of row.
	 * @return The row as String.
	 */
	public String row2String(int pvRow) {
		StringBuffer sb = new StringBuffer();
		List<?> lvColumn = rows.get(pvRow);
		int lvSize = lvColumn.size();
		for (int i=0;i<lvSize; i++) {
			sb.append(lvColumn.get(i));
			if (i < (lvSize-1)) {
				sb.append(getDelimiter());
			}
		}
		return sb.toString();
	}
	
	/**
	 * Transform the table in a String.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getColumnNames()).append("\n");
		for (int i=0; i<rows.size(); i++) {
			sb.append(row2String(i)).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Check every row. All rows must have the same number of columns.
	 * Is one column size differrent, than thrown this method a <code>IllegalStateException</code>.
	 * 
	 * @throws IllegalStateException if not all rows have the same number of columns.
	 *
	 */
	public void validateAndRemoveEmptyRows() {
		Iterator<List<?>> it = rows.iterator();
		int i = -1;
		while (it.hasNext()) {
			i++;
			List<?> lvRow = it.next();
			if (lvRow.size() != numberOfColumns) {
				if (lvRow.size() == 1 && lvRow.get(0).toString().trim().length() == 0) {
					it.remove();
				} else {
					throw new IllegalStateException("Row: " + i + " - expected number of columns: " + numberOfColumns + " but was " + lvRow.size() + " --> " + lvRow);
				}	
			}
		}
	}
}
