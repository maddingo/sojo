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
	
	private String name;
	private Node parent;
	private List<Object> children = new ArrayList<Object>();
	private Map<Object, Object> namedChildren = new HashMap<Object, Object>();
	
	public Node() { }
	
	public Node(String pvName) { 
	  this.name = pvName;
	}

	public final void setName(String pvName) { 
	  name = pvName; 
	}
	
	public String getName() { 
	  return name; 
	}
	
	public Node getParent() { 
	  return parent; 
	}
	
	public void setParent(Node pvParent) { parent = pvParent; }
	
	public List<Object> getChildren() {
	  return children; 
	}
	
	public void setChildren(List<Object> children) {
	  this.children = children;
	}
	
	public Map<Object, Object> getNamedChildren() {
	  return namedChildren; 
	}

	public void setNamedChildren(Map<Object,Object> namedChildren) {
	  this.namedChildren = namedChildren;
	}
	
	@Override
	public String toString() {
		return getName() + " - " + super.toString();
	}
}
