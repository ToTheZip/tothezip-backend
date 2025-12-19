package com.ssafy.tothezip.user.model;

import lombok.Data;

import java.util.List;

@Data
public class PreferenceDto {
    // 관심 주변 시설, 지역
    private List<Integer> tagIds;

    // 희망 가격, 평수 범위
    private Integer minPrice;
    private Integer maxPrice;
    private Integer minArea;
    private Integer maxArea;

}
