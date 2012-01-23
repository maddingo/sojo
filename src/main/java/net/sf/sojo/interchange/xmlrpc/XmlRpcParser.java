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

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class XmlRpcParser {

	private boolean returnValueAsList = true;
	private boolean convertResult2XmlRpcExceptionAndThrow = false;
	private String methodName = null;
	

	public String getMethodName() { return methodName; }
	
	public void setReturnValueAsList(boolean pvReturnValueAsList) { returnValueAsList = pvReturnValueAsList; }
	public boolean getReturnValueAsList() { return returnValueAsList; }
	
	public void setConvertResult2XmlRpcExceptionAndThrow(boolean pvConvertResult2XmlRpcException) {
		convertResult2XmlRpcExceptionAndThrow = pvConvertResult2XmlRpcException; 
	}
	public boolean getConvertResult2XmlRpcExceptionAndThrow() {
		return convertResult2XmlRpcExceptionAndThrow; 
	}

	public Object parse(final String pvXmlRpcString) throws XmlRpcException {
		Object lvReturn = null;
		boolean lvIsFault = false;
		try {
			if (pvXmlRpcString != null) {
				SAXParserFactory lvFactory = SAXParserFactory.newInstance(); 
				SAXParser lvParser = lvFactory.newSAXParser();
				XMLReader lvReader = lvParser.getXMLReader();
				XmlRpcContentHandler lvContentHandler = new XmlRpcContentHandler();
				lvContentHandler.setReturnValueAsList(getReturnValueAsList());
				lvReader.setContentHandler(lvContentHandler);
				
				ByteArrayInputStream lvArrayInputStream = new ByteArrayInputStream(pvXmlRpcString.getBytes());
				lvReader.parse(new InputSource(lvArrayInputStream));
				lvReturn = lvContentHandler.getResults();
				methodName = lvContentHandler.getMethodName();
				lvIsFault = lvContentHandler.isFault();
			}
		} catch (SAXParseException e) {
			throw new XmlRpcException(e.getMessage(), e);
		} catch (Exception e) {
			throw new XmlRpcException("Exception by parse XML-RPC-String: " + pvXmlRpcString, e);
		}

		if (lvIsFault == true && getConvertResult2XmlRpcExceptionAndThrow()) {
			Map<?,?> lvMap = (Map<?,?>) lvReturn;
			Object lvFaultCode = lvMap.get("faultCode");
			Object lvMessage = lvMap.get("faultString");
			throw new XmlRpcException(lvFaultCode + ": " + lvMessage);
		}
		return lvReturn;

	}
}
