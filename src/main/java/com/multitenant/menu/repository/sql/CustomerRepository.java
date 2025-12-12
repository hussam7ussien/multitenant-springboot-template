package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
