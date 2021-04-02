
package com.ebarapp.ebar.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;

public interface BarRepository extends JpaRepository<Bar, Integer> {

  Bar getBarById(Integer id);

	@Query("select o from Owner o where o.username=:username")
	Owner getOwnerByUsername(String username);

	@Query("select b from Bar b where b.owner=:u")
	Set<Bar> getBarByUser(User u);

	@Query("select b from Bar b where b.owner=:o")
	Set<Bar> getBarByOwner(Owner o);

}
