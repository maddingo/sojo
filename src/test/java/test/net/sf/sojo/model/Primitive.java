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

public class Primitive {
	
	private boolean booleanValue = false;
	private byte byteValue = 0;
	private short shortValue = 0;
	private int intValue = 0;
	private long longValue = 0;
	private double doubleValue = 0;
	private float floatValue = 0;
	private char charValue = 'A';
	
	public static Primitive createPrimitiveExample() {
        Primitive lvPrimitive = new Primitive();
        lvPrimitive.setBooleanValue(true);
        lvPrimitive.setByteValue((byte) 2);
        lvPrimitive.setCharValue('a');
        lvPrimitive.setDoubleValue(2.3d);
        lvPrimitive.setFloatValue(3.4f);
        lvPrimitive.setIntValue(3);
        lvPrimitive.setLongValue(5l);
        lvPrimitive.setShortValue((short) 7);
        return lvPrimitive;
	}
	
	@Override
	public boolean equals(Object pvPrimitive) {
		if (pvPrimitive == null) {
			return false;
		}
		if (pvPrimitive instanceof Primitive) {
			Primitive lvPrimitive = (Primitive) pvPrimitive;
			if (lvPrimitive.getBooleanValue() == this.getBooleanValue() &&
				lvPrimitive.getByteValue() == this.getByteValue() &&
				lvPrimitive.getCharValue() == this.getCharValue() && 
				lvPrimitive.getDoubleValue() == this.getDoubleValue() && 
				lvPrimitive.getFloatValue() == this.getFloatValue() &&
				lvPrimitive.getIntValue() == this.getIntValue() && 
				lvPrimitive.getLongValue() == this.getLongValue() && 
				lvPrimitive.getShortValue() == this.getShortValue()
				) 
			{
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int lvHashCode = super.hashCode();
		return lvHashCode;
	}
	public boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean pvBooleanValue) {
		booleanValue = pvBooleanValue;
	}
	public byte getByteValue() {
		return byteValue;
	}
	public void setByteValue(byte pvByteValue) {
		byteValue = pvByteValue;
	}
	public char getCharValue() {
		return charValue;
	}
	public void setCharValue(char pvCharValue) {
		charValue = pvCharValue;
	}
	public double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double pvDoubleValue) {
		doubleValue = pvDoubleValue;
	}
	public float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(float pvFloatValue) {
		floatValue = pvFloatValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int pvIntValue) {
		intValue = pvIntValue;
	}
	public long getLongValue() {
		return longValue;
	}
	public void setLongValue(long pvLongValue) {
		longValue = pvLongValue;
	}
	public short getShortValue() {
		return shortValue;
	}
	public void setShortValue(short pvShortValue) {
		shortValue = pvShortValue;
	}
	

}
