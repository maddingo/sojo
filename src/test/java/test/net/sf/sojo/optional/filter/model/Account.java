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
 * @@ClassAttribute()
 * 
 * @author linke
 *
 */
public class Account {

	/**
	 * @@PropertyAttribute()
	 * 
	 * This filter-property don't work. The field name is different from the property-name. 
	 */
	private String aAccountNumber = null;
	private double amount = 0;
	/**
	 * @@PropertyAttribute()
	 * 
	 */
	private Date createDate = null;
	
	public String getAccountNumber() { return aAccountNumber; }
	public void setAccountNumber(String pvAccountNumber) { aAccountNumber = pvAccountNumber; }
	
	/**
	 * @@PropertyAttribute()
	 * 
	 * @return The Amount.
	 */
	public double getAmount() { return amount; }
	public void setAmount(double pvAmount) { amount = pvAmount; }

	/**
	 * 
	 * @return Date of created Account.
	 */
	public Date getCreateDate() { return createDate; }
	public void setCreateDate(Date pvCreateDate) { createDate = pvCreateDate; }
}
