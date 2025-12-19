package com.ssafy.tothezip.calendar.model.service;

import com.ssafy.tothezip.calendar.model.CalendarListDto;
import com.ssafy.tothezip.calendar.model.mapper.CalendarMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@AllArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private CalendarMapper calendarMapper;


    @Override
    public CalendarListDto getNotice(int year, int month) {

        LocalDate from = YearMonth.of(year, month).atDay(1);
        LocalDate to = YearMonth.of(year, month).atEndOfMonth();

        return new CalendarListDto(calendarMapper.getNotice(from, to));
    }

    @Override
    public CalendarListDto getMine(Integer userId, int year, int month) {

        LocalDate from = YearMonth.of(year, month).atDay(1);
        LocalDate to = YearMonth.of(year, month).atEndOfMonth();
        return new CalendarListDto(calendarMapper.getMine(userId, "청약", from, to));
    }
}
