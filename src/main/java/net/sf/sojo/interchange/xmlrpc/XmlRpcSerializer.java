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

import net.sf.sojo.interchange.AbstractSerializer;

public class XmlRpcSerializer extends AbstractSerializer {

	private XmlRpcParser xmlRpcParser = new XmlRpcParser();
	private boolean returnValueAsList = false;
	private boolean convertResult2XmlRpcExceptionAndThrow = false;
	private String methodName = null;
	XmlRpcWalkerInterceptor xmlRpcWalker = new XmlRpcWalkerInterceptor();

	
	public XmlRpcSerializer() {
		setWithSimpleKeyMapper(false);
		walker.addInterceptor(xmlRpcWalker);
	}

	public XmlRpcSerializer(boolean pvReturnValueAsList) {
		this();
		setReturnValueAsList(pvReturnValueAsList);
	}

	
	public String getMethodName() { return methodName; }
	
	public void setReturnValueAsList(boolean pvReturnValueAsList) { returnValueAsList = pvReturnValueAsList; }
	public boolean getReturnValueAsList() { return returnValueAsList; }


	
	public void setConvertResult2XmlRpcExceptionAndThrow(boolean pvConvertResult2XmlRpcException) {
		convertResult2XmlRpcExceptionAndThrow = pvConvertResult2XmlRpcException; 
	}
	public boolean getConvertResult2XmlRpcExceptionAndThrow() {
		return convertResult2XmlRpcExceptionAndThrow; 
	}


	
	@Override
	public Object serialize (Object pvRootObject) {
		walker.walk(pvRootObject);
		String lvReturn = xmlRpcWalker.getXmlRpcString();
		return lvReturn;
	}
	
	public String serializeXmlRpcRequest (String pvMethodName, Object pvArgs) {
		if (pvMethodName == null || pvMethodName.length() == 0) {
			throw new XmlRpcException("Missing method-name: " + getMethodName());
		}
		Object o = serialize(pvArgs);
		StringBuffer lvReturn = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><methodCall><methodName>");
		lvReturn.append(pvMethodName).append("</methodName>" + o + "</methodCall>");
		return lvReturn.toString();
	}
	
	public String serializeXmlRpcResponse (Object pvReturnValue) {
		Object o = serialize(pvReturnValue);
		StringBuffer lvReturn = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><methodResponse>");
		lvReturn.append(o).append("</methodResponse>");
		return lvReturn.toString();		
	}

	@Override
	public Object deserialize(Object pvSourceObject, Class<?> pvRootClass) {
		Object lvReturn = null;
		if (pvSourceObject != null) {
			xmlRpcParser.setReturnValueAsList(getReturnValueAsList());
			xmlRpcParser.setConvertResult2XmlRpcExceptionAndThrow(getConvertResult2XmlRpcExceptionAndThrow());
			lvReturn = xmlRpcParser.parse(pvSourceObject.toString());
			methodName = xmlRpcParser.getMethodName();
		}
		lvReturn = getObjectUtil().makeComplex(lvReturn, pvRootClass);
		return lvReturn;
	}
}
