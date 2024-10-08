package com.sideproject.cafe_cok.image.domain;


import com.sideproject.cafe_cok.image.domain.enums.ImageType;
import com.sideproject.cafe_cok.cafe.domain.Cafe;
import com.sideproject.cafe_cok.global.entity.BaseEntity;
import com.sideproject.cafe_cok.menu.domain.Menu;
import com.sideproject.cafe_cok.review.domain.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@Table(name = "images")
@NoArgsConstructor
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "medium")
    private String medium;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cafes_id")
    private Cafe cafe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reviews_id")
    private Review review;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menus_id")
    private Menu menu;

    @Builder
    public Image(final Long id,
                 final ImageType imageType,
                 final String origin,
                 final String thumbnail,
                 final String medium,
                 final Cafe cafe,
                 final Review review,
                 final Menu menu) {
        this.id = id;
        this.imageType = imageType;
        this.origin = origin;
        this.thumbnail = thumbnail;
        this.medium = medium;
        if(cafe != null) changeCafe(cafe);
        if(review != null) changeReview(review);
        this.menu = menu;
    }

    public void changeReview(final Review review) {
        this.review = review;
        review.getImages().add(this);
    }

    public void changeCafe(final Cafe cafe) {
        this.cafe = cafe;
        cafe.getImages().add(this);
    }
}
