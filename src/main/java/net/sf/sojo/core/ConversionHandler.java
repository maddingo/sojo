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
package net.sf.sojo.core;

import java.util.ArrayList;
import java.util.List;


/**
 * Handle all registered Conversion. All Conversion are added to a List.
 * The order of call the add-method is the order to find the right Conversion.
 * 
 * @author linke
 *
 */
public final class ConversionHandler {

	private final List<Conversion> conversions = new ArrayList<Conversion>();
	
	public void addConversion(final Conversion pvConversion) {
		if (pvConversion == null) {
			throw new IllegalArgumentException("The Conversion must be different from null");
		}
		// TODO should the conversions be a Set?
		if (!containsConversion(pvConversion)) {
			conversions.add(pvConversion);
		}
	}

	public Conversion replaceConversion(final Conversion pvConversion) {
		return replaceConversion(pvConversion, false);
	}

	public Conversion replaceAllConversion(final Conversion pvConversion) {
		return replaceConversion(pvConversion, true);
	}

	protected Conversion replaceConversion(final Conversion pvConversion, boolean pvReplaceAll) {
		Conversion lvConversion = null;
		for (int i=0; i<conversions.size(); i++) {
			lvConversion = (Conversion) conversions.get(i);
			if (lvConversion.getClass().equals(pvConversion.getClass())) {
				conversions.set(i, pvConversion);
				if (pvReplaceAll == false) {
					break;
				}
			}
		}
		
		return lvConversion;
	}

	

	public Conversion getConversion(final Object pvObject, final Class<?> pvToType) {
	  for (Conversion lvConversion : conversions) {
			if (pvToType == null) {
				if (lvConversion.isAssignableFrom(pvObject)) {
					return lvConversion;
				}
			} else {
				if (lvConversion.isAssignableTo(pvToType) && lvConversion.isAssignableFrom(pvObject)) {
					return lvConversion;
				}
			}
		}
		return null;
	}
		
	public Conversion getConversionByPosition(final int pvPosition) {
		return conversions.get(pvPosition);
	}
	
	public int size() { 
	  return conversions.size(); 
	}
	
	public void clear() { 
	  conversions.clear(); 
	}

	public Conversion removeConversion(final Conversion pvConversion) {
		if (conversions.remove(pvConversion)) {
		  return pvConversion;
		}
		return null;
	}
	
	public boolean containsConversion(Conversion pvConversion) {
	  return conversions.contains(pvConversion);
	}
}
