package com.multitenant.menu.repository;

import com.multitenant.menu.entity.sql.OptionChoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionChoiceRepository extends JpaRepository<OptionChoiceEntity, Long> {
    List<OptionChoiceEntity> findByOptionId(Long optionId);
}
