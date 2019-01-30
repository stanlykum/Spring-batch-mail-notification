package com.ajtech.mail.batch.domain;

import java.util.Date;

public class Person {
	private int custId;
	private String name;
	private String email;
	private String contactNo;
	private Date dob;
	private String status;
	private double outstandingAmount;
	private Date lastDueDate;

	public int getCustId() {
		return custId;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public Date getLastDueDate() {
		return lastDueDate;
	}

	public void setLastDueDate(Date lastDueDate) {
		this.lastDueDate = lastDueDate;
	}

}
