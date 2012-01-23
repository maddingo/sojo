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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.sojo.util.Util;

/**
 * This class describes, which properties are to be ignored when converting a class to a new representation. 
 * The properties, which are added to this filter are transient by
 * call a serialize method by the <code>net.sf.sojo.interchange.Serializer</code>. 
 * 
 * <b>Hint:</b> a property of a JavaBean is by a getter/setter method. This corresponds to a key/value pair in a map.
 *  
 * @author linke
 *
 */
public class ClassPropertyFilter {

	
	private Class<?> filterClass = null;
	private List<String> propertyList = new ArrayList<String>();
	private boolean support4AddClassProperty = false;
	
	public ClassPropertyFilter() { }
	
	public ClassPropertyFilter(Class<?> pvClass) {
		setFilterClass(pvClass);
	}
	
	
	public ClassPropertyFilter(Class<?> pvClass, String pvProperties[]) {
		setFilterClass(pvClass);
		addProperties(pvProperties);
	}


	public Class<?> getFilterClass() { 
		return filterClass; 
	}
	
	private void setFilterClass(Class<?> pvClass) { 
		this.filterClass = pvClass; 
	}
	
	public void setSupport4AddClassProperty(boolean pvSupport4AddClassProperty) { 
		support4AddClassProperty = pvSupport4AddClassProperty; 
	}
	
	public boolean getSupport4AddClassProperty() { 
		return support4AddClassProperty; 
	}
	
	public ClassPropertyFilter addProperties(String pvProperties[]) {
		if (pvProperties != null) {
			for (String prop : pvProperties) {
				addProperty(prop);
			}
		}
		return this;
	}

	public ClassPropertyFilter addProperty(String pvProperty) {
		if (!Util.getKeyWordClass().equals(pvProperty) || getSupport4AddClassProperty()) {
			propertyList.add(pvProperty);
		}
		return this;
	}
	
	public ClassPropertyFilter removeProperty(String pvProperty) {
		propertyList.remove(pvProperty);
		return this;
	}
	
	public ClassPropertyFilter removeProperties(String pvProperty[]) {
		if (pvProperty != null) {
			for (String prop : pvProperty) {
				removeProperty(prop);
			}
		}
		return this;
	}

	public boolean isKnownProperty(String pvProperty) {
	  if (pvProperty != null) {
	    
		for (String prop : propertyList) {
			
  			if (prop.equals(pvProperty)) {
  				return true;
  			} else if (Pattern.matches(prop, pvProperty)) {
				return true;
  			}
  		}
	  }
	  return false;		
	}
	
	public int getPropertySize() {
		return propertyList.size();
	}
	
	public String[] getAllProperties() {
		return propertyList.toArray(new String [propertyList.size()]);
	}
}
