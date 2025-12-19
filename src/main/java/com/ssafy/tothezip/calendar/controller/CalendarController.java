package com.ssafy.tothezip.calendar.controller;

import com.ssafy.tothezip.calendar.model.CalendarListDto;
import com.ssafy.tothezip.calendar.model.service.CalendarService;
import com.ssafy.tothezip.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
public class CalendarController {

    private CalendarService calendarService;

    // 공지용 캘린더
    @GetMapping("/notice/calendar")
    public CalendarListDto noticeCalendar(@RequestParam int year,
                                          @RequestParam int month){

        return calendarService.getNotice(year, month);
    }

    // 개인용 캘린더
    @GetMapping("/user/calendar")
    public CalendarListDto myCalendar(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestParam int year,
                                      @RequestParam int month){

        Integer userId = userDetails.getUser().getUserId();
        return calendarService.getMine(userId, year, month);
    }
}
