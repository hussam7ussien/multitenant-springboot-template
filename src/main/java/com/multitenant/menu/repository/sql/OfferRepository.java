package com.multitenant.menu.repository.sql;

import com.multitenant.menu.entity.sql.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {
    List<OfferEntity> findByValidFromBeforeAndValidToAfter(LocalDateTime now1, LocalDateTime now2);
}

