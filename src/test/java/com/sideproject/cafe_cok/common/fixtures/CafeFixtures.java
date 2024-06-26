package com.sideproject.cafe_cok.common.fixtures;

import com.sideproject.cafe_cok.cafe.domain.Cafe;
import com.sideproject.cafe_cok.cafe.domain.enums.OpenStatus;
import com.sideproject.cafe_cok.cafe.dto.CafeDto;
import com.sideproject.cafe_cok.cafe.dto.request.CafeFindCategoryRequest;
import com.sideproject.cafe_cok.cafe.dto.response.*;
import com.sideproject.cafe_cok.image.domain.Image;
import com.sideproject.cafe_cok.image.domain.enums.ImageType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.sideproject.cafe_cok.common.fixtures.BookmarkFixtures.북마크_ID;
import static com.sideproject.cafe_cok.common.fixtures.BookmarkFixtures.북마크_ID_DTO;
import static com.sideproject.cafe_cok.common.fixtures.ImageFixtures.*;
import static com.sideproject.cafe_cok.common.fixtures.KeywordFixtures.*;
import static com.sideproject.cafe_cok.common.fixtures.MenuFixtures.메뉴_이미지_DTO_리스트;
import static com.sideproject.cafe_cok.common.fixtures.ReviewFixtures.*;

public class CafeFixtures {

    public static final Long 카페_아이디 = 1L;
    public static final String 카페_이름 = "카페 이름";
    public static final String 카페_전화번호 = "010-1234-5678";
    public static final String 카페_도로명_주소 = "OO시 OO구 OO동";
    public static final BigDecimal 카페_위도 = getRandomBigDecimal(0, 90);
    public static final BigDecimal 카페_경도 = getRandomBigDecimal(0, 180);
    public static final BigDecimal 카페_별점 = getRandomBigDecimal(0, 5);

    public static final OpenStatus 영업_여부 = OpenStatus.OPEN;

    public static List<String> 영업_시간_리스트 = Arrays.asList("영업 시간");
    public static List<String> 휴무일_리스트 = Arrays.asList("휴무일");

    public static final String 카페_이미지_URL = "//카페이미지";
    public static final String 카페_대표_썸네일_이미지_URL = "//카페 대표 이미지 썸네일 URL";
    public static final String 카페_대표_원본_이미지_URL = "//카페 대표 이미지 원본 URL";
    public static final Long 커서 = 1L;
    public static final Boolean 다음_페이지_존재_여부 = true;


    public static CafeFindCategoryRequest 카페_카테고리_검색_요청() {
        return new CafeFindCategoryRequest(카페_위도, 카페_경도, 키워드_이름_리스트);
    }

    public static CafeSearchResponse 카페_검색_결과_응답() {
        return new CafeSearchResponse(카페_DTO_리스트());
    }

    public static CafeDetailImageAllResponse 카페_상세_사진_전체_응답() {
        return CafeDetailImageAllResponse.from(Arrays.asList(카페_이미지_URL_DTO()));
    }

    public static CafeDetailReviewPageResponse 카페_상세_리뷰_페이징_응답() {
        return CafeDetailReviewPageResponse
                .of(Arrays.asList(키워드_카운트_DTO()), Arrays.asList(카페_상세_리뷰_DTO()), 커서, 다음_페이지_존재_여부);
    }

    public static CafeDetailReviewAllResponse 카페_상세_리뷰_전체_응답() {
        return CafeDetailReviewAllResponse.of(Arrays.asList(키워드_카운트_DTO()),Arrays.asList(카페_상세_리뷰_DTO()));
    }

    public static CafeDetailImagePageResponse 카페_상세_사진_페이징_응답() {
        return new CafeDetailImagePageResponse(Arrays.asList(카페_이미지_URL_DTO()), 커서, 다음_페이지_존재_여부);
    }

    public static CafeDetailMenuResponse 카페_상세_메뉴_응답() {
        return new CafeDetailMenuResponse(메뉴_이미지_DTO_리스트());
    }

    public static CafeDetailBasicInfoResponse 카페_상세_기본_정보_응답() {
        return new CafeDetailBasicInfoResponse
                (카페(), 영업_여부, 영업_시간_리스트, 휴무일_리스트, 메뉴_이미지_DTO_리스트(),
                        Arrays.asList(카페_이미지_URL_DTO(), 리뷰_이미지_URL_DTO()),
                        Arrays.asList(키워드_카운트_DTO()), Arrays.asList(카페_상세_리뷰_DTO()));
    }

    public static Cafe 카페() {
        Cafe cafe = new Cafe(카페_이름, 카페_전화번호, 카페_도로명_주소, 카페_위도, 카페_경도);
        setCafeIdAndImages(cafe, 카페_아이디, new ArrayList<>());
        return cafe;
    }

    public static CafeDto 카페_DTO() {
        CafeDto cafeDto = new CafeDto(카페(), 카페_대표_썸네일_이미지_URL, Arrays.asList(북마크_ID_DTO()));
        return cafeDto;
    }


    public static List<CafeDto> 카페_DTO_리스트() {
        return Arrays.asList(카페_DTO());
    }

    public static CafeDetailTopResponse 카페_상세_상단_응답() {
        return new CafeDetailTopResponse(
                카페(),카페_메인_이미지(ImageType.CAFE, 카페()),키워드_DTO_리스트(), Arrays.asList(북마크_ID_DTO()));
    }

    public static BigDecimal getRandomBigDecimal(int min, int max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return BigDecimal.valueOf(shifted);
    }

    public static Cafe setCafeIdAndImages(Cafe cafe, final Long id, final List<Image> images) {

        try {
            Field idField = Cafe.class.getDeclaredField("id");
            Field imagesField = Cafe.class.getDeclaredField("images");
            idField.setAccessible(true);
            imagesField.setAccessible(true);
            idField.set(cafe, id);
            imagesField.set(cafe, images);
            return cafe;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
