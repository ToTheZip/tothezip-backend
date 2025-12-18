package com.ssafy.tothezip.notice.model.mapper;

import com.ssafy.tothezip.notice.model.NoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    // 중요 공지 목록
    List<NoticeDto.Summary> getPinned(@Param("typeFilter") String typeFilter);

    // 일반 공지 페이징용
    long countNotices(@Param("typeFilter") String typeFilter);

    // 일반 공지 목록
    List<NoticeDto.Summary> getNotices(
            @Param("typeFilter") String typeFilter,
            @Param("sort") String sort,
            @Param("limit") int limit, // 페이징용
            @Param("offset") int offset
    );

    // 상세용
    NoticeDto.Detail detail(@Param("noticeId") long noticeId);

    // 조회수 증가 ( 상세 페이지 가기 전에 올리고 싶음 )
    int increaseViews(@Param("noticeId") long noticeId);

    // 관리자용
    // 공지 등록
    int createNotice(@Param("index") NoticeDto.CreateNotice index, @Param("writer") String writer);

    // 공지 수정
    int updateNotice(@Param("noticeId") long noticeId, @Param("index") NoticeDto.UpdateNotice index);

    // 공지 삭제
    int deleteNotice(@Param("noticeId") long noticeId);

    // 중요 공지 o
    int pin(@Param("noticeId") long noticeId, @Param("pinnedBy") String pinnedBy);

    // 중요 공지 x
    int unpin(@Param("noticeId") long noticeId);

}
