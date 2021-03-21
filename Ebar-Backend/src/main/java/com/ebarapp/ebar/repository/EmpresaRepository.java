package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebarapp.ebar.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

}
