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
package net.sf.sojo.core;

/**
 * This class contains fields, that are fill with values by the transformation and set they to the 
 * interceptor-implementations.
 * 
 * @author Linke
 *
 */
public class ConversionContext {

	/**
	 * Cancel (interrupt) the convert process.
	 */
	public boolean cancelConvert = false;
	/**
	 * Number of recursion.
	 */
	public int numberOfRecursion = 0;
	/**
	 * The choosing Conversion.
	 */
	public Conversion conversion = null;
	/**
	 * Value before or after conversion.
	 */
	public Object value = null;
	/**
	 * Can be a key-value from a Map or the property-name from a bean.
	 */
	public Object key = null; 

	
	public ConversionContext() { }

	public ConversionContext(int pvNumberOfRecursion, Conversion pvConversion, Object pvValue) { 
		numberOfRecursion = pvNumberOfRecursion;
		conversion = pvConversion;
		value = pvValue;
	}
	
	public ConversionContext(int pvNumberOfRecursion, Object pvKey, Object pvValue) { 
		numberOfRecursion = pvNumberOfRecursion;
		key = pvKey;
		value = pvValue;
	}

}
