package com.ssafy.tothezip.property.model;

import lombok.Data;

import java.util.List;

public class PropertySearchDto {

    @Data
    public static class SearchRequest {
        // 지역
        private String sido;
        private String gugun;
        private String dong;

        // 아파트명(부분 검색)
        private String aptName;
        private String aptSeq;

        // 주변시설
        private Boolean nearSubway;
        private Boolean nearSchool;
        private Boolean nearHospital;
        private Boolean nearCulture;

        // 거래유형(복수)
        private List<String> dealType;

        // 가격
        private Integer depositMin;
        private Integer depositMax;
        private Integer monthlyRentMin;
        private Integer monthlyRentMax;
        private Integer jeonseMin;
        private Integer jeonseMax;
        private Integer buyMin;
        private Integer buyMax;

        // 면적/층
        private Double areaMin;
        private Double areaMax;
        private Integer floorMin;
        private Integer floorMax;

        // 준공/평점 (infos 기준)
        private Integer buildYearMin;
        private Integer buildYearMax;
        private Double ratingMin;
        private Double ratingMax;

        // paging
        private Integer limit = 30;
        private Integer offset = 0;
    }

    @Data
    public static class BuildingCard {
        private String aptSeq;
        private String aptName;
        private String roadAddress;
        private Double latitude;
        private Double longitude;
        private Integer buildYear;
        private Double propertyRating;
        private String imageUrl;  // 대표 이미지
        private List<String> tags;
        private List<String> images;  // 전체 이미지

        private String minDealType;  // 전세/월세/매매
        private Integer minPrice;  // properties.price
        private Integer minDeposit;  // properties.deposit (월세일 때)
    }

    @Data
    public static class AptItem {
        private String aptSeq;
        private String aptName;
    }
}
