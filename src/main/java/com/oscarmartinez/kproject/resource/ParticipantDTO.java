package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantDTO implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String lastName;
	private String address;
	private String phone;
	private String bloodType;
	private String dojo;
	private String country;
	private long categoryId;
}
