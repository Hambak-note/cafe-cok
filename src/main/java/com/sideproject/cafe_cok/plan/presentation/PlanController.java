package com.sideproject.cafe_cok.plan.presentation;

import com.sideproject.cafe_cok.auth.application.AuthService;
import com.sideproject.cafe_cok.auth.dto.LoginMember;
import com.sideproject.cafe_cok.auth.exception.EmptyAuthorizationHeaderException;
import com.sideproject.cafe_cok.auth.exception.InvalidTokenException;
import com.sideproject.cafe_cok.auth.presentation.AuthenticationPrincipal;
import com.sideproject.cafe_cok.auth.presentation.AuthorizationExtractor;
import com.sideproject.cafe_cok.bookmark.presentation.BookmarkController;
import com.sideproject.cafe_cok.bookmark.presentation.BookmarkFolderController;
import com.sideproject.cafe_cok.cafe.presentation.CafeController;
import com.sideproject.cafe_cok.plan.dto.response.PlanResponse;
import com.sideproject.cafe_cok.member.exception.NoSuchMemberException;
import com.sideproject.cafe_cok.plan.application.PlanService;
import com.sideproject.cafe_cok.plan.domain.enums.PlanStatus;
import com.sideproject.cafe_cok.plan.dto.request.PlanSaveRequest;
import com.sideproject.cafe_cok.plan.dto.response.SavePlanResponse;
import com.sideproject.cafe_cok.plan.dto.response.PlanIdResponse;
import com.sideproject.cafe_cok.util.HttpHeadersUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "plans", description = "계획 관련 API")
@ApiResponse(responseCode = "200", description = "성공")
public class PlanController {

    private final PlanService planService;
    private final AuthService authService;
    private final HttpHeadersUtil httpHeadersUtil;

    @PostMapping
    @Operation(summary = "계획 저장")
    public ResponseEntity<SavePlanResponse> save(@RequestBody PlanSaveRequest request,
                                                 HttpServletRequest servletRequest) {

        Long memberId = getMemberId(servletRequest);
        SavePlanResponse response = planService.save(request, memberId);
        response.add(linkTo(methodOn(PlanController.class).save(request, servletRequest)).withSelfRel().withType("POST"))
                .add(linkTo(methodOn(PlanController.class).update(null, null, null)).withRel("update").withType("UPDATE"))
                .add(linkTo(methodOn(CafeController.class).findTop(null, null)).withRel("detail").withType("GET"))
                .add(linkTo(methodOn(CafeController.class).findBasic(null)).withRel("detail").withType("GET"))
                .add(linkTo(methodOn(CafeController.class).findMenus(null)).withRel("detail").withType("GET"))
                .add(linkTo(methodOn(CafeController.class).findImages(null)).withRel("detail").withType("GET"))
                .add(linkTo(methodOn(CafeController.class).findReviews(null)).withRel("detail").withType("GET"))
                .add(linkTo(methodOn(BookmarkController.class).save(null, null)).withRel("save").withType("POST"));
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("plans/save");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "planId에 해당하는 계획 조회")
    @Parameter(name = "planId", description = "조회하려는 계획 ID", example = "1")
    public ResponseEntity<PlanResponse> detail(@AuthenticationPrincipal LoginMember loginMember,
                                               @PathVariable Long planId){

        PlanResponse response = planService.find(loginMember, planId);
        response.add(linkTo(methodOn(PlanController.class).detail(loginMember, planId)).withSelfRel().withType("GET"))
                .add(linkTo(methodOn(PlanController.class).delete(null, null, null)).withRel("delete").withType("DELETE"))
                .add(linkTo(methodOn(PlanController.class).update(null, null, null)).withRel("update").withType("UPDATE"));
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("plans/detail");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PatchMapping("/{planId}")
    @Operation(summary = "planId에 해당하는 계획 수정")
    @Parameters({
            @Parameter(name = "planId", description = "수정하려는 계획 ID", example = "1"),
            @Parameter(name = "status", description = "계획의 타입")})
    public ResponseEntity<PlanIdResponse> update(@AuthenticationPrincipal LoginMember loginMember,
                                                 @PathVariable Long planId,
                                                 @RequestParam PlanStatus status) {

        PlanIdResponse response = planService.update(status, planId, loginMember);
        response.add(linkTo(methodOn(PlanController.class).update(loginMember, planId, status)).withSelfRel().withType("UPDATE"));
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("plans/update");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "planId에 해당하는 계획 삭제")
    @Parameters({
            @Parameter(name = "planId", description = "삭제하려는 계획 ID", example = "1"),
            @Parameter(name = "status", description = "계획의 타입")})
    public ResponseEntity<PlanIdResponse> delete(@AuthenticationPrincipal LoginMember loginMember,
                                                 @PathVariable Long planId,
                                                 @RequestParam PlanStatus status){

        PlanIdResponse response = planService.delete(status, planId);
        response.add(linkTo(methodOn(PlanController.class).delete(loginMember, planId, status)).withSelfRel().withType("DELETE"));
        HttpHeaders headers = httpHeadersUtil.createLinkHeaders("plans/delete");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
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
