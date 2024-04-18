package com.sideproject.hororok.favorite.presentation;


import com.sideproject.hororok.auth.dto.LoginMember;
import com.sideproject.hororok.auth.presentation.AuthenticationPrincipal;
import com.sideproject.hororok.favorite.application.BookmarkFolderService;
import com.sideproject.hororok.favorite.dto.request.BookmarkFolderSaveRequest;
import com.sideproject.hororok.favorite.dto.request.BookmarkFolderUpdateRequest;
import com.sideproject.hororok.favorite.dto.response.BookmarkFoldersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark")
@Tag(name = "Favorite", description = "즐겨찾기(북마크) 관련 API")
public class BookmarkController {

    private final BookmarkFolderService bookmarkFolderService;
    
    @GetMapping("/folders")
    @Operation(summary = "하단 탭의 \"저장\" 버튼을 눌렀을 때 필요한 정보 제공")
    public ResponseEntity<BookmarkFoldersResponse> myPlace(@AuthenticationPrincipal LoginMember loginMember) {
        BookmarkFoldersResponse response = bookmarkFolderService.bookmarkFolders(loginMember);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/folder/save")
    @Operation(summary = "북마크 -> 새 폴더 추가 -> 완료 선택 시 기능")
    public ResponseEntity<BookmarkFoldersResponse> folderSave(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody BookmarkFolderSaveRequest request) {

        BookmarkFoldersResponse response = bookmarkFolderService.save(request, loginMember);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/folder/update")
    @Operation(summary = "'편집하기' -> '수정' -> '완료' 선택 시 기능")
    public ResponseEntity<BookmarkFoldersResponse> folderUpdate(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody BookmarkFolderUpdateRequest request) {

        BookmarkFoldersResponse response = bookmarkFolderService.update(request, loginMember);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/folder/{folderId}/update/visible")
    @Operation(summary = "폴더의  토글(지도 노출 여부) 버튼을 눌렀을 때 동작하는 기능")
    public ResponseEntity<Void> folderVisibleUpdate(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long folderId){

        bookmarkFolderService.updateFolderVisible(folderId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/folder/{folderId}/delete")
    @Operation(summary = "폴더 삭제 버튼을 눌렀을 때 동작하는 기능")
    public ResponseEntity<BookmarkFoldersResponse> folderDelete(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long folderId){

        BookmarkFoldersResponse response
                = bookmarkFolderService.deleteFolder(folderId, loginMember);
        return ResponseEntity.ok(response);
    }
}