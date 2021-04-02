
package com.ebarapp.ebar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebarapp.ebar.model.ItemMenu;

@Repository
public interface ItemMenuRepository extends JpaRepository<ItemMenu, Integer> {

}