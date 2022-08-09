package com.oscarmartinez.kproject.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private Date paymentDate;
	private String depositTicket;
	private String month;
	private int value;
	private boolean latePayment;

	@ManyToOne
	private Student student;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}
