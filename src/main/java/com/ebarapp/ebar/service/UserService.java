package com.ebarapp.ebar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
	}

	public void saveUser(User user) {
		this.userRepository.save(user);
	}
	
	public boolean existsUserByUsername(String username) {
		return this.userRepository.existsByUsername(username);
	}

	public boolean existsUserByEmail(String email) {
		return this.userRepository.existsByEmail(email);
	}
}
