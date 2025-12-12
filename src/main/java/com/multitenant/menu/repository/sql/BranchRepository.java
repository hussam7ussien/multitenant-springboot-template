package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
}

