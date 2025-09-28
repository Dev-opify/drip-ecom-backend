// dripyard-backend/src/main/java/com/aditi/dripyard/service/impl/CustomeUserServiceImplementation.java
package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomeUserServiceImplementation implements UserDetailsService {

	private final UserRepository userRepository;

	// --- CONSTRUCTOR UPDATED ---
	// Removed SellerRepository dependency
	public CustomeUserServiceImplementation(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// --- METHOD SIMPLIFIED ---
	// Removed all logic related to checking for sellers
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with email - " + username);
		}

		// --- Role Assignment ---
		// Ensures that if a role is somehow null, it defaults to ROLE_CUSTOMER
		USER_ROLE role = user.getRole();
		if (role == null) {
			role = USER_ROLE.ROLE_CUSTOMER;
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role.toString()));

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}
}