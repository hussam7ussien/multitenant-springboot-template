package com.multitenant.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryGroupDTO {
    private String date; // YYYY-MM-DD
    private List<OrderSummaryDTO> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummaryDTO {
        private Long id;
        private String orderCode;
        private String status;
        private Double total;
        private java.time.LocalDateTime createdAt;
        private List<OrderItemSummaryDTO> items;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OrderItemSummaryDTO {
            private Long productId;
            private Integer quantity;
        }
    }
}


