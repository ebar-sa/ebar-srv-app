
package com.ebarapp.ebar.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ebarapp.ebar.model.BarTable;
import com.ebarapp.ebar.model.Bill;
import com.ebarapp.ebar.model.User;

import java.util.List;

public interface BarTableRepository extends JpaRepository<BarTable, Integer> {
	
	@Query("SELECT bt.bill from BarTable bt WHERE bt.id = :id")
	Bill getBillByTableId(@Param("id") int id);
	
	@Query("SELECT bt from BarTable bt WHERE bt.bar.id = :id")
	Set<BarTable> getBarTablesByBarId(@Param("id") int id);
	
	
	@Query("SELECT us from User us WHERE us.username = :userName")
	User getClientByPrincipalUserName(@Param("userName") String userName);

	@Query("SELECT bt from BarTable bt WHERE bt.token = :token")
	BarTable findByToken(@Param("token") String token);
	
	@Query("SELECT bt.token FROM BarTable bt WHERE bt.bar.id = :id AND bt.free=false")
	List<String> getAllValidTokenByBarId(@Param("id") int id);

	@Query("SELECT CASE WHEN o.braintreeMerchantId IS NULL OR o.braintreePublicKey IS NULL OR o.braintreePrivateKey IS NULL THEN false ELSE true END FROM BarTable bt JOIN bt.bar b JOIN b.owner o WHERE bt.id = :id ")
    Boolean checkIfPaymentIsSet(@Param("id") Integer id);

	BarTable getBarTableByToken(String token);
}
