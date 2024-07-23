package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountStatusDTO implements Serializable {

	private static final long serialVersionUID = -7581070973334972828L;
	
	private long id;
	private Date from;
	private Date to;

}
