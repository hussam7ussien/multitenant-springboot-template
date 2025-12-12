package com.multitenant.menu.controller.order;

import com.multitenant.menu.api.OrdersApi;
import com.multitenant.menu.dto.SignupWithOrderRequest;
import com.multitenant.menu.dto.SignupWithOrderResponse;
import com.multitenant.menu.dto.OrderDetailsResponse;
import com.multitenant.menu.model.CreateOrderRequest;
import com.multitenant.menu.model.OrderResponse;
import com.multitenant.menu.model.PaymentCompletedRequest;
import com.multitenant.menu.model.PaymentResponse;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.services.OrderSignupService;
import com.multitenant.menu.entity.sql.OrderEntity;
import com.multitenant.menu.repository.sql.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrdersController extends AbstractController implements OrdersApi {
    
    private final OrderSignupService orderSignupService;
    private final OrderRepository orderRepository;

    @Override
    public ResponseEntity<OrderResponse> cancelOrder(String xTenantID, Integer id) {
        logInfo("Cancelling order ID: " + id);
        return ResponseEntity.ok(new OrderResponse());
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(String xTenantID, CreateOrderRequest createOrderRequest) {
        logInfo("Creating new order for tenant: " + xTenantID);
        return ResponseEntity.ok(new OrderResponse());
    }

    @Override
    public ResponseEntity<OrderResponse> getOrder(String xTenantID, Integer orderId) {
        logInfo("Fetching order ID: " + orderId);
        return ResponseEntity.ok(new OrderResponse());
    }

    @Override
    public ResponseEntity<PaymentResponse> paymentCompleted(String xTenantID, PaymentCompletedRequest paymentCompletedRequest) {
        logInfo("Marking payment as completed for order");
        return ResponseEntity.ok(new PaymentResponse());
    }
    
    /**
     * Sign up a new user and create an order in one request
     * POST /api/v1/orders/signup
     */
    @PostMapping("/orders/signup")
    public ResponseEntity<SignupWithOrderResponse> signupAndCreateOrder(
            @RequestHeader("X-Tenant-ID") String xTenantID,
            @RequestBody SignupWithOrderRequest request) {
        logInfo("Processing signup with order for tenant: " + xTenantID);
        SignupWithOrderResponse response = orderSignupService.signupAndCreateOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get order details by order code (public endpoint for restaurant staff scanning QR)
     * GET /api/v1/orders/code/{orderCode}
     */
    @GetMapping("/orders/code/{orderCode}")
    public ResponseEntity<OrderDetailsResponse> getOrderByCode(
            @PathVariable String orderCode) {
        logInfo("Fetching order details for code: " + orderCode);
        
        var order = orderRepository.findByOrderCode(orderCode);
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        OrderEntity orderEntity = order.get();
        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setOrderId(orderEntity.getId());
        response.setOrderCode(orderEntity.getOrderCode());
        response.setOrderMode(orderEntity.getOrderMode());
        response.setStatus(orderEntity.getStatus());
        response.setCreatedAt(orderEntity.getCreatedAt());
        response.setTotal(orderEntity.getTotal());
        
        // Map order items
        response.setItems(orderEntity.getItems().stream().map(item -> 
            new OrderDetailsResponse.OrderItemDTO(
                item.getProduct() != null ? item.getProduct().getName() : "Unknown Product",
                item.getQuantity(),
                item.getProduct() != null ? item.getProduct().getPrice() : null,
                item.getVariations()
            )
        ).toList());
        
        return ResponseEntity.ok(response);
    }
}

