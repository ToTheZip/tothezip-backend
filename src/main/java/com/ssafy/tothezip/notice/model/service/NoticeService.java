package com.ssafy.tothezip.notice.model.service;

import com.ssafy.tothezip.notice.model.NoticeDto;

public interface NoticeService {

    // 공지사항 페이지
    NoticeDto.NoticeList list(String typeFilter, String sort, int page);

    // 메인 공지(요약)
    NoticeDto.NoticeList main(String typeFilter, String sort);

    // 상세 페이지(조회수 1 올린 후 상세 조회)
    NoticeDto.Detail detail(int noticeId);

    // 관리자용
    // 공지 등록
    int createNotice(NoticeDto.CreateNotice index, String loginEmail);

    // 공지 수정
    void updateNotice(int noticeId, NoticeDto.UpdateNotice index, String loginEmail);

    // 공지 삭제
    void deleteNotice(int noticeId, String loginEmail);

    // 중요 공지 o
    void pin(int noticeId, String loginEmail);

    // 중요 공지 x
    void unpin(int noticeId, String loginEmail);
}
