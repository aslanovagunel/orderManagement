package com.app.yolla.modules.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

	@Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
	Page<Order> getAll(@Param("userId") UUID userId, Pageable pageable);

	@Query(value = "select * from orders where user_id=?1 ", nativeQuery = true)
	List<Order> findOrder(UUID id);

	List<Order> findAllByMarketId(UUID marketId);

}