package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {
    Optional<OrganizationEntity> findBySlug(String slug);
}

