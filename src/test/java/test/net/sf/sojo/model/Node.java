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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	
	private String name = null;
	private Node parent = null;
	private List childs = new ArrayList();
	private Map namedChilds = new HashMap();
	
	public Node() { }
	public Node(String pvName) { 
		setName(pvName);
	}

	public final void setName(String pvName) { name = pvName; }
	public String getName() { return name; }
	
	public Node getParent() { return parent; }
	public void setParent(Node pvParent) { parent = pvParent; }
	
	public List getChilds() { return childs; }
	public void setChilds(List pvChilds) { childs = pvChilds; }
	
	public Map getNamedChilds() { return namedChilds; }
	public void setNamedChilds(Map pvNamedChilds) { namedChilds = pvNamedChilds; }
	
	public String toString() {
		return getName() + " - " + super.toString();
	}
}
