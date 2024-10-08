package com.sideproject.cafe_cok.plan.domain.repository;

import com.sideproject.cafe_cok.config.JpaTestConfig;
import com.sideproject.cafe_cok.keword.domain.Keyword;
import com.sideproject.cafe_cok.keword.domain.enums.Category;
import com.sideproject.cafe_cok.keword.domain.repository.KeywordRepository;
import com.sideproject.cafe_cok.member.domain.Member;
import com.sideproject.cafe_cok.member.domain.repository.MemberRepository;
import com.sideproject.cafe_cok.plan.domain.Plan;
import com.sideproject.cafe_cok.plan.domain.PlanKeyword;
import com.sideproject.cafe_cok.plan.domain.condition.PlanSearchCondition;
import com.sideproject.cafe_cok.plan.domain.enums.PlanSortBy;
import com.sideproject.cafe_cok.plan.domain.enums.PlanStatus;
import com.sideproject.cafe_cok.plan.dto.PlanKeywordDto;
import com.sideproject.cafe_cok.plan.exception.NoSuchPlanException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.sideproject.cafe_cok.constant.TestConstants.*;
import static com.sideproject.cafe_cok.constant.TestConstants.PLAN_MATCH_TYPE;
import static com.sideproject.cafe_cok.util.FormatConverter.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
class PlanRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanKeywordRepository planKeywordRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Test
    void 회원_ID로_계획의_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Plan plan1 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(true)
                .isShared(true)
                .build();
        Plan savedPlan1 = planRepository.save(plan1);

        Plan plan2 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME_2)
                .visitDate(PLAN_VISIT_DATE_2)
                .visitStartTime(PLAN_VISIT_START_TIME_2)
                .visitEndTime(PLAN_VISIT_END_TIME_2)
                .minutes(PLAN_MINUTES_2)
                .matchType(PLAN_MATCH_TYPE_2)
                .isSaved(false)
                .isShared(false)
                .build();
        Plan savedPlan2 = planRepository.save(plan2);

        //when
        List<Plan> findPlans = planRepository.findByMemberId(savedMember.getId());

        //then
        assertThat(findPlans).hasSize(2);
        assertThat(findPlans).extracting("locationName")
                .containsExactlyInAnyOrder(PLAN_LOCATION_NAME, PLAN_LOCATION_NAME_2);
        assertThat(findPlans).extracting("visitDate")
                .containsExactlyInAnyOrder(PLAN_VISIT_DATE, PLAN_VISIT_DATE_2);
        assertThat(findPlans).extracting("visitStartTime")
                .containsExactlyInAnyOrder(PLAN_VISIT_START_TIME, PLAN_VISIT_START_TIME_2);
        assertThat(findPlans).extracting("visitEndTime")
                .containsExactlyInAnyOrder(PLAN_VISIT_END_TIME, PLAN_VISIT_END_TIME_2);
        assertThat(findPlans).extracting("minutes")
                .containsExactlyInAnyOrder(PLAN_MINUTES, PLAN_MINUTES_2);
    }

    @Test
    @DisplayName("id 기반으로 plan을 조회한다.")
    void 계획_ID로_계획을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Plan plan = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan = planRepository.save(plan);

        //when
        Plan findPlan = planRepository.getById(savedPlan.getId());

        //then
        assertThat(findPlan).isEqualTo(savedPlan);
        assertThat(findPlan.getMinutes()).isEqualTo(PLAN_MINUTES);
        assertThat(findPlan.getVisitStartTime()).isEqualTo(PLAN_VISIT_START_TIME);
        assertThat(findPlan.getVisitEndTime()).isEqualTo(PLAN_VISIT_END_TIME);
        assertThat(findPlan.getLocationName()).isEqualTo(PLAN_LOCATION_NAME);
        assertThat(findPlan.getVisitDate()).isEqualTo(PLAN_VISIT_DATE);
    }

    @Test
    @DisplayName("id 기반으로 plan 조회 시 존재하지 않을 경우 에러를 반환한다..")
    void 계획_ID_기반으로_조회시_존재하지_않을_경우_에러를_반환한다() {

        //when & then
        assertThatExceptionOfType(NoSuchPlanException.class)
                .isThrownBy(() -> planRepository.getById(NON_EXISTENT_ID))
                .withMessage("[ID : " + NON_EXISTENT_ID + "] 에 해당하는 계획이 존재하지 않습니다.");
    }

//    @Test
//    @DisplayName("plan 기반 특정 조건을 제외하고 일지하는 plan 리스트를 조회한다.(특정 조건: id, isSaved, isShared, createdDate, lastModifiedDate)")
//    void find_matching_plan() {
//
//        //given
//            Member member = Member.builder()
//                    .email(MEMBER_EMAIL)
//                    .nickname(MEMBER_NICKNAME)
//                    .socialType(MEMBER_SOCIAL_TYPE)
//                    .build();
//        Member savedMember = memberRepository.save(member);
//        Plan plan1 = new Plan(savedMember, PLAN_LOCATION_NAME, PLAN_VISIT_DATE, PLAN_VISIT_START_TIME,
//                PLAN_VISIT_END_TIME, PLAN_MINUTES, PLAN_MATCH_TYPE, PLAN_IS_SAVED_TRUE, PLAN_IS_SHARED_TRUE);
//        Plan plan2 = new Plan(savedMember, PLAN_LOCATION_NAME, PLAN_VISIT_DATE, PLAN_VISIT_START_TIME,
//                PLAN_VISIT_END_TIME, PLAN_MINUTES, PLAN_MATCH_TYPE, PLAN_IS_SAVED_TRUE, PLAN_IS_SHARED_TRUE);
//        Plan savedPlan1 = planRepository.save(plan1);
//        Plan savedPlan2 = planRepository.save(plan2);
//
//        //when
//        List<Plan> findPlans = planRepository.findMatchingPlan(plan1);
//
//        //then
//        assertThat(findPlans).hasSize(2);
//        assertThat(findPlans).extracting("locationName")
//                .containsExactlyInAnyOrder(PLAN_LOCATION_NAME, PLAN_LOCATION_NAME);
//        assertThat(findPlans).extracting("visitDate")
//                .containsExactlyInAnyOrder(PLAN_VISIT_DATE, PLAN_VISIT_DATE);
//        assertThat(findPlans).extracting("visitStartTime")
//                .containsExactlyInAnyOrder(PLAN_VISIT_START_TIME, PLAN_VISIT_START_TIME);
//        assertThat(findPlans).extracting("visitEndTime")
//                .containsExactlyInAnyOrder(PLAN_VISIT_END_TIME, PLAN_VISIT_END_TIME);
//        assertThat(findPlans).extracting("minutes")
//                .containsExactlyInAnyOrder(PLAN_MINUTES, PLAN_MINUTES);
//    }

    @Test
    void 저장된_계획의_계획_키워드_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Keyword keyword = Keyword.builder()
                .name(KEYWORD_NAME)
                .category(Category.PURPOSE)
                .build();
        Keyword savedKeyword = keywordRepository.save(keyword);

        Plan plan1 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan1 = planRepository.save(plan1);

        Plan plan2 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME_2)
                .visitDate(PLAN_VISIT_DATE_2)
                .visitStartTime(PLAN_VISIT_START_TIME_2)
                .visitEndTime(PLAN_VISIT_END_TIME_2)
                .minutes(PLAN_MINUTES_2)
                .matchType(PLAN_MATCH_TYPE_2)
                .isSaved(false)
                .isShared(true)
                .build();
        Plan savedPlan2 = planRepository.save(plan2);

        PlanKeyword planKeyword1 = PlanKeyword.builder()
                .plan(savedPlan1)
                .keyword(savedKeyword)
                .build();
        planKeywordRepository.save(planKeyword1);

        PlanKeyword planKeyword2 = PlanKeyword.builder()
                .plan(savedPlan2)
                .keyword(savedKeyword)
                .build();
        planKeywordRepository.save(planKeyword2);


        Sort sort = Sort.by(Sort.Direction.DESC, PlanSortBy.RECENT.getValue());
        Pageable pageable = PageRequest.of(0, PLAN_PAGE_SIZE, sort);
        PlanSearchCondition searchCondition
                = new PlanSearchCondition(savedMember.getId(), Category.PURPOSE, PlanSortBy.RECENT, PlanStatus.SAVED);

        //when
        List<PlanKeywordDto> findList = planRepository.findPlanKeywords(searchCondition, pageable);

        //then
        assertThat(findList).hasSize(1);
        assertThat(findList).extracting("id").containsExactlyInAnyOrder(savedPlan1.getId());
        assertThat(findList).extracting("location").containsExactlyInAnyOrder(PLAN_LOCATION_NAME);
        assertThat(findList).extracting("visitDateTime")
                .containsExactlyInAnyOrder(convertLocalDateLocalTimeToString(PLAN_VISIT_DATE, PLAN_VISIT_START_TIME));
        assertThat(findList).extracting("keywordName").containsExactlyInAnyOrder(savedKeyword.getName());
    }

    @Test
    void 공유된_계획의_계획_키워드_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Keyword keyword = Keyword.builder()
                .name(KEYWORD_NAME)
                .category(Category.PURPOSE)
                .build();
        Keyword savedKeyword = keywordRepository.save(keyword);

        Plan plan1 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(false)
                .isShared(true)
                .build();
        Plan savedPlan1 = planRepository.save(plan1);

        Plan plan2 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME_2)
                .visitDate(PLAN_VISIT_DATE_2)
                .visitStartTime(PLAN_VISIT_START_TIME_2)
                .visitEndTime(PLAN_VISIT_END_TIME_2)
                .minutes(PLAN_MINUTES_2)
                .matchType(PLAN_MATCH_TYPE_2)
                .isSaved(false)
                .isShared(false)
                .build();
        Plan savedPlan2 = planRepository.save(plan2);

        PlanKeyword planKeyword1 = PlanKeyword.builder()
                .keyword(savedKeyword)
                .plan(savedPlan1)
                .build();
        planKeywordRepository.save(planKeyword1);

        PlanKeyword planKeyword2 = PlanKeyword.builder()
                .keyword(savedKeyword)
                .plan(savedPlan2)
                .build();
        planKeywordRepository.save(planKeyword2);

        Sort sort = Sort.by(Sort.Direction.DESC, PlanSortBy.RECENT.getValue());
        Pageable pageable = PageRequest.of(0, PLAN_PAGE_SIZE, sort);
        PlanSearchCondition searchCondition
                = new PlanSearchCondition(savedMember.getId(), Category.PURPOSE, PlanSortBy.RECENT, PlanStatus.SHARED);

        //when
        List<PlanKeywordDto> findList = planRepository.findPlanKeywords(searchCondition, pageable);

        //then
        assertThat(findList).hasSize(1);
        assertThat(findList).extracting("id").containsExactlyInAnyOrder(savedPlan1.getId());
        assertThat(findList).extracting("location").containsExactlyInAnyOrder(PLAN_LOCATION_NAME);
        assertThat(findList).extracting("visitDateTime")
                .containsExactlyInAnyOrder(convertLocalDateLocalTimeToString(PLAN_VISIT_DATE, PLAN_VISIT_START_TIME));
        assertThat(findList).extracting("keywordName").containsExactlyInAnyOrder(savedKeyword.getName());
    }

    @Test
    void 생성일시_기준으로_내림차순_정렬된_계획_키워드의_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Keyword keyword = Keyword.builder()
                .name(KEYWORD_NAME)
                .category(Category.PURPOSE)
                .build();
        Keyword savedKeyword = keywordRepository.save(keyword);

        Plan plan1 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan1 = planRepository.save(plan1);

        Plan plan2 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME_2)
                .visitDate(PLAN_VISIT_DATE_2)
                .visitStartTime(PLAN_VISIT_START_TIME_2)
                .visitEndTime(PLAN_VISIT_END_TIME_2)
                .minutes(PLAN_MINUTES_2)
                .matchType(PLAN_MATCH_TYPE_2)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan2 = planRepository.save(plan2);

        PlanKeyword planKeyword1 = PlanKeyword.builder()
                .keyword(savedKeyword)
                .plan(savedPlan1)
                .build();
        planKeywordRepository.save(planKeyword1);

        PlanKeyword planKeyword2 = PlanKeyword.builder()
                .keyword(savedKeyword)
                .plan(savedPlan2)
                .build();
        planKeywordRepository.save(planKeyword2);

        Sort sort = Sort.by(Sort.Direction.DESC, PlanSortBy.RECENT.getValue());
        Pageable pageable = PageRequest.of(0, PLAN_PAGE_SIZE, sort);
        PlanSearchCondition searchCondition
                = new PlanSearchCondition(savedMember.getId(), Category.PURPOSE, PlanSortBy.RECENT, PlanStatus.SAVED);

        //when
        List<PlanKeywordDto> findList = planRepository.findPlanKeywords(searchCondition, pageable);

        //then
        assertThat(findList).hasSize(2);
        assertThat(findList).extracting("id").containsExactly(savedPlan2.getId(), savedPlan1.getId());
        assertThat(findList).extracting("location")
                .containsExactlyInAnyOrder(PLAN_LOCATION_NAME, PLAN_LOCATION_NAME_2);
        assertThat(findList).extracting("visitDateTime")
                .containsExactlyInAnyOrder(convertLocalDateLocalTimeToString(PLAN_VISIT_DATE, PLAN_VISIT_START_TIME),
                        convertLocalDateLocalTimeToString(PLAN_VISIT_DATE_2, PLAN_VISIT_START_TIME_2));
    }

    @Test
    void 방문일시_기준_오름차순_정렬된_계획_키워드_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Keyword keyword = Keyword.builder()
                .name(KEYWORD_NAME)
                .category(Category.PURPOSE)
                .build();
        Keyword savedKeyword = keywordRepository.save(keyword);

        Plan plan1 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE_2)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan1 = planRepository.save(plan1);

        Plan plan2 = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME_2)
                .visitDate(PLAN_VISIT_DATE)
                .visitStartTime(PLAN_VISIT_START_TIME_2)
                .visitEndTime(PLAN_VISIT_END_TIME_2)
                .minutes(PLAN_MINUTES_2)
                .matchType(PLAN_MATCH_TYPE_2)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan2 = planRepository.save(plan2);

        PlanKeyword planKeyword1 = PlanKeyword.builder()
                .keyword(savedKeyword)
                .plan(savedPlan1)
                .build();
        planKeywordRepository.save(planKeyword1);

        PlanKeyword planKeyword2 = PlanKeyword.builder()
                .keyword(savedKeyword)
                .plan(savedPlan2)
                .build();
        planKeywordRepository.save(planKeyword2);

        Sort sort = Sort.by(Sort.Direction.ASC, PlanSortBy.UPCOMING.getValue(), "visitStartTime", "id");
        Pageable pageable = PageRequest.of(0, PLAN_PAGE_SIZE, sort);
        PlanSearchCondition searchCondition
                = new PlanSearchCondition(savedMember.getId(), Category.PURPOSE, PlanSortBy.RECENT, PlanStatus.SAVED);

        //when
        List<PlanKeywordDto> findList = planRepository.findPlanKeywords(searchCondition, pageable);

        //then
        assertThat(findList).hasSize(2);
        assertThat(findList).extracting("id")
                .containsExactlyInAnyOrder(savedPlan1.getId(), savedPlan2.getId());
        assertThat(findList).extracting("location")
                .containsExactlyInAnyOrder(PLAN_LOCATION_NAME, PLAN_LOCATION_NAME_2);
        assertThat(findList).isSortedAccordingTo((target1, target2) -> target1.getVisitDateTime().compareTo(target2.getVisitDateTime()));
    }

    @Test
    void 계획_ID로_계획_키워드의_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        Keyword keyword1 = Keyword.builder()
                .name(KEYWORD_NAME)
                .category(Category.PURPOSE)
                .build();
        Keyword savedKeyword1 = keywordRepository.save(keyword1);

        Keyword keyword2 = Keyword.builder()
                .name(KEYWORD_NAME)
                .category(Category.PURPOSE)
                .build();
        Keyword savedKeyword2 = keywordRepository.save(keyword2);

        Plan plan = Plan.builder()
                .member(savedMember)
                .locationName(PLAN_LOCATION_NAME)
                .visitDate(PLAN_VISIT_DATE_2)
                .visitStartTime(PLAN_VISIT_START_TIME)
                .visitEndTime(PLAN_VISIT_END_TIME)
                .minutes(PLAN_MINUTES)
                .matchType(PLAN_MATCH_TYPE)
                .isSaved(true)
                .isShared(false)
                .build();
        Plan savedPlan = planRepository.save(plan);

        PlanKeyword planKeyword1 = PlanKeyword.builder()
                .keyword(savedKeyword1)
                .plan(savedPlan)
                .build();
        PlanKeyword savePlanKeyword1 = planKeywordRepository.save(planKeyword1);

        PlanKeyword planKeyword2 = PlanKeyword.builder()
                .keyword(savedKeyword2)
                .plan(savedPlan)
                .build();
        PlanKeyword savePlanKeyword2 = planKeywordRepository.save(planKeyword2);


        //when
        List<PlanKeyword> findPlanKeywords = planKeywordRepository.findByPlanId(savedPlan.getId());

        //then
        assertThat(findPlanKeywords).hasSize(2);
        assertThat(findPlanKeywords).extracting("keyword.id")
                .containsExactlyInAnyOrder(savedKeyword1.getId(), savedKeyword2.getId());
    }

}