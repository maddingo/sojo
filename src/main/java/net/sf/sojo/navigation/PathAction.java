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
package net.sf.sojo.navigation;

public class PathAction {
	
	public static final int ACTION_TYPE_SIMPLE = 0;
	public static final int ACTION_TYPE_INDEX = 1;
	public static final int ACTION_TYPE_KEY = 2;
	
	private int type = ACTION_TYPE_SIMPLE;
	private String path = null;
	private String property = null;
	private String key = null;
	private int index = -1;

	
	public PathAction() {}
	public PathAction(int pvType) {
		setType(pvType);
	}
	
	public String getPath() { return path; }
	public void setPath(String pvPath) { path = pvPath; }
	
	public String getProperty() { return property; }
	public void setProperty(String pvProperty) { property = pvProperty; }
	
	public String getKey() { return key; }
	public void setKey(String pvKey) { key = pvKey; }
	
	public int getIndex() { return index; }
	public void setIndex(int pvIndex) { index = pvIndex; }
	
	public int getType() { return type; }
	public void setType(int pvType) { type = pvType; }
	
	@Override
	public String toString() {
		return "path: " + getPath() + " - property: " + getProperty() 
				+ " - index/key: " + getIndex() + "/" + getKey() 
				+ " - type: " + getType() + " -- " +  super.toString();
	}
	
	
	

}
