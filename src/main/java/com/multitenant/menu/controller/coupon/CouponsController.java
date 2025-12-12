package com.multitenant.menu.controller.coupon;

import com.multitenant.menu.api.CouponsApi;
import com.multitenant.menu.model.CouponResponse;
import com.multitenant.menu.model.GetCouponRequest;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CouponsController extends AbstractController implements CouponsApi {

    @Override
    public ResponseEntity<CouponResponse> getCoupon(String xTenantID, GetCouponRequest getCouponRequest) {
        logInfo("Getting coupon details");
        return ResponseEntity.ok(new CouponResponse());
    }

    @Override
    public ResponseEntity<CouponResponse> getWelcomeCoupon(String xTenantID) {
        logInfo("Getting welcome coupon for tenant: " + xTenantID);
        return ResponseEntity.ok(new CouponResponse());
    }
}

