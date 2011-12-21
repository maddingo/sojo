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

import java.util.ArrayList;
import java.util.List;

/**
 * Handle all register <code>ConverterInterceptor</code>.
 * 
 * @author Linke
 *
 */
public class ConverterInterceptorHandler {

	private List<ConverterInterceptor> interceptors = new ArrayList<ConverterInterceptor>();
	
	public void addConverterInterceptor(ConverterInterceptor pvConverterInterceptor) {
		if (pvConverterInterceptor == null) {
			throw new IllegalArgumentException("The ConverterInterceptor must be different from null");
		}
		// TODO should the interceptors be a Set?
		interceptors.remove(pvConverterInterceptor);
		interceptors.add(pvConverterInterceptor);
	}
	
	public ConverterInterceptor getConverterInterceptorByPosition(int pvPosition) {
		return (ConverterInterceptor) interceptors.get(pvPosition);
	}
	
	public void removeConverterInterceptor(ConverterInterceptor pvSearchInterceptor) {
		interceptors.remove(pvSearchInterceptor);
	}

	
	public int size() { return interceptors.size(); }
	public void clear() { interceptors.clear(); }

	
	public Object fireBeforeConvert(final Object pvConvertObject, final Class<?> pvToType) {
		Object lvReturn = pvConvertObject;
		for (ConverterInterceptor lvInterceptor : interceptors) {
			lvReturn = lvInterceptor.beforeConvert(pvConvertObject, pvToType);
		}
		return lvReturn;
	}
	
	public Object fireAfterConvert(final Object pvResult, final Class<?> pvToType) {
		Object lvReturn = pvResult;
    for (ConverterInterceptor lvInterceptor : interceptors) {
			lvReturn = lvInterceptor.afterConvert(pvResult, pvToType);
		}		
		return lvReturn;
	}
	
	public void fireOnError(final Exception pvException) {
	  for (ConverterInterceptor lvInterceptor : interceptors) {
			lvInterceptor.onError(pvException);
		}
	}

	
	public void fireBeforeConvertRecursion(final ConversionContext pvContext) {
	  for (ConverterInterceptor o : interceptors) {
			if (o instanceof ConverterInterceptorRecursive) {
				ConverterInterceptorRecursive lvInterceptor = (ConverterInterceptorRecursive) o;
				lvInterceptor.beforeConvertRecursion(pvContext);
			}
		}		
	}
	
	public void fireAfterConvertRecursion(final ConversionContext pvContext) {
    for (ConverterInterceptor o : interceptors) {
			if (o instanceof ConverterInterceptorRecursive) {
				ConverterInterceptorRecursive lvInterceptor = (ConverterInterceptorRecursive) o;
				lvInterceptor.afterConvertRecursion(pvContext);
			}
		}		
	}
}
