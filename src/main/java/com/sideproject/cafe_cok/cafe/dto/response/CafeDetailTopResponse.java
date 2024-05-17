package com.sideproject.cafe_cok.cafe.dto.response;


import com.sideproject.cafe_cok.keword.dto.KeywordDto;
import com.sideproject.cafe_cok.bookmark.dto.BookmarkCafeDto;
import com.sideproject.cafe_cok.cafe.domain.Cafe;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CafeDetailTopResponse {

    private Long cafeId;
    private List<BookmarkCafeDto> bookmarks;
    private String cafeName;
    private String roadAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double starRating;
    private Long reviewCount;
    private String originImage;
    private String thumbnailImage;
    private List<KeywordDto> keywords;

    public static CafeDetailTopResponse of(final Cafe cafe, final String originImage, final String thumbnailImage,
                                           final Long reviewCount, final List<KeywordDto> keywords) {
        return CafeDetailTopResponse.builder()
                .cafeId(cafe.getId())
                .cafeName(cafe.getName())
                .roadAddress(cafe.getRoadAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .starRating(cafe.getStarRating().doubleValue())
                .originImage(originImage)
                .thumbnailImage(thumbnailImage)
                .reviewCount(reviewCount)
                .keywords(keywords)
                .build();
    }

    public static CafeDetailTopResponse of(final Cafe cafe, final List<BookmarkCafeDto> bookmarks, final String originImage,
                                           final String thumbnailImage, final Long reviewCount, final List<KeywordDto> keywords) {
        return CafeDetailTopResponse.builder()
                .cafeId(cafe.getId())
                .bookmarks(bookmarks)
                .cafeName(cafe.getName())
                .roadAddress(cafe.getRoadAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .starRating(cafe.getStarRating().doubleValue())
                .reviewCount(reviewCount)
                .originImage(originImage)
                .thumbnailImage(thumbnailImage)
                .keywords(keywords)
                .build();
    }

}
