package com.sideproject.cafe_cok.member.presentation;

import com.sideproject.cafe_cok.auth.dto.LoginMember;
import com.sideproject.cafe_cok.member.dto.request.MemberFeedbackRequest;
import com.sideproject.cafe_cok.member.dto.response.*;
import com.sideproject.cafe_cok.common.annotation.ControllerTest;
import com.sideproject.cafe_cok.plan.domain.enums.PlanSortBy;
import com.sideproject.cafe_cok.plan.domain.enums.PlanStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.multipart.MultipartFile;


import static com.sideproject.cafe_cok.common.fixtures.MemberFixtures.마이페이지_계획_상세_응답;
import static com.sideproject.cafe_cok.common.fixtures.MemberFixtures.맴버_닉네임;
import static com.sideproject.cafe_cok.common.fixtures.MyPageFixtures.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MyPageControllerTest extends ControllerTest {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_VALUE = "Bearer fake-token";

    @Test
    @DisplayName("마이페이지 상단의 프로필을 호출하는 API - 성공")
    public void test_myPage_profile_success() throws Exception{

        MyPageProfileResponse response = 마이페이지_프로필_응답();

        when(myPageService
                .profile(any(LoginMember.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/profile")
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/profile/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        responseFields(
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("picture").description("사용자 프로필 사진 URL"),
                                fieldWithPath("reviewCount").description("리뷰 개수"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로필을 수정하는 API - 성공")
    public void test_myPage_profile_edit_success() throws Exception{

        String nickname = 맴버_닉네임;
        String fileName = "test.jpg";
        byte[] content = "Test file content".getBytes();
        String contentType = "image/jpeg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, contentType, content);

        MyPageProfileEditResponse response = 마이페이지_프로필_수정_응답();

        when(myPageService
                .editProfile(any(LoginMember.class), any(String.class), any(MultipartFile.class)))
                .thenReturn(response);


        mockMvc.perform(
                        multipart(HttpMethod.POST,"/api/myPage/profile/edit?nickname="+nickname)
                                .file(file)
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andDo(document("myPage/profile/edit/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        requestParts(
                                partWithName("file").description("사용자 프로필 이미지(null으로 전달 시 기본 이미지 설정)")),
                        queryParameters(
                                parameterWithName("nickname").description("변경하려는 사용자의 닉네임(null 전달시 변경X)")),
                        responseFields(
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("picture").description("사용자 프로필 사진 URL"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("저장된 계획의 전체 리스트를 나타내는 API - 성공")
    public void test_saved_plans_success() throws Exception {

        MyPagePlansResponse response = 마이페이지_계획_리스트_응답();

        when(myPageService
                .getPlans(any(LoginMember.class), any(PlanSortBy.class), any(PlanStatus.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/saved/plans?sortBy="+PlanSortBy.RECENT.name()+"&page=1&size=10")
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/saved/plans/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        queryParameters(
                                parameterWithName("sortBy")
                                        .description("정렬 방식을 구분하는 필드\n\n" +
                                                "- RECENT: 최신 저장 순(Default)\n\n" + "- UPCOMING: 다가오는 여정 순"),
                                parameterWithName("page").description("페이지 번호를 지정(미지정시 첫번째 페이지 탐색)"),
                                parameterWithName("size").description("페이지의 사이즈를 지정(Default = 10)")),
                        responseFields(
                                fieldWithPath("page").description("페이지 번호"),
                                fieldWithPath("plans").type(JsonFieldType.ARRAY).description("계획(어정) 리스트"),
                                fieldWithPath("plans[].id").description("계획(여정)의 ID"),
                                fieldWithPath("plans[].keywordName").description("계획(여정)의 대표 키워드"),
                                fieldWithPath("plans[].location").description("사용자가 검색한 장소_필수X"),
                                fieldWithPath("plans[].visitDateTime").description("사용자가 검색한 날짜와 시간_필수X"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공유된 계획의 전체 리스트를 나타내는 API - 성공")
    public void test_shared_plans_success() throws Exception {

        MyPagePlansResponse response = 마이페이지_계획_리스트_응답();

        when(myPageService
                .getPlans(any(LoginMember.class), any(PlanSortBy.class), any(PlanStatus.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/shared/plans?sortBy="+PlanSortBy.RECENT.name()+"&page=1&size=10")
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/shared/plans/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        queryParameters(
                                parameterWithName("sortBy")
                                        .description("정렬 방식을 구분하는 필드\n\n" +
                                                "- RECENT: 최신 저장 순(Default)\n\n" + "- UPCOMING: 다가오는 여정 순"),
                                parameterWithName("page").description("페이지 번호를 지정(미지정시 첫번째 페이지 탐색)"),
                                parameterWithName("size").description("페이지의 사이즈를 지정(Default = 10)")),
                        responseFields(
                                fieldWithPath("page").description("페이지 번호"),
                                fieldWithPath("plans").type(JsonFieldType.ARRAY).description("계획(어정) 리스트"),
                                fieldWithPath("plans[].id").description("계획(여정)의 ID"),
                                fieldWithPath("plans[].keywordName").description("계획(여정)의 대표 키워드"),
                                fieldWithPath("plans[].location").description("사용자가 검색한 장소_필수X"),
                                fieldWithPath("plans[].visitDateTime").description("사용자가 검색한 날짜와 시간_필수X"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 계획탭에서 하나의 계획(여정)을 선택했을 때 동작 - 성공")
    public void test_my_page_detail_success() throws Exception{

        MyPagePlanDetailResponse response = 마이페이지_계획_상세_응답();

        when(myPageService.planDetail(any(LoginMember.class), any(Long.class)))
                .thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/myPage/plan/{planId}", response.getPlanId())
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/plan/detail/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        pathParameters(parameterWithName("planId").description("선택한 계획(여정)의 ID")),
                        responseFields(
                                fieldWithPath("planId").description("계획 ID"),
                                fieldWithPath("matchType").description("계획하기 결과 타입(MATCH, SIMILAR, MISMATCH)"),
                                fieldWithPath("locationName").description("검색한 위치 이름"),
                                fieldWithPath("minutes").description("도보 시간"),
                                fieldWithPath("visitDateTime").description("방문 날짜와 시간(월-일-시간-분"),
                                fieldWithPath("categoryKeywords").type(JsonFieldType.OBJECT).description("카테고리 별 키워드 정보"),
                                fieldWithPath("categoryKeywords.purpose").type(JsonFieldType.ARRAY).description("목적 카테고리 리스트(필수)"),
                                fieldWithPath("categoryKeywords.menu").type(JsonFieldType.ARRAY).description("메뉴 카테고리 리스트(없으면 빈 리스트)"),
                                fieldWithPath("categoryKeywords.theme").type(JsonFieldType.ARRAY).description("테마 카테고리 리스트(없으면 빈 리스트)"),
                                fieldWithPath("categoryKeywords.facility").type(JsonFieldType.ARRAY).description("시설 카테고리 리스트(없으면 빈 리스트)"),
                                fieldWithPath("categoryKeywords.atmosphere").type(JsonFieldType.ARRAY).description("분위기 카테고리 리스트(없으면 빈 리스트)"),
                                fieldWithPath("similarCafes").type(JsonFieldType.ARRAY)
                                        .description("유사한 카페(결과 타입이 MATCH, SIMILAR인 경우 존재, 아닌 경우 빈 리스트)"),
                                fieldWithPath("similarCafes[].id").description("카페 ID"),
                                fieldWithPath("similarCafes[].name").description("카페 이름"),
                                fieldWithPath("similarCafes[].phoneNumber").description("전화번호"),
                                fieldWithPath("similarCafes[].roadAddress").description("도로명 주소"),
                                fieldWithPath("similarCafes[].longitude").description("경도"),
                                fieldWithPath("similarCafes[].latitude").description("위도"),
                                fieldWithPath("similarCafes[].starRating").description("별점"),
                                fieldWithPath("similarCafes[].reviewCount").description("리뷰 수"),
                                fieldWithPath("similarCafes[].imageUrl").description("카페 이미지 URL"),
                                fieldWithPath("similarCafes[].bookmarks").type(JsonFieldType.ARRAY).description("북마크 리스트"),
                                fieldWithPath("similarCafes[].bookmarks[].bookmarkId").description("북마크 ID"),
                                fieldWithPath("similarCafes[].bookmarks[].folderId").description("북마크 폴더 ID"),
                                fieldWithPath("matchCafes").type(JsonFieldType.ARRAY)
                                        .description("일치하는 카페(결과 타입이 MATCH인 경우 존재, 아닌 경우 빈 리스트, 형식은 유사한 카페와 동일)"),
                                fieldWithPath("matchCafes[].id").description("카페 ID"),
                                fieldWithPath("matchCafes[].name").description("카페 이름"),
                                fieldWithPath("matchCafes[].phoneNumber").description("전화번호"),
                                fieldWithPath("matchCafes[].roadAddress").description("도로명 주소"),
                                fieldWithPath("matchCafes[].longitude").description("경도"),
                                fieldWithPath("matchCafes[].latitude").description("위도"),
                                fieldWithPath("matchCafes[].starRating").description("별점"),
                                fieldWithPath("matchCafes[].reviewCount").description("리뷰 수"),
                                fieldWithPath("matchCafes[].imageUrl").description("카페 이미지 URL"),
                                fieldWithPath("matchCafes[].bookmarks").type(JsonFieldType.ARRAY).description("북마크 리스트"),
                                fieldWithPath("matchCafes[].bookmarks[].bookmarkId").description("북마크 ID"),
                                fieldWithPath("matchCafes[].bookmarks[].folderId").description("북마크 폴더 ID"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 리뷰 탭을 눌렀을 때 동작하는 기능")
    public void test_my_page_reviews_success() throws Exception{

        MyPageReviewResponse response = 마이페이지_리뷰_리스트_응답();
        when(reviewService.getReviews(any(LoginMember.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/reviews")
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/reviews/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        responseFields(
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("리뷰의 리스트"),
                                fieldWithPath("reviews[].cafeId").description("카페 ID"),
                                fieldWithPath("reviews[].cafeName").description("카페 이름"),
                                fieldWithPath("reviews[].reviewId").description("리뷰 ID"),
                                fieldWithPath("reviews[].starRating").description("리뷰 별점"),
                                fieldWithPath("reviews[].content").description("리뷰 내용"),
                                fieldWithPath("reviews[].specialNote").description("리뷰 특이사항"),
                                fieldWithPath("reviews[].createdDate").description("리뷰 생성 일자"),
                                fieldWithPath("reviews[].imageUrls").type(JsonFieldType.ARRAY).description("리뷰의 이미지 리스트"),
                                fieldWithPath("reviews[].imageUrls[].originUrl").description("리뷰 원본 이미지 URL"),
                                fieldWithPath("reviews[].imageUrls[].thumbnailUrl").description("리뷰 썸네일 이미지 URL"),
                                fieldWithPath("reviews[].keywords").type(JsonFieldType.ARRAY).description("리뷰의 키워드 리스트"),
                                fieldWithPath("reviews[].keywords[].id").description("리뷰 키워드 ID"),
                                fieldWithPath("reviews[].keywords[].category").description("리뷰 키워드 카테고리"),
                                fieldWithPath("reviews[].keywords[].name").description("리뷰 키워드 이름"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 내 조합 탭을 눌렀을 때 동작하는 기능")
    public void test_my_page_combination_success() throws Exception {

        MyPageCombinationResponse response = 마이페이지_내조합_응답();

        when(myPageService.combination(any(LoginMember.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/combination")
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/combination/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        responseFields(
                                fieldWithPath("combinations").type(JsonFieldType.ARRAY).description("조합의 리스트"),
                                fieldWithPath("combinations[].id").description("조합 ID"),
                                fieldWithPath("combinations[].name").description("조합 이름"),
                                fieldWithPath("combinations[].icon").description("조합 아이콘"))))
                .andExpect(status().isOk());

        verify(myPageService, times(1)).combination(any(LoginMember.class));
    }

    @Test
    @DisplayName("저장된 계획의 전체 리스트 - 성공")
    public void test_my_page_saved_plan_all_success() throws Exception {

        MyPagePlansAllResponse response = 저장된_계획_전체_리스트();

        when(myPageService.getPlansAll(any(LoginMember.class), any(PlanSortBy.class), any(PlanStatus.class))).thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/saved/plans/all?sortBy="+PlanSortBy.RECENT.name())
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/saved/plans/all/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        queryParameters(parameterWithName("sortBy").description("정렬 방식을 구분하는 필드\n\n" +
                                                "- RECENT: 최신 저장 순(Default)\n\n" + "- UPCOMING: 다가오는 여정 순")),
                        responseFields(
                                fieldWithPath("plans").type(JsonFieldType.ARRAY).description("계획(어정) 리스트"),
                                fieldWithPath("plans[].id").description("계획(여정)의 ID"),
                                fieldWithPath("plans[].keywordName").description("계획(여정)의 대표 키워드"),
                                fieldWithPath("plans[].location").description("사용자가 검색한 장소_필수X"),
                                fieldWithPath("plans[].visitDateTime").description("사용자가 검색한 날짜와 시간_필수X"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("저장된 계획의 전체 리스트 - 성공")
    public void test_my_page_shared_plan_all_success() throws Exception {

        MyPagePlansAllResponse response = 공유된_계획_전체_리스트();

        when(myPageService.getPlansAll(any(LoginMember.class), any(PlanSortBy.class), any(PlanStatus.class))).thenReturn(response);

        mockMvc.perform(
                        get("/api/myPage/shared/plans/all?sortBy="+PlanSortBy.RECENT.name())
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("myPage/shared/plans/all/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        queryParameters(parameterWithName("sortBy").description("정렬 방식을 구분하는 필드\n\n" +
                                "- RECENT: 최신 저장 순(Default)\n\n" + "- UPCOMING: 다가오는 여정 순")),
                        responseFields(
                                fieldWithPath("plans").type(JsonFieldType.ARRAY).description("계획(어정) 리스트"),
                                fieldWithPath("plans[].id").description("계획(여정)의 ID"),
                                fieldWithPath("plans[].keywordName").description("계획(여정)의 대표 키워드"),
                                fieldWithPath("plans[].location").description("사용자가 검색한 장소_필수X"),
                                fieldWithPath("plans[].visitDateTime").description("사용자가 검색한 날짜와 시간_필수X"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("개선 의견 남기기 - 성공")
    public void test_add_feedback() throws Exception {

        MemberFeedbackRequest request = 개선의견_요청();

        mockMvc.perform(
                        post("/api/myPage/feedback")
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(document("myPage/feedback/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer JWT 엑세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("사용자가 작성한 개선 의견"))))
                .andExpect(status().isNoContent());
    }
}