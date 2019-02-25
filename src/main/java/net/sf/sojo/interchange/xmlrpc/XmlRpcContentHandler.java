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
package net.sf.sojo.interchange.xmlrpc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.sf.sojo.core.NonCriticalExceptionHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlRpcContentHandler extends DefaultHandler {
	
	private String value = null;
	private List<Object> params = new ArrayList<Object>();
	private Stack<Object> stack = new Stack<Object>();
	private boolean returnValueAsList = true;
	private boolean isFault = false;
	private String methodName = null;
	
	private static final Map<String, String> validTags = new HashMap<String,String>(); 
	{ 
		validTags.put("fault", "");
		validTags.put("data", "");
		validTags.put("param", "");
		validTags.put("params", "");
		validTags.put("value", "");
		validTags.put("member", "");
		validTags.put("methodCall", "");
		validTags.put("methodResponse", "");
	}
	
	@Override
	public void startDocument() throws SAXException { 
		stack.clear();
		isFault = false;
		methodName = null;
	}

	@Override
	public void endDocument() throws SAXException { 	}

	@Override
	public void startElement(String pvUri, String pvLocalName, String pvName, Attributes pvAttributes) throws SAXException {
		if ("params".equals(pvName)) {
			params.clear();
		}
		else if ("array".equals(pvName)) {
			stack.push(new ArrayList<Object>());
		}
		else if ("struct".equals(pvName)) {
			stack.push(new LinkedHashMap<Object, Object>());
		}
		else if ("fault".equals(pvName)) {
			isFault = true;
		}				
	}

	@SuppressWarnings("unchecked")
	private void addValue(String pvName, Object pvValue) {
		if (stack.size() > 0) {
			Object lvCurrentStackObj = stack.peek();
			if (lvCurrentStackObj instanceof List) {
				((List<Object>) lvCurrentStackObj).add(pvValue);
			} 
			else if (lvCurrentStackObj instanceof Map) {
				Map<Object, Object> lvMap = (Map<Object, Object>) lvCurrentStackObj;
				lvMap.put(pvName, pvValue);
				for (Map.Entry entry : lvMap.entrySet()) {
				    if (entry.getKey().equals(pvName)) {
				        stack.push(entry);
				        break;
                    }
                }
			}
			else if (lvCurrentStackObj instanceof Map.Entry) {
                ((Map.Entry)lvCurrentStackObj).setValue(pvValue);
                stack.pop();
            }
			else {
				if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
					NonCriticalExceptionHandler.handleException(XmlRpcContentHandler.class, "Not supported Object in Stack: " + lvCurrentStackObj);
				}
			}
		}
		else {
			params.add(pvValue);
		}
	}
	
	@Override
	public void endElement(String pvUri, String pvLocalName, String pvName) throws SAXException {

		Object currentValue = null;
		
		if ("string".equals(pvName) || ("value".equals(pvName) && value != null)) {
			value = value.replaceAll("&lt;", "<");
			value = value.replaceAll("&gt;", ">");
			currentValue = value;
			addValue("key", currentValue);
		}
		else if ("i4".equals(pvName) || "int".equals(pvName)) {
			currentValue = Integer.valueOf(value);
			addValue("key", currentValue);
		}
		else if ("boolean".equals(pvName)) {
			if ("1".equals(value)) {
				currentValue = Boolean.TRUE;
			} 
			else if ("0".equals(value)) {
				currentValue = Boolean.FALSE;
			}
			else {
				throw new XmlRpcException("The value: " + value + " is not a valid boolean value.");
			}
			addValue("key", currentValue);
		}
		else if ("double".equals(pvName)) {
			try {
				currentValue = Double.valueOf(value);
				addValue("key", currentValue);
			} catch (NumberFormatException e) {
				throw new XmlRpcException("The value: " + value + " is not a valid double value.");
			}
			
		}
		else if ("dateTime.iso8601".equals(pvName)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
			try {
				currentValue = sdf.parse(value);
				addValue("key", currentValue);
			} catch (ParseException e) {
				throw new XmlRpcException("The value: " + value + " is not a valid date value.");
			}
		}
		else if ("array".equals(pvName)) { 
			Object lvCurrentStackObj = stack.pop();
			addValue("key", lvCurrentStackObj);
		}
		else if ("struct".equals(pvName)) { 
			Object lvCurrentStackObj = stack.pop();
			addValue("key", lvCurrentStackObj);			
		}
		
		
		// ----------------- Apache XML-RPC Extensions -----------------
		else if ("ex:i1".equals(pvName)) {
			currentValue = Byte.valueOf(value);
			addValue("key", currentValue);
		}				
		else if ("ex:i2".equals(pvName)) {
			currentValue = Short.valueOf(value);
			addValue("key", currentValue);
		}
		else if ("ex:i8".equals(pvName)) {
			currentValue = Long.valueOf(value);
			addValue("key", currentValue);
		}						
		else if ("ex:float".equals(pvName)) {
			currentValue = Float.valueOf(value);
			addValue("key", currentValue);
		}				
		else if ("ex:bigdecimal".equals(pvName)) {
			currentValue = new BigDecimal(value);
			addValue("key", currentValue);
		}				
		else if ("ex:biginteger".equals(pvName)) {
			currentValue = new BigInteger(value);
			addValue("key", currentValue);
		}				
		else if ("ex:dateTime".equals(pvName)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS");
			try {
				currentValue = sdf.parse(value);
				Calendar lvCalendar = Calendar.getInstance();
				lvCalendar.setTime( (Date) currentValue);
				currentValue = lvCalendar; 
				addValue("key", currentValue);
			} catch (ParseException e) {
				throw new XmlRpcException("The value: " + value + " is not a valid date value.");
			}
		}
		else if ("ex:nil".equals(pvName)) {
			currentValue = null;
			addValue("key", currentValue);
		}				
		else if ("methodName".equals(pvName)) { 
			methodName = value;	
		}
		else if ("name".equals(pvName)) {
			addValue(value, null);
		}
		else if ("member".equals(pvName)) {
		    // make sure the stack does not contain a map entry, can be caused by improper serialization
            if (stack.peek() instanceof Map.Entry) {
                stack.pop();
            }
        }

		
		
		else if (validTags.containsKey(pvName) == false)  
		{
			throw new XmlRpcException("The Tag: " + pvName + " is not supported");
		}

		value = null;
	}
	
	@Override
	public void characters(char[] pvCh, int pvStart, int pvLength) throws SAXException {
		String lvOldValue = "";
		if (value != null) {
			lvOldValue = value;
		}
		value = lvOldValue + new String(pvCh, pvStart, pvLength);
	}
	
	public Object getResults() {
		if (getReturnValueAsList() == false && params.size() == 1) {
			return params.get(0);
		} else {
			return params;			
		}
	}
	public String getMethodName() { return methodName; }
	public boolean isFault() { return isFault; }
	
	public void setReturnValueAsList(boolean pvReturnValueAsList) { returnValueAsList = pvReturnValueAsList; }
	public boolean getReturnValueAsList() { return returnValueAsList; }

}
