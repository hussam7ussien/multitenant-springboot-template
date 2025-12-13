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
import com.multitenant.menu.entity.sql.OrderItemEntity;
import com.multitenant.menu.entity.sql.ProductEntity;
import com.multitenant.menu.repository.sql.OrderRepository;
import com.multitenant.menu.repository.sql.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.multitenant.menu.util.QrCodeGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrdersController extends AbstractController implements OrdersApi {
    
    private final OrderSignupService orderSignupService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public ResponseEntity<OrderResponse> cancelOrder(String xTenantID, Integer id) {
        logInfo("Cancelling order ID: " + id);
        return ResponseEntity.ok(new OrderResponse());
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(String xTenantID, CreateOrderRequest createOrderRequest) {
        logInfo("Creating new order for tenant: " + xTenantID);
        
        // Validate request
        if (createOrderRequest.getItems() == null || createOrderRequest.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            logInfo("CreateOrderRequest received - items: " + (createOrderRequest.getItems() != null ? createOrderRequest.getItems().size() : "null") + 
                    ", orderMode: " + createOrderRequest.getOrderMode());
            
            // Create the order
            OrderEntity order = new OrderEntity();
            String orderMode = createOrderRequest.getOrderMode() != null ? 
                createOrderRequest.getOrderMode().toString() : "eat-in";
            logInfo("Order mode: " + orderMode);
            order.setOrderMode(orderMode);
            order.setDeliveryAddress(createOrderRequest.getDeliveryAddress());
            order.setPromoCode(createOrderRequest.getCouponCode());
            order.setStatus("pending");
            // createdAt is set automatically by @Column(nullable = false)
            
            // Generate unique order code
            String orderCode = generateOrderCode();
            order.setOrderCode(orderCode);
            logInfo("Generated order code: " + orderCode);
            
            // Create order items and calculate total
            List<OrderItemEntity> items = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            
            for (var itemRequest : createOrderRequest.getItems()) {
                logInfo("Processing item: productId=" + itemRequest.getProductId() + ", quantity=" + itemRequest.getQuantity());
                
                // ProductEntity ID is Long, but itemRequest.getProductId() returns Integer
                Long productId = itemRequest.getProductId() != null ? 
                    Long.valueOf(itemRequest.getProductId()) : null;
                    
                if (productId == null) {
                    throw new IllegalArgumentException("Product ID is required");
                }
                
                ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));
                
                OrderItemEntity item = new OrderItemEntity();
                item.setProduct(product);
                item.setQuantity(itemRequest.getQuantity());
                item.setVariations(itemRequest.getVariations() != null ? 
                    itemRequest.getVariations().toString() : "");
                item.setOrder(order);
                
                items.add(item);
                
                // Calculate total
                BigDecimal itemTotal = product.getPrice()
                    .multiply(new BigDecimal(itemRequest.getQuantity()));
                total = total.add(itemTotal);
            }
            
            order.setItems(items);
            order.setTotal(total);
            
            // Save order
            OrderEntity savedOrder = orderRepository.save(order);
            logInfo("Order created successfully with ID: " + savedOrder.getId());
            
            // Generate QR code for eat-in orders
            String qrCodeUrl = null;
            if ("eat-in".equalsIgnoreCase(savedOrder.getOrderMode())) {
                try {
                    QrCodeGenerator qrGenerator = new QrCodeGenerator();
                    qrCodeUrl = qrGenerator.generateQrCodeAsBase64(savedOrder.getOrderCode());
                    savedOrder.setQrCodeUrl(qrCodeUrl);
                    orderRepository.save(savedOrder);
                    logInfo("QR code generated for order: " + savedOrder.getOrderCode());
                } catch (Exception e) {
                    logError("Failed to generate QR code: " + e.getMessage(), e);
                }
            }
            
            // Build response - return a Map as response body since OrderResponse doesn't have orderCode/qrCodeUrl
            // Jackson will serialize the Map as JSON response
            @SuppressWarnings("unchecked")
            ResponseEntity<OrderResponse> result = (ResponseEntity<OrderResponse>) (Object) ResponseEntity.ok()
                .body(createResponseMap(savedOrder, createOrderRequest, qrCodeUrl));
            
            return result;
        } catch (Exception e) {
            logError("Error creating order: " + e.getMessage(), e);
            @SuppressWarnings("unchecked")
            ResponseEntity<OrderResponse> error = (ResponseEntity<OrderResponse>) (Object) ResponseEntity.badRequest().build();
            return error;
        }
    }
    
    /**
     * Create response map with order details including orderCode and qrCodeUrl
     */
    private Map<String, Object> createResponseMap(OrderEntity order, CreateOrderRequest request, String qrCodeUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId().intValue());
        map.put("status", order.getStatus());
        map.put("total", order.getTotal().doubleValue());
        map.put("items", request.getItems());
        map.put("orderCode", order.getOrderCode());
        map.put("orderMode", order.getOrderMode());
        map.put("qrCodeUrl", qrCodeUrl);
        return map;
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
     * Get order details by order ID (query parameter) or order code
     * GET /api/v1/orders?order_id={orderId or orderCode}
     * 
     * Accepts both:
     * - Numeric database IDs: /api/v1/orders?order_id=1
     * - Order codes: /api/v1/orders?order_id=ORD-1765635963124-5829
     */
    @GetMapping("/orders")
    public ResponseEntity<OrderDetailsResponse> getOrderByIdQuery(
            @RequestParam(value = "order_id", required = false) String orderIdOrCode) {
        logInfo("Fetching order by ID/code query: " + orderIdOrCode);
        
        if (orderIdOrCode == null || orderIdOrCode.trim().isEmpty()) {
            logInfo("order_id parameter is required");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // First, try to parse as Long (database ID) if it's purely numeric
            if (orderIdOrCode.matches("\\d+")) {
                try {
                    Long id = Long.parseLong(orderIdOrCode);
                    var order = orderRepository.findById(id);
                    
                    if (order.isPresent()) {
                        logInfo("Order found by database ID: " + id);
                        return ResponseEntity.ok(buildOrderDetailsResponse(order.get()));
                    }
                } catch (NumberFormatException e) {
                    logInfo("Could not parse as Long, will try as order code: " + orderIdOrCode);
                }
            }
            
            // Try as order code (alphanumeric format like ORD-1765635963124-5829)
            var orderByCode = orderRepository.findByOrderCode(orderIdOrCode);
            if (orderByCode.isPresent()) {
                logInfo("Order found by code: " + orderIdOrCode);
                return ResponseEntity.ok(buildOrderDetailsResponse(orderByCode.get()));
            }
            
            logInfo("Order not found with ID or code: " + orderIdOrCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new OrderDetailsResponse()); // Return empty response with 404 status
        } catch (Exception e) {
            logError("Error fetching order: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
        
        return ResponseEntity.ok(buildOrderDetailsResponse(order.get()));
    }
    
    /**
     * Build OrderDetailsResponse from OrderEntity
     */
    private OrderDetailsResponse buildOrderDetailsResponse(OrderEntity orderEntity) {
        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setOrderId(orderEntity.getId());
        response.setOrderCode(orderEntity.getOrderCode());
        response.setOrderMode(orderEntity.getOrderMode());
        response.setStatus(orderEntity.getStatus());
        response.setCreatedAt(orderEntity.getCreatedAt());
        response.setTotal(orderEntity.getTotal());
        
        // Map order items
        if (orderEntity.getItems() != null && !orderEntity.getItems().isEmpty()) {
            response.setItems(orderEntity.getItems().stream().map(item -> 
                new OrderDetailsResponse.OrderItemDTO(
                    item.getProduct() != null ? item.getProduct().getName() : "Unknown Product",
                    item.getQuantity(),
                    item.getProduct() != null ? item.getProduct().getPrice() : null,
                    item.getVariations()
                )
            ).toList());
        }
        
        return response;
    }
    
    /**
     * Generate a unique order code
     */
    private String generateOrderCode() {
        Random random = new Random();
        long timestamp = System.currentTimeMillis();
        int randomNum = random.nextInt(10000);
        return String.format("ORD-%d-%d", timestamp, randomNum);
    }
}

