package com.sideproject.cafe_cok.member.domain;

import com.sideproject.cafe_cok.bookmark.domain.BookmarkFolder;
import com.sideproject.cafe_cok.combination.domain.Combination;
import com.sideproject.cafe_cok.global.entity.BaseEntity;
import com.sideproject.cafe_cok.member.domain.enums.SocialType;
import com.sideproject.cafe_cok.member.exception.InvalidMemberException;
import com.sideproject.cafe_cok.plan.domain.Plan;
import com.sideproject.cafe_cok.review.domain.Review;
import com.sideproject.cafe_cok.util.Constants;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "picture")
    private String picture;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<BookmarkFolder> bookmarkFolders = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Combination> combinations = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Member(final Long id,
                  final String email,
                  final String picture,
                  final String nickname,
                  final SocialType socialType,
                  final LocalDateTime deletedAt) {

        this.id = id;
        this.nickname = nickname;
        this.picture = picture;
        this.socialType = socialType;
        this.deletedAt = deletedAt;
        if(email != null) {
            validateEmail(email);
            this.email = email;
        }
    }

    private void validateEmail(final String email) {

        Matcher matcher = Constants.EMAIL_PATTERN.matcher(email);
        if(!matcher.matches()) {
            throw new InvalidMemberException("이메일 형식이 올바르지 않습니다.");
        }
    }
}
