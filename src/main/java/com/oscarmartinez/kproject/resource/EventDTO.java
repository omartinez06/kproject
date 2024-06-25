package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private List<Date> dateRange;
	private List<CategoryDTO> categories;

}
