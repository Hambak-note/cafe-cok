package com.sideproject.cafe_cok.cafe.presentation;

import com.sideproject.cafe_cok.admin.dto.request.AdminCafeSaveRequest;
import com.sideproject.cafe_cok.admin.dto.request.AdminCafeUpdateRequest;
import com.sideproject.cafe_cok.cafe.dto.response.CafeSaveResponse;
import com.sideproject.cafe_cok.auth.application.AuthService;
import com.sideproject.cafe_cok.auth.exception.EmptyAuthorizationHeaderException;
import com.sideproject.cafe_cok.auth.exception.InvalidTokenException;
import com.sideproject.cafe_cok.auth.presentation.AuthorizationExtractor;
import com.sideproject.cafe_cok.cafe.dto.response.*;
import com.sideproject.cafe_cok.cafe.application.CafeService;
import com.sideproject.cafe_cok.image.dto.response.ImagesResponse;
import com.sideproject.cafe_cok.member.exception.NoSuchMemberException;
import com.sideproject.cafe_cok.menu.dto.response.MenusResponse;
import com.sideproject.cafe_cok.review.dto.response.CafeReviewsResponse;
import com.sideproject.cafe_cok.util.HttpHeadersUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cafes")
@RequiredArgsConstructor
@Tag(name = "cafe", description = "카페 API")
public class CafeController {

    private final CafeService cafeService;
    private final AuthService authService;
    private final HttpHeadersUtil httpHeadersUtil;

    @GetMapping("/{cafeId}/top")
    @Operation(summary = "cafeId에 해당하는 카페의 상단 정보 조회")
    public ResponseEntity<CafeTopResponse> top(@PathVariable Long cafeId,
                                               HttpServletRequest servletRequest){

        Long memberId = getMemberId(servletRequest);
        CafeTopResponse response = cafeService.detailTop(cafeId, memberId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/top");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{cafeId}/basic")
    @Operation(summary = "cafeId에 해당하는 카페의 기본 정보 조회")
    public ResponseEntity<CafeBasicResponse> basic(@PathVariable Long cafeId) {

        CafeBasicResponse response = cafeService.detailBasicInfo(cafeId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/basic");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "좌표 기반 카페 조회")
    public ResponseEntity<CafesResponse> findByCoordinates(@RequestParam BigDecimal latitude,
                                                           @RequestParam BigDecimal longitude,
                                                           HttpServletRequest servletRequest) {

        Long memberId = getMemberId(servletRequest);
        CafesResponse response = cafeService.getNearestCafes(latitude, longitude, memberId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/findByCoordinates");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/keyword")
    @Operation(summary = "좌표/키워드 기반 카페 조회")
    public ResponseEntity<CafesResponse> findByCoordinatesAndKeyword(@RequestParam BigDecimal latitude,
                                                                     @RequestParam BigDecimal longitude,
                                                                     @RequestParam List<String> keywords,
                                                                     HttpServletRequest servletRequest) {

        Long memberId = getMemberId(servletRequest);
        CafesResponse response = cafeService.findCafeByKeyword(latitude, longitude, keywords, memberId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/findByCoordinatesAndKeyword");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "카페 저장")
    public ResponseEntity<CafeSaveResponse> save(@RequestBody AdminCafeSaveRequest request) {
        CafeSaveResponse response = cafeService.save(request);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/save");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PutMapping("/{cafeId}")
    @Operation(summary = "cafeId에 해당하는 카페 수정")
    public ResponseEntity<CafeSaveResponse> update(@PathVariable Long cafeId,
                                                   @RequestBody AdminCafeUpdateRequest request) {

        CafeSaveResponse response = cafeService.update(cafeId, request);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/update");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{cafeId}/reviews")
    @Operation(summary = "cafeId에 해당하는 모든 리뷰 조회")
    public ResponseEntity<CafeReviewsResponse> reviews(@PathVariable Long cafeId) {

        CafeReviewsResponse response = cafeService.findReviews(cafeId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/reviews");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{cafeId}/images")
    @Operation(summary = "cafeId에 해당하는 모든 이미지 조회")
    public ResponseEntity<ImagesResponse> images(@PathVariable Long cafeId) {

        ImagesResponse response = cafeService.findImages(cafeId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/images");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{cafeId}/menus")
    @Operation(summary = "cafeId에 해당하는 모든 메뉴 조회")
    public ResponseEntity<MenusResponse> menus(@PathVariable Long cafeId) {

        MenusResponse response = cafeService.findMenus(cafeId);
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("cafe/menus");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @Hidden
    @GetMapping("/{kakaoId}")
    public ResponseEntity<Boolean> isExistByKakaoId(@PathVariable Long kakaoId) {
        boolean exists = cafeService.isExistByKakaoId(kakaoId);
        return ResponseEntity.ok(exists);
    }

    private Long getMemberId(final HttpServletRequest request) {

        try {
            String accessToken = AuthorizationExtractor.extract(request);
            Long memberId = authService.extractMemberId(accessToken);
            return memberId;
        } catch (final InvalidTokenException | EmptyAuthorizationHeaderException |
                       NoSuchMemberException e) {
            return null;
        }
    }
}
