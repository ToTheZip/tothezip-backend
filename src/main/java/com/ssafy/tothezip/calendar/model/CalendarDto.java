package com.ssafy.tothezip.calendar.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarDto {

    private Integer subscriptionId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate announcementDate;

}
