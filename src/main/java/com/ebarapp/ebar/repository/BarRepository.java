
package com.ebarapp.ebar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.model.User;

@Repository
public interface BarRepository extends JpaRepository<Bar, Integer> {

	Bar getBarById(Integer id);

	@Query("select o from Owner o where o.username=:username")
	Owner getOwnerByUsername(String username);

	@Query("select b from Bar b where b.owner=:u")
	List<Bar> getBarByOwner(User u);

	@Query("select b from Bar b JOIN b.employees e where e=:u")
	List<Bar> getBarByEmployee(User u);

	@Query("select b from Bar b where b.name like :text")
	List<Bar> getBarsBySearch(String text);
}
