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
 * This interface is a extension from the <code>ConverterInterceptor</code>. The method from this
 * interface are calling, if a conversion is recursive. This is the case by maps, collctions, arrays,
 * object graphs and so on.
 * 
 * @author Linke
 *
 */
public interface ConverterInterceptorRecursive extends ConverterInterceptor {

	public void beforeConvertRecursion(ConversionContext pvContext);
	
	public void afterConvertRecursion(ConversionContext pvContext);
}
