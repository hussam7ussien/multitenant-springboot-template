package com.multitenant.menu.services;

import com.multitenant.menu.dto.SignupWithOrderRequest;
import com.multitenant.menu.dto.SignupWithOrderResponse;
import com.multitenant.menu.entity.sql.UserEntity;
import com.multitenant.menu.entity.sql.OrderEntity;
import com.multitenant.menu.entity.sql.OrderItemEntity;
import com.multitenant.menu.entity.sql.ProductEntity;
import com.multitenant.menu.exception.DuplicateEmailException;
import com.multitenant.menu.repository.sql.UserRepository;
import com.multitenant.menu.repository.sql.OrderRepository;
import com.multitenant.menu.repository.sql.ProductRepository;
import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSignupService {
    
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Sign up a new user and create their order in one transaction
     */
    @Transactional
    public SignupWithOrderResponse signupAndCreateOrder(SignupWithOrderRequest request) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Processing signup with order for tenant: {}", tenantId);
        
        // 0. Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Attempted signup with duplicate email: {} for tenant: {}", request.getEmail(), tenantId);
            throw new DuplicateEmailException(request.getEmail());
        }
        
        // 1. Create new user
        UserEntity newUser = createUser(request);
        userRepository.save(newUser);
        log.info("User created: {} for tenant: {}", newUser.getId(), tenantId);
        
        // 2. Create order for the user
        OrderEntity order = createOrder(newUser, request);
        orderRepository.save(order);
        log.info("Order created: {} for user: {}", order.getId(), newUser.getId());
        
        // 3. Build response
        SignupWithOrderResponse response = new SignupWithOrderResponse();
        response.setStatus(200);
        response.setMessage("User registered and order created successfully");
        response.setOrderId(order.getId());
        response.setOrderCode(order.getOrderCode());
        
        // Map user data to response
        SignupWithOrderResponse.UserDTO userDTO = new SignupWithOrderResponse.UserDTO();
        userDTO.setId(newUser.getId());
        userDTO.setUsername(newUser.getUsername());
        userDTO.setEmail(newUser.getEmail());
        userDTO.setDisplayName(newUser.getDisplayName());
        userDTO.setPhone(newUser.getPhone());
        userDTO.setGender(newUser.getGender());
        userDTO.setBirthday(newUser.getBirthday());
        
        response.setUser(userDTO);
        
        return response;
    }
    
    /**
     * Create a new UserEntity from signup request
     */
    private UserEntity createUser(SignupWithOrderRequest request) {
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDisplayName(request.getName());
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        
        // Generate username from email
        String username = request.getEmail().split("@")[0] + "_" + System.currentTimeMillis();
        user.setUsername(username);
        
        // Generate a default password (user should change it later)
        String defaultPassword = generateDefaultPassword();
        user.setPassword(passwordEncoder.encode(defaultPassword));
        
        user.setVerified(false);
        user.setOtp(generateOtp());
        user.setCreatedAt(LocalDateTime.now());
        
        return user;
    }
    
    /**
     * Create OrderEntity from request
     */
    private OrderEntity createOrder(UserEntity user, SignupWithOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setCustomer(user);
        order.setOrderCode(generateOrderCode());
        order.setOrderMode(request.getOrderMode());
        
        // Set financial details
        order.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : BigDecimal.ZERO);
        order.setVat(request.getVat() != null ? request.getVat() : BigDecimal.ZERO);
        order.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        order.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
        order.setCoversFee(request.getCoversFee() != null ? request.getCoversFee() : BigDecimal.ZERO);
        
        order.setStatus("pending");
        order.setCreatedAt(LocalDateTime.now());
        
        // Store payment method
        if (request.getPaymentMethod() != null) {
            order.setPaymentMethod(request.getPaymentMethod().getSlug());
        }
        
        // Store promo code if provided
        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            order.setPromoCode(request.getPromoCode());
        }
        
        // Create order items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<OrderItemEntity> items = new ArrayList<>();
            for (SignupWithOrderRequest.OrderItemDTO itemDTO : request.getItems()) {
                OrderItemEntity item = new OrderItemEntity();
                item.setOrder(order);
                item.setQuantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1);
                
                // Store variations and options as JSON string
                StringBuilder variationsJson = new StringBuilder();
                if (itemDTO.getOptions() != null) {
                    variationsJson.append(itemDTO.getOptions().toString());
                }
                if (itemDTO.getVariations() != null) {
                    variationsJson.append(itemDTO.getVariations().toString());
                }
                
                if (variationsJson.length() > 0) {
                    item.setVariations(variationsJson.toString());
                }
                
                items.add(item);
            }
            order.setItems(items);
        }
        
        return order;
    }
    
    private String generateOrderCode() {
        return "ORD-" + System.currentTimeMillis() + "-" + new Random().nextInt(9999);
    }
    
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
    
    private String generateDefaultPassword() {
        Random random = new Random();
        return "Temp" + random.nextInt(100000);
    }
}
