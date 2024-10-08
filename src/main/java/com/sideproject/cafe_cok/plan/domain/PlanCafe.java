package com.sideproject.cafe_cok.plan.domain;


import com.sideproject.cafe_cok.global.entity.BaseEntity;
import com.sideproject.cafe_cok.plan.domain.enums.MatchType;
import com.sideproject.cafe_cok.cafe.domain.Cafe;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "plan_cafes")
public class PlanCafe extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "plans_id")
    private Plan plan;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cafes_id")
    private Cafe cafe;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;

    @Builder
    public PlanCafe(final Long id,
                    final Plan plan,
                    final Cafe cafe,
                    final MatchType matchType) {

        if(plan != null) changePlan(plan);
        this.id = id;
        this.cafe = cafe;
        this.matchType = matchType;
    }

    private void changePlan(final Plan plan) {
        this.plan = plan;
        plan.getPlanCafes().add(this);
    }
}
