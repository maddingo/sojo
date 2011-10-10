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

import java.util.Date;
import java.util.Properties;

public class Car {

	private String name = null;
	private String description = null;
	private Date build = null;
	private Properties properties = null;

	public Car() { }
	public Car(String pvName) { 
		setName(pvName);
	}
	public Car(String pvName, String pvDescription) { 
		setName(pvName);
		setDescription(pvDescription);
	}
	
	public void setName(String pvName) { name = pvName; }
	public String getName() { return name; }
	
	public String getDescription() { return description; }
	public void setDescription(String pvDescription) { description = pvDescription; }

	public Date getBuild() { return build; }
	public void setBuild(Date pvBuild) { build = pvBuild; }
	
	public void setProperties(Properties pvProperties) { properties = pvProperties; }
	public Properties getProperties() { return properties; }

	public String toString() {
		if (getName() != null) {
			return getName();
		}
		return super.toString();
	}
}
