package com.app.yolla.modules.market.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.yolla.modules.market.dto.MarketAddRequest;
import com.app.yolla.modules.market.dto.MarketDTO;
import com.app.yolla.modules.market.dto.MarketUpdateRequest;
import com.app.yolla.modules.market.entity.Market;
import com.app.yolla.modules.market.repository.MarketRepository;
import com.app.yolla.shared.exception.MyException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Transactional
@Service
public class MarketService {

	@Autowired
	private MarketRepository repository;


	public MarketDTO convertToDTO(Market market) {
		return new MarketDTO(market.getId(), market.getName(), market.getAddress());
	}

	public void saveMarket(Market market) {
		repository.save(market);

	}

	public MarketDTO createMarket(@Valid MarketAddRequest req) {
		Market market = new Market();
		market.setName(req.getName());
		market.setAddress(req.getAddress());
		
		repository.save(market);
		return convertToDTO(market);
	}

	public MarketDTO updateMarket(UUID id, @Valid MarketUpdateRequest req) {
		Optional<Market> op = repository.findById(id);
		if (!op.isPresent()) {
			throw new MyException("Market tapılmadı");
		}
		Market m = op.get();
		if (req.getName() != null) {
			m.setName(req.getName());
		}
		if (req.getAddress() != null) {
			m.setAddress(req.getAddress());
		}
		repository.save(m);
		return convertToDTO(m);
	}

	public void deleteById(UUID id) {
		Optional<Market> op = repository.findById(id);
		if (!op.isPresent()) {
			throw new MyException("Market tapılmadı");
		}
		repository.deleteById(id);

	}

	public MarketDTO findByNameAndAddress(String marketName, String address) {
		Optional<Market> byName = repository.findByNameAndAddress(marketName, address);
		if (!byName.isPresent()) {
			throw new MyException("Market tapılmadı");
		}
		Market market = byName.get();
		return convertToDTO(market);
	}

	public Market findByMarket(UUID marketId) {
		Optional<Market> byName = repository.findById(marketId);
		if (!byName.isPresent()) {
			throw new MyException("Market tapılmadı");
		}
		Market market = byName.get();
		return market;
	}
}
