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
package net.sf.sojo.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a helper class, to find cycles in Collections or Maps. <br>
 * This implementation don't find all possibilities of cycles!
 * 
 * @author linke
 *
 */
public class CycleDetector {
	
	public static final int DEFAULT_MAX_COUNTER = 100;
	
	private int counter = 0;
	private int maxCounter = DEFAULT_MAX_COUNTER;
	
	public CycleDetector() {
		counter = 0;
	}
	
	public int getCounter() { return counter; }
	public int getMaxCounter() { return maxCounter; }
	public void setMaxCounter(int pvMaxCounter) { maxCounter = pvMaxCounter; }

	public boolean cycleDetection (Object pvSearchObject) {
		counter = 0;
		return cycleDetection(pvSearchObject, pvSearchObject);
	}
		
	private boolean cycleDetectionCollection (Object pvRootObject, Collection<?> pvCollection) {
		Iterator<?> it = pvCollection.iterator();
		return cycleDetectionIntern(pvRootObject, it);
	}
	
	private boolean cycleDetectionMap (Object pvRootObject, Map<?,?> pvMap) {
		boolean lvReturn = cycleDetectionIntern(pvRootObject, pvMap.values().iterator());
		if (lvReturn == false && counter < maxCounter) {
			lvReturn = cycleDetectionIntern(pvRootObject, pvMap.keySet().iterator());
		}
		return lvReturn;
	}

	
	private boolean cycleDetection (Object pvRootObject, Object pvSearchObject) {
		boolean lvResult = false;
		if (pvSearchObject instanceof Collection) {
			lvResult = cycleDetectionCollection(pvRootObject, (Collection<?>) pvSearchObject);
		}
		else if (pvSearchObject instanceof Map) {
			lvResult = cycleDetectionMap(pvRootObject, (Map<?, ?>) pvSearchObject);
		}
		return lvResult;
	}

	private boolean cycleDetectionIntern (Object pvRootObject, Iterator<?> pvIterator) {
		counter++; 
		boolean b = false;
		if (counter < maxCounter) {
			while (pvIterator.hasNext()) {
				Object o = pvIterator.next();
				if (pvRootObject == o) {
					b = true;
					break;
				}
				if (b == false) {
					b = cycleDetection(pvRootObject, o);
				}
			}
		}
		return b;
	}

}
