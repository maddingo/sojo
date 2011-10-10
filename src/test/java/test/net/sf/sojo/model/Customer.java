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
import java.util.HashSet;
import java.util.Set;

public class Customer {
	
	private String firstName = null;
	private String lastName = null;
	private Date birthDate = null;
	private Set addresses = new HashSet();
	private Object partner[] = null;
	private byte bytes[] = null;
	
	public Customer() { }	
	public Customer(String pvLastName) {
		setLastName(pvLastName);
	}
	
	public Object[] getPartner() { return partner; }
	public void setPartner(Object[] pvPartner) { partner = pvPartner; }
	
	public Set getAddresses() { return addresses; }
	public void setAddresses(Set pvAddresses) { addresses = pvAddresses; }
	
	public Date getBirthDate() { return birthDate; }
	public void setBirthDate(Date pvBirthDate) { birthDate = pvBirthDate; }
	
	public String getFirstName() { return firstName; }
	public void setFirstName(String pvFirstName) { firstName = pvFirstName; }
	
	public String getLastName() { return lastName; }
	public void setLastName(String pvLastName) { lastName = pvLastName; }
	
	public byte[] getBytes() { return bytes; }
	public void setBytes(byte[] pvBytes) { bytes = pvBytes; }

}
