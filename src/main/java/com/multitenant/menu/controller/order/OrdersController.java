package com.multitenant.menu.controller.order;

import com.multitenant.menu.api.OrdersApi;
import com.multitenant.menu.model.CreateOrderRequest;
import com.multitenant.menu.model.OrderResponse;
import com.multitenant.menu.model.PaymentCompletedRequest;
import com.multitenant.menu.model.PaymentResponse;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrdersController extends AbstractController implements OrdersApi {

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
}
