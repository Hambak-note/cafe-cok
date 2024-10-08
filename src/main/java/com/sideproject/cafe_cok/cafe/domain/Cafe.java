package com.sideproject.cafe_cok.cafe.domain;

import com.sideproject.cafe_cok.cafe.dto.CafeAdminDto;
import com.sideproject.cafe_cok.cafe.dto.CafeDto;
import com.sideproject.cafe_cok.cafe.exception.InvalidCafeException;
import com.sideproject.cafe_cok.global.entity.BaseEntity;
import com.sideproject.cafe_cok.image.domain.Image;
import com.sideproject.cafe_cok.keword.domain.CafeReviewKeyword;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sideproject.cafe_cok.util.FormatConverter.*;
import static jakarta.persistence.GenerationType.IDENTITY;


@Getter
@Setter
@Table(name = "cafes")
@Entity
@NoArgsConstructor
public class Cafe extends BaseEntity {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{2,5}-\\d{3,4}-\\d{4}$");
    private static final Integer X_NUM_DIGITS = 3;
    private static final Integer Y_NUM_DIGITS = 2;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "road_address")
    private String roadAddress;

    @Column(name = "longitude",
            precision = 17, scale = 14)
    private BigDecimal longitude;

    @Column(name = "latitude",
            precision = 17, scale = 14)
    private BigDecimal latitude;

    @Column(name = "star_rating", precision = 2, scale = 1)
    private BigDecimal starRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Long reviewCount = 0L;

    @Column(name = "kakao_id")
    private Long kakaoId;

    @OneToMany(mappedBy = "cafe")
    private List<OperationHour> operationHours = new ArrayList<>();

    @OneToMany(mappedBy = "cafe")
    private List<CafeReviewKeyword> cafeReviewKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "cafe")
    private List<Image> images = new ArrayList<>();

    @Builder
    public Cafe(final Long id,
                final String name,
                final String phoneNumber,
                final  String roadAddress,
                final BigDecimal longitude,
                final BigDecimal latitude,
                final BigDecimal starRating,
                final Long reviewCount,
                final Long kakaoId) {

        this.id = id;
        this.name = name;
        this.roadAddress = roadAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.starRating = starRating;
        this.reviewCount = reviewCount;
        this.kakaoId = kakaoId;

        if(phoneNumber != null) {
            validatePhoneNumber(convertFormatPhoneNumber(phoneNumber));
            this.phoneNumber = phoneNumber;
        }
    }

    public void addReviewCountAndCalculateStarRating(final Integer starRating) {
        BigDecimal totalScore = this.starRating.multiply(BigDecimal.valueOf(this.reviewCount));
        BigDecimal newReviewScore = BigDecimal.valueOf(starRating);
        totalScore = totalScore.add(newReviewScore);

        this.reviewCount++;
        BigDecimal newReviewCount = BigDecimal.valueOf(this.reviewCount);
        this.starRating = totalScore.divide(newReviewCount, 2, RoundingMode.HALF_UP);
    }

    public void minusReviewCountAndCalculateStarRating(final Integer starRating) {
        BigDecimal totalScore = this.starRating.multiply(BigDecimal.valueOf(this.reviewCount));
        BigDecimal minusReviewScore = BigDecimal.valueOf(starRating);
        totalScore = totalScore.subtract(minusReviewScore);

        this.reviewCount--;
        BigDecimal newReviewCount = BigDecimal.valueOf(this.reviewCount);
        this.starRating = totalScore.divide(newReviewCount, 2, RoundingMode.HALF_UP);
    }

    private void validatePhoneNumber(final String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty()) return;
        Matcher matcher = PHONE_NUMBER_PATTERN.matcher(phoneNumber);
        if(!matcher.matches()) {
            throw new InvalidCafeException("전화번호 형식이 올바르지 않습니다.");
        }
    }

}
