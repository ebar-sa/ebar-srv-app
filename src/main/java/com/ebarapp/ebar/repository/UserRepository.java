package com.ebarapp.ebar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
