package com.sideproject.cafe_cok.common.fixtures;

import com.sideproject.cafe_cok.plan.domain.Plan;
import com.sideproject.cafe_cok.plan.domain.enums.MatchType;
import com.sideproject.cafe_cok.plan.dto.PlanKeywordDto;
import com.sideproject.cafe_cok.plan.dto.request.CreatePlanRequest;
import com.sideproject.cafe_cok.plan.dto.request.SavePlanRequest;
import com.sideproject.cafe_cok.plan.dto.request.SharePlanRequest;
import com.sideproject.cafe_cok.plan.dto.response.CreatePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.DeletePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.SavePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.SharePlanResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static com.sideproject.cafe_cok.common.fixtures.CafeFixtures.*;
import static com.sideproject.cafe_cok.common.fixtures.CafeFixtures.카페_DTO_리스트;
import static com.sideproject.cafe_cok.common.fixtures.KeywordFixtures.*;
import static com.sideproject.cafe_cok.common.fixtures.MemberFixtures.사용자;

public class PlanFixtures {

    public static final Long 계획_ID = 1L;
    public static final String 위치_이름 = "위치 이름";
    public static final BigDecimal 계획_위도 = getRandomBigDecimal(0, 90);
    public static final BigDecimal 계획_경도 = getRandomBigDecimal(0, 180);
    public static final Integer 도보_시간 = 10;
    public static final LocalDate 날짜 = LocalDate.now();
    public static final LocalTime 시작_시간 = LocalTime.of(9, 0);
    public static final LocalTime 끝_시간 = LocalTime.of(11, 0);
    public static final String 계획_일자_시간 = "00월 00일 00시 00분";

    public static Plan 계획() {
        Plan plan = new Plan(사용자(), 위치_이름, 날짜, 시작_시간, 끝_시간, 도보_시간, MatchType.SIMILAR, true, true);
        setId(plan, 계획_ID);
        return plan;
    }

    public static PlanKeywordDto 마이페이지_계획_DTO() {
        return new PlanKeywordDto(계획_ID, 위치_이름, 날짜, 시작_시간, 키워드_이름);
    }

    public static CreatePlanRequest 계획_요청() {
        return new CreatePlanRequest(
                위치_이름, 계획_위도, 계획_경도, 도보_시간, 날짜, 시작_시간, 끝_시간, 키워드_이름_리스트);
    }

    public static CreatePlanResponse 계획_응답() {

        return CreatePlanResponse.builder()
                .planId(계획_ID)
                .matchType(MatchType.MISMATCH)
                .locationName(위치_이름)
                .minutes(도보_시간)
                .visitDateTime(계획_일자_시간)
                .categoryKeywords(카테고리_키워드_DTO())
                .recommendCafes(카페_DTO_리스트())
                .similarCafes(Collections.emptyList())
                .matchCafes(Collections.emptyList())
                .build();
    }

    public static SavePlanRequest 계획_저장_요청() {
        return new SavePlanRequest(계획_ID);
    }

    public static SavePlanResponse 계획_저장_응답() {
        return new SavePlanResponse(계획_ID);
    }

    public static SharePlanRequest 계획_공유_요청() {
        return new SharePlanRequest(계획_ID);
    }

    public static SharePlanResponse 계획_공유_응답() {
        return new SharePlanResponse(계획_ID);
    }

    public static DeletePlanResponse 계획_삭제_응답() {
        return new DeletePlanResponse(계획_ID);
    }

    public static Plan setId(Plan plan, final Long id) {

        try {
            Field idField = Plan.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(plan, id);
            return plan;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }



}
