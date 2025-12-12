package com.multitenant.menu.controller.reservation;

import com.multitenant.menu.api.ReservationsApi;
import com.multitenant.menu.model.CreateReservationRequest;
import com.multitenant.menu.model.PaymentCompletedRequest;
import com.multitenant.menu.model.PaymentResponse;
import com.multitenant.menu.model.ReservationResponse;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ReservationsController extends AbstractController implements ReservationsApi {

    @Override
    public ResponseEntity<ReservationResponse> createReservation(String xTenantID, CreateReservationRequest createReservationRequest) {
        logInfo("Creating new reservation for tenant: " + xTenantID);
        return ResponseEntity.ok(new ReservationResponse());
    }

    @Override
    public ResponseEntity<PaymentResponse> reservationPaymentCompleted(String xTenantID, PaymentCompletedRequest paymentCompletedRequest) {
        logInfo("Marking reservation payment as completed");
        return ResponseEntity.ok(new PaymentResponse());
    }
}
