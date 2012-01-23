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
package net.sf.sojo.core.filter;

import java.util.HashMap;
import java.util.Map;


/**
 * Hanlde <code>ClassPropertyFilter</code>. This means classic operations how: add, remove, size, ...
 * 
 * @author linke
 *
 */
public class ClassPropertyFilterHandlerImpl implements ClassPropertyFilterHandler {
	
	private Map<Class<?>, ClassPropertyFilter> classPropertyFilterMap = new HashMap<Class<?>, ClassPropertyFilter>();
	private boolean withAssignableFilterClasses = true;
	
	public ClassPropertyFilterHandlerImpl() { }
	public ClassPropertyFilterHandlerImpl(ClassPropertyFilter pvFilter) { 
		addClassPropertyFilter(pvFilter);
	}
	
	public ClassPropertyFilterHandlerImpl(Class<?> pvClassPropertyFilterClasses[]) {
		addClassPropertyFilterByFilterClasses(pvClassPropertyFilterClasses);
	}

	public void setWithAssignableFilterClasses(boolean pvWithAssignableFilterClasses) {
		withAssignableFilterClasses = pvWithAssignableFilterClasses;
	}
	
	public boolean getWithAssignableFilterClasses() {
		return withAssignableFilterClasses;
	}
	
	public void addClassPropertyFilterByFilterClasses(Class<?> pvClassPropertyFilterClasses[]) {
		if (pvClassPropertyFilterClasses != null) {
			for (Class<?> cls : pvClassPropertyFilterClasses) {
				ClassPropertyFilter lvFilter = ClassPropertyFilterHelper.createClassPropertyFilterByClass(cls);
				addClassPropertyFilter(lvFilter);
			}
		}
	}
	
	public void addClassPropertyFilter(ClassPropertyFilter pvClassPropertyFilter) {
		classPropertyFilterMap.put(pvClassPropertyFilter.getFilterClass(), pvClassPropertyFilter);
	}

	public void removeClassPropertyFilterByClassName(Class<?> pvFilterClass) {
		classPropertyFilterMap.remove(pvFilterClass);
	}

	@Override
	public ClassPropertyFilter getClassPropertyFilterByClass(Class<?> pvFilterClass) {
		if (withAssignableFilterClasses) {
			for (Map.Entry<Class<?>, ClassPropertyFilter> lvEntry : classPropertyFilterMap.entrySet()) {
				Class<?> lvClass = lvEntry.getKey();
				if (lvClass.isAssignableFrom(pvFilterClass)) {
					return lvEntry.getValue();
				}
			}
			return null;
		} else {
			return classPropertyFilterMap.get(pvFilterClass);
		}
	}
	
	public int getClassPropertyFilterSize() {
		return classPropertyFilterMap.size();
	}
}
