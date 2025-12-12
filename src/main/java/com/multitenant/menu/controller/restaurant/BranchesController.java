package com.multitenant.menu.controller.restaurant;

import com.multitenant.menu.api.BranchesApi;
import com.multitenant.menu.model.Branch;
import com.multitenant.menu.model.CitiesAndAreasResponse;
import com.multitenant.menu.model.PromoCode;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.dto.BranchesResponse;
import com.multitenant.menu.services.BranchService;
import com.multitenant.menu.mapper.BranchMapper;
import com.multitenant.menu.mapper.CouponMapper;
import com.multitenant.menu.entity.sql.BranchEntity;
import com.multitenant.menu.entity.sql.CouponEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BranchesController extends AbstractController implements BranchesApi {
    
    private final BranchService branchService;
    private final BranchMapper branchMapper;
    private final CouponMapper couponMapper;

    @Override
    public ResponseEntity<List<Branch>> getBranches(String xTenantID) {
        logInfo("Fetching all branches for tenant: " + xTenantID);
        List<BranchEntity> branchEntities = branchService.getAllBranches();
        List<Branch> branches = branchEntities.stream()
                .map(branchMapper::toApiModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(branches);
    }

    @GetMapping("/branches")
    public ResponseEntity<BranchesResponse> getBranchesWithMetadata(@RequestParam(required = false) String xTenantID) {
        logInfo("Fetching all branches with metadata for tenant: " + xTenantID);
        
        List<BranchEntity> branchEntities = branchService.getAllBranches();
        List<Branch> branches = branchEntities.stream()
                .map(branchMapper::toApiModel)
                .collect(Collectors.toList());
        
        List<CouponEntity> couponEntities = branchService.getAllPromoCodes();
        List<PromoCode> promoCodes = couponEntities.stream()
            .map(couponMapper::toApiModel)
            .collect(Collectors.toList());
        
        Optional<CouponEntity> welcomeCoupon = branchService.getWelcomeCoupon();
        String welcomeCode = welcomeCoupon.map(CouponEntity::getCode).orElse(null);
        
        BranchesResponse response = new BranchesResponse();
        response.setBranches(branches);
        response.setPromo_codes(promoCodes);
        response.setWelcome_code(welcomeCode);
        response.setAllow_cancel_after(3600);
        response.setVerification_status("verified");
        response.setOrganization_discount("10%");
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CitiesAndAreasResponse> getCitiesAndAreas(String xTenantID) {
        logInfo("Fetching cities and areas for tenant: " + xTenantID);
        return ResponseEntity.ok(new CitiesAndAreasResponse());
    }
}
