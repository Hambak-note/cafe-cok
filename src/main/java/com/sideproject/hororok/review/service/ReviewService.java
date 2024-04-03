package com.sideproject.hororok.review.service;

import com.sideproject.hororok.S3.component.S3Uploader;
import com.sideproject.hororok.aop.annotation.LogTrace;
import com.sideproject.hororok.cafe.cond.CafeCategorySearchCond;
import com.sideproject.hororok.cafe.entity.Cafe;
import com.sideproject.hororok.cafe.repository.CafeRepository;
import com.sideproject.hororok.category.dto.CategoryKeywords;
import com.sideproject.hororok.keword.dto.KeywordDto;
import com.sideproject.hororok.keword.entity.Keyword;
import com.sideproject.hororok.keword.repository.KeywordRepository;
import com.sideproject.hororok.review.Entity.Review;
import com.sideproject.hororok.review.dto.ReviewDto;
import com.sideproject.hororok.review.dto.ReviewInfo;
import com.sideproject.hororok.review.repository.ReviewRepository;
import com.sideproject.hororok.reviewImage.entity.ReviewImage;
import com.sideproject.hororok.user.entity.User;
import com.sideproject.hororok.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final KeywordRepository keywordRepository;

    @LogTrace
    public void addReview(ReviewInfo reviewInfo, Long userId, List<MultipartFile> files) throws IOException {

        //1. 이미지 외부 저장소 저장
        List<ReviewImage> reviewImages = new ArrayList<>();
        for (MultipartFile file : files) {
            ReviewImage reviewImage = new ReviewImage(s3Uploader.upload(file, "review"));
            reviewImages.add(reviewImage);
        }


        //2. 이미지 url을 받아와서 리뷰 저장
        Review review = new Review();
        review.setContent(reviewInfo.getContent());
        review.setSpecialNote(reviewInfo.getSpecialNote());
        review.setStarRating(reviewInfo.getStarRating());
        review.setImages(reviewImages);
        Cafe cafe = cafeRepository.findById(reviewInfo.getCafeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid cafe id"));

        review.setCafe(cafe);

        //유저 저장
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
        review.setUser(user);


        //카테고리 키워드 저장
        CategoryKeywords categoryKeywords = reviewInfo.getCategoryKeywords();
        List<String> keywordNames = new ArrayList<>();
        List<Keyword> keywords = new ArrayList<>();
        List<String> atmosphere = categoryKeywords.getAtmosphere();
        List<String> facility = categoryKeywords.getFacility();
        List<String> purpose = categoryKeywords.getPurpose();
        List<String> theme = categoryKeywords.getTheme();
        List<String> menu = categoryKeywords.getMenu();

        if (atmosphere != null) {
            keywordNames.addAll(atmosphere);
        }
        if (facility != null) {
            keywordNames.addAll(facility);
        }
        if (purpose != null) {
            keywordNames.addAll(purpose);
        }
        if (theme != null) {
            keywordNames.addAll(theme);
        }
        if (menu != null) {
            keywordNames.addAll(menu);
        }

        for (String keyword : keywordNames) {
            Keyword findKeyword = keywordRepository.findByName(keyword)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid keyword name"));
            keywords.add(findKeyword);
        }
        review.setKeywords(keywords);


        reviewRepository.save(review);

    }


    @LogTrace
    public List<ReviewDto> findReviewByCafeId(Long cafeId){

        List<Review> reviews = reviewRepository.findByCafeId(cafeId);
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            reviewDtos.add(ReviewDto.of(review, review.getUser().getNickname()));
        }

        return reviewDtos;
    }

    @LogTrace
    public List<KeywordDto> findKeywordInReviewByCafeIdOrderByDesc(Long cafeId) {

        List<Keyword> keywords = reviewRepository.findKeywordInReviewByCafeIdOrderByDesc(cafeId);
        List<KeywordDto> keywordDtoList = new ArrayList<>();
        int idx = 0;
        for (Keyword keyword : keywords) {
            if(idx == 3) break;
            keywordDtoList.add(KeywordDto.from(keyword));
            idx++;
        }

        return keywordDtoList;
    }

    @LogTrace
    public List<Cafe> findCafeWithKeywordsInReview(CafeCategorySearchCond searchCond) {


        CategoryKeywords categoryKeywords = searchCond.getCategoryKeywords();
        List<String> keywords = new ArrayList<>();
        List<String> atmosphere = categoryKeywords.getAtmosphere();
        List<String> facility = categoryKeywords.getFacility();
        List<String> purpose = categoryKeywords.getPurpose();
        List<String> theme = categoryKeywords.getTheme();
        List<String> menu = categoryKeywords.getMenu();

        if (atmosphere != null) {
            keywords.addAll(atmosphere);
        }
        if (facility != null) {
            keywords.addAll(facility);
        }
        if (purpose != null) {
            keywords.addAll(purpose);
        }
        if (theme != null) {
            keywords.addAll(theme);
        }
        if (menu != null) {
            keywords.addAll(menu);
        }


        List<Cafe> cafeWithKeywordsInReview = reviewRepository.findCafeWithKeywordsInReview(keywords);


        return reviewRepository.findCafeWithKeywordsInReview(keywords);
    }


    @LogTrace
    public List<ReviewImage> findReviewImagesByCafeId(Long cafeId) {
        return reviewRepository.findReviewImagesByCafeId(cafeId);
    }

}
