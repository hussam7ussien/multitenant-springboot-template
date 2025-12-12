package com.multitenant.menu.controller.offer;

import com.multitenant.menu.api.OffersApi;
import com.multitenant.menu.model.Offer;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OffersController extends AbstractController implements OffersApi {

    @Override
    public ResponseEntity<List<Offer>> getOffers(String xTenantID) {
        logInfo("Fetching all offers for tenant: " + xTenantID);
        return ResponseEntity.ok(new ArrayList<>());
    }
}
