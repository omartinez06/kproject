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
public class Student {

	public enum Status {
		UP_TO_DATE, PENDING, DELIQUENT, LOCKED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private String name;
	private String lastName;
	private String email;
	private Date birth;
	private String bloodType;
	private String tutor;
	private int quota;
	private int inscription;
	private String license;
	private boolean applyLatePayment;
	private Date addedDate;

	@Enumerated(EnumType.STRING)
	private Status status;

	@ManyToOne
	private Schedule schedule;

	@ManyToOne
	private Kyu kyu;

	@ManyToOne
	@JoinColumn(name = "gym_id")
	private Gym gym;
}
