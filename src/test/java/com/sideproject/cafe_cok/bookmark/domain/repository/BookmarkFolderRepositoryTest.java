package com.sideproject.cafe_cok.bookmark.domain.repository;

import com.sideproject.cafe_cok.bookmark.domain.Bookmark;
import com.sideproject.cafe_cok.bookmark.domain.BookmarkFolder;
import com.sideproject.cafe_cok.bookmark.dto.BookmarkFolderDetailDto;
import com.sideproject.cafe_cok.bookmark.exception.NoSuchFolderException;
import com.sideproject.cafe_cok.cafe.domain.Cafe;
import com.sideproject.cafe_cok.cafe.domain.repository.CafeRepository;
import com.sideproject.cafe_cok.member.domain.Member;
import com.sideproject.cafe_cok.member.domain.repository.MemberRepository;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.sideproject.cafe_cok.constant.TestConstants.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class BookmarkFolderRepositoryTest {

    @Autowired
    private BookmarkFolderRepository bookmarkFolderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;


    @Test
    void 북마크_폴더_ID로_북마크_폴더를_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        BookmarkFolder bookmarkFolder = BookmarkFolder.builder()
                .name(BOOKMARK_FOLDER_NAME_1)
                .color(BOOKMARK_FOLDER_COLOR_1)
                .isVisible(true)
                .isDefaultFolder(true)
                .member(savedMember)
                .build();
        BookmarkFolder savedBookmarkFolder = bookmarkFolderRepository.save(bookmarkFolder);

        //when
        BookmarkFolder findBookmarkFolder = bookmarkFolderRepository.getById(savedBookmarkFolder.getId());

        //then
        assertThat(findBookmarkFolder).isNotNull();
        assertThat(findBookmarkFolder.getId()).isEqualTo(savedBookmarkFolder.getId());
        assertThat(findBookmarkFolder.getName()).isEqualTo(savedBookmarkFolder.getName());
        assertThat(findBookmarkFolder.getColor()).isEqualTo(savedBookmarkFolder.getColor());
        assertThat(findBookmarkFolder.getIsVisible()).isEqualTo(savedBookmarkFolder.getIsVisible());
        assertThat(findBookmarkFolder.getIsDefaultFolder()).isEqualTo(savedBookmarkFolder.getIsDefaultFolder());
        assertThat(findBookmarkFolder.getMember()).isEqualTo(savedBookmarkFolder.getMember());
    }

    @Test
    @DisplayName("존재하지 않는 북마크 폴더 id로 조회 시 에러를 발생시킨다.")
    void get_by_non_existent_id() {

        //when & then
        assertThatExceptionOfType(NoSuchFolderException.class)
                .isThrownBy(() -> bookmarkFolderRepository.getById(NON_EXISTENT_ID))
                .withMessage("[ID : " + NON_EXISTENT_ID + "] 에 해당하는 북마크 폴더가 존재하지 않습니다.");
    }

    @Test
    void 회원_ID로_북마크_폴더_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        BookmarkFolder bookmarkFolder1 = BookmarkFolder.builder()
                .name(BOOKMARK_FOLDER_NAME_1)
                .color(BOOKMARK_FOLDER_COLOR_1)
                .isVisible(true)
                .isDefaultFolder(true)
                .member(savedMember)
                .build();
        BookmarkFolder savedBookmarkFolder1 = bookmarkFolderRepository.save(bookmarkFolder1);

        BookmarkFolder bookmarkFolder2 = BookmarkFolder.builder()
                .name(BOOKMARK_FOLDER_NAME_2)
                .color(BOOKMARK_FOLDER_COLOR_2)
                .isVisible(true)
                .isDefaultFolder(false)
                .member(savedMember)
                .build();
        BookmarkFolder savedBookmarkFolder2 = bookmarkFolderRepository.save(bookmarkFolder2);

        //when
        List<BookmarkFolder> findBookmarkFolders = bookmarkFolderRepository.findByMemberId(savedMember.getId());

        //then
        assertThat(findBookmarkFolders).hasSize(2);
        assertThat(findBookmarkFolders).extracting("name").containsExactlyInAnyOrder(BOOKMARK_FOLDER_NAME_1, BOOKMARK_FOLDER_NAME_2);
        assertThat(findBookmarkFolders).extracting("color").containsExactlyInAnyOrder(BOOKMARK_FOLDER_COLOR_1, BOOKMARK_FOLDER_COLOR_2);
    }

    @Test
    @DisplayName("존재하지 않는 memberId로 북마크 폴더 목록을 조회할 시 빈 리스트를 반환한다.")
    void find_bookmark_folder_list_by_non_existent_member_id() {

        //when
        List<BookmarkFolder> findBookmarkFolders = bookmarkFolderRepository.findByMemberId(NON_EXISTENT_ID);

        //then
        assertThat(findBookmarkFolders).isEmpty();
    }

    @Test
    void 회원_ID로_북마크_폴더_상세_목록을_조회한다() {

        //given
        Member member = Member.builder()
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_NICKNAME)
                .socialType(MEMBER_SOCIAL_TYPE)
                .build();
        Member savedMember = memberRepository.save(member);

        BookmarkFolder bookmarkFolder1 = BookmarkFolder.builder()
                .name(BOOKMARK_FOLDER_NAME_1)
                .color(BOOKMARK_FOLDER_COLOR_1)
                .isVisible(true)
                .isDefaultFolder(true)
                .member(savedMember)
                .build();
        BookmarkFolder savedBookmarkFolder1 = bookmarkFolderRepository.save(bookmarkFolder1);

        BookmarkFolder bookmarkFolder2 = BookmarkFolder.builder()
                .name(BOOKMARK_FOLDER_NAME_2)
                .color(BOOKMARK_FOLDER_COLOR_2)
                .isVisible(true)
                .isDefaultFolder(false)
                .member(savedMember)
                .build();
        BookmarkFolder savedBookmarkFolder2 = bookmarkFolderRepository.save(bookmarkFolder2);

        Cafe cafe = Cafe.builder()
                .name(CAFE_NAME)
                .phoneNumber(CAFE_PHONE_NUMBER)
                .roadAddress(CAFE_ROAD_ADDRESS)
                .longitude(CAFE_LONGITUDE)
                .latitude(CAFE_LATITUDE)
                .kakaoId(CAFE_KAKAO_ID)
                .build();
        Cafe savedCafe = cafeRepository.save(cafe);

        Bookmark bookmark = Bookmark.builder()
                .cafe(savedCafe)
                .bookmarkFolder(savedBookmarkFolder1)
                .build();
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        //when
        List<BookmarkFolderDetailDto> findBookmarkFolderDetailDtoList
                = bookmarkFolderRepository.getBookmarkFolderDetails(savedMember.getId());

        //then
        assertThat(findBookmarkFolderDetailDtoList).hasSize(2);
        assertThat(findBookmarkFolderDetailDtoList)
                .extracting("bookmarkCount")
                .containsExactlyInAnyOrder(1L, 0L);
    }

}