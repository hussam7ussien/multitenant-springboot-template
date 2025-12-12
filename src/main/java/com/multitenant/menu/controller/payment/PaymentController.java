package com.multitenant.menu.controller.payment;

import com.multitenant.menu.api.PaymentApi;
import com.multitenant.menu.model.PaymentSessionResponse;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentController extends AbstractController implements PaymentApi {

    @Override
    public ResponseEntity<PaymentSessionResponse> createPaymentSession(String xTenantID, Integer orderId) {
        logInfo("Creating payment session for order ID: " + orderId);
        return ResponseEntity.ok(new PaymentSessionResponse());
    }
}
