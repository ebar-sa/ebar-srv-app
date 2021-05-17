package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Client;
import com.ebarapp.ebar.model.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public User getByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public Owner getOwnerByUsername(String username) {
        return this.userRepository.findOwnerByUsername(username);
    }

    public Client getClientByUsername(String username) {
        return this.userRepository.findClientByUsername(username);
    }

    public Optional<User> getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
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

    public boolean existsUserByDni(String dni) {
        return this.userRepository.existsByDni(dni);
    }
}
