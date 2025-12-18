package com.ssafy.tothezip.notice.controller;

import com.ssafy.tothezip.notice.model.NoticeDto;
import com.ssafy.tothezip.notice.model.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
@AllArgsConstructor
@Slf4j
public class AdminNoticeController {

    private NoticeService noticeService;

    // 공지 등록
    @PostMapping
    public ResponseEntity<Integer> create(
            @RequestBody NoticeDto.CreateNotice index,
            Authentication authentication) {

        String email = (authentication != null) ? authentication.getName() : null;
        int noticeId = noticeService.createNotice(index, email);

        return ResponseEntity.ok(noticeId);
    }

    // 공지 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<Void> update(
            @PathVariable int noticeId,
            @RequestBody NoticeDto.UpdateNotice index,
            Authentication authentication) {

        String email = (authentication != null) ? authentication.getName() : null;
        noticeService.updateNotice(noticeId, index, email);

        return ResponseEntity.ok().build();
    }

    // 공지 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> delete(
            @PathVariable int noticeId,
            Authentication authentication) {

        String email = (authentication != null) ? authentication.getName() : null;
        noticeService.deleteNotice(noticeId, email);

        return ResponseEntity.ok().build();
    }

    // 중요 공지 o
    @PostMapping("/{noticeId}/pin")
    public ResponseEntity<Void> pin(
            @PathVariable int noticeId,
            Authentication authentication,
            HttpServletRequest request) {
        System.out.println("Authorization=" + request.getHeader("Authorization"));
        System.out.println("auth=" + authentication);
        System.out.println("auth=" + authentication);
        System.out.println("name=" + (authentication != null ? authentication.getName() : null));
        System.out.println("principal=" + (authentication != null ? authentication.getPrincipal() : null));
        System.out.println("authorities=" + (authentication != null ? authentication.getAuthorities() : null));

        String email = (authentication != null) ? authentication.getName() : null;
        noticeService.pin(noticeId, email);

        return ResponseEntity.ok().build();
    }

    // 중요 공지 x
    @DeleteMapping("/{noticeId}/pin")
    public ResponseEntity<Void> unpin(
            @PathVariable int noticeId,
            Authentication authentication) {

        String email = (authentication != null) ? authentication.getName() : null;
        noticeService.unpin(noticeId, email);

        return ResponseEntity.ok().build();
    }
}
