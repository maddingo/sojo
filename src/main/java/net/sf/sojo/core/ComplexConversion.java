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
 * This is the basis for complex conversions. A complex can be a transformation from a JavaBean to a Map.
 *  
 * @author Linke
 *
 */
public abstract class ComplexConversion extends ConversionIterator {
	
	/**
	 * 
	 * @param pvObject Object, that is to convert.
	 * @param pvToType Type/Class where is to convert.
	 * @param pvConverterExtension Get UniqueId for a Object.
	 * @return The result of converting.
	 */
	public abstract Object convert(final Object pvObject, final Class<?> pvToType, final IConverterExtension pvConverterExtension);
	
}
