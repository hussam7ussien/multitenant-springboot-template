package com.multitenant.menu.controller.order;

import com.multitenant.menu.api.OrdersApi;
import com.multitenant.menu.model.*;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.services.OrderSignupService;
import com.multitenant.menu.repository.sql.OrderRepository;
import com.multitenant.menu.repository.sql.ProductRepository;
import com.multitenant.menu.dto.OrderHistoryGroupDTO;
import com.multitenant.menu.services.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrdersController extends AbstractController implements OrdersApi {
    private final OrderSignupService orderSignupService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderHistoryService orderHistoryService;

    // ... existing endpoints ...

    /**
     * Sign up and create order
     * POST /api/v1/orders/signup
     */
    @Override
    public ResponseEntity<SignupWithOrderResponse> signupAndCreateOrder(
            String xTenantID,
            SignupWithOrderRequest signupWithOrderRequest
    ) {
        return ResponseEntity.ok(orderSignupService.signupAndCreateOrder(signupWithOrderRequest));
    }

    /**
     * Get paginated past orders (grouped by date) for current user
     * GET /api/v1/orders/history
     */
    @Override
    public ResponseEntity<GetPastOrdersHistory200Response> getPastOrdersHistory(
            String xTenantID,
            Integer page,
            Integer size
    ) {
        // Get user id from the current security principal
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Convert page and size to Pageable (default size to 10 if not provided)
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNum, pageSize);

        Page<OrderHistoryGroupDTO> result = orderHistoryService.getOrderHistoryByDate(userId, pageable);

        // Convert DTOs to model classes
        List<OrderHistoryGroup> content = result.getContent().stream()
            .map(dto -> {
                OrderHistoryGroup group = new OrderHistoryGroup();
                group.setDate(LocalDate.parse(dto.getDate()));
                List<OrderResponse> orders = dto.getOrders().stream()
                    .map(summary -> {
                        OrderResponse orderResponse = new OrderResponse();
                        orderResponse.setStatus(summary.getStatus());
                        return orderResponse;
                    })
                    .toList();
                group.setOrders(orders);
                return group;
            })
            .toList();

        // Convert to GetPastOrdersHistory200Response
        GetPastOrdersHistory200Response response = new GetPastOrdersHistory200Response();
        response.setContent(content);
        response.setPageable(result.getPageable());
        response.setTotalPages(result.getTotalPages());
        response.setTotalElements((int) result.getTotalElements());

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        // Get user from security context
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof com.multitenant.menu.entity.sql.UserEntity userEntity) {
            return userEntity.getId();
        } else if (principal instanceof UserDetails userDetails) {
            // If you store username as email, fetch user by username here
            // return userRepository.findByUsername(userDetails.getUsername()).map(UserEntity::getId).orElse(null);
            return null;
        }
        return null;
    }

    // ... rest of the class ...
}
