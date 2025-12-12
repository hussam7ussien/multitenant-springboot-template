package com.multitenant.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for fetching order details by order code (for restaurant staff scanning QR)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private Long orderId;
    private String orderCode;
    private String orderMode;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private List<OrderItemDTO> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private String variations;  // JSON string of variations/options
    }
}
