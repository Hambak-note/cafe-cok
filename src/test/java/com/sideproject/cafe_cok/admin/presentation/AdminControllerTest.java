package com.sideproject.cafe_cok.admin.presentation;

import com.sideproject.cafe_cok.admin.dto.request.AdminCafeSaveRequest;
import com.sideproject.cafe_cok.admin.dto.response.AdminCafeExistResponse;
import com.sideproject.cafe_cok.admin.dto.response.AdminCafeFindResponse;
import com.sideproject.cafe_cok.admin.dto.response.AdminCafeSaveResponse;
import com.sideproject.cafe_cok.common.annotation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.sideproject.cafe_cok.common.fixtures.AdminFixtures.*;
import static com.sideproject.cafe_cok.common.fixtures.CafeFixtures.카페_경도;
import static com.sideproject.cafe_cok.common.fixtures.CafeFixtures.카페_위도;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends ControllerTest {

    @Test
    @DisplayName("카페를 저장하는 기능 - 성공")
    public void test_create_cafe_success() throws Exception{

        byte[] content = "Test file content".getBytes();
        MockMultipartFile mainImage = new MockMultipartFile(
                        "mainImage", "mainImage.jpg", "image/jpeg", content);
        MockMultipartFile otherImage = new MockMultipartFile(
                                "otherImages", "otherImage.jpg", "image/jpeg", content);
        List<MultipartFile> otherImages = Arrays.asList(otherImage);

        byte[] requestContent = objectMapper.writeValueAsString(카페_저장_요청()).getBytes();
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", requestContent);
        AdminCafeSaveResponse response = 카페_저장_응답();

        when(adminService.saveCafe(any(AdminCafeSaveRequest.class), any(MultipartFile.class), eq(otherImages)))
                .thenReturn(response);

        mockMvc.perform(
                        multipart(HttpMethod.POST,"/api/admin/cafe/save")
                                .file(request)
                                .file(mainImage)
                                .file(otherImage)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andDo(document("admin/cafe/save/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("파일을 제외한 리뷰 요청 객체"),
                                partWithName("mainImage").description("카페 대표 이미지(필수)"),
                                partWithName("otherImages").description("메인 이미지를 제외한 나머지 카페 이미지")),
                        requestPartFields("request",
                                fieldWithPath("name").description("카페 이름(필수)"),
                                fieldWithPath("roadAddress").description("카페 주소(필수)"),
                                fieldWithPath("mapx").description("카페 경도(필수)"),
                                fieldWithPath("mapy").description("카페 위도(필수)"),
                                fieldWithPath("telephone").description("카페 전화번호"),
                                fieldWithPath("menus").type(JsonFieldType.ARRAY).description("카페의 메뉴에 관한 정보"),
                                fieldWithPath("menus[].name").description("메뉴 이름(메뉴가 존재한다면 필수)"),
                                fieldWithPath("menus[].price").description("메뉴 가격(메뉴가 존재한다면 필수)"),
                                fieldWithPath("menus[].image").description("메뉴 이미지(필수X)"),
                                fieldWithPath("hours").type(JsonFieldType.ARRAY).description("카페 운영 시간에 관한 정보(월~일(7개)"),
                                fieldWithPath("hours[]").type(JsonFieldType.ARRAY).description("시작시간과 종료을 담은 리스트"),
                                fieldWithPath("hours[][]").description("idx:0(시작시간), idx:1(종료시간)")),
                        responseFields(
                                fieldWithPath("id").description("카페 ID"),
                                fieldWithPath("name").description("카페 이름"),
                                fieldWithPath("roadAddress").description("카페 주소"),
                                fieldWithPath("mapx").description("카페 경도"),
                                fieldWithPath("mapy").description("카페 위도"),
                                fieldWithPath("telephone").description("카페 전화번호"),
                                fieldWithPath("mainImage").type(JsonFieldType.OBJECT).description("저장된 카페 이미지들의 정보"),
                                fieldWithPath("mainImage.id").description("메인 이미지 ID"),
                                fieldWithPath("mainImage.origin").description("메인 이미지 원본 URL"),
                                fieldWithPath("mainImage.medium").description("메인 이미지 중간 URL"),
                                fieldWithPath("mainImage.thumbnail").description("메인 이미지 썸네일 URL"),
                                fieldWithPath("otherImages").type(JsonFieldType.ARRAY).description("나머지 카페 이미지 리스트"),
                                fieldWithPath("otherImages[].id").description("나머지 이미지 ID"),
                                fieldWithPath("otherImages[].origin").description("나머지 이미지 원본 URL"),
                                fieldWithPath("otherImages[].thumbnail").description("나머지 이미지 썸네일 URL"),
                                fieldWithPath("menus").type(JsonFieldType.ARRAY).description("카페의 메뉴 리스트"),
                                fieldWithPath("menus[].id").description("메뉴 ID"),
                                fieldWithPath("menus[].name").description("메뉴 이름"),
                                fieldWithPath("menus[].price").description("메뉴 가격"),
                                fieldWithPath("menus[].image").type(JsonFieldType.OBJECT).description("메뉴 이미지 객체"),
                                fieldWithPath("menus[].image.id").description("메뉴 이미지 ID"),
                                fieldWithPath("menus[].image.origin").description("메뉴 원본 이미지 URL"),
                                fieldWithPath("menus[].image.thumbnail").description("메뉴 썸네일 이미지 URL"),
                                fieldWithPath("hours").type(JsonFieldType.ARRAY).description("카페 운영 시간에 관한 정보(월~일(7개)"),
                                fieldWithPath("hours[]").type(JsonFieldType.ARRAY).description("시작시간과 종료을 담은 리스트"),
                                fieldWithPath("hours[][]").description("idx:0(시작시간), idx:1(종료시간)"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("저장하려는 카페가 존재하는지 확인하는 기능 - 성공")
    public void test_check_cafe_exist_success() throws Exception{

        AdminCafeExistResponse response = 카페_존재_여부_응답();

        when(adminService.checkCafeExist(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(response);


        mockMvc.perform(get("/api/admin/cafe/exist")
                        .param("mapx", 카페_경도.toString())
                        .param("mapy", 카페_위도.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("admin/cafe/exist/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("mapx").description("검색한 위치의 경도"),
                                parameterWithName("mapy").description("검색한 위치의 위도")),
                        responseFields(
                                fieldWithPath("exist").description("카페 존재 여부(true/false)"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("검색한 카페가 존재하면 카페의 정보를 반환하는 기능 - 성공")
    public void test_find_cafe_success() throws Exception{

        AdminCafeFindResponse response = 카페_조회_응답();

        when(adminService.findCafe(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(response);

        mockMvc.perform(get("/api/admin/cafe")
                        .param("mapx", 카페_경도.toString())
                        .param("mapy", 카페_위도.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("admin/cafe/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("mapx").description("검색한 위치의 경도"),
                                parameterWithName("mapy").description("검색한 위치의 위도")),
                        responseFields(
                                fieldWithPath("cafe").type(JsonFieldType.OBJECT).description("조회한 카페관련 정보를 담은 객체"),
                                fieldWithPath("cafe.id").description("카페 ID"),
                                fieldWithPath("cafe.name").description("카페 이름"),
                                fieldWithPath("cafe.roadAddress").description("카페 주소"),
                                fieldWithPath("cafe.mapx").description("카페 경도"),
                                fieldWithPath("cafe.mapy").description("카페 위도"),
                                fieldWithPath("cafe.telephone").description("카페 전화번호"),
                                fieldWithPath("cafe.mainImage").type(JsonFieldType.OBJECT).description("저장된 카페 이미지들의 정보"),
                                fieldWithPath("cafe.mainImage.id").description("메인 이미지 ID"),
                                fieldWithPath("cafe.mainImage.origin").description("메인 이미지 원본 URL"),
                                fieldWithPath("cafe.mainImage.medium").description("메인 이미지 중간 URL"),
                                fieldWithPath("cafe.mainImage.thumbnail").description("메인 이미지 썸네일 URL"),
                                fieldWithPath("cafe.otherImages").type(JsonFieldType.ARRAY).description("나머지 카페 이미지 리스트"),
                                fieldWithPath("cafe.otherImages[].id").description("나머지 이미지 ID"),
                                fieldWithPath("cafe.otherImages[].origin").description("나머지 이미지 원본 URL"),
                                fieldWithPath("cafe.otherImages[].thumbnail").description("나머지 이미지 썸네일 URL"),
                                fieldWithPath("cafe.menus").type(JsonFieldType.ARRAY).description("카페의 메뉴 리스트"),
                                fieldWithPath("cafe.menus[].id").description("메뉴 ID"),
                                fieldWithPath("cafe.menus[].name").description("메뉴 이름"),
                                fieldWithPath("cafe.menus[].price").description("메뉴 가격"),
                                fieldWithPath("cafe.menus[].image").type(JsonFieldType.OBJECT).description("메뉴 이미지 객체"),
                                fieldWithPath("cafe.menus[].image.id").description("메뉴 이미지 ID"),
                                fieldWithPath("cafe.menus[].image.origin").description("메뉴 원본 이미지 URL"),
                                fieldWithPath("cafe.menus[].image.thumbnail").description("메뉴 썸네일 이미지 URL"),
                                fieldWithPath("cafe.hours").type(JsonFieldType.ARRAY).description("카페 운영 시간에 관한 정보(월~일(7개)"),
                                fieldWithPath("cafe.hours[]").type(JsonFieldType.ARRAY).description("시작시간과 종료을 담은 리스트"),
                                fieldWithPath("cafe.hours[][]").description("idx:0(시작시간), idx:1(종료시간)"))))
                .andExpect(status().isOk());
    }


}