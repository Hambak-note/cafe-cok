package com.sideproject.hororok.bookmark.domain;

import com.sideproject.hororok.bookmark.exception.NoSuchFolderException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkFolderRepository extends JpaRepository<BookmarkFolder, Long> {

    Long countByMemberId(Long memberId);
    List<BookmarkFolder> findByMemberId(Long memberId);


    default BookmarkFolder getById(final Long id) {
        return findById(id)
                .orElseThrow(NoSuchFolderException::new);
    }

}
