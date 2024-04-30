package com.sideproject.hororok.cafe.presentation;

import com.sideproject.hororok.cafe.dto.response.CafeDetailBasicInfoResponse;
import com.sideproject.hororok.cafe.dto.response.CafeDetailTopResponse;
import com.sideproject.hororok.common.annotation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.sideproject.hororok.common.fixtures.CafeFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CafeControllerTest extends ControllerTest {

    @Test
    @DisplayName("카페 상세정보 상단 호출 - 성공")
    public void test_cafe_detail_top_success() throws Exception {

        CafeDetailTopResponse response = 카페_상세_상단_응답();

        when(cafeService
                .detailTop(any(Long.class)))
                .thenReturn(response);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/cafe/{cafeId}/top", 카페_아이디)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("cafe/detail/top/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("cafeId").description("선택한 카페의 ID")),
                        responseFields(
                                fieldWithPath("cafeId").description("카페 ID"),
                                fieldWithPath("cafeName").description("카페 이름"),
                                fieldWithPath("roadAddress").description("카페 도로명 주소"),
                                fieldWithPath("latitude").description("카페 위도"),
                                fieldWithPath("longitude").description("카페 경도"),
                                fieldWithPath("starRating").description("카페 별점"),
                                fieldWithPath("reviewCount").description("카페의 리뷰 개수"),
                                fieldWithPath("imageUrl").description("카페 대표 이미지 URL"),
                                fieldWithPath("keywords").type(JsonFieldType.ARRAY)
                                        .description("선택된 키워드 리스트\n\n" + "- 선택된 키워드 순 내림차순, 최대 3개 제공"),
                                fieldWithPath("keywords[].id").description("키워드 ID"),
                                fieldWithPath("keywords[].category").description("키워드 카테고리"),
                                fieldWithPath("keywords[].name").description("키워드 이름"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailTop(any(Long.class));
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
                                fieldWithPath("menus[].imageUrl").description("메뉴 이미지 URL"),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY)
                                        .description("전체 이미지 URL 리스트(최대6개)\n\n - 카페 이미지(최대 3개) \n\n - 나머지 리뷰 이미지"),
                                fieldWithPath("userChoiceKeywords").type(JsonFieldType.ARRAY).description("사용자가 선택한 키워드 리스"),
                                fieldWithPath("userChoiceKeywords[].name").description("키워드 이름"),
                                fieldWithPath("userChoiceKeywords[].count").description("키워드 선택 횟수"),
                                fieldWithPath("reviews").type(JsonFieldType.ARRAY).description("카페 리뷰 리스(최대 2개)"),
                                fieldWithPath("reviews[].content").description("리뷰 내용"),
                                fieldWithPath("reviews[].starRating").description("리뷰 별점"),
                                fieldWithPath("reviews[].specialNote").description("리뷰 특이사항"),
                                fieldWithPath("reviews[].createDate").description("리뷰 작성일자"),
                                fieldWithPath("reviews[].picture").description("리뷰 작성자 프로필 이미지 경로"),
                                fieldWithPath("reviews[].nickname").description("리뷰 작성자 닉네임"),
                                fieldWithPath("reviews[].imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 리스트(최대 5개)"),
                                fieldWithPath("reviews[].recommendMenus").type(JsonFieldType.ARRAY).description("추천 메뉴 리스트(최대3개)"))))
                .andExpect(status().isOk());

        verify(cafeService, times(1)).detailBasicInfo(any(Long.class));
    }



}