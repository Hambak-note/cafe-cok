package com.sideproject.cafe_cok.plan.application;

import com.sideproject.cafe_cok.auth.dto.LoginMember;
import com.sideproject.cafe_cok.cafe.domain.repository.CafeRepository;
import com.sideproject.cafe_cok.cafe.dto.CafeDto;
import com.sideproject.cafe_cok.image.domain.Image;
import com.sideproject.cafe_cok.image.domain.enums.ImageType;
import com.sideproject.cafe_cok.image.domain.repository.ImageRepository;
import com.sideproject.cafe_cok.keword.domain.CafeReviewKeyword;
import com.sideproject.cafe_cok.keword.domain.repository.CafeReviewKeywordRepository;
import com.sideproject.cafe_cok.keword.domain.repository.KeywordRepository;
import com.sideproject.cafe_cok.keword.dto.CategoryKeywordsDto;
import com.sideproject.cafe_cok.member.domain.repository.MemberRepository;
import com.sideproject.cafe_cok.plan.domain.Plan;
import com.sideproject.cafe_cok.plan.domain.PlanCafe;
import com.sideproject.cafe_cok.plan.domain.PlanKeyword;
import com.sideproject.cafe_cok.plan.domain.enums.MatchType;
import com.sideproject.cafe_cok.plan.domain.enums.PlanCafeMatchType;
import com.sideproject.cafe_cok.plan.domain.enums.PlanStatus;
import com.sideproject.cafe_cok.plan.domain.repository.PlanCafeRepository;
import com.sideproject.cafe_cok.plan.domain.repository.PlanKeywordRepository;
import com.sideproject.cafe_cok.plan.domain.repository.PlanRepository;
import com.sideproject.cafe_cok.plan.dto.request.CreatePlanRequest;
import com.sideproject.cafe_cok.plan.dto.request.SavePlanRequest;
import com.sideproject.cafe_cok.plan.dto.request.SharePlanRequest;
import com.sideproject.cafe_cok.plan.dto.response.CreatePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.DeletePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.SavePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.SharePlanResponse;
import com.sideproject.cafe_cok.utils.Constants;
import com.sideproject.cafe_cok.utils.GeometricUtils;
import com.sideproject.cafe_cok.utils.ListUtils;
import com.sideproject.cafe_cok.utils.tmap.client.TmapClient;
import com.sideproject.cafe_cok.keword.domain.Keyword;
import com.sideproject.cafe_cok.member.domain.Member;
import com.sideproject.cafe_cok.cafe.domain.Cafe;
import com.sideproject.cafe_cok.plan.exception.NoSuchPlanKeywordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final TmapClient tmapClient;

    private final CafeRepository cafeRepository;
    private final ImageRepository imageRepository;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final PlanCafeRepository planCafeRepository;
    private final PlanKeywordRepository planKeywordRepository;
    private final CafeReviewKeywordRepository cafeReviewKeywordRepository;

    @Transactional
    public CreatePlanResponse plan(CreatePlanRequest request) {

        List<Cafe> filteredCafes = new ArrayList<>();
        CategoryKeywordsDto categoryKeywords = new CategoryKeywordsDto(keywordRepository.findByNameIn(request.getKeywords()));
        if(categoryKeywords.getPurpose().isEmpty()) throw new NoSuchPlanKeywordException();

        Boolean isMismatch = isMismatchPlan(request, filteredCafes);
        if(isMismatch) return createMisMatchPlan(request, categoryKeywords);

        List<Cafe> allMatchCafes = new ArrayList<>();
        Boolean isSimilar = isSimilarPlan(request, filteredCafes, allMatchCafes);
        if(isSimilar) return createSimilarPlan(request, categoryKeywords, filteredCafes);

        return createMatchPlan(request, categoryKeywords, filteredCafes, allMatchCafes);
    }

    @Transactional
    public SavePlanResponse save(SavePlanRequest request, LoginMember loginMember) {

        Plan findPlan = planRepository.getById(request.getPlanId());
        Member findMember = memberRepository.getById(loginMember.getId());
        findPlan.setIsSaved(true);
        findPlan.setMember(findMember);
        Plan savedPlan = planRepository.save(findPlan);

        return new SavePlanResponse(savedPlan.getId());
    }

    @Transactional
    public SharePlanResponse share(SharePlanRequest request, LoginMember loginMember) {

        Plan findPlan = planRepository.getById(request.getPlanId());
        Member findMember = memberRepository.getById(loginMember.getId());
        findPlan.setIsShared(true);
        findPlan.setMember(findMember);
        Plan savedPlan = planRepository.save(findPlan);

        return new SharePlanResponse(savedPlan.getId());
    }

    @Transactional
    public DeletePlanResponse delete(PlanStatus status, Long planId) {

        Plan findPlan = planRepository.getById(planId);

        if(status.equals(PlanStatus.SAVED)) {
            findPlan.setIsSaved(false);
        } else {
            findPlan.setIsShared(false);
        }
        Plan updatedPlan = planRepository.save(findPlan);

        if(!updatedPlan.getIsSaved() && !updatedPlan.getIsShared()) {
            planKeywordRepository.deleteByPlanId(updatedPlan.getId());
            planCafeRepository.deleteByPlanId(updatedPlan.getId());
            planRepository.delete(updatedPlan);
        }

        return new DeletePlanResponse(updatedPlan.getId());
    }

    private Boolean isMismatchPlan(CreatePlanRequest request, List<Cafe> filteredCafes) {

        List<Cafe> cafesByDateAndTimeRange = getCafesByDateAndTimeRange(
                request.getDate(), request.getStartTime(), request.getEndTime());
        List<Cafe> cafesByKeyword = getCafesByCafesAndKeywordNames(cafesByDateAndTimeRange, request.getKeywords());
        List<Cafe> cafesByDistance =
                getCafesByDistance(cafesByKeyword, request.getLatitude(), request.getLongitude(), request.getMinutes());

        if(cafesByDistance.isEmpty()) return true;
        filteredCafes.addAll(cafesByDistance);
        return false;
    }

    private List<Cafe> getCafesByDistance(final List<Cafe> cafes, final BigDecimal latitude,
                                          final BigDecimal longitude, final Integer withinMinutes) {

        if(latitude == null) return cafes;
        if(withinMinutes == null) return getFilteredCafesByWithinRadius(latitude, longitude, Constants.MAX_RADIUS_TIME, cafes);

        List<Cafe> withinRadiusCafes = getFilteredCafesByWithinRadius(latitude, longitude, withinMinutes, cafes);

        List<Cafe> distanceFilteredCafe = withinRadiusCafes.stream()
                .filter(cafe -> {
                    int walkingTimeUsingTmap = tmapClient.getWalkingTimeUsingTmap(
                            longitude.toString(), latitude.toString(),
                            cafe.getLongitude().toString(), cafe.getLatitude().toString());
                    return walkingTimeUsingTmap <= withinMinutes;
                })
                .collect(Collectors.toList());

        return distanceFilteredCafe;
    }

    private List<Cafe> getFilteredCafesByWithinRadius(
            final BigDecimal latitude, final BigDecimal longitude,
            final Integer withinMinutes, final List<Cafe> cafes) {

        return cafes.stream()
                .filter(cafe ->
                        GeometricUtils.isWithinRadius(latitude, longitude, cafe.getLatitude(),
                                cafe.getLongitude(), GeometricUtils.calculateDistanceInMeter(withinMinutes)))
                .collect(Collectors.toList());
    }

    private List<Cafe> getCafesByCafesAndKeywordNames(final List<Cafe> cafes, final List<String> keywordNames) {

        List<Keyword> findKeywords = keywordRepository.findByNameIn(keywordNames);
        List<CafeReviewKeyword> findCafeReviewKeywords = cafeReviewKeywordRepository.findByKeywordIn(findKeywords);
        List<Cafe> filteredCafesByKeyword = getCafesByCafeReviewKeywords(findCafeReviewKeywords);

        if(!cafes.isEmpty()) return ListUtils.getEquals(cafes, filteredCafesByKeyword);
        return getCafesByCafeReviewKeywords(findCafeReviewKeywords);
    }

    private List<Cafe> getCafesByCafeReviewKeywords(List<CafeReviewKeyword> cafeReviewKeywords) {

        return cafeReviewKeywords .stream()
                .map(cafeReviewKeyword -> cafeReviewKeyword.getCafe())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Cafe> getCafesByDateAndTimeRange(final LocalDate visitDate, final LocalTime startTime, final LocalTime endTime) {

        if(visitDate == null) return Arrays.asList();
        if(startTime == null) return cafeRepository.findByDate(visitDate.getDayOfWeek());
        if(endTime == null) return cafeRepository.findByDateAndStartTime(visitDate.getDayOfWeek(), startTime);

        return cafeRepository.findOpenHoursByDateAndTimeRange(visitDate.getDayOfWeek(), startTime, endTime);
    }

    private Boolean isSimilarPlan(CreatePlanRequest request, List<Cafe> filteredCafes, List<Cafe> allMatchCafes) {

        List<Cafe> cafesByKeywordAllMatch = getCafesByKeywordAllMatch(filteredCafes, request.getKeywords());
        if(cafesByKeywordAllMatch.isEmpty()) return true;
        allMatchCafes.addAll(cafesByKeywordAllMatch);

        return false;
    }

    private List<Cafe> getCafesByKeywordAllMatch(List<Cafe> cafes, List<String> keywords) {

        List<Cafe> cafesByKeywordAllMatch = cafes.stream()
                .filter(cafe -> keywords.stream()
                        .allMatch(keyword -> keywordRepository.findByCafeId(cafe.getId())
                                .stream()
                                .anyMatch(findKeyword -> findKeyword.getName().equals(keyword))))
                .collect(Collectors.toList());
        return cafesByKeywordAllMatch;
    }


    private CreatePlanResponse createMisMatchPlan(CreatePlanRequest request, CategoryKeywordsDto categoryKeywords) {

        List<Cafe> recommendCafes = cafeRepository.findAllByOrderByStarRatingDescNameAsc();
        CreatePlanResponse response = new CreatePlanResponse(
                MatchType.MISMATCH, request, categoryKeywords, getCafeDtosByCafes(recommendCafes));

        return response;
    }

    private CreatePlanResponse createSimilarPlan(
            CreatePlanRequest request, CategoryKeywordsDto categoryKeywords, List<Cafe> filteredCafes) {

        CreatePlanResponse response =  new CreatePlanResponse(
                MatchType.SIMILAR, request, categoryKeywords,
                getCafeDtosByCafes(filteredCafes));

        return createPlan(response, request);
    }

    private CreatePlanResponse createMatchPlan(
            CreatePlanRequest request, CategoryKeywordsDto categoryKeywords,
            List<Cafe> filteredCafes, List<Cafe> allMatchCafes) {

        orderByDistanceAndStarRating(allMatchCafes, request.getLatitude(), request.getLongitude());
        filteredCafes.removeAll(allMatchCafes);

        CreatePlanResponse response =
                new CreatePlanResponse(MatchType.MATCH, request, categoryKeywords,
                        getCafeDtosByCafes(allMatchCafes), getCafeDtosByCafes(filteredCafes));

        return createPlan(response, request);
    }

    private List<CafeDto> getCafeDtosByCafes(List<Cafe> cafes){

        List<CafeDto> cafeDtos = new ArrayList<>();
        for (Cafe cafe : cafes) {
            Image findImage = imageRepository.getImageByCafeAndImageType(cafe, ImageType.CAFE_MAIN_THUMBNAIL);
            cafeDtos.add(CafeDto.of(cafe, findImage.getImageUrl()));
        }

        return cafeDtos;
    }

    private CreatePlanResponse createPlan(CreatePlanResponse response, CreatePlanRequest request) {

        Member findMember = memberRepository.getById(Constants.NO_MEMBER_ID);

        Plan plan = request.toEntity(findMember, response.getMatchType());
        List<Plan> matchingPlans = planRepository.findMatchingPlan(plan);
        if(!matchingPlans.isEmpty()) {

            Optional<Long> matchingPlanId = matchingPlans.stream()
                    .filter(matchingPlan -> {
                        List<String> findKeywordNames = planKeywordRepository.findByPlanId(matchingPlan.getId())
                                .stream().map(planKeyword -> planKeyword.getKeyword().getName())
                                .collect(Collectors.toList());
                        return ListUtils.areListEqual(request.getKeywords(), findKeywordNames);
                    })
                    .map(Plan::getId)
                    .findFirst();

            if(matchingPlanId.isPresent()) {
                Long planId = matchingPlanId.get();
                response.setPlanId(planId);
                return response;
            }
        }

        Plan savedPlan = planRepository.save(plan);
        response.setPlanId(savedPlan.getId());

        List<CafeDto> similarCafes = response.getSimilarCafes();
        savePlanCafeAllByPlanAndCafeDtosAndMatchType(savedPlan, similarCafes, PlanCafeMatchType.SIMILAR);

        List<CafeDto> matchCafes = response.getMatchCafes();
        if(!matchCafes.isEmpty()) savePlanCafeAllByPlanAndCafeDtosAndMatchType(savedPlan, matchCafes, PlanCafeMatchType.MATCH);

        savePlanKeywordAllByPlanAndKeywordNames(savedPlan, request.getKeywords());

        return response;
    }

    private void savePlanCafeAllByPlanAndCafeDtosAndMatchType
            (final Plan plan, final List<CafeDto> cafeDtos, final PlanCafeMatchType matchType) {

        List<Long> cafeIds = cafeDtos.stream()
                .map(cafeDto -> cafeDto.getId())
                .collect(Collectors.toList());
        List<Cafe> findCafes = cafeRepository.findByIdIn(cafeIds);
        List<PlanCafe> planCafes = findCafes.stream()
                .map(findCafe -> new PlanCafe(plan, findCafe, matchType))
                .collect(Collectors.toList());
        planCafeRepository.saveAll(planCafes);
    }

    private void savePlanKeywordAllByPlanAndKeywordNames(final Plan plan, final List<String> keywordNames) {

        List<Keyword> findKeywords = keywordRepository.findByNameIn(keywordNames);
        List<PlanKeyword> planKeywords = findKeywords.stream()
                .map(keyword -> new PlanKeyword(plan, keyword))
                .collect(Collectors.toList());

        planKeywordRepository.saveAll(planKeywords);
    }

    private void orderByDistanceAndStarRating(List<Cafe> targetCafeList, BigDecimal latitude, BigDecimal longitude) {

        Comparator<Cafe> distanceAndRatingComparator = new Comparator<Cafe>() {
            @Override
            public int compare(Cafe cafe1, Cafe cafe2) {

                if(latitude == null) return cafe2.getStarRating().compareTo(cafe1.getStarRating());

                double distance1 = GeometricUtils.calculateDistance(latitude, longitude, cafe1.getLatitude(), cafe1.getLongitude());
                double distance2 = GeometricUtils.calculateDistance(latitude, longitude, cafe2.getLatitude(), cafe2.getLongitude());
                int distanceComparison = Double.compare(distance1, distance2);
                if (distanceComparison != 0) {
                    return distanceComparison; // 거리가 다르면 거리 순으로 정렬
                } else {
                    return cafe2.getStarRating().compareTo(cafe1.getStarRating()); // 거리가 같으면 별점 순으로 정렬
                }
            }
        };

        Collections.sort(targetCafeList, distanceAndRatingComparator);
    }
}
