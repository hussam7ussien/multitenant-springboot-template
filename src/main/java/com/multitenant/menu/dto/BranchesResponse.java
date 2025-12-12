package com.multitenant.menu.dto;

import com.multitenant.menu.model.Branch;
import com.multitenant.menu.model.PromoCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchesResponse {
    private List<Branch> branches;
    private List<PromoCode> promo_codes;
    private Integer allow_cancel_after;
    private String welcome_code;
    private String verification_status;
    private String organization_discount;
}
