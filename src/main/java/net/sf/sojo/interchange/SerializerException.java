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
package net.sf.sojo.interchange;

/**
 * By problems thrown this runtime exception.
 * 
 * @author linke
 *
 */
public class SerializerException extends RuntimeException {

	private static final long serialVersionUID = 5356948263806374838L;
	
	public SerializerException(String pvMessage) {
		super(pvMessage);
	}
	
	public SerializerException(String pvMessage, Throwable pvThrowable) {
		super(pvMessage, pvThrowable);
	}


}
