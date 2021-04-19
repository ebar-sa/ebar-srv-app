
package com.ebarapp.ebar.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	@Query("SELECT e from Employee e WHERE e.bar.id = :id")
	Set<Employee> getEmployeeByBarId(@Param("id") int id);

	Optional<Employee> findByUsername(String username);

}
