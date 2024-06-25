package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String lastName;
	private String email;
	private Date birth;
	private String bloodType;
	private String tutor;
	private String quota;
	private long schedule;
	private long kyuId;
	private boolean applyLatePayment;

}
