package com.app.yolla.modules.market.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.market.entity.Market;

@Repository
public interface MarketRepository extends JpaRepository<Market, UUID> {

	Optional<Market> findByNameAndAddress(String marketName, String address);

}
