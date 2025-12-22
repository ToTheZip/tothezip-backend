package com.ssafy.tothezip.region.model;

import lombok.AllArgsConstructor;
import lombok.Data;

public class RegionDto {

    @Data
    @AllArgsConstructor
    public static class Dong {
        private String dongCode; // 10자리
        private String dongName;
    }
}
