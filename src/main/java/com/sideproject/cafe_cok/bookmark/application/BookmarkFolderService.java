package com.sideproject.cafe_cok.bookmark.application;

import com.sideproject.cafe_cok.auth.dto.LoginMember;
import com.sideproject.cafe_cok.bookmark.dto.BookmarkCafeDto;
import com.sideproject.cafe_cok.bookmark.dto.BookmarkFolderCountDto;
import com.sideproject.cafe_cok.bookmark.dto.request.BookmarkFolderSaveRequest;
import com.sideproject.cafe_cok.bookmark.dto.request.BookmarkFolderUpdateRequest;
import com.sideproject.cafe_cok.bookmark.dto.response.BookmarkFolderDeleteResponse;
import com.sideproject.cafe_cok.bookmark.dto.response.BookmarkFoldersResponse;
import com.sideproject.cafe_cok.bookmark.dto.response.BookmarksResponse;
import com.sideproject.cafe_cok.bookmark.exception.DefaultFolderDeletionNotAllowedException;
import com.sideproject.cafe_cok.bookmark.exception.DefaultFolderUpdateNotAllowedException;
import com.sideproject.cafe_cok.bookmark.domain.BookmarkFolder;
import com.sideproject.cafe_cok.bookmark.domain.repository.BookmarkFolderRepository;
import com.sideproject.cafe_cok.member.domain.Member;
import com.sideproject.cafe_cok.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkFolderService {

    private final MemberRepository memberRepository;
    private final BookmarkFolderRepository bookmarkFolderRepository;

    public BookmarksResponse bookmarks(final Long folderId){

        BookmarkFolder findFolder = bookmarkFolderRepository.getById(folderId);
        List<BookmarkCafeDto> findBookmarkCafeDtoList
                = findFolder.getBookmarks().stream()
                .map(bookmark -> new BookmarkCafeDto(bookmark.getId(), bookmark.getCafe()))
                .collect(Collectors.toList());
        return new BookmarksResponse(findFolder, findBookmarkCafeDtoList);
    }

    public BookmarkFoldersResponse bookmarkFolders(final LoginMember loginMember) {

        List<BookmarkFolderCountDto> findBookmarkFolderCountDtoList =
                bookmarkFolderRepository.findBookmarkFolderCountDtoListByMemberId(loginMember.getId());
        return new BookmarkFoldersResponse(findBookmarkFolderCountDtoList);
    }

    @Transactional
    public void save(final BookmarkFolderSaveRequest request,
                     final LoginMember loginMember){

        Member findMember = memberRepository.getById(loginMember.getId());
        BookmarkFolder bookmarkFolder = request.toBookmarkFolder(findMember);
        bookmarkFolderRepository.save(bookmarkFolder);
    }

    @Transactional
    public void update(final BookmarkFolderUpdateRequest request){

        BookmarkFolder findFolder = bookmarkFolderRepository.getById(request.getFolderId());
        if(findFolder.getIsDefaultFolder()) throw new DefaultFolderUpdateNotAllowedException();
        findFolder.change(request);
    }

    @Transactional
    public void updateFolderVisible(final Long folderId) {

        BookmarkFolder findFolder = bookmarkFolderRepository.getById(folderId);
        findFolder.changeVisible();
    }

    @Transactional
    public BookmarkFolderDeleteResponse delete(final Long folderId) {

        BookmarkFolder findFolder = bookmarkFolderRepository.getById(folderId);
        if(findFolder.getIsDefaultFolder()) throw new DefaultFolderDeletionNotAllowedException();
        bookmarkFolderRepository.deleteById(folderId);
        return new BookmarkFolderDeleteResponse(folderId);
    }
}
