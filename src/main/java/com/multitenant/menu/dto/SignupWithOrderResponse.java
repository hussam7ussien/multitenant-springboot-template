package com.multitenant.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for signup with order creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupWithOrderResponse {
    private int status;
    private String message;
    private UserDTO user;
    private Long orderId;
    private String orderCode;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
        private String displayName;
        private String phone;
        private String gender;
        private String birthday;
    }
}
