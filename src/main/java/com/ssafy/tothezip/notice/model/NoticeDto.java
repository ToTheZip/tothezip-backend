package com.ssafy.tothezip.notice.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class NoticeDto {

    // 목록용 요약
    @Data
    public static class Summary {
        private Integer noticeId;
        private String type;
        private String title;
        private String writer;
        private Integer views;
        private LocalDate registDate;
        private Boolean pinned;
    }

    // 상세용
    @Data
    public static class Detail {
        private Integer noticeId;
        private String type;
        private String title;
        private String content;
        private String writer;
        private Integer views;
        private LocalDate registDate;
        private Boolean pinned;
    }

    // 리스트 응답
    @Data
    public static class NoticeList {
        private List<Summary> pinned;
        private List<Summary> notices;
        private int page;
        private long totalItems;
        private String sort;  // hot, current
        private String typeFilter;
    }

    // 관리자용 / 뉴스만 등록 및 수정 가능
    // 공지 등록
    @Data
    public static class CreateNotice {
        private String title;
        private String content;
        // 공지 등록 후 바로 상세페이지 가는 용도
        private Integer noticeId;
    }

    // 공지 수정
    @Data
    public static class UpdateNotice {
        private String title;
        private String content;
    }
}
