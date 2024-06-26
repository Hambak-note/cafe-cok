package com.sideproject.cafe_cok.keword.domain;


import com.sideproject.cafe_cok.cafe.domain.Cafe;
import com.sideproject.cafe_cok.global.entity.BaseEntity;
import com.sideproject.cafe_cok.review.domain.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "cafe_review_keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CafeReviewKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafes_id")
    private Cafe cafe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviews_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keywords_id")
    private Keyword keyword;

    public CafeReviewKeyword(final Cafe cafe,
                             final Review review,
                             final Keyword keyword) {
        if(cafe != null) changeCafe(cafe);
        if(review != null) changeReview(review);
        this.keyword = keyword;
    }

    public void changeReview(final Review review) {
        this.review = review;
        review.getCafeReviewKeywords().add(this);
    }

    public void changeCafe(final Cafe cafe) {
        this.cafe = cafe;
        cafe.getCafeReviewKeywords().add(this);
    }
}
