package com.ssafy.tothezip.calendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CalendarListDto {
    private List<CalendarDto> items;
}
