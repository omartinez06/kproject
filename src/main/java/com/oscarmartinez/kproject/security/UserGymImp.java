package com.oscarmartinez.kproject.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.oscarmartinez.kproject.entity.Gym;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserGymImp implements UserDetails {

	private long id;
	private String address;
	private String name;
	private String manager;
	private String gymUser;
	private String gymPassword;
	private Collection<? extends GrantedAuthority> authorities;

	public static UserGymImp build(Gym gym) {
		Set<GrantedAuthority> authorities = gym.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
		return UserGymImp.builder().address(gym.getAddress()).name(gym.getName()).manager(gym.getManager())
				.gymUser(gym.getGymUser()).gymPassword(gym.getGymPassword()).authorities(authorities).build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return gymPassword;
	}

	@Override
	public String getUsername() {
		return gymUser;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
