package com.sideproject.cafe_cok.cafe.presentation;

import com.sideproject.cafe_cok.cafe.dto.request.CafeFindCategoryRequest;
import com.sideproject.cafe_cok.cafe.dto.response.*;
import com.sideproject.cafe_cok.common.annotation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;

import static com.sideproject.cafe_cok.common.fixtures.CafeFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CafeControllerTest extends ControllerTest {


    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_VALUE = "Bearer fake-token";

    @Test
    @DisplayName("카페 상세정보 상단 호출 - 성공")
    public void test_cafe_detail_top_success() throws Exception {

        CafeDetailTopResponse response = 카페_상세_상단_응답();
        when(cafeService.detailTop(any(Long.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/top", 카페_아이디)
                                .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/top/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        responseFields(
                                fieldWithPath("cafeId").description("카페 ID"),
                                fieldWithPath("bookmarks").type(JsonFieldType.ARRAY).description("북마크 리스트(존재하지 않을 경우 빈 리스트)"),
                                fieldWithPath("bookmarks[].bookmarkId").description("북마크 ID"),
                                fieldWithPath("bookmarks[].folderId").description("폴더 ID"),
                                fieldWithPath("cafeName").description("카페 이름"),
                                fieldWithPath("roadAddress").description("카페 도로명 주소"),
                                fieldWithPath("latitude").description("카페 위도"),
                                fieldWithPath("longitude").description("카페 경도"),
                                fieldWithPath("starRating").description("카페 별점"),
                                fieldWithPath("reviewCount").description("카페의 리뷰 개수"),
                                fieldWithPath("originUrl").description("카페 대표 이미지 원본 URL"),
                                fieldWithPath("thumbnailUrl").description("카페 대표 이미지 중간 사이즈 썸네일 URL"),
                                fieldWithPath("keywords").type(JsonFieldType.ARRAY)
                                        .description("선택된 키워드 리스트\n\n" + "- 선택된 키워드 순 내림차순, 최대 3개 제공"),
                                fieldWithPath("keywords[].id").description("키워드 ID"),
                                fieldWithPath("keywords[].category").description("키워드 카테고리"),
                                fieldWithPath("keywords[].name").description("키워드 이름"))))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("카페 상세정보 기본 정보 호출 - 성공")
    public void test_cafe_detail_basic_info_success() throws Exception {

        CafeDetailBasicInfoResponse response = 카페_상세_기본_정보_응답();

        when(cafeService.detailBasicInfo(any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/basicInfo", 카페_아이디)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/basicInfo/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        responseFields(
                                fieldWithPath("roadAddress").description("카페 도로명 주소"),
                                fieldWithPath("openStatus").description("카페 영업상태(영업중/영업종료)"),
                                fieldWithPath("businessHours").type(JsonFieldType.ARRAY).description("카페 운영시간 리스트"),
                                fieldWithPath("closedDay").type(JsonFieldType.ARRAY).description("카페 휴무일 리스트"),
                                fieldWithPath("phoneNumber").description("카페 전화번호"),
                                fieldWithPath("menus").type(JsonFieldType.ARRAY).description("카페 메뉴리스트(2개)"),
                                fieldWithPath("menus[].name").description("메뉴 이름"),
                                fieldWithPath("menus[].price").description("메뉴 가격"),
                                fieldWithPath("menus[].originUrl").description("메뉴 원본 이미지 URL"),
                                fieldWithPath("menus[].thumbnailUrl").description("메뉴 썸네일 이미지 URL"),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY)
                                        .description("전체 이미지 URL 리스트(최대6개)\n\n - 카페 이미지(최대 3개) \n\n - 나머지 리뷰 이미지"),
                                fieldWithPath("imageUrls[].originUrl").description("원본 이미지 URL"),
                                fieldWithPath("imageUrls[].thumbnailUrl").description("썸네일 이미지 URL"),
                                fieldWithPath("userChoiceKeywords").type(JsonFieldType.ARRAY).description("사용자가 선택한 키워드 리스"),
                                fieldWithPath("userChoiceKeywords[].name").description("키워드 이름"),
                                fieldWithPath("userChoiceKeywords[].count").description("키워드 선택 횟수"),
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("카페 리뷰 리스(최대 2개)"),
                                fieldWithPath("reviews[].id").description("리뷰 id"),
                                fieldWithPath("reviews[].content").description("리뷰 내용"),
                                fieldWithPath("reviews[].starRating").description("리뷰 별점"),
                                fieldWithPath("reviews[].specialNote").description("리뷰 특이사항"),
                                fieldWithPath("reviews[].createDate").description("리뷰 작성일자"),
                                fieldWithPath("reviews[].picture").description("리뷰 작성자 프로필 이미지 경로"),
                                fieldWithPath("reviews[].nickname").description("리뷰 작성자 닉네임"),
                                fieldWithPath("reviews[].imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 리스트(최대 5개)"),
                                fieldWithPath("reviews[].imageUrls[].originUrl").description("리뷰 원본 이미지 URL"),
                                fieldWithPath("reviews[].imageUrls[].thumbnailUrl").description("리뷰 썸네일 이미지 URL"),
                                fieldWithPath("reviews[].recommendMenus").type(JsonFieldType.ARRAY).description("추천 메뉴 리스트(최대3개)"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailBasicInfo(any(Long.class));
    }

    @Test
    @DisplayName("카페 상세정보 메뉴 탭 호출 - 성공")
    public void test_cafe_detail_menus_success() throws Exception {

        CafeDetailMenuResponse response = 카페_상세_메뉴_응답();

        when(cafeService.detailMenus(any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/menus", 카페_아이디)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/menus/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        responseFields(
                                fieldWithPath("menus").type(JsonFieldType.ARRAY).description("카페 ID"),
                                fieldWithPath("menus[].name").description("메뉴 이름"),
                                fieldWithPath("menus[].price").description("메뉴 가격"),
                                fieldWithPath("menus[].originUrl").description("원본 이미지 URL"),
                                fieldWithPath("menus[].thumbnailUrl").description("썸네일 이미지 URL"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailMenus(any(Long.class));
    }

    @Test
    @DisplayName("카페 상세정보 사진 탭 호출(페이징) - 성공")
    public void test_cafe_detail_images_success() throws Exception {

        CafeDetailImagePageResponse response = 카페_상세_사진_페이징_응답();

        when(cafeService.detailImages(any(Long.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/images", 카페_아이디)
                                .param("cursor", 커서.toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/images/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        queryParameters(parameterWithName("cursor").description("페이징을 위한 커서 정보(null 전달 시 첫 번째 페이지 조회)")),
                        responseFields(
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 리스트(최대 8개)"),
                                fieldWithPath("imageUrls[].originUrl").description("원본 이미지 URL"),
                                fieldWithPath("imageUrls[].thumbnailUrl").description("썸네일 이미지 URL"),
                                fieldWithPath("cursor").description("페이징 시 사용되는 커서 정보"),
                                fieldWithPath("hasNextPage").description("다음 페이지 존재 여부"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailImages(any(Long.class), any(Long.class));
    }

    @Test
    @DisplayName("카페 상세정보 사진 탭 호출 전체 이미지 - 성공")
    public void test_cafe_detail_images_all_success() throws Exception {

        CafeDetailImageAllResponse response = 카페_상세_사진_전체_응답();

        when(cafeService.detailImagesAll(any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/images/all", 카페_아이디)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/images/all/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        responseFields(fieldWithPath("imageUrls")
                                .type(JsonFieldType.ARRAY).description("이미지 URL 리스트(전체)"),
                                fieldWithPath("imageUrls[].originUrl").description("원본 이미지 URL"),
                                fieldWithPath("imageUrls[].thumbnailUrl").description("썸네일 이미지 URL"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailImagesAll(any(Long.class));
    }

    @Test
    @DisplayName("카페 상세정보 리뷰 탭 호출(페이징) - 성공")
    public void test_cafe_detail_reviews_success() throws Exception {

        CafeDetailReviewPageResponse response = 카페_상세_리뷰_페이징_응답();

        when(cafeService.detailReviews(any(Long.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/reviews", 카페_아이디)
                                .param("cursor", 커서.toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/reviews/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        queryParameters(parameterWithName("cursor").description("페이징을 위한 커서 정보(null 전달 시 첫 번째 페이지 조회)")),
                        responseFields(
                                fieldWithPath("userChoiceKeywords").type(JsonFieldType.ARRAY).description("사용자가 선택한 키워드 리스"),
                                fieldWithPath("userChoiceKeywords[].name").description("키워드 이름"),
                                fieldWithPath("userChoiceKeywords[].count").description("키워드 선택 횟수"),
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("카페 리뷰 리스(최대 2개)"),
                                fieldWithPath("reviews[].id").description("리뷰 id"),
                                fieldWithPath("reviews[].content").description("리뷰 내용"),
                                fieldWithPath("reviews[].starRating").description("리뷰 별점"),
                                fieldWithPath("reviews[].specialNote").description("리뷰 특이사항"),
                                fieldWithPath("reviews[].createDate").description("리뷰 작성일자"),
                                fieldWithPath("reviews[].picture").description("리뷰 작성자 프로필 이미지 경로"),
                                fieldWithPath("reviews[].nickname").description("리뷰 작성자 닉네임"),
                                fieldWithPath("reviews[].imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 리스트(최대 5개)"),
                                fieldWithPath("reviews[].imageUrls[].originUrl").description("리뷰 원본 이미지 URL"),
                                fieldWithPath("reviews[].imageUrls[].thumbnailUrl").description("리뷰 썸네일 이미지 URL"),
                                fieldWithPath("reviews[].recommendMenus").type(JsonFieldType.ARRAY).description("추천 메뉴 리스트(최대3개)"),
                                fieldWithPath("cursor").description("페이징 시 사용되는 커서 정보"),
                                fieldWithPath("hasNextPage").description("다음 페이지 존재 여부"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailReviews(any(Long.class), any(Long.class));
    }

    @Test
    @DisplayName("카페 상세정보 리뷰 탭 호출(전체 리스트) - 성공")
    public void test_cafe_detail_reviews_ALL_success() throws Exception {

        CafeDetailReviewAllResponse response = 카페_상세_리뷰_전체_응답();

        when(cafeService.detailReviewsAll(any(Long.class))).thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/reviews/all", 카페_아이디)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/reviews/all/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        responseFields(
                                fieldWithPath("userChoiceKeywords").type(JsonFieldType.ARRAY).description("사용자가 선택한 키워드 리스"),
                                fieldWithPath("userChoiceKeywords[].name").description("키워드 이름"),
                                fieldWithPath("userChoiceKeywords[].count").description("키워드 선택 횟수"),
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("카페 리뷰 리스(최대 2개)"),
                                fieldWithPath("reviews[].id").description("리뷰 id"),
                                fieldWithPath("reviews[].content").description("리뷰 내용"),
                                fieldWithPath("reviews[].starRating").description("리뷰 별점"),
                                fieldWithPath("reviews[].specialNote").description("리뷰 특이사항"),
                                fieldWithPath("reviews[].createDate").description("리뷰 작성일자"),
                                fieldWithPath("reviews[].picture").description("리뷰 작성자 프로필 이미지 경로"),
                                fieldWithPath("reviews[].nickname").description("리뷰 작성자 닉네임"),
                                fieldWithPath("reviews[].imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 리스트(최대 5개)"),
                                fieldWithPath("reviews[].imageUrls[].originUrl").description("리뷰 원본 이미지 URL"),
                                fieldWithPath("reviews[].imageUrls[].thumbnailUrl").description("리뷰 썸네일 이미지 URL"),
                                fieldWithPath("reviews[].recommendMenus").type(JsonFieldType.ARRAY).description("추천 메뉴 리스트(최대3개)"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailReviewsAll(any(Long.class));
    }

    @Test
    @DisplayName("특정 지점에서 카페를 재검색 하는 기능 - 성공")
    public void test_cafe_find_again_success() throws Exception {

        CafeSearchResponse response = 카페_검색_결과_응답();

        when(cafeService.getCafeListByAgain(any(BigDecimal.class), any(BigDecimal.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/cafe/find/again")
                        .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                        .param("latitude", 카페_위도.toString())
                        .param("longitude", 카페_경도.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/find/again/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("latitude").description("재검색 지점의 위도"),
                                parameterWithName("longitude").description("재검색 지점의 경도")),
                        responseFields(
                                fieldWithPath("cafes").type(JsonFieldType.ARRAY).description("반경(2km) 내 카페 리스트"),
                                fieldWithPath("cafes[].id").description("카페 ID"),
                                fieldWithPath("cafes[].bookmarks").description("북마크 리스트(존재하지 않을 경우 빈 리스트)"),
                                fieldWithPath("cafes[].bookmarks[].bookmarkId").description("북마크 ID"),
                                fieldWithPath("cafes[].bookmarks[].folderId").description("폴더 ID"),
                                fieldWithPath("cafes[].name").description("카페 이름"),
                                fieldWithPath("cafes[].phoneNumber").description("카페 전화번호"),
                                fieldWithPath("cafes[].roadAddress").description("카페 도로명 주소"),
                                fieldWithPath("cafes[].latitude").description("카페 위도"),
                                fieldWithPath("cafes[].longitude").description("카페 경도"),
                                fieldWithPath("cafes[].starRating").description("카페 별점"),
                                fieldWithPath("cafes[].reviewCount").description("카페 리뷰 개수"),
                                fieldWithPath("cafes[].imageUrl").description("카페 대표 이미지 URL"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("검색창에서 카페를 검색 하는 기능 - 성공")
    public void test_cafe_find_bar_success() throws Exception {

        CafeSearchResponse response = 카페_검색_결과_응답();

        when(cafeService.findCafeByBar(any(BigDecimal.class), any(BigDecimal.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/cafe/find/bar")
                        .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                        .param("latitude", 카페_위도.toString())
                        .param("longitude", 카페_경도.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/find/bar/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("latitude").description("검색한 위치의 위도"),
                                parameterWithName("longitude").description("검색한 위치의 경도")),
                        responseFields(
                                fieldWithPath("cafes").type(JsonFieldType.ARRAY)
                                        .description("검색한 위치의 카페와 반경 2km내의 카페 리스트 \n\n - 카페가 존재하면 리스트 상단"),
                                fieldWithPath("cafes[].id").description("카페 ID"),
                                fieldWithPath("cafes[].bookmarks").description("북마크 리스트(존재하지 않을 경우 빈 리스트)"),
                                fieldWithPath("cafes[].bookmarks[].bookmarkId").description("북마크 ID"),
                                fieldWithPath("cafes[].bookmarks[].folderId").description("폴더 ID"),
                                fieldWithPath("cafes[].name").description("카페 이름"),
                                fieldWithPath("cafes[].phoneNumber").description("카페 전화번호"),
                                fieldWithPath("cafes[].roadAddress").description("카페 도로명 주소"),
                                fieldWithPath("cafes[].latitude").description("카페 위도"),
                                fieldWithPath("cafes[].longitude").description("카페 경도"),
                                fieldWithPath("cafes[].starRating").description("카페 별점"),
                                fieldWithPath("cafes[].reviewCount").description("카페 리뷰 개수"),
                                fieldWithPath("cafes[].imageUrl").description("카페 대표 이미지 URL"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("선택한 키워드와 현재 위치를 기준으로 검색 - 성공")
    public void test_cafe_find_keyword_success() throws Exception {

        CafeFindCategoryRequest request = 카페_카테고리_검색_요청();
        CafeSearchResponse response = 카페_검색_결과_응답();

        when(cafeService.findCafeByKeyword(any(CafeFindCategoryRequest.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(post("/api/cafe/find/keyword")
                        .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(document("cafe/find/keyword/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("latitude").description("검색 위치 위도"),
                                fieldWithPath("longitude").description("검색 위치 경도"),
                                fieldWithPath("keywords").type(JsonFieldType.ARRAY).description("사용자가 선택한 키워드 이름 리스트")),
                        responseFields(
                                fieldWithPath("cafes").type(JsonFieldType.ARRAY).description("선택한 키워드와 위치 기반의 카페 리스트"),
                                fieldWithPath("cafes[].id").description("카페 ID"),
                                fieldWithPath("cafes[].bookmarks").description("북마크 리스트(존재하지 않을 경우 빈 리스트)"),
                                fieldWithPath("cafes[].bookmarks[].bookmarkId").description("북마크 ID"),
                                fieldWithPath("cafes[].bookmarks[].folderId").description("폴더 ID"),
                                fieldWithPath("cafes[].name").description("카페 이름"),
                                fieldWithPath("cafes[].phoneNumber").description("카페 전화번호"),
                                fieldWithPath("cafes[].roadAddress").description("카페 도로명 주소"),
                                fieldWithPath("cafes[].latitude").description("카페 위도"),
                                fieldWithPath("cafes[].longitude").description("카페 경도"),
                                fieldWithPath("cafes[].starRating").description("카페 별점"),
                                fieldWithPath("cafes[].reviewCount").description("카페 리뷰 개수"),
                                fieldWithPath("cafes[].imageUrl").description("카페 대표 이미지 URL"))))
                .andExpect(status().isOk());
    }
}