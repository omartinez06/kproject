package com.oscarmartinez.kproject.resource;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentStatusDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int pendingBalance;
	private String status;

}
