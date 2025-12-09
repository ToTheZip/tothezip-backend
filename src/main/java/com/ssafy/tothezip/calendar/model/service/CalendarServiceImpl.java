package com.ssafy.tothezip.calendar.model.service;

import com.ssafy.tothezip.calendar.model.mapper.CalendarMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private CalendarMapper calendarMapper;


}
