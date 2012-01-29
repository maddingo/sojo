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
package net.sf.sojo.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * To add all iterable classes, how array, list, set or map and provide
 * functions on this classes (for example: sort).
 * 
 * @author linke
 *
 */
public class IterableUtil {
	
	public static <T> List<T> sort(List<T> pvList) {
		Collections.sort(pvList, new GenericComparator());
		return pvList;
	}
	
	public static Object[] sort(Object pvObjArray[]) {
		Arrays.sort(pvObjArray, new GenericComparator());
		return pvObjArray;
	}
	
	public static <T> Set<T> sort(Set<T> pvSet) {
		TreeSet<T> lvTreeSet = new TreeSet<T>(new GenericComparator(true));
		lvTreeSet.addAll(pvSet);
		return lvTreeSet;
	}

	public static <K,V> Map<K, V> sort(Map<K,V> pvMap) {
		TreeMap<K, V> lvTreeMap = new TreeMap<K, V>(new GenericComparator());
		lvTreeMap.putAll(pvMap);
		return lvTreeMap;
	}
}
