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
package test.net.sf.sojo;

import net.sf.sojo.core.ConverterInterceptor;

public class TestConverterInterceptor implements ConverterInterceptor {

	public int afterConvert = 0;
	public int beforeConvert = 0;
	public int onError = 0;

	@Override
	public Object afterConvert(Object pvResult, final Class<?> pvToType) {
		afterConvert++;
		return pvResult;
	}

	@Override
	public Object beforeConvert(Object pvConvertObject, final Class<?> pvToType) {
		beforeConvert++;
		return pvConvertObject;
	}

	@Override
	public void onError(Exception pvException) {
		onError++;
	}

}
