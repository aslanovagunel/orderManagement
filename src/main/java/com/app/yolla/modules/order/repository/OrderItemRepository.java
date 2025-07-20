package com.app.yolla.modules.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.order.entity.OrderItem;

import jakarta.transaction.Transactional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

	List<OrderItem> findByOrderId(UUID id);

	@Transactional
	@Modifying
	@Query("DELETE FROM OrderItem oi WHERE oi.product.id = :productId")
	void deleteByProductId(@Param("productId") UUID productId);

}
