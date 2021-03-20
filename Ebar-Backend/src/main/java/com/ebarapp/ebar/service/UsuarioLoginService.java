package com.ebarapp.ebar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.repository.UsuarioLoginRepository;

@Service
public class UsuarioLoginService implements UserDetailsService {

	@Autowired
	private UsuarioLoginRepository usuarioLoginRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.usuarioLoginRepository.findByUsername(username).get();
	}
}
