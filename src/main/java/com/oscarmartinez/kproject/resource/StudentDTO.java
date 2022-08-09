package com.oscarmartinez.kproject.resource;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class StudentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String lastName;
	private String dpi;
	private Date birth;
	private String bloodType;
	private String tutor;
	private String quota;
	private long schedule;
	private long kyuId;

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

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getTutor() {
		return tutor;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public long getKyuId() {
		return kyuId;
	}

	public void setKyuId(long kyuId) {
		this.kyuId = kyuId;
	}

	public long getSchedule() {
		return schedule;
	}

	public void setSchedule(long schedule) {
		this.schedule = schedule;
	}

}
