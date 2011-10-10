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
package test.net.sf.sojo.model;

import java.math.BigDecimal;

public class TestExceptionWithBadConstructor extends Exception {

	private static final long serialVersionUID = 3228299026937206814L;

	private BigDecimal bigDecimal = null;
	
	public TestExceptionWithBadConstructor(BigDecimal pvBigDecimal) {
		this.bigDecimal = pvBigDecimal;
	}
	
	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}
	
}
