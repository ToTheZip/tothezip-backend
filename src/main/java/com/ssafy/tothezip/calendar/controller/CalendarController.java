package com.ssafy.tothezip.calendar.controller;

import com.ssafy.tothezip.calendar.model.service.CalendarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
@AllArgsConstructor
@Slf4j
public class CalendarController {

    private CalendarService calendarService;

}
