package com.ssafy.tothezip.property.model;

import lombok.AllArgsConstructor;
import lombok.Data;

public class PropertySearchDto {

    @Data
    @AllArgsConstructor
    public static class AptItem {
        private String aptSeq;
        private String aptName;
    }
}
