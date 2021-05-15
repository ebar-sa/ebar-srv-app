
package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.ItemMenu;

import java.util.Set;

@Repository
public interface ItemMenuRepository extends JpaRepository<ItemMenu, Integer> {

	ItemMenu getItemById(Integer idItemMenu);

	@Query("SELECT i FROM ItemMenu i JOIN i.reviews r WHERE r.creator.username = :username")
	Set<ItemMenu> getItemMenusReviewedByUsername(@Param("username") String username);

}
