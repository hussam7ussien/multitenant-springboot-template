package com.multitenant.template.controller;

import com.example.api.CustomersApi;
import com.example.model.CustomerResponse;
import com.example.model.RegisterCustomerRequest;
import com.multitenant.template.entity.sql.Customer;
import com.multitenant.template.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController implements CustomersApi {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<CustomerResponse> registerCustomer(String xTenantID, RegisterCustomerRequest registerCustomerRequest) {
        Customer customer = customerService.createCustomer(xTenantID, registerCustomerRequest.getName());
        return ResponseEntity.ok(
                new CustomerResponse()
                        .id(customer.getId())
                        .name(customer.getName())
        );
    }
}
