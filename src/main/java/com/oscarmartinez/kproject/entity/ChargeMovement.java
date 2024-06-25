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
public class ChargeMovement {
	
	public enum ChargeType {
        DELINQUENCY,
        QUOTE,
        ADDITIONAL
    }
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private Date addedDate;
	private int amount;
	private String description;
	
	@Enumerated(EnumType.STRING)
    private ChargeType type;

	@ManyToOne
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "gym_id")
	private Gym gym;

}
