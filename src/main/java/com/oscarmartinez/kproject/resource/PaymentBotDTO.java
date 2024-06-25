package com.oscarmartinez.kproject.resource;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentBotDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String depositTicket;
	private int value;
	private String studentLicense;
	private String insertedBy;
}
