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
package test.net.sf.sojo.example;

import net.sf.sojo.common.WalkerInterceptor;
import net.sf.sojo.core.Constants;

public class MyWalkerInterceptor implements WalkerInterceptor {
	
	public int counter = 0;

	@Override
	public void startWalk(Object startObject) { 
		System.out.println("Start the walking.");
	}
	
	@Override
	public void endWalk() { 
		System.out.println("\nWalking is done.");
	}


	@Override
	public boolean visitElement(Object key, int index, Object value, int type, String path, int numberOfRecursion) {
		if (type == Constants.TYPE_SIMPLE || type == Constants.TYPE_NULL) {
			System.out.print(path + ": " + value + ", ");	
		}
		return false;
	}

	@Override
	public void visitIterateableElement(Object value, int type, String path, int typeBeginOrEnd) {
		System.out.println(" " + path);
		if (typeBeginOrEnd == Constants.ITERATOR_BEGIN) {
			if (type == Constants.TYPE_ITERATEABLE) {
				System.out.print("[");
			} else if (type == Constants.TYPE_MAP) {
				System.out.print("(");
			}
		} else if (typeBeginOrEnd == Constants.ITERATOR_END) {
			if (type == Constants.TYPE_ITERATEABLE) {
				System.out.print("]");
			} else if (type == Constants.TYPE_MAP) {
				System.out.print(")");
			}
		}
		
	}


}
