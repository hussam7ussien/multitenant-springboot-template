package com.multitenant.menu.services;

import com.multitenant.menu.entity.mongo.LogEntry;
import com.multitenant.menu.entity.sql.CustomerEntity;
import com.multitenant.menu.repository.mongo.TenantAwareMongoTemplate;
import com.multitenant.menu.repository.sql.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final TenantAwareMongoTemplate tenantAwareMongoTemplate;

    public CustomerService(CustomerRepository customerRepository, TenantAwareMongoTemplate tenantAwareMongoTemplate) {
        this.customerRepository = customerRepository;
        this.tenantAwareMongoTemplate = tenantAwareMongoTemplate;
    }

    public CustomerEntity createCustomer(String tenantId, String name){
        CustomerEntity customer = new CustomerEntity();
        customer.setName(name);
        customer = customerRepository.save(customer);

        LogEntry log =  LogEntry.builder()
                .message("Customer created: "+ customer.getName())
                .timestamp(Instant.now().toEpochMilli())
                .build();

        tenantAwareMongoTemplate.saveForTenant(tenantId,"public","logs", log);

        return customer;
    }
}
