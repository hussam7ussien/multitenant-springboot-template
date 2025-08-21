package com.multitenant.template.repository.sql;

import com.multitenant.template.entity.sql.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
