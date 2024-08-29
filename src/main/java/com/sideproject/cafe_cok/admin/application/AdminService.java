package com.sideproject.cafe_cok.admin.application;

import com.sideproject.cafe_cok.admin.dto.*;
import com.sideproject.cafe_cok.cafe.domain.Cafe;
import com.sideproject.cafe_cok.cafe.domain.OperationHour;
import com.sideproject.cafe_cok.cafe.domain.repository.CafeRepository;
import com.sideproject.cafe_cok.cafe.domain.repository.OperationHourRepository;
import com.sideproject.cafe_cok.cafe.dto.OperationHourDto;
import com.sideproject.cafe_cok.cafe.dto.CafeDetailDto;
import com.sideproject.cafe_cok.image.domain.Image;
import com.sideproject.cafe_cok.image.domain.enums.ImageType;
import com.sideproject.cafe_cok.image.domain.repository.ImageRepository;
import com.sideproject.cafe_cok.image.dto.ImageDto;
import com.sideproject.cafe_cok.member.domain.Feedback;
import com.sideproject.cafe_cok.member.domain.enums.FeedbackCategory;
import com.sideproject.cafe_cok.member.domain.repository.FeedbackRepository;
import com.sideproject.cafe_cok.menu.domain.repository.MenuRepository;
import com.sideproject.cafe_cok.menu.dto.MenuDetailDto;
import com.sideproject.cafe_cok.utils.FormatConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final FeedbackRepository feedbackRepository;
    private final CafeRepository cafeRepository;
    private final MenuRepository menuRepository;
    private final ImageRepository imageRepository;
    private final OperationHourRepository operationHourRepository;

    public List<AdminSuggestionDto> findFeedbackByCategory(final FeedbackCategory feedbackCategory) {

        List<Feedback> findFeedbacks = feedbackRepository.findByCategoryOrderByIdDesc(feedbackCategory);
        return findFeedbacks.stream()
                .map(feedback -> new AdminSuggestionDto(feedback))
                .collect(Collectors.toList());
    }

    public List<CafeDetailDto> findCafes() {

        List<Cafe> findCafes = cafeRepository.findAllByOrderByIdDesc();
        return findCafes.stream()
                .map(cafe -> new CafeDetailDto(cafe))
                .collect(Collectors.toList());
    }

    public CafeDetailDto findCafeById(final Long id) {
        Cafe findCafe = cafeRepository.getById(id);
        List<Image> filteredImage = findCafe.getImages().stream()
                .filter(image -> image.getImageType().equals(ImageType.CAFE) || image.getImageType().equals(ImageType.CAFE_MAIN))
                .collect(Collectors.toList());
        List<ImageDto> images = ImageDto.fromList(filteredImage);

        List<MenuDetailDto> findMenus = menuRepository.findByCafeId(id).stream()
                .map(menu -> {
                    List<Image> findMenuImages = imageRepository.findByMenu(menu);
                    if (findMenuImages.isEmpty()) return new MenuDetailDto(menu);
                    return new MenuDetailDto(menu, ImageDto.from(findMenuImages.get(0)));
                }).collect(Collectors.toList());


        List<OperationHour> findOperationHourList = operationHourRepository.findByCafeId(findCafe.getId());
        List<OperationHourDto> hours = generateHours();
        for (OperationHourDto hour : hours) {
            String day = hour.getDay();
            for (OperationHour operationHour : findOperationHourList) {
                if(day.equals(FormatConverter.getKoreanDayOfWeek(operationHour.getDate()))) {
                    hour.changeTime(operationHour);
                }
            }
        }

        return new CafeDetailDto(findCafe, images, findMenus, hours);
    }

    private List<OperationHourDto> generateHours() {

        List<String> daysOfWeek = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
        List<OperationHourDto> hours = new ArrayList<>();
        for (String day : daysOfWeek) {
            hours.add(new OperationHourDto(day));
        }

        return hours;
    }
}
