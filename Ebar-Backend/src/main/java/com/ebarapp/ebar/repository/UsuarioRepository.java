package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebarapp.ebar.model.User;

public interface UsuarioRepository extends JpaRepository<User, Long>{

}
