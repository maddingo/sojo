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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import net.sf.sojo.common.WalkerInterceptor;
import net.sf.sojo.core.Constants;

public class XmlRpcWalkerInterceptor implements WalkerInterceptor {

	private StringBuffer xmlRpcString = new StringBuffer();
	private Object rootObjectArray = null;
	private Stack<Integer> stack = new Stack<Integer>();
	
	public String getXmlRpcString() {
		return xmlRpcString.toString();
	}
	
	@Override
	public void startWalk(Object pvStartObject) {
		xmlRpcString = new StringBuffer("<params>");
		if (pvStartObject != null && pvStartObject.getClass().isArray()) {
			rootObjectArray = pvStartObject;
		}
	}

	@Override
	public void endWalk() {
		xmlRpcString.append("</params>");
		stack.clear();
	}

	private void createParamTag (String pvPath, String pvParam) {
		if ((pvPath.length() == 0 
				|| (pvPath.startsWith("[") && pvPath.indexOf(".") < 0) 
				|| (pvPath.startsWith("[") && pvPath.indexOf(".") == (pvPath.length() - 1))
				)
			) {
			
			xmlRpcString.append(pvParam);
		}
	}
	
	private void createMemberTag (Object pvKey) {
		if (pvKey.getClass().equals(String.class)) {
			xmlRpcString.append("<member><name>").append(pvKey).append("</name>");
		} else {
			throw new XmlRpcException("Key must be from type String and not: " + pvKey.getClass());
		}		
	}
	
	@Override
	public boolean visitElement(Object pvKey, int pvIndex, Object pvValue, int pvType, String pvPath, int pvNumberOfRecursion) {
		if (pvType == Constants.TYPE_SIMPLE || pvType == Constants.TYPE_NULL) {
			createParamTag(pvPath, "<param>"); 
			if (pvKey != null) { createMemberTag(pvKey); }
			mapping2XmlRpcDataType(pvValue);
			if (pvKey != null) { xmlRpcString.append("</member>"); }
			createParamTag(pvPath, "</param>");
		}
		else if (pvKey != null) {
			createMemberTag(pvKey);
			stack.push(new Integer(pvType));
		}
		return false;
	}

	@Override
	public void visitIterateableElement(Object pvValue, int pvType, String pvPath, int pvTypeBeginOrEnd) {
		if (pvTypeBeginOrEnd == Constants.ITERATOR_BEGIN) {
			if (pvType == Constants.TYPE_ITERATEABLE && pvValue.equals(rootObjectArray) == false) {
					createParamTag(pvPath, "<param>");
					xmlRpcString.append("<value><array><data>");
			} else if (pvType == Constants.TYPE_MAP) {
				createParamTag(pvPath, "<param>");
				xmlRpcString.append("<value><struct>");
			}
		} 

		
		else if (pvTypeBeginOrEnd == Constants.ITERATOR_END) {
			if (pvType == Constants.TYPE_ITERATEABLE) {
				if (pvValue.equals(rootObjectArray) == false) {
					xmlRpcString.append("</data></array></value>");
				}
				if (stack.size() > 0) {
					Integer i = stack.peek();
					if (i.intValue() == Constants.TYPE_ITERATEABLE) {
						stack.pop();
						xmlRpcString.append("</member>");
					}
				}
				if (pvValue.equals(rootObjectArray) == false) {
					createParamTag(pvPath, "</param>");
				}
				
			}
			else if (pvType == Constants.TYPE_MAP) {
				xmlRpcString.append("</struct></value>");
				if (stack.size() > 0) {
					Integer i = stack.peek();
					if (i.intValue() == Constants.TYPE_MAP) {
						stack.pop();
						xmlRpcString.append("</member>");
					}
				} 
				createParamTag(pvPath, "</param>");
			}
		}
	}

	public void mapping2XmlRpcDataType(Object pvValue) {
		xmlRpcString.append("<value>");
		if (pvValue == null) {
			xmlRpcString.append("<ex:nil>null</ex:nil>");
		}
		else if (pvValue instanceof String || pvValue instanceof Character) {
			String s = pvValue.toString();
			s = s.replaceAll("<", "&lt;");
			s = s.replaceAll(">", "&gt;");
			xmlRpcString.append("<string>").append(s).append("</string>");
		}
		else if (pvValue instanceof Integer) {
			xmlRpcString.append("<i4>").append(pvValue).append("</i4>");
		}
		else if (pvValue instanceof Boolean) {
			Boolean lvBoolean = Boolean.valueOf(pvValue.toString());
			int b = (lvBoolean.equals(Boolean.TRUE) ? 1 : 0);
			xmlRpcString.append("<boolean>").append(b).append("</boolean>");
		}
		else if (pvValue instanceof Double) {
			xmlRpcString.append("<double>").append(pvValue).append("</double>");
		}
		else if (pvValue instanceof Date) {
			Date d = (Date) pvValue;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
			String s = sdf.format(d);
			xmlRpcString.append("<dateTime.iso8601>").append(s).append("</dateTime.iso8601>");
		}

		// ----------------- Apache XML-RPC Extensions -----------------
		else if (pvValue.getClass().equals(Byte.class)) {
			xmlRpcString.append("<ex:i1>").append(pvValue).append("</ex:i1>");
		}				
		else if (pvValue.getClass().equals(Short.class)) {
			xmlRpcString.append("<ex:i2>").append(pvValue).append("</ex:i2>");
		}
		else if (pvValue.getClass().equals(Long.class)) {
			xmlRpcString.append("<ex:i8>").append(pvValue).append("</ex:i8>");
		}				
		else if (pvValue.getClass().equals(Float.class)) {
			xmlRpcString.append("<ex:float>").append(pvValue).append("</ex:float>");
		}		
		else if (pvValue.getClass().equals(BigDecimal.class)) {
			xmlRpcString.append("<ex:bigdecimal>").append(pvValue).append("</ex:bigdecimal>");
		}		
		else if (pvValue.getClass().equals(BigInteger.class)) {
			xmlRpcString.append("<ex:biginteger>").append(pvValue).append("</ex:biginteger>");
		}		
		else if (Calendar.class.isAssignableFrom(pvValue.getClass())) {
			Calendar lvCalendar = (Calendar) pvValue;
			// @TODO is the time zone missing here?
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS");
			String s = sdf.format(lvCalendar.getTime());
			xmlRpcString.append("<ex:dateTime>").append(s).append("</ex:dateTime>");
		}		

		xmlRpcString.append("</value>");
	}
}
