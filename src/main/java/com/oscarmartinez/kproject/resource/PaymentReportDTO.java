package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Set;

import com.oscarmartinez.kproject.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String month;
	private int value;

}
