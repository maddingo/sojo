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
 * This is the base interface for all kind of conversions. 
 * 
 * @author Linke
 *
 */
public interface Conversion {

	/**
	 * The <code>ConversionHandler</code> iterate over all register Conversion and call the
	 * method <code>isAssignabelFrom</code>. If the return value is <code>true</code>, than use the
	 * Handler this conversion.
	 * 
	 * <b>Hint: </b> all implementation must catch the case where pvObject is null!
	 * 
	 * @param pvObject
	 * @return <code>TRUE</code> if the pvObject is assignable to the <code>Conversion</code> implementation.
	 */
	public boolean isAssignableFrom(final Object pvObject);
	
	/**
	 * The <code>ConversionHandler</code> use method, to find the right conversion. This method is different
	 * to <code>isAssignabelFrom</code>, that is only invoke, if the target type is known.
	 * 
	 * @param pvToType The type (target type) to wanted the value convert.
	 * @return <code>TRUE</code> if the pvObject is assignable to the <code>Conversion</code> implementation.
	 */
	public boolean isAssignableTo(final Class<?> pvToType);

	/**
	 * On every <code>Conversion</code> can you register <code>ConverterInterceptor</code> to get information
	 * about the current doing from the <code>Converter</code> 
	 * @return The <code>ConverterInterceptorHandler</code>
	 */
	public ConverterInterceptorHandler getConverterInterceptorHandler();
}
