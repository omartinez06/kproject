package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;

public class PaymentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date paymentDate;
	private String depositTicket;
	private String month;
	private int value;
	private boolean latePayment;
	private long studentId;

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getDepositTicket() {
		return depositTicket;
	}

	public void setDepositTicket(String depositTicket) {
		this.depositTicket = depositTicket;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isLatePayment() {
		return latePayment;
	}

	public void setLatePayment(boolean latePayment) {
		this.latePayment = latePayment;
	}

	public long getStudentId() {
		return studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}

}
