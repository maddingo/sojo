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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import net.sf.sojo.common.WalkerInterceptor;
import net.sf.sojo.core.Constants;
import net.sf.sojo.util.Table;

public class CsvWalkerInterceptor implements WalkerInterceptor {
	
	private Table table = new Table();
	private boolean wrapSimpleValueAsList = false;
	private boolean withColumnNames = true;
	private String nullValue = "";
	
	public String getNullValue() { return nullValue; }
	public void setNullValue(String pvNullValue) { nullValue = pvNullValue; }
	
	public void setWrapSimpleValueAsList(boolean pvWrapSimpleValueAsList) { wrapSimpleValueAsList = pvWrapSimpleValueAsList; }
	public boolean gettWrapSimpleValueAsList() { return wrapSimpleValueAsList; }
	
	public void setWithColumnNames(boolean pvWithColumnNames) { withColumnNames = pvWithColumnNames; }
	public boolean getWithColumnNames() { return withColumnNames; }
	
	@Override
	public void endWalk() {	}

	@Override
	public void startWalk(Object pvStartObject) {
		table.clear();
		table.setWithColumnNames(getWithColumnNames());
	}

	private String getColumnName (Object pvKey, String pvPath) {
		String lvName = null;
		if (getWithColumnNames()) {
			if (pvKey == null) {
				if (gettWrapSimpleValueAsList() ) {
					lvName = "";
				} else {
					lvName = pvPath;
				}
			} else {
				lvName = pvKey.toString();
			}	
		}
		return lvName;
	}
	
	private String convertValue2CsvValidValue(Object pvValue) {
		String lvValue = pvValue.toString();
		if (lvValue.indexOf(CsvParser.DQUOTE) > -1) {
			lvValue = CsvParser.DQUOTE + lvValue.replaceAll("\"", "\"\"" ) + CsvParser.DQUOTE;
		}
		else if (lvValue.trim().length() > CsvParser.CRLF.length() && lvValue.indexOf(CsvParser.CRLF) > -1) {
			lvValue = CsvParser.DQUOTE + lvValue + CsvParser.DQUOTE;
		}
		else if (lvValue.indexOf(CsvParser.COMMA) > -1) {
			lvValue = CsvParser.DQUOTE + lvValue + CsvParser.DQUOTE;
		}
		return lvValue;
	}
	
	@Override
	public boolean visitElement(Object pvKey, int pvIndex, Object pvValue, int pvType, String pvPath, int pvNumberOfRecursion) {
		
		if (pvType == Constants.TYPE_SIMPLE ) {
			// special case, where simple type is in List
			if (table.getCurrentRowNumber() == 0) { table.newRow(); }
			table.addValue2CurrentRow(getColumnName (pvKey, pvPath), convertValue2CsvValidValue(pvValue));
		}
		else if (pvType == Constants.TYPE_NULL) {
			table.addValue2CurrentRow(getColumnName (pvKey, pvPath), nullValue);
		}
		
		return false;
	}

	@Override
	public void visitIterateableElement(Object pvValue, int pvType, String pvPath, int pvTypeBeginOrEnd) {

		
		if (Constants.ITERATOR_BEGIN == pvTypeBeginOrEnd  && pvPath.length() > 0 && getSize(pvValue, pvType) > 0) {
			int c = pvPath.split("\\.").length;
			if (c > 1) {
				throw new CsvParserException("The properties are to deep nested for CSV (property-path: " + pvPath + ")");
			}
			table.newRow();
		}
	}
	
	
	private int getSize (Object pvValue, int pvType) {
		int lvSize = 0;
		if (Constants.TYPE_ITERATEABLE == pvType) {
			if (pvValue instanceof Collection) {
				lvSize = ((Collection<?>) pvValue).size();
			} else {
				lvSize = Arrays.asList( (Object[]) pvValue).size();
			}
		}
		else {
			lvSize = ((Map<?,?>) pvValue).size();
		}
		return lvSize;
	}
	

	
	public String getCsvString() {
		if (getWithColumnNames()) {
			return table.getRowsWithColumnsAndColumnsNamesAsString();
		} else {
			return table.getRowsWithColumnsAsString();
		}
	}
	
	public Table getTable() {
		return table;
	}

}
