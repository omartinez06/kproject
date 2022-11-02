package com.oscarmartinez.kproject.resource;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {

	private long id;
	private String token;
	private Date validFrom;
	private Date validUntil;

}
