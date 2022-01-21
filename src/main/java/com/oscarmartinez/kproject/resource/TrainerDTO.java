package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;

public class TrainerDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String lastName;
	private long kyuId;
	private Date birth;
	private String dpi;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getKyuId() {
		return kyuId;
	}

	public void setKyuId(long kyuId) {
		this.kyuId = kyuId;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}
}
