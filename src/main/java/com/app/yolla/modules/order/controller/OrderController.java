package com.app.yolla.modules.order.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.order.dto.OrderCreateRequest;
import com.app.yolla.modules.order.dto.OrderDTO;
import com.app.yolla.modules.order.dto.OrderResponse;
import com.app.yolla.modules.order.dto.OrderUpdateRequest;
import com.app.yolla.modules.order.service.OrderService;
import com.app.yolla.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Order", description = "Sifarişlərə dair əməliyyatlar")
@RestController
@RequestMapping(path = "/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
	private OrderService service;

    @PostMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	@Operation(summary = "Yeni sifariş yarat", description = "İstifadəçidən alınan məlumatlara əsasən yeni sifariş yaradılır. Bu əməliyyatı ADMIN və ya CUSTOMER rolu olan istifadəçilər həyata keçirə bilər.")
	public ResponseEntity<ApiResponse<OrderDTO>> createdOrder(@Valid @RequestBody OrderCreateRequest request) {
		try {
			OrderDTO createdOrder = service.createdOrder(request);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifaris uğurla yaradıldı", createdOrder);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifaris yaradılarkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping(path = "/begin/{begin}/length/{length}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	@Operation(summary = "Sifarişləri siyahıla", description = "Verilmiş aralıqda bütün sifarişləri gətirir. Bu əməliyyatı ADMIN və ya CUSTOMER rolu olan istifadəçilər görə bilər.")
	public ResponseEntity<ApiResponse<OrderResponse>> getAll(
			@Parameter(description = "Başlanğıc index") @PathVariable("begin") Integer begin,
			@Parameter(description = "Gətiriləcək element sayı") @PathVariable("length") Integer length) {
		try {
			OrderResponse resp = service.getAll(begin, length);
			ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Sifarisler", resp);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			ApiResponse<OrderResponse> response = new ApiResponse<>(false,
					"Sifarisler getirilerken xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@DeleteMapping(path = "/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	@Operation(summary = "Sifarişi sil", description = "Verilmiş ID-yə uyğun sifarişi silir. Bu əməliyyatı yalnız ADMIN və ya CUSTOMER rolu olan istifadəçilər həyata keçirə bilər.")
	public ResponseEntity<?> deleteById(
			@Parameter(description = "Silinəcək sifarişin ID-si") @PathVariable("id") UUID id) {
		try {
			service.deleteById(id);
			ApiResponse<String> response = new ApiResponse<>(true, "Sifariş uğurla silindi", null);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<String> response = new ApiResponse<>(false,
					"Məhsul dəyişdirilərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping(path = "/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	@Operation(summary = "Sifarişi yenilə", description = "Verilmiş ID-yə uyğun sifarişin məlumatlarını yeniləyir. Bu əməliyyatı ADMIN və ya CUSTOMER rolu olan istifadəçilər həyata keçirə bilər.")
	public ResponseEntity<ApiResponse<OrderDTO>> update(
			@Parameter(description = "Yenilənəcək sifarişin ID-si") @PathVariable("id") UUID id,
			@Valid @RequestBody OrderUpdateRequest req, BindingResult result) {
		try {
			OrderDTO updateOrder = service.updateOrder(id, req);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş uğurla dəyişdirildi", updateOrder);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş dəyişdirilərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/confirm")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	@Operation(summary = "Sifarişi təsdiqlə", description = "Verilmiş ID-yə uyğun sifarişi təsdiqləyir. Yalnız uyğun statusda olan sifarişlər təsdiqlənə bilər. Bu əməliyyatı ADMIN və ya CUSTOMER rolu olan istifadəçilər yerinə yetirə bilər.")
	public ResponseEntity<ApiResponse<OrderDTO>> confirmOrder(
			@Parameter(description = "Təsdiqlənəcək sifarişin ID-si") @PathVariable("id") UUID id,
			Authentication authentication) {
		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.confirmOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş təsdiqləndi", updatedOrder);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş təsdiqlənərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/ship")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PREPARER')")
	@Operation(summary = "Sifarişi göndər", description = "Verilmiş ID-yə uyğun sifarişi 'shipped' statusuna keçirir. Bu əməliyyatı yalnız ADMIN və ya PREPARER rolu olan istifadəçilər edə bilər.")
	public ResponseEntity<ApiResponse<OrderDTO>> shipOrder(
			@Parameter(description = "Göndəriləcək sifarişin ID-si") @PathVariable("id") UUID id,
			Authentication authentication) {
		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.shipOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş göndərildi (shipped)", updatedOrder);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş göndərilərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/deliver")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PREPARER')")
	@Operation(summary = "Sifarişi çatdır", description = "Verilmiş ID-yə uyğun sifarişi 'delivered' statusuna keçirir. Bu əməliyyatı yalnız ADMIN və ya PREPARER rolu olan istifadəçilər edə bilər.")
	public ResponseEntity<ApiResponse<OrderDTO>> deliverOrder(
			@Parameter(description = "Çatdırılacaq sifarişin ID-si") @PathVariable("id") UUID id,
			Authentication authentication) {
		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.deliverOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş çatdırıldı (delivered)", updatedOrder);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş çatdırılarkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/cancel")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	@Operation(summary = "Sifarişi ləğv et", description = "Verilmiş ID-yə uyğun sifarişi 'cancelled' statusuna keçirir. Yalnız müəyyən statuslarda ləğv edilə bilər. Bu əməliyyatı yalnız ADMIN və ya CUSTOMER rolu olan istifadəçilər yerinə yetirə bilər.")
	public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
			@Parameter(description = "Ləğv ediləcək sifarişin ID-si") @PathVariable("id") UUID id,
			Authentication authentication) {
		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.cancelOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş ləğv olundu (cancelled)", updatedOrder);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş ləğv olunarkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}
}
