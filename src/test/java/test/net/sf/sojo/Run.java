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
package test.net.sf.sojo;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sf.sojo.core.reflect.ReflectionFieldHelper;
import net.sf.sojo.navigation.PathExecuter;







/**
 * This class is <b>ONLY</b> for the developer, to test code snippet.
 * 
 * @author linke
 *
 */
public class Run {
	

	
	public static void main(String[] args) {

		try {
//			String lvFilter[] = new String [] {"class", "parent", "children", "userObject", "allowsChildren"};
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
			DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
			root.add(child);
			
			// add class
			ReflectionFieldHelper.addAllFields2MapByClass(DefaultMutableTreeNode.class, null);

			PathExecuter.setSimpleProperty(root, "userObject", "Test-Node");

			// remove class
			ReflectionFieldHelper.removePropertiesByClass(DefaultMutableTreeNode.class);

			System.out.println(root);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
