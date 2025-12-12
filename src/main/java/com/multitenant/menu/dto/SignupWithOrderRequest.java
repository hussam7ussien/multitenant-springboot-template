package com.multitenant.menu.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for signup with order creation request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupWithOrderRequest {
    
    // Customer Information
    private String name;
    private String email;
    private String phone;
    private String birthday;
    private String gender;
    
    // Order Information
    private String orderMode; // eat-in, pickup, delivery_order
    private List<OrderItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal vat;
    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal specialDiscount;
    private BigDecimal coversFee;
    
    // Payment Info
    private PaymentMethodDTO paymentMethod;
    
    // Additional Order Details
    private String note;
    private String promoCode;
    private String deviceToken;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal extrasPrice;
        private String title;
        private String note;
        private Map<String, Object> options;
        private List<Map<String, Object>> variations;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodDTO {
        private String slug;
        private String title;
        private String icon;
    }
}
