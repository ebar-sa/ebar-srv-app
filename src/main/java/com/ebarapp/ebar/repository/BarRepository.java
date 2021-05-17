
package com.ebarapp.ebar.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.ebarapp.ebar.model.Bar;
import com.ebarapp.ebar.model.User;

@Repository
public interface BarRepository extends JpaRepository<Bar, Integer> {

	Bar getBarById(Integer id);

	@Query("select b from Bar b where b.owner=:u")
	List<Bar> getBarByOwner(User u);

	@Query("select b from Bar b JOIN b.employees e where e=:u")
	List<Bar> getBarByEmployee(User u);

	//@Query("select b from Bar b where b.name like :text")
	@Query("select b from Bar b where b.name like :text")
	List<Bar> getBarsBySearch(String text, Pageable pageable);

	@Query("SELECT b FROM Bar b JOIN b.menu.items i WHERE i.id = :id")
	Bar findBarByItemMenuId(@Param("id") Integer id);

}
