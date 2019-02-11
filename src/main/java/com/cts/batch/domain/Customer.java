package com.cts.batch.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "customer")
public class Customer {

	private String actNbr;
	private String bankActNbr;
	private String cardMemberName;
	private Date paymentDueDate;

	public String getActNbr() {
		return actNbr;
	}

	public void setActNbr(String actNbr) {
		this.actNbr = actNbr;
	}

	public String getBankActNbr() {
		return bankActNbr;
	}

	public void setBankActNbr(String bankActNbr) {
		this.bankActNbr = bankActNbr;
	}

	public String getCardMemberName() {
		return cardMemberName;
	}

	public void setCardMemberName(String cardMemberName) {
		this.cardMemberName = cardMemberName;
	}

	public Date getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(Date paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

}
