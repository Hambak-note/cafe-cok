package com.sideproject.cafe_cok.plan.domain.repository;

import com.sideproject.cafe_cok.plan.domain.Plan;
import com.sideproject.cafe_cok.plan.exception.NoSuchPlanException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryCustom {

    List<Plan> findByMemberId(final Long memberId);

    default List<Plan> findMatchingPlan(final Plan plan) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "isSaved", "isShared", "createdDate", "lastModifiedDate")
                .withIncludeNullValues()
                .withIgnoreCase();

        Example<Plan> example = Example.of(plan, matcher);
        return findAll(example);
    }

    default Plan getById(final Long id) {
        return findById(id)
                .orElseThrow(NoSuchPlanException::new);
    }


}
