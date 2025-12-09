package com.ssafy.tothezip.notice.model.service;

import com.ssafy.tothezip.notice.model.mapper.NoticeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private NoticeMapper noticeMapper;


}
