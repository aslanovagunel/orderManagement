package com.app.yolla.modules.market.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarketAddRequest {
	@NotBlank(message = "Market adı boş ola bilməz")
	private String name;

	@NotBlank(message = "Ünvan boş ola bilməz")
	private String address;
}
