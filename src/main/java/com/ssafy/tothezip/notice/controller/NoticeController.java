package com.ssafy.tothezip.notice.controller;

import com.ssafy.tothezip.notice.model.NoticeDto;
import com.ssafy.tothezip.notice.model.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@AllArgsConstructor
@Slf4j
public class NoticeController {

    private NoticeService noticeService;

    // 메인 공지 요약용
    @GetMapping("/main")
    public ResponseEntity<NoticeDto.NoticeList> main(
            @RequestParam(required = false, defaultValue = "ALL") String typeFilter,
            @RequestParam(required = false, defaultValue = "latest") String sort) {

        return ResponseEntity.ok(noticeService.main(typeFilter, sort));
    }

    // 공지사항 페이지
    @GetMapping
    public ResponseEntity<NoticeDto.NoticeList> list(
            @RequestParam(required = false, defaultValue = "ALL") String typeFilter,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(required = false, defaultValue = "1") int page) {

        return ResponseEntity.ok(noticeService.list(typeFilter, sort, page));
    }

    // 상세 페이지
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDto.Detail> detail(@PathVariable int noticeId) {

        return ResponseEntity.ok(noticeService.detail(noticeId));
    }
}
