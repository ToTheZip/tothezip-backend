package com.ssafy.tothezip.property.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

public class AiRecoRankDto {

    @Data
    public static class PricePoint {
        private String date;
        private String amount;
    }

    @Data
    public static class Base {
        private Integer propertyId;
        private String type;
        private String price;
        private String deposit;
        private Double area;
        private Double latitude;
        private Double longitude;

        private String aptName;

        // ✅ 추가
        private Double rating;
        private List<PricePoint> recentPriceSeries;
        private String trend;
    }

    @Data
    public static class Candidate {
        private Integer propertyId;
        private String aptName;

        private String price;
        private String deposit;
        private Double area;
        private Double distM;

        // ✅ 추가
        private Double rating;
        private List<PricePoint> recentPriceSeries;
        private String trend;
    }

    @Data
    public static class RankRequest {
        private Base base;
        private List<Candidate> candidates;

        // 옵션
        private Integer topK;          // 예: 10
        private Integer maxReasons;    // 예: 3
        private String mode;           // compare
    }

    @Data
    public static class CandidateResult {
        private Integer propertyId;

        private Double score;          // 0~100
        private String judgeCode;      // STRONG_RECO / RECO / CAUTION / WEAK_RECO
        private String summary;
        private List<String> reasons;

        private Map<String, Double> breakdown;
    }

    @Data
    public static class RankResponse {
        private String status;     // ok
        private String model;      // gpt-4.1
        private String error;      // 있으면 에러
        private List<CandidateResult> results;
    }
}
