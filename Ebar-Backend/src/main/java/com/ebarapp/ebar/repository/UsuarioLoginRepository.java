package com.ebarapp.ebar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.UsuarioLogin;

@Repository
public interface UsuarioLoginRepository extends JpaRepository<UsuarioLogin, String>{

	Optional<UsuarioLogin> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
