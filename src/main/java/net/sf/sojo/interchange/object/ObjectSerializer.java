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
package net.sf.sojo.interchange.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import net.sf.sojo.core.NonCriticalExceptionHandler;
import net.sf.sojo.interchange.AbstractSerializer;
import net.sf.sojo.interchange.SerializerException;


public class ObjectSerializer extends AbstractSerializer {
	
	private boolean convertBySerialization = true;
	
	
	public boolean getConvertBySerialization() { return convertBySerialization; }
	public void setConvertBySerialization(boolean pvConvertBySerialization) { convertBySerialization = pvConvertBySerialization; }

	@Override
	public Object serialize(Object pvRootObject, String[] pvExcludedProperties) {
		ObjectOutputStream lvObjectOutputStream = null;
		ByteArrayOutputStream lvArrayOutputStream = new ByteArrayOutputStream();
		try {
			Object lvSimple = pvRootObject;
			if (getConvertBySerialization()) {
				lvSimple = getObjectUtil().makeSimple(pvRootObject, pvExcludedProperties);
			}
			lvObjectOutputStream = new ObjectOutputStream(lvArrayOutputStream);
			lvObjectOutputStream.writeObject(lvSimple);
		} catch (Exception e) {
			throw new SerializerException("Exception by serialize object: " + pvRootObject + " - " + e, e);
		} finally {
			try {
				lvObjectOutputStream.close();
			} catch (IOException e) {
				if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
					NonCriticalExceptionHandler.handleException(ObjectSerializer.class, "Exception by close ObjectOutputStream: " + lvObjectOutputStream);
				}
			}
		}
		return lvArrayOutputStream.toByteArray();		
	}

	
	@Override
	public Object serialize(Object pvRootObject) {
		return serialize(pvRootObject, null); 
	}

	@Override
	public Object deserialize(Object pvSourceObject, Class<?> pvRootClass) {
		Object lvReturn = null;
		if (pvSourceObject instanceof byte[]) {
			ObjectInputStream lvObjectInputStream = null;
			try {
				byte lvBytes[] = (byte[]) pvSourceObject;
				ByteArrayInputStream lvArrayInputStream = new ByteArrayInputStream(lvBytes);
				lvObjectInputStream = new ObjectInputStream(lvArrayInputStream);
				lvReturn = lvObjectInputStream.readObject();
				if (getConvertBySerialization()) {
					lvReturn = getObjectUtil().makeComplex(lvReturn, pvRootClass);
				}
			} catch (Exception e) {
				throw new SerializerException("Exception by deserialize object: " + lvReturn + " - " + e, e);
			} finally {
				try {
					if (lvObjectInputStream != null) {
						lvObjectInputStream.close();
					}
				} catch (IOException e) {
					if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
						NonCriticalExceptionHandler.handleException(ObjectSerializer.class, e, "Exception by close ObjectInputStream: " + lvObjectInputStream);
					}
				}
			}
			
		} else {
			String lvClassName = (pvSourceObject == null ? null : pvSourceObject.getClass().getName());
			throw new SerializerException("The deserialize object must be an byte array and not: " + lvClassName);
		}
		
		return lvReturn;
	}

	
	public void serializeToFile(final Object pvRootObject, String pvPath) throws IOException {
		FileOutputStream lvFileOutputStream = null;
		try {
			lvFileOutputStream = new FileOutputStream(pvPath);
			serializeToOutputStream(pvRootObject, lvFileOutputStream);
		} finally {
			try {
				if (lvFileOutputStream != null) {
					lvFileOutputStream.close();
				}
			} catch (Exception e) {
				if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
					NonCriticalExceptionHandler.handleException(ObjectSerializer.class, e, "Exception by close FileOutputStream: " + lvFileOutputStream + " and path: " + pvPath);
				}
			}
		}
	}
	
	public void serializeToOutputStream(final Object pvRootObject, OutputStream pvOutputStream) throws IOException {
		byte lvBytes[] = (byte[]) serialize(pvRootObject);
		pvOutputStream.write(lvBytes);
	}

	public Object deserializeFromFile(String pvPath) throws IOException, ClassNotFoundException {
		FileInputStream lvFileInputStream = new FileInputStream(pvPath);
		Object lvReturn = deserializeFromInputStream(lvFileInputStream);
		return lvReturn;
	}

	public Object deserializeFromInputStream(final InputStream pvInputStream) throws IOException, ClassNotFoundException {
		Object lvReturn = null;
		int i = 1;		
		byte b[] = new byte[1024];
		while (i > 0) {
			i = pvInputStream.read(b);
		}
		lvReturn = deserialize(b);			
		return lvReturn;
	}


}
