package com.ebarapp.ebar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ebarapp.ebar.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer>{
	
	@Modifying
	@Query("UPDATE Client cl SET cl.table.id = :barTableId WHERE cl.username = :username")
	void updateBarTableOnClient(@Param("barTableId") Integer barTableId, @Param("username") String username);

	@Query("SELECT cl FROM Client cl WHERE cl.username = :username")
	public Optional<Client> findClientByUsername(@Param("username") String username);
	

	
}
