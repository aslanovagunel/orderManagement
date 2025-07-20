package com.app.yolla.modules.market.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketDTO {
	private UUID id;
	private String name;
	private String address;
}
