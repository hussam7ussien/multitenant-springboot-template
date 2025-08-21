package com.multitenant.template.services;

import com.multitenant.template.entity.mongo.LogEntry;
import com.multitenant.template.entity.sql.Customer;
import com.multitenant.template.repository.mongo.TenantAwareMongoTemplate;
import com.multitenant.template.repository.sql.CustomerRepository;
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

    public Customer createCustomer(String tenantId, String name){
        Customer customer = new Customer();
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
