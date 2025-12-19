package com.ssafy.tothezip.calendar.model.mapper;

import com.ssafy.tothezip.calendar.model.CalendarDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CalendarMapper {

    List<CalendarDto> getNotice(@Param("from") LocalDate from,
                                   @Param("to") LocalDate to);

    List<CalendarDto> getMine(@Param("userId") Integer userId,
                                 @Param("type") String type,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to);
}
