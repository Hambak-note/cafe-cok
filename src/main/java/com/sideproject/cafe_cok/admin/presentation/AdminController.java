package com.sideproject.cafe_cok.admin.presentation;

import com.sideproject.cafe_cok.admin.application.AdminService;
import com.sideproject.cafe_cok.admin.dto.AdminCafeDto;
import com.sideproject.cafe_cok.admin.dto.AdminSuggestionDto;
import com.sideproject.cafe_cok.member.domain.enums.FeedbackCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final List<String> daysOfWeek = Arrays.asList("월", "화", "수", "목", "금", "토", "일");

    @GetMapping()
    public String home() {

        return "page/home";
    }

    @GetMapping("/suggestions")
    public String suggestions(Model model) {
        List<AdminSuggestionDto> findSuggestions = adminService.findFeedbackByCategory(FeedbackCategory.IMPROVEMENT_SUGGESTION);
        model.addAttribute("suggestions", findSuggestions);
        return "page/feedback/suggestions";
    }

    @GetMapping("/withdrawal-reason")
    public String withdrawalReason(Model model) {

        List<AdminSuggestionDto> findWithdrawalReasons = adminService.findFeedbackByCategory(FeedbackCategory.WITHDRAWAL_REASON);
        model.addAttribute("withdrawalReasons", findWithdrawalReasons);
        return "page/feedback/withdrawal-reason";
    }

    @GetMapping("/cafes")
    public String getCafes(Model model) {
        List<AdminCafeDto> findCafes = adminService.findCafes();
        model.addAttribute("cafes", findCafes);
        return "page/cafe/list";
    }

    @GetMapping("/cafe/{id}")
    public String getCafeById(@PathVariable Long id, Model model) {
        AdminCafeDto findCafe = adminService.findCafeById(id);
        model.addAttribute("cafe", findCafe);
        model.addAttribute("daysOfWeek", daysOfWeek);
        if (model.containsAttribute("message")) {
            model.addAttribute("message", model.asMap().get("message"));
        }
        return "page/cafe/detail";
    }

    @GetMapping("/cafe/add")
    public String cafeRegisterForm(Model model) {
        model.addAttribute("daysOfWeek", daysOfWeek);
        return "page/cafe/add";
    }
}
