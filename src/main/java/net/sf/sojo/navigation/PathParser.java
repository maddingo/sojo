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



public class PathParser {
	
	public static PathAction[] parse(String pvParth) {
		PathAction lvPathAction[] = null;
		
		if (pvParth == null) {
			lvPathAction = new PathAction[0];
		} else {
			String lvPathArray[] = pvParth.split("\\.");
			lvPathAction = new PathAction[lvPathArray.length];
			for (int i = 0; i < lvPathArray.length; i++) {
				String lvPath = lvPathArray[i];
				if (lvPath.trim().length() > 0) {
					PathAction lvAction = getActionByPath(lvPath);
					lvPathAction[i] = lvAction;
				}
			}
			
			// no point, but the length of String is greater 0 -> simple name, how: name
			if (pvParth.indexOf(".") < 0 && pvParth.trim().length() > 0) {
				lvPathAction = new PathAction[1];
				PathAction lvAction = getActionByPath(pvParth.trim());
				lvPathAction[0] = lvAction;
			}
		}
		
		lvPathAction = removeEmptyActions(lvPathAction);
		return lvPathAction;
	}
	
	protected static int testBrackets(String pvPath, String pvOpen, String pvClosed) {
		int lvIndexClosedBracket = pvPath.indexOf(pvClosed);
		int lvIndexOpenBracket = pvPath.indexOf(pvOpen);
		int lvIndexOpenBracketLast = pvPath.lastIndexOf(pvOpen);
		int l = pvPath.length() - 1;
		if (lvIndexOpenBracket < 0) {
			throw new PathParseException("Missing open bracket '" + pvOpen + "' in path: " + pvPath);
		}
		if (lvIndexOpenBracket != lvIndexOpenBracketLast) {
			throw new PathParseException("In the path is only one open bracket '" + pvOpen + "' allowed: " + pvPath);
		}
		if (lvIndexClosedBracket != l) {
			throw new PathParseException("In the path is only one closed bracket '" + pvClosed + "' allowed: " + pvPath);
		}
//		if (lvIndexOpenBracket > lvIndexClosedBracket) {
//			throw new PathParseException("The open bracket must be before the closed bracket: " + pvPath);
//		}
		return lvIndexOpenBracket;
	}
	
	public static PathAction getActionByPath(String pvPath) {
		int lvActionType = PathAction.ACTION_TYPE_SIMPLE;
		PathAction lvPathAction = new PathAction();
		lvPathAction.setPath(pvPath);
		// Key-Action
		if (pvPath.indexOf(")") > 0) {
			int x = testBrackets(pvPath, "(", ")");
			String lvPropertyName = pvPath.substring(0, x);
			String lvKey = pvPath.substring(x + 1, pvPath.length() - 1);
			if (lvPropertyName != null && lvPropertyName.length() > 0) {
				lvPathAction.setProperty(lvPropertyName);
			}
			lvPathAction.setKey(lvKey);
			lvActionType = PathAction.ACTION_TYPE_KEY;
		}
		// Index-Action
		else if (pvPath.indexOf("]") > 0) {
			int x = testBrackets(pvPath, "[", "]");
			String lvPropertyName = pvPath.substring(0, x);
			String lvIndex = pvPath.substring(x + 1, pvPath.length() - 1);
			if (lvPropertyName != null && lvPropertyName.length() > 0) {
				lvPathAction.setProperty(lvPropertyName);
			}
			if (lvIndex.length() > 0) {
				try {
					Integer lvIndexInt = Integer.valueOf(lvIndex.trim());
					lvPathAction.setIndex(lvIndexInt.intValue());
				} catch (Exception e) {
					throw new PathParseException("The index must be a integer and not: " + lvIndex);
				}
			}			
			
			lvActionType = PathAction.ACTION_TYPE_INDEX;
		}
		// Simple Action
		else {
			lvPathAction.setProperty(pvPath);
		}
		
		lvPathAction.setType(lvActionType);
		return lvPathAction;
	}
	
	public static PathAction[] removeEmptyActions(PathAction[] pvActions) {
		int lvNumberOfNullValues = 0;
		for (int i = 0; i < pvActions.length; i++) {
			if (pvActions[i] == null) {
				lvNumberOfNullValues++;
			}
		}
		
		PathAction lvNewPathAction[] = new PathAction[pvActions.length - lvNumberOfNullValues];
		for (int i = 0; i < pvActions.length; i++) {
			if (pvActions[i] != null) {
				lvNewPathAction[i] = pvActions[i];
			}
		}

		return lvNewPathAction;
	}

}
