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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.sojo.core.reflect.Property;
import net.sf.sojo.core.reflect.ReflectionPropertyHelper;
import net.sf.sojo.util.Util;

public class PathExecuter {
	
	public static void setNestedProperty (Object pvRootObject, final String pvPath, final Object pvValue) {
		Object lvRootObject = pvRootObject;
		PathAction lvAction[] = PathParser.parse(pvPath);
		int l = lvAction.length - 1;
		for (int i = 0; i < lvAction.length; i++) {
			// than is it a simple PathAction, this mean it is one property in path
			if (l == 0 && lvAction[i].getProperty() != null) {
				lvAction[i].setType(PathAction.ACTION_TYPE_SIMPLE);
				lvRootObject = PathExecuter.getNestedProperty(lvRootObject, lvAction[i]);
			} 
			else if (i < l) {
				lvRootObject = PathExecuter.getNestedProperty(lvRootObject, lvAction[i]);
			}
			
			if (i == l) {
				if (lvRootObject instanceof Collection || lvRootObject.getClass().isArray()) {
					lvAction[i].setType(PathAction.ACTION_TYPE_INDEX);
				} else if (lvRootObject instanceof Map) {
					lvAction[i].setType(PathAction.ACTION_TYPE_KEY);
				} 
				setNestedProperty(lvRootObject, lvAction[i], pvValue);
			}
		}
		
	}
	
	public static void setNestedProperty (final Object pvRootObject, final PathAction pvPathAction, final Object pvValue) {
		switch (pvPathAction.getType()) {
			case PathAction.ACTION_TYPE_SIMPLE:
				setSimpleProperty(pvRootObject, pvPathAction.getPath(), pvValue);
				break;
			case PathAction.ACTION_TYPE_INDEX:
				setIndexProperty(pvRootObject, pvPathAction.getIndex(), pvValue);
				break;
			case PathAction.ACTION_TYPE_KEY:
				setKeyProperty(pvRootObject, pvPathAction.getKey(), pvValue);
				break;
			default:
				throw new PathExecuteException("Invalide action type: " + pvPathAction.getType());
	}
		
	}
	
	public static Object getNestedProperty (final Object pvRootObject, final String pvPath) {
		Object lvResult = pvRootObject;
		PathAction lvAction[] = PathParser.parse(pvPath);
		for (int i = 0; i < lvAction.length; i++) {
			lvResult = PathExecuter.getNestedProperty(lvResult, lvAction[i]);
		}
		return lvResult;
	}
	
	public static Object getNestedProperty (final Object pvRootObject, final PathAction pvPathAction) {
		Object lvReturn = null;
		Object lvRootObject = pvRootObject;
		
		switch (pvPathAction.getType()) {
			case PathAction.ACTION_TYPE_SIMPLE:
				lvReturn = getSimpleProperty(lvRootObject, pvPathAction.getProperty());
				break;
			case PathAction.ACTION_TYPE_INDEX:
				if (pvPathAction.getProperty() != null) {
					lvRootObject = getSimpleProperty(lvRootObject, pvPathAction.getProperty());
				}
				lvReturn = getIndexProperty(lvRootObject, pvPathAction.getIndex());
				break;
			case PathAction.ACTION_TYPE_KEY:
				if (pvPathAction.getProperty() != null) {
					lvRootObject = getSimpleProperty(lvRootObject, pvPathAction.getProperty());
				}				
				lvReturn = getKeyProperty(lvRootObject, pvPathAction.getKey());
				break;
			default:
				throw new PathExecuteException("Invalide action type: " + pvPathAction.getType());
		}
		
		return lvReturn;
	}
	
	public static Object getSimpleProperty (Object pvRootObject, String pvPath) {
		Object lvReturn = null;
		if (pvPath == null) {
			throw new PathExecuteException("The property-path must be different from null.");
		}
		if (pvPath.length() == 0) {
			return pvRootObject;
		}
		if (pvPath.equals(Util.DEFAULT_KEY_WORD_CLASS)) {
			return pvRootObject.getClass();
		}
		if (pvRootObject instanceof Map) {
			lvReturn = ((Map<?,?>) pvRootObject).get(pvPath);
		} else {
			Map<?,?> lvAllGetterMethod = ReflectionPropertyHelper.getAllGetterProperties(pvRootObject.getClass(), null);
			AccessibleObject lvAccessibleObject = (AccessibleObject) lvAllGetterMethod.get(pvPath);
			if (lvAccessibleObject == null) {
				throw new PathExecuteException("No such method find for path: " + pvPath + " and class: " + pvRootObject.getClass().getName());
			}
			try {
				lvReturn = new Property(lvAccessibleObject).executeGetValue(pvRootObject);
			} catch (Exception e) {
				throw new PathExecuteException("Can't execute property " + lvAccessibleObject + " for path: " + pvPath, e);
			}
		}
		return lvReturn;
	}

	@SuppressWarnings("unchecked")
	public static void setSimpleProperty (Object pvRootObject, String pvPath, Object pvValue) {
		if (pvPath == null) {
			throw new PathExecuteException("The property-path must be different from null.");
		}
		if (pvPath.length() > 0) {
			if (pvRootObject instanceof Map) {
				((Map<String,Object>) pvRootObject).put(pvPath, pvValue);
			} else {
				Map<?,?> lvAllSetterMethod = ReflectionPropertyHelper.getAllSetterProperties(pvRootObject.getClass(), null);
				AccessibleObject lvAccessibleObject = (AccessibleObject) lvAllSetterMethod.get(pvPath);
				if (lvAccessibleObject == null) {
					throw new PathExecuteException("No such method find for path: " + pvPath + " and class: " + pvRootObject.getClass().getName());
				}
				try {
					new Property(lvAccessibleObject).executeSetValue(pvRootObject, pvValue);
				} catch (Exception e) {
					throw new PathExecuteException("Can't execute property " + lvAccessibleObject + " for path: " + pvPath, e);
				}
			}
		}
	}

	public static Object getIndexProperty (Object pvRootObject, int pvIndex) {
		Object lvReturn = null;
		if (pvIndex < 0) {
			lvReturn = pvRootObject;
		}
		else if (pvRootObject instanceof List) {
			lvReturn = ((List<?>) pvRootObject).get(pvIndex);
		}
		else if (pvRootObject instanceof Collection) {
			Collection<?> lvColl = (Collection<?>) pvRootObject;
			Iterator<?> lvIterator = lvColl.iterator();
			int lvConter = 0;
			while (lvIterator.hasNext()) {
				Object lvObject = lvIterator.next();
				if (lvConter == pvIndex) {
					lvReturn = lvObject;
					break;
				}
				lvConter++;
			}
		} 
		else if (pvRootObject.getClass().isArray()) {
			lvReturn = ((Object[]) pvRootObject)[pvIndex];
		}
		else {
			throw new PathExecuteException("The object must be a Collection: " + pvRootObject);
		}
		return lvReturn;
	}
	
	public static void setIndexProperty (Object pvRootObject, Object pvValue) {
		setIndexProperty(pvRootObject, -1, pvValue);
	}
	
	@SuppressWarnings("unchecked")
	public static void setIndexProperty (Object pvRootObject, int pvIndex, Object pvValue) {
		if (pvRootObject instanceof List) {
			if (pvIndex >= 0) {
				((List<Object>) pvRootObject).add(pvIndex, pvValue);
			} else {
				((List<Object>) pvRootObject).add(pvValue);
			}
		}
		else if (pvRootObject instanceof Collection) {
			((Collection<Object>) pvRootObject).add(pvValue);
		} 
		else if (pvRootObject.getClass().isArray()) {
			if (pvIndex >= 0) {
				Array.set(pvRootObject, pvIndex, pvValue);
			} else {
				throw new PathExecuteException("Can't set the value: " + pvValue + " by a array, without a valid index.");
//				Object lvOldArray[] = (Object[]) pvRootObject;
//				Object lvNewArray[] = new Object[lvOldArray.length + 1];
//				for (int i = 0; i < lvOldArray.length; i++) {
//					lvNewArray[i] = lvOldArray[i];
//				}
//				lvNewArray[lvOldArray.length] = pvValue;
//				pvRootObject = lvNewArray;
			}
		}
		else {
			throw new PathExecuteException("The object must be a Collection: " + pvRootObject);
		}
	}


	public static Object getKeyProperty (Object pvRootObject, Object pvKey) {
		Object lvReturn = null;
		if (pvKey.toString().length() == 0) {
			lvReturn = pvRootObject;
		}
		else if (pvRootObject instanceof Map) {
			Map<?, ?> lvMap = (Map<?, ?>) pvRootObject;
			lvReturn = lvMap.get(pvKey);
			if (lvReturn == null) {
				lvReturn = findKeyIfKeyIsNotStringType(lvMap, pvKey);
			}
		} else {
			throw new PathExecuteException("The object must be a Map: " + pvRootObject);
		}
			
		return lvReturn;
	}

	
	@SuppressWarnings("unchecked")
	public static void setKeyProperty (Object pvRootObject, Object pvKey, Object pvValue) {
		if (pvRootObject instanceof Map) {
			((Map<Object, Object>) pvRootObject).put(pvKey, pvValue);
		} else {
			throw new PathExecuteException("The object must be a Map: " + pvRootObject);
		} 
	}
	
	protected static Object findKeyIfKeyIsNotStringType (Map<?, ?> pvMap, Object pvKey) {
		for (Map.Entry<?, ?> lvEntry : pvMap.entrySet()) {
			if (lvEntry.getKey().toString().equals(pvKey)) {
				return lvEntry.getValue();
			}
		}
		return null;
	}
	
}
