package com.ssafy.tothezip.user.model;

import lombok.Data;

import java.util.List;

@Data
public class PreferenceDto {
    // 관심 주변 시설, 지역
    private List<Integer> tagIds;

    private String sido;
    private String gugun;

    // 희망 평수 범위
    private Integer minArea;
    private Integer maxArea;

    // 희망 층수 범위
    private Integer minFloor;
    private Integer maxFloor;

}
