package com.sideproject.hororok.favorite.application;

import com.sideproject.hororok.favorite.domain.Bookmark;
import com.sideproject.hororok.favorite.domain.BookmarkFolder;
import com.sideproject.hororok.favorite.domain.BookmarkFolderRepository;
import com.sideproject.hororok.favorite.domain.BookmarkRepository;
import com.sideproject.hororok.favorite.dto.BookmarkDto;
import com.sideproject.hororok.favorite.dto.response.BookmarksResponse;
import com.sideproject.hororok.favorite.exception.NoSuchFolderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkFolderRepository bookmarkFolderRepository;


    public BookmarksResponse bookmarks(Long folderId){

        BookmarkFolder findFolder = bookmarkFolderRepository.findById(folderId)
                .orElseThrow(() -> new NoSuchFolderException());

        List<Bookmark> findBookmarks
                = bookmarkRepository.findByBookmarkFolderId(folderId);

        if(findBookmarks.isEmpty()) {
            return new BookmarksResponse(
                    findFolder.getId(), findFolder.getName(),
                    findFolder.getColor());
        }

        List<BookmarkDto> convertBookmarks
                = findBookmarks.stream()
                .map(bookmark -> BookmarkDto.from(bookmark.getCafe()))
                .collect(Collectors.toList());


        return new BookmarksResponse(
                findFolder.getId(), findFolder.getName(),
                findFolder.getColor(), convertBookmarks);
    }

}