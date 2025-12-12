package com.multitenant.menu.services;

import com.multitenant.menu.entity.sql.BranchEntity;
import com.multitenant.menu.entity.sql.CouponEntity;
import com.multitenant.menu.repository.sql.BranchRepository;
import com.multitenant.menu.repository.sql.CouponRepository;
import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {
    private final BranchRepository branchRepository;
    private final CouponRepository couponRepository;

    /**
     * Fetch all branches for the current tenant from their respective database
     * The tenant is resolved from TenantContext which is set by the TenantFilter
     */
    public List<BranchEntity> getAllBranches() {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching all branches for tenant: {}", tenantId);
        return branchRepository.findAll();
    }

    /**
     * Fetch a branch by ID for the current tenant
     */
    public Optional<BranchEntity> getBranchById(Long id) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching branch {} for tenant: {}", id, tenantId);
        return branchRepository.findById(id);
    }

    /**
     * Create a new branch for the current tenant
     */
    public BranchEntity createBranch(BranchEntity branch) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Creating branch for tenant: {}", tenantId);
        return branchRepository.save(branch);
    }

    /**
     * Update an existing branch for the current tenant
     */
    public BranchEntity updateBranch(Long id, BranchEntity branchDetails) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Updating branch {} for tenant: {}", id, tenantId);
        
        Optional<BranchEntity> existingBranch = branchRepository.findById(id);
        if (existingBranch.isPresent()) {
            BranchEntity branch = existingBranch.get();
            if (branchDetails.getName() != null) {
                branch.setName(branchDetails.getName());
            }
            if (branchDetails.getAddress() != null) {
                branch.setAddress(branchDetails.getAddress());
            }
            if (branchDetails.getCity() != null) {
                branch.setCity(branchDetails.getCity());
            }
            if (branchDetails.getArea() != null) {
                branch.setArea(branchDetails.getArea());
            }
            if (branchDetails.getPhone() != null) {
                branch.setPhone(branchDetails.getPhone());
            }
            return branchRepository.save(branch);
        }
        throw new IllegalArgumentException("Branch not found with id: " + id);
    }

    /**
     * Delete a branch for the current tenant
     */
    public void deleteBranch(Long id) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Deleting branch {} for tenant: {}", id, tenantId);
        branchRepository.deleteById(id);
    }

    /**
     * Get all valid coupons (promo codes) for the current tenant
     */
    public List<CouponEntity> getAllPromoCodes() {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching all promo codes for tenant: {}", tenantId);
        return couponRepository.findAll();
    }

    /**
     * Get welcome coupon for the current tenant
     */
    public Optional<CouponEntity> getWelcomeCoupon() {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching welcome coupon for tenant: {}", tenantId);
        return couponRepository.findByIsWelcomeCouponTrue();
    }
}
