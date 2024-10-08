package com.sideproject.cafe_cok.auth.application;

import com.sideproject.cafe_cok.auth.dto.LoginMember;
import com.sideproject.cafe_cok.auth.dto.OAuthMember;
import com.sideproject.cafe_cok.auth.dto.response.AuthEmptyResponse;
import com.sideproject.cafe_cok.auth.exception.InvalidRestoreMemberException;
import com.sideproject.cafe_cok.member.domain.Feedback;
import com.sideproject.cafe_cok.member.domain.enums.FeedbackCategory;
import com.sideproject.cafe_cok.member.domain.enums.SocialType;
import com.sideproject.cafe_cok.member.domain.repository.FeedbackRepository;
import com.sideproject.cafe_cok.member.domain.repository.MemberRepository;
import com.sideproject.cafe_cok.auth.domain.AuthToken;
import com.sideproject.cafe_cok.auth.domain.OAuthToken;
import com.sideproject.cafe_cok.auth.domain.OAuthTokenRepository;
import com.sideproject.cafe_cok.auth.domain.redis.AuthRefreshToken;
import com.sideproject.cafe_cok.auth.domain.redis.AuthRefreshTokenRepository;
import com.sideproject.cafe_cok.auth.dto.request.TokenRenewalRequest;
import com.sideproject.cafe_cok.auth.dto.response.AccessAndRefreshTokenResponse;
import com.sideproject.cafe_cok.auth.dto.response.AccessTokenResponse;
import com.sideproject.cafe_cok.bookmark.domain.BookmarkFolder;
import com.sideproject.cafe_cok.bookmark.domain.repository.BookmarkFolderRepository;
import com.sideproject.cafe_cok.member.domain.Member;
import com.sideproject.cafe_cok.nickname.application.NicknameService;
import com.sideproject.cafe_cok.review.domain.Review;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final TokenCreator tokenCreator;
    private final MemberRepository memberRepository;
    private final FeedbackRepository feedbackRepository;
    private final OAuthTokenRepository oAuthTokenRepository;
    private final AuthRefreshTokenRepository authRefreshTokenRepository;
    private final BookmarkFolderRepository bookmarkFolderRepository;

    private final NicknameService nicknameService;

    private final String BASIC_FOLDER_NAME = "기본 폴더";
    private final String BASIC_FOLDER_COLOR = "#FE8282";
    private final Boolean BASIC_FOLDER_VISIBLE = true;
    private final Boolean BASIC_FOLDER_DEFAULT = true;

    @Transactional
    public AccessAndRefreshTokenResponse generateAccessAndRefreshToken(final OAuthMember oAuthMember) {
        Member foundMember = findMember(oAuthMember);

        OAuthToken savedOAuthToken = saveOAuthToken(oAuthMember, foundMember);
        AuthToken authToken = tokenCreator.createAuthToken(savedOAuthToken.getMember().getId());
        return new AccessAndRefreshTokenResponse(authToken.getAccessToken(), authToken.getRefreshToken());
    }

    public AccessTokenResponse generateAccessToken(final TokenRenewalRequest tokenRenewalRequest) {
        String refreshToken = tokenRenewalRequest.getRefreshToken();
        AuthToken authToken = tokenCreator.renewAuthToken(refreshToken);
        return new AccessTokenResponse(authToken.getAccessToken());
    }

    public Long extractMemberId(final String accessToken) {
        Long memberId = tokenCreator.extractPayload(accessToken);
        Member findMember = memberRepository.getById(memberId);
        return findMember.getId();
    }

    @Transactional
    public AuthEmptyResponse logout(final LoginMember loginMember) {

        Optional<OAuthToken> findOAuthToken = oAuthTokenRepository.findByMemberId(loginMember.getId());
        if(findOAuthToken.isPresent()) oAuthTokenRepository.delete(findOAuthToken.get());

        Optional<AuthRefreshToken> findRefreshToken = authRefreshTokenRepository.findById(loginMember.getId());
        if(findRefreshToken.isPresent()) authRefreshTokenRepository.delete(findRefreshToken.get());

        return new AuthEmptyResponse();
    }

    @Transactional
    public AuthEmptyResponse withdrawal(final LoginMember loginMember,
                           final String reason) {

        Member findMember = memberRepository.getById(loginMember.getId());
        memberRepository.update(findMember.getId(), LocalDateTime.now());

        Feedback newFeedback = Feedback.builder()
                .email(findMember.getEmail())
                .category(FeedbackCategory.WITHDRAWAL_REASON)
                .content(reason)
                .build();

        feedbackRepository.save(newFeedback);
        List<Review> findReviews = findMember.getReviews();
        findReviews.stream()
                .forEach(review -> review.getCafe().minusReviewCountAndCalculateStarRating(review.getStarRating()));
        return new AuthEmptyResponse();
    }

    private Member findMember(final OAuthMember oAuthMember) {
        String email = oAuthMember.getEmail();

        Optional<Member> findOptionalMember = memberRepository.findByEmailAndDeletedAtIsNull(email);
        if(findOptionalMember.isPresent()) return findOptionalMember.get();

        findOptionalMember = memberRepository.findByEmailAndDeletedAtIsNotNull(email);
        if(findOptionalMember.isPresent()) {
            Member findMember = findOptionalMember.get();
            LocalDateTime deletedAt = findMember.getDeletedAt();
            if(isMoreThanSevenDaysAgo(deletedAt)) throw new InvalidRestoreMemberException(deletedAt);
        }

        return saveMember(oAuthMember);
    }

    private boolean isMoreThanSevenDaysAgo(final LocalDateTime deletedAt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deletedAtPlusSevenDay = deletedAt.plusDays(7);
        return now.isAfter(deletedAt) && now.isBefore(deletedAtPlusSevenDay);
    }

    private Member saveMember(final OAuthMember oAuthMember) {

        String randomNickname = nicknameService.generateNickname();
        Member targetMember = Member.builder()
                .email(oAuthMember.getEmail())
                .nickname(randomNickname)
                .socialType(SocialType.KAKAO)
                .build();

        Member savedMember = memberRepository.save(targetMember);
        bookmarkFolderRepository.save(BookmarkFolder.builder()
                        .name(BASIC_FOLDER_NAME)
                        .color(BASIC_FOLDER_COLOR)
                        .isVisible(BASIC_FOLDER_VISIBLE)
                        .isDefaultFolder(BASIC_FOLDER_DEFAULT)
                        .build());
        return savedMember;
    }

    private OAuthToken saveOAuthToken(final OAuthMember oAuthMember, final Member member) {
        Long memberId = member.getId();
        if (oAuthTokenRepository.existsByMemberId(memberId)) {
            OAuthToken findOAuthToken = oAuthTokenRepository.getByMemberId(memberId);
            findOAuthToken.changeRefreshToken(oAuthMember.getRefreshToken());
            return oAuthTokenRepository.save(findOAuthToken);
        }

        return oAuthTokenRepository.save(new OAuthToken(member, oAuthMember.getRefreshToken()));
    }
}
