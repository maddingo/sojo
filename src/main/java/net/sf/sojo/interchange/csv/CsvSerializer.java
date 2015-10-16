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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.sojo.core.Conversion;
import net.sf.sojo.core.ConversionException;
import net.sf.sojo.core.conversion.Simple2SimpleConversion;
import net.sf.sojo.core.reflect.ReflectionHelper;
import net.sf.sojo.interchange.AbstractSerializer;
import net.sf.sojo.util.Table;

public class CsvSerializer extends AbstractSerializer {

	private CsvWalkerInterceptor csvWalkerInterceptor = new CsvWalkerInterceptor();
	private CsvParser csvParser = new CsvParser();
	private boolean ignoreNullValues = false;
	
	public CsvSerializer() {
		setWithSimpleKeyMapper(false);
		walker.addInterceptor(csvWalkerInterceptor);
		setIgnoreNullValues(false);
	}
	
	public void setIgnoreNullValues(boolean pvIgnoreNullValues) {
		ignoreNullValues = pvIgnoreNullValues;
		walker.setIgnoreNullValues(ignoreNullValues);
	}
	public boolean getIgnoreNullValues() { return ignoreNullValues; }
	
	public void setNullValue(String pvNullValue) { csvWalkerInterceptor.setNullValue(pvNullValue); }
	public String getNullValue() { return csvWalkerInterceptor.getNullValue(); }
	
	public void setWithPropertyNamesInFirstLine(boolean pvWithPropertyNamesInFirstLine) {
		csvWalkerInterceptor.setWithColumnNames(pvWithPropertyNamesInFirstLine);
	}
	public boolean getWithPropertyNamesInFirstLine() {
		return csvWalkerInterceptor.getWithColumnNames();
	}

	public void setDelimiter(String pvDelimiter) {
		csvWalkerInterceptor.getTable().setDelimiter(pvDelimiter);
	}
	public String getDelimiter() {
		return csvWalkerInterceptor.getTable().getDelimiter();
	}
	
	@Override
	public Object serialize(Object pvRootObject) {
		csvWalkerInterceptor.setWrapSimpleValueAsList(false);
		if (pvRootObject == null) {
			return null;
		} else {
			Object o = pvRootObject;
			if ( ! (o instanceof Collection) && o.getClass().isArray() == false) {
				csvWalkerInterceptor.setWrapSimpleValueAsList(true);
				List<Object> l = new ArrayList<Object>();
				l.add(pvRootObject);
				o = l;
			}
			walker.walk(o);
			return csvWalkerInterceptor.getCsvString();			
		}
	}

	

	@Override
	public Object deserialize(Object pvSourceObject, Class<?> pvRootClass) {
		Object o = null;
		if (pvSourceObject == null) {
			o = null;
		}
		else if ( ! (pvSourceObject instanceof String) ) {
			throw new CsvParserException("Only objects from type String can deserialize: " + pvSourceObject.getClass().getName());
		}
		else {
			Table lvTable = csvParser.parse(pvSourceObject.toString());
			List<?> lvRows = lvTable.getRows();
			if (lvRows.size() == 0) {
				o = getNullValue();
			}
			else if (lvRows.size() == 1) {
				List<?> lvColumn = (List<?>) lvRows.get(0);
				if (lvColumn.size() == 1) {
					o = lvColumn.get(0);
				} else {
					o = lvColumn;
				}
				try {
					o = convertString2Object(o, pvRootClass);	
				} catch (ConversionException e) {
					throw new CsvParserException("Can't convert object: " + o + " (" + o.getClass().getName() + ") to class: " + pvRootClass); 
				}
				
			}
			// more than one row
			else {
				if (getWithPropertyNamesInFirstLine()) {
					@SuppressWarnings("unchecked")
					List<?> lvObjectList = convertSimple2ObjectList((List<List<?>>) lvRows, pvRootClass);
					o = lvObjectList;
					if (pvRootClass != null && pvRootClass.isArray()) {
						Object objArr[] = (Object[]) Array.newInstance(pvRootClass.getComponentType(), lvObjectList.size());
						for (int i = 0; i < lvObjectList.size(); i++) {
							objArr[i] = lvObjectList.get(i);
						}
						o = objArr;
					}
				} else {
					o = lvRows;
				}
			}
		}
		return o;
	}
	
	
	
	private Object convertString2Object(Object pvSourceObject, Class<?> pvRootClass) {
		Object o = pvSourceObject;
		if (pvRootClass != null) {
			if (ReflectionHelper.isSimpleType(pvRootClass)) {
				Conversion lvConversion = new Simple2SimpleConversion(String.class, pvRootClass);
				try {
					getObjectUtil().getConverter().addConversion(lvConversion);
					o = getObjectUtil().makeComplex(pvSourceObject, pvRootClass);
				} finally {
					getObjectUtil().getConverter().removeConversion(lvConversion);
				}
			} else {
				o = getObjectUtil().makeComplex(pvSourceObject, pvRootClass);
			}
		}
		return o;
	}

	public List<?> convertSimple2ObjectList(List<List<?>> pvNamedList, Class<?> pvRootClass) {
		int lvRowSize = pvNamedList.size();
		List<Object> lvReturn = new ArrayList<Object>(lvRowSize - 1);
		List<?> lvColumnNames = pvNamedList.get(0);
		int lvColumnNamesSize = lvColumnNames.size();
		
		for (int i=1; i<lvRowSize; i++) {
			Map<Object,Object> lvNameValueMap = new HashMap<Object, Object>();
			List<?> lvColumnValues = pvNamedList.get(i);
			
			for (int j=0;j<lvColumnNamesSize; j++) {
				Object lvName = lvColumnNames.get(j);
				Object lvValue = lvColumnValues.get(j);
				if (lvValue.equals(getNullValue())) {
					lvValue = null;
				}
				lvNameValueMap.put(lvName, lvValue);
			}
			Class<?> lvRootClass = (pvRootClass != null && pvRootClass.isArray() ? pvRootClass.getComponentType() : pvRootClass);
			Object o = getObjectUtil().makeComplex(lvNameValueMap, lvRootClass);
			lvReturn.add(o);
		}
		return lvReturn;
	}
	
}
