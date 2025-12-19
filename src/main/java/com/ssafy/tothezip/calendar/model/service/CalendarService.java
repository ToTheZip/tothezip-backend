package com.ssafy.tothezip.calendar.model.service;

import com.ssafy.tothezip.calendar.model.CalendarListDto;

public interface CalendarService {

    // 공지용 캘린더
    CalendarListDto getNotice(int year, int month);

    // 개인용 캘린더
    CalendarListDto getMine(Integer userId, int year, int month);
}
