package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    List<PricingRule> findByActiveTrue();
}

