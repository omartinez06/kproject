package com.oscarmartinez.kproject.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Gym {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	private String address;
	private String name;
	private String manager;
	private String gymUser;
	private String gymPassword;
	private int latePayment;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "gym_roles", joinColumns = @JoinColumn(name = "gym_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

}
