package com.multitenant.menu.controller.organization;

import com.multitenant.menu.api.OrganizationsApi;
import com.multitenant.menu.model.OrganizationResponse;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrganizationsController extends AbstractController implements OrganizationsApi {

    @Override
    public ResponseEntity<OrganizationResponse> getOrganizationBySlug(String xTenantID, String slug) {
        logInfo("Fetching organization by slug: " + slug);
        return ResponseEntity.ok(new OrganizationResponse());
    }
}
