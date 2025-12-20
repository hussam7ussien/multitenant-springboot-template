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
        private String orderCode;
        private String status;
    }
}

