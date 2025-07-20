package com.app.yolla.modules.market.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.market.dto.MarketAddRequest;
import com.app.yolla.modules.market.dto.MarketDTO;
import com.app.yolla.modules.market.dto.MarketUpdateRequest;
import com.app.yolla.modules.market.service.MarketService;
import com.app.yolla.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Market", description = "Marketin idarə olunması")
@RestController
@RequestMapping(path = "/markets")
@CrossOrigin(origins = "*")
public class MarketController {

	@Autowired
	private MarketService service;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Yeni market əlavə et", description = "Yeni market əlavə edir. Bu əməliyyatı yalnız ADMIN rolu olan istifadəçilər yerinə yetirə bilər.")
	public ResponseEntity<ApiResponse<MarketDTO>> add(@Valid @RequestBody MarketAddRequest req) {
		try {
			MarketDTO createdMarket = service.createMarket(req);
			ApiResponse<MarketDTO> response = new ApiResponse<>(true, "Market uğurla yaradıldı", createdMarket);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			ApiResponse<MarketDTO> response = new ApiResponse<>(false,
					"Market yaradılarkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Market-i yenilə", description = "Verilmiş ID-yə uyğun market məlumatlarını yeniləyir. Bu əməliyyatı yalnız ADMIN rolu olan istifadəçilər yerinə yetirə bilər.")
	public ResponseEntity<ApiResponse<MarketDTO>> update(
			@Parameter(description = "Yenilənəcək marketin ID-si") @PathVariable("id") UUID id,
			@Valid @RequestBody MarketUpdateRequest req) {
		try {
			MarketDTO updatedMarket = service.updateMarket(id, req);
			ApiResponse<MarketDTO> response = new ApiResponse<>(true, "Market uğurla yeniləndi", updatedMarket);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<MarketDTO> response = new ApiResponse<>(false,
					"Market yenilənərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Market sil", description = "Verilmiş ID-yə uyğun marketi silir. Bu əməliyyatı yalnız ADMIN rolu olan istifadəçilər yerinə yetirə bilər.")
	public ResponseEntity<ApiResponse<String>> deleteById(
			@Parameter(description = "Silinəcək marketin ID-si") @PathVariable("id") UUID id) {
		try {
			service.deleteById(id);
			ApiResponse<String> response = new ApiResponse<>(true, "Market uğurla silindi", null);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<String> response = new ApiResponse<>(false,
					"Market silinərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}
}
