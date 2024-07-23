package com.oscarmartinez.kproject.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.oscarmartinez.kproject.entity.Student.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Payment {
	
	public enum Type {
		CASH, CHECK, DEPOSIT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private Date paymentDate;
	private String depositTicket;
	private String month;
	private int value;
	private boolean latePayment;
	private boolean valid;
	private String insertedBy;
	
	@Enumerated(EnumType.STRING)
	private Type type;

	@ManyToOne
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "gym_id")
	private Gym gym;
}
