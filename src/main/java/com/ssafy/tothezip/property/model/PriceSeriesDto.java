package com.ssafy.tothezip.property.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PriceSeriesDto {
    private String aptSeq;
    private String dealType;   // 전세/매매 등
    private String period;     // month / quarter
    private String from;       // yyyy-MM-dd
    private String to;         // yyyy-MM-dd
    private List<Point> points;

    @Data
    public static class Point {
        private String date;       // "2024-11" or "2024-Q3"
        private Integer count;
        private BigDecimal avgPrice;
        private Long medianPrice;
    }
}
