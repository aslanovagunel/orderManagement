package com.app.yolla.modules.product.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.yolla.modules.order.repository.OrderItemRepository;
import com.app.yolla.modules.product.dto.ProductAddRequest;
import com.app.yolla.modules.product.dto.ProductDTO;
import com.app.yolla.modules.product.dto.ProductUpdateRequest;
import com.app.yolla.modules.product.entity.Product;
import com.app.yolla.modules.product.repository.ProductRepository;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.service.UserService;
import com.app.yolla.shared.exception.MyException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderItemRepository orderItemRepository;

	public ProductDTO createdProduct(@Valid ProductAddRequest req) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String phoneNumber = authentication.getName();
		UserDTO en = userService.findByPhoneNumber(phoneNumber);

		Product product = new Product();

		product.setName(req.getName());
		product.setDescription(req.getDescription());
		product.setPrice(req.getPrice());
		product.setStockQuantity(req.getStockQuantity());
		product.setCreatedAt(LocalDateTime.now());
		product.setActive(true);
		product.setUserId(en.getId());
		repository.save(product);

		return convertToDTO(product);

	}

	private ProductDTO convertToDTO(Product product) {
		return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getStockQuantity(),
				product.getActive(), product.getCreatedAt(), product.getUpdatedAt());
	}



	public ProductDTO updateProduct(UUID id, ProductUpdateRequest req) {
		Product p = findProduct(id);

		if (req.getDescription() != null && !req.getDescription().trim().isEmpty()) {
			p.setDescription(req.getDescription());
		}
		if (req.getName() != null && !req.getName().trim().isEmpty()) {
			p.setName(req.getName());
		}
		if (req.getPrice() != null) {
			p.setPrice(req.getPrice());
		}
		if (req.getStockQuantity() != null) {
			p.setStockQuantity(req.getStockQuantity());
		}
		repository.save(p);
		ProductDTO dto = convertToDTO(p);
		return dto;
	}

	public Product findProduct(UUID id) {
		Optional<Product> op = repository.findById(id);
		if (!op.isPresent()) {
			throw new MyException("Bu id'li məhsul yoxdur");
		}
		Product p = op.get();
		return p;

	}

	public void deleteById(UUID id) {
		Product p = findProduct(id);
		orderItemRepository.deleteByProductId(id);
		repository.deleteById(id);
	}

	public void savePro(Product product) {
		repository.save(product);

	}
}
