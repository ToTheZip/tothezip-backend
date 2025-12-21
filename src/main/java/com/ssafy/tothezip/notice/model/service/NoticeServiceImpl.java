package com.ssafy.tothezip.notice.model.service;

import com.ssafy.tothezip.notice.model.NoticeDto;
import com.ssafy.tothezip.notice.model.mapper.NoticeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private NoticeMapper noticeMapper;

    // 기본 값 세팅
    // 기본 분류 = 전체보기
    private String baseTypeFilter(String typeFilter) {
        if (typeFilter == null || typeFilter.isBlank())
            return "ALL";
        if ("뉴스".equals(typeFilter) || "청약".equals(typeFilter) || "ALL".equals(typeFilter))
            return typeFilter;
        return "ALL";
    }

    // 기본 정렬 = 최신순
    private String baseSort(String sort) {
        if (sort == null || sort.isBlank())
            return "latest";
        if ("hot".equals(sort))
            return "hot";
        return "latest";
    }

    // 관리자 권한
    private void checkAdmin(String email) {
        if (!"admin".equals(email)) {
            throw new RuntimeException("관리자만 가능한 기능입니다.");
        }
    }

    @Override
    public NoticeDto.NoticeList list(String typeFilter, String sort, int page) {
        // 분류 및 정렬
        String btf = baseTypeFilter(typeFilter);
        String bs = baseSort(sort);
        // 페이징용
        int p = Math.max(page, 1); // page
        int size = 15; // size
        int offset = (p-1)*size; // offset

        List<NoticeDto.Summary> pinned = noticeMapper.getPinned(btf);
        long total = noticeMapper.countNotices(btf);
        List<NoticeDto.Summary> notices = noticeMapper.getNotices(btf, bs, size, offset);

        NoticeDto.NoticeList nList = new NoticeDto.NoticeList();
        nList.setPinned(pinned);
        nList.setNotices(notices);
        nList.setPage(p);
        nList.setTotalItems(total);
        nList.setSort(bs);
        nList.setTypeFilter(btf);

        return nList;
    }

    @Override
    public NoticeDto.NoticeList main(String typeFilter, String sort) {
        // 분류 및 정렬
        String btf = baseTypeFilter(typeFilter);
        String bs = baseSort(sort);
        // 메인에 요약으로 띄우는거라 페이징 x
        int size = 4; // size

        List<NoticeDto.Summary> pinned = noticeMapper.getPinned(btf);
        List<NoticeDto.Summary> notices = noticeMapper.getNotices(btf, bs, size, 0);

        NoticeDto.NoticeList nList = new NoticeDto.NoticeList();
        nList.setPinned(pinned);
        nList.setNotices(notices);
        nList.setPage(1);
        nList.setTotalItems(noticeMapper.countNotices(btf));
        nList.setSort(bs);
        nList.setTypeFilter(btf);

        return nList;
    }

    @Override
    @Transactional
    public NoticeDto.Detail detail(int noticeId) {
        // 조회수 1 증가 후 상세 페이지
        noticeMapper.increaseViews(noticeId);
        return noticeMapper.detail(noticeId);
    }

    @Override
    @Transactional
    public int createNotice(NoticeDto.CreateNotice index, String loginEmail) {
        // 관리자 확인
        checkAdmin(loginEmail);

        noticeMapper.createNotice(index, "admin");
        if(index.getNoticeId() == null)
            return -1;

        return index.getNoticeId();
    }

    @Override
    @Transactional
    public void updateNotice(int noticeId, NoticeDto.UpdateNotice index, String loginEmail) {
        // 관리자 확인
        checkAdmin(loginEmail);

        int updated = noticeMapper.updateNotice(noticeId, index);
        if (updated == 0)
            throw new RuntimeException("수정할 공지가 없거나 뉴스 공지가 아닙니다.");
    }

    @Override
    @Transactional
    public void deleteNotice(int noticeId, String loginEmail) {
        // 관리자 확인
        checkAdmin(loginEmail);

        int deleted = noticeMapper.deleteNotice(noticeId);
        if (deleted == 0)
            throw new RuntimeException("삭제할 공지가 없거나 뉴스 공지가 아닙니다.");
    }

    @Override
    @Transactional
    public void pin(int noticeId, String loginEmail) {
        // 관리자 확인
        checkAdmin(loginEmail);

        noticeMapper.pin(noticeId, "admin");
    }

    @Override
    @Transactional
    public void unpin(int noticeId, String loginEmail) {
        // 관리자 확인
        checkAdmin(loginEmail);

        noticeMapper.unpin(noticeId);
    }
}
