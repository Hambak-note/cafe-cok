package com.sideproject.hororok.plan.domain.repository;

import com.sideproject.hororok.cafe.domain.Cafe;
import com.sideproject.hororok.plan.domain.PlanCafe;
import com.sideproject.hororok.plan.domain.enums.PlanCafeMatchType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanCafeRepository extends JpaRepository<PlanCafe, Long> {


    List<PlanCafe> findByPlanId(Long planId);

    void deleteByPlanId(Long planId);

    List<PlanCafe> findByPlanIdAndMatchType(Long planId, PlanCafeMatchType matchType);
}