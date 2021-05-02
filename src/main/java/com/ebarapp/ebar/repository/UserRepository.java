package com.ebarapp.ebar.repository;

import java.util.Optional;

import com.ebarapp.ebar.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Boolean existsByDni(String dni);

	@Query("SELECT o FROM Owner o WHERE o.username = :user")
	Owner findOwnerByUsername(@Param("user") String username);

	@Query("SELECT o FROM BarTable bt JOIN bt.bar b JOIN b.owner o WHERE bt.id = :id")
	Owner getOwnerByBarTableId(@Param("id") Integer barTableId);
}
