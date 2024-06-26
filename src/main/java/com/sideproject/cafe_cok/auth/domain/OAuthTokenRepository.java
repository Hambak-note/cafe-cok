package com.sideproject.cafe_cok.auth.domain;

import com.sideproject.cafe_cok.auth.exception.NoSuchOAuthTokenException;
import com.sideproject.cafe_cok.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {

    boolean existsByMemberId(final Long memberId);

    @Query("SELECT o " +
            "FROM OAuthToken o " +
            "WHERE o.member.id = :memberId")
    Optional<OAuthToken> findByMemberId(final Long memberId);

    default OAuthToken getByMemberId(final Long memberId) {
        return findByMemberId(memberId)
                .orElseThrow(NoSuchOAuthTokenException::new);
    }

    void deleteAllByMemberIn(List<Member> members);
}
