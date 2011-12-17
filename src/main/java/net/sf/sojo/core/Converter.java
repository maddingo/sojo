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

import net.sf.sojo.core.filter.ClassPropertyFilterHandler;
import net.sf.sojo.core.reflect.ReflectionHelper;

/**
 * This is the main class for converting objects from one structure to a other.
 * The kind of converting is defined by add implementation from the <code>Conversion</code> interface.
 * A simple example:
 * <pre>
 Converter converter = new Converter();
 converter.addConversion(new Simple2SimpleConversion(String.class, Integer.class));
 converter.addConversion(new NullConversion("Replace_Null_Value_String"));
 
 // result is equals new Integer("4711")
 Object result = converter.convert("4711");
  
 // result is "Replace_Null_Value_String"
 result = converter.convert(null);
 
 * </pre>
 * 
 *   
 * @author linke
 *
 */
public final class Converter implements IConverter {

	private ConverterInterceptorHandler interceptorHandler = new ConverterInterceptorHandler();
	private ConversionHandler conversionHandler = new ConversionHandler();
	private UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();
	private boolean throwExceptionIfNoConversionFind = false;
	private int numberOfRecursion = -1;
	protected ClassPropertyFilterHandler classPropertyFilterHandler = null;

	public Converter() { }

	
	/**
	 * If is <code>true</code>, then the Converter is thrown a Exception. 
	 * If is <code>false</code>, then is the Exception handle
	 * from the logger. Default is <code>false</code>. 
	 * @param pvThrowExceptionIfNoConversionFind The value for decision for thrown Exception or not.
	 */
	public void setThrowExceptionIfNoConversionFind(boolean pvThrowExceptionIfNoConversionFind) { throwExceptionIfNoConversionFind = pvThrowExceptionIfNoConversionFind; }
	public boolean getThrowExceptionIfNoConversionFind() { return throwExceptionIfNoConversionFind; }
	
	/**
	 * Set your own ConversionHandler or user the default ConversionHandler.
	 * @param pvConversionHandler The ConversionHandler.
	 */
	public void setConversionHandler(ConversionHandler pvConversionHandler) { conversionHandler = pvConversionHandler; }
	public ConversionHandler getConversionHandler() { return conversionHandler; }

	public Conversion replaceConversion(final Conversion pvConversion) {
		return conversionHandler.replaceConversion(pvConversion);
	}

	public Conversion replaceAllConversion(final Conversion pvConversion) {
		return conversionHandler.replaceAllConversion(pvConversion);
	}

	/**
	 * Add Conversion, to decide for the Converter target type. 
	 * The add method is delegate to the <code>ConversionHandler</code>.
	 * 
	 * @param pvConversion Implementation from <code>Conversion</code>.
	 */
	public void addConversion(Conversion pvConversion) { conversionHandler.addConversion(pvConversion); }
	public void clearConversion() { conversionHandler.clear(); }
	
	public Conversion removeConversion(final Conversion pvConversion) { return conversionHandler.removeConversion(pvConversion); }
	
	
	public void addConverterInterceptor(ConverterInterceptor pvConverterInterceptor) {
		interceptorHandler.addConverterInterceptor(pvConverterInterceptor);
	}
	public void removeConverterInterceptor(ConverterInterceptor pvSearchInterceptor) {
		interceptorHandler.removeConverterInterceptor(pvSearchInterceptor);
	}
	public ConverterInterceptor getConverterInterceptorByPosition(int pvPosition) {
		return interceptorHandler.getConverterInterceptorByPosition(pvPosition);
	}
	public int getConverterInterceptorSize() {
		return interceptorHandler.size();
	}
	public void clearConverterInterceptorSize() {
		interceptorHandler.clear();
	}
	/**
	 * 
	 * @return The number of recursion for the last convert event.
	 */
	public int getNumberOfRecursion() {
		return numberOfRecursion;
	}
	
	/**
	 * Handle filter fro class properties.
	 * It means, that the handler is delegate to the register <code>ComplexConversion</code>. 
	 * 
	 * @param pvClassPropertyFilterHandler
	 */
	public void setClassPropertyFilterHandler(ClassPropertyFilterHandler pvClassPropertyFilterHandler) { 
	  classPropertyFilterHandler = pvClassPropertyFilterHandler; 
	}
	
	public ClassPropertyFilterHandler getClassPropertyFilterHandler() { 
	  return classPropertyFilterHandler; 
	}
	

	/**
	 * Convert one object (by object graph, the root object) to another object.
	 * The target object is dependent on the register Conversion.
	 * The only thrown Exception is <code>net.sf.sojo.ConverterException</code>.
	 * 
	 * @param pvObject the (root) source object.
	 * @return The (converted) target object.
	 * 
	 */
	@Override
	public Object convert(final Object pvObject) {
		return convert(pvObject, null);
	}
	
	/**
	 * @see net.sf.sojo.core.Converter#convert(Object)
	 * 
	 * @param pvObject the (root) source object.
	 * @param pvToType Type from target object, if for this type no Conversion ist register.
	 * @return The (converted) target object. 
	 */
	@Override
	public Object convert(final Object pvObject, final Class<?> pvToType) {
		Object lvResult = pvObject;
		lvResult = interceptorHandler.fireBeforeConvert(lvResult, pvToType);
		try {
			lvResult = convertInternal(lvResult, pvToType);
		} catch (Exception e) {
			interceptorHandler.fireOnError(e);
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			} else {
				throw new ConversionException(e);
			}
		} finally {
			numberOfRecursion = -1;
			uniqueIdGenerator.clear();
			lvResult = interceptorHandler.fireAfterConvert(lvResult, pvToType);
		}
		
		return lvResult;
	}
	
	protected Object convertInternal(final Object pvObject, final Class<?> pvToType) {
		Object lvReturn = pvObject;
		
		Conversion lvConversion = conversionHandler.getConversion(lvReturn, pvToType);
		numberOfRecursion++;
		ConversionContext lvContext = new ConversionContext(numberOfRecursion, lvConversion, lvReturn);
		interceptorHandler.fireBeforeConvertRecursion(lvContext);
		
		try {
	
			if (lvConversion != null && lvContext.cancelConvert == false) {
				
				lvReturn = lvConversion.getConverterInterceptorHandler().fireBeforeConvert(lvReturn, pvToType);
					if (lvConversion instanceof SimpleConversion) {
						SimpleConversion lvSimpleConversion = (SimpleConversion) lvConversion;
						lvReturn = lvSimpleConversion.convert(lvReturn, pvToType);
					}					
					else if (lvConversion instanceof IterableConversion) {
						IterableConversion lvIterateableConversion = (IterableConversion) lvConversion;
						lvIterateableConversion.setClassPropertyFilterHandler(classPropertyFilterHandler);
						lvReturn = lvIterateableConversion.convert(lvReturn, pvToType, new InternalRecursiveConverterExtension(this));
					}
					else if (lvConversion instanceof ComplexConversion) {
						ComplexConversion lvComplexConversion = (ComplexConversion) lvConversion;
						lvComplexConversion.setClassPropertyFilterHandler(classPropertyFilterHandler);
						lvReturn = lvComplexConversion.convert(lvReturn, pvToType, new InternalRecursiveConverterExtension(this));
					}
					else {
						handleException("Not supported conversion type: " + lvConversion + " and object: " + lvReturn);
					}				
				lvReturn = lvConversion.getConverterInterceptorHandler().fireAfterConvert(lvReturn, pvToType);

				
			} else {
				handleException("No conversion find for object: " + lvReturn + " from type: " + (lvReturn == null ? "NoClassAvail" : lvReturn.getClass().getName())); 
			}
		} finally {
			lvContext.value = lvReturn;
			interceptorHandler.fireAfterConvertRecursion(lvContext);
		}
				
		return lvReturn;
	}
	

	private void handleException(String pvMessage) {
		if (getThrowExceptionIfNoConversionFind() == true) {
			throw new ConversionException(pvMessage);
		} else {
			if (NonCriticalExceptionHandler.isNonCriticalExceptionHandlerEnabled()) {
				NonCriticalExceptionHandler.handleException(Converter.class, pvMessage);
			}
		}					
	}
	
	
	
	
	private static class InternalRecursiveConverterExtension implements IConverterExtension {

		protected Converter converter = null;

		public InternalRecursiveConverterExtension(Converter pvConverter) {
			this.converter = pvConverter;
		}

		@Override
		public Object convert(Object pvObject) {
			return convert(pvObject, null);
		}
		
		@Override
		public Object convert(final Object pvObject, final Class<?> pvToType) {
			Object lvReturn = null;
			if (ReflectionHelper.isSimpleType(pvObject)) {
				lvReturn = converter.convertInternal(pvObject, pvToType);
			} else {
				if (converter.uniqueIdGenerator.isKnownObject(pvObject)) {
					String lvId = converter.uniqueIdGenerator.getUniqueId(pvObject);
					lvReturn = UniqueIdGenerator.getUniqueIdStringByNumber(lvId);
				} else {
					lvReturn = converter.convertInternal(pvObject, pvToType);
				}
			}
			return lvReturn;
		}		

		@Override
		public final String getUniqueId(Object pvObject) {
			return converter.uniqueIdGenerator.getUniqueId(pvObject);
		}

		@Override
		public void addObject(String pvUniqueId, Object pvObject) {
			converter.uniqueIdGenerator.addObject(pvUniqueId, pvObject);
		}

		@Override
		public Object getObjectByUniqueId(String pvUniqueId) {
			return converter.uniqueIdGenerator.getObjectByUniqueId(pvUniqueId);
		}
	}
}
