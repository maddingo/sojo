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
package test.net.sf.sojo.optional.filter.model;

import java.util.Date;

/**
 * @@ClassAttribute(filterUniqueId=true)
 * 
 * @author linke
 *
 */
public class Person {
	
	private String firstName = null;
	/**
	 * @@PropertyAttribute()
	 */	
	private String lastName = null;
	private Date birthDay = null;
	private Account account = null;
	private Account accountFilter = null;
	
	public Date getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Date pvBirthDay) {
		birthDay = pvBirthDay;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String pvFirstName) {
		firstName = pvFirstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String pvLastName) {
		lastName = pvLastName;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account pvAccount) {
		account = pvAccount;
	}
	
	/**
	 * @@PropertyAttribute()
	 */	
	public Account getAccountFilter() {
		return accountFilter;
	}
	public void setAccountFilter(Account pvAccountFilter) {
		accountFilter = pvAccountFilter;
	}
	
	

}
