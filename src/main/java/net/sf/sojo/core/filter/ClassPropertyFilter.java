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
 * This class descripe, which properties are to ignore by convert class to a new representation. 
 * The properties, which are added to this filter are transient by
 * call a serialize method by the <code>net.sf.sojo.interchange.Serializer</code>. 
 * 
 * <b>Hint:</b> a property is by a JavaBean the name of the getter/setter method. By a Map is the property equivalent with the key.
 *  
 * @author linke
 *
 */
public class ClassPropertyFilter {

	
	private Class filterClass = null;
	private List propertyList = new ArrayList();
	private boolean support4AddClassProperty = false;
	
	public ClassPropertyFilter() { }
	
	public ClassPropertyFilter(Class pvClass) {
		setFilterClass(pvClass);
	}
	
	
	public ClassPropertyFilter(Class pvClass, String pvProperties[]) {
		setFilterClass(pvClass);
		addProperties(pvProperties);
	}


	public Class getFilterClass() { return filterClass; }
	private void setFilterClass(Class pvClass) { this.filterClass = pvClass; }
	
	public void setSupport4AddClassProperty(boolean pvSupport4AddClassProperty) { support4AddClassProperty = pvSupport4AddClassProperty; }
	public boolean getSupport4AddClassProperty() { return support4AddClassProperty; }
	
	
	public ClassPropertyFilter addProperties(String pvProperties[]) {
		if (pvProperties != null) {
			for (int i = 0; i < pvProperties.length; i++) {
				addProperty(pvProperties[i]);
			}
		}
		return this;
	}

	public ClassPropertyFilter addProperty(String pvProperty) {
		if (Util.getKeyWordClass().equals(pvProperty) == false || getSupport4AddClassProperty()) {
			propertyList.add(pvProperty);
		}
		return this;
	}
	
	public ClassPropertyFilter removeProperty(String pvProperty) {
		for (int i=0; i<propertyList.size(); i++) {
			if (propertyList.get(i).equals(pvProperty)) {
				propertyList.remove(i);
			}
		}
		return this;
	}
	
	public ClassPropertyFilter removeProperties(String pvProperty[]) {
		if (pvProperty != null) {
			for (int i = 0; i < pvProperty.length; i++) {
				removeProperty(pvProperty[i]);
			}
		}
		return this;
	}

	public boolean isKnownProperty(String pvProperty) {
	  if (pvProperty != null) {
	    
  		for (int i=0; i<propertyList.size(); i++) {
  			if (propertyList.get(i).equals(pvProperty)) {
  				return true;
  			} 
  			else {
  				boolean b = Pattern.matches((String) propertyList.get(i), pvProperty);
  				if (b == true) {
  					return true;
  				}
  			}
  		}
	  }
		return false;		
	}
	
	public int getPropertySize() {
		return propertyList.size();
	}
	
	public String[] getAllProperties() {
		return (String[]) propertyList.toArray(new String [propertyList.size()]);
	}

}
